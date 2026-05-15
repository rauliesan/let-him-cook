package com.daw.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.daw.dtos.request.GenerarRecetasRequestDTO;
import com.daw.dtos.request.PublicarRecetaIaRequestDTO;
import com.daw.dtos.response.PublicarRecetaResponseDTO;
import com.daw.dtos.response.RecetaResponseDTO;
import com.daw.dtos.response.RecetaSugerenciaDTO;
import com.daw.entities.Api;
import com.daw.entities.IaModelo;
import com.daw.entities.Receta;
import com.daw.entities.TipoComida;
import com.daw.entities.Usuario;
import com.daw.exceptions.OperacionInvalidaException;
import com.daw.exceptions.RecursoNoEncontradoException;
import com.daw.mappers.RecetaMapper;
import com.daw.repositories.IaModeloRepository;
import com.daw.repositories.RecetaRepository;
import com.daw.repositories.TipoComidaRepository;
import com.daw.repositories.UsuarioRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;

import lombok.RequiredArgsConstructor;

/**
 * Servicio que gestiona la generación de recetas mediante IA y su publicación.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class IaGeneracionService {

    private final UsuarioRepository usuarioRepository;
    private final TipoComidaRepository tipoComidaRepository;
    private final RecetaRepository recetaRepository;
    private final IaModeloRepository iaModeloRepository;
    private final RecetaMapper recetaMapper;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    /** Clave DeepSeek de la aplicación — cargada desde application-secret.properties */
    @Value("${deepseek.api.key:}")
    private String appDeepseekKey;

    private static final int PUNTOS_POR_RECETA = 50;
    private static final String DEEPSEEK_ENDPOINT = "https://api.deepseek.com/v1/chat/completions";
    private static final String DEEPSEEK_MODEL = "deepseek-chat";

    /**
     * Llama a la IA configurada y devuelve 3 sugerencias de recetas.
     * Prioridad: clave personalizada del perfil > modelo asignado en perfil > clave de la app (DeepSeek).
     */
    @Transactional(readOnly = true)
    public List<RecetaSugerenciaDTO> generarSugerencias(GenerarRecetasRequestDTO dto, UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        String apiKey;
        String endpoint;
        String modelo;

        if (usuario.getIaCustomApiKey() != null && !usuario.getIaCustomApiKey().isBlank()) {
            // 1. IA personalizada configurada en el perfil
            apiKey   = usuario.getIaCustomApiKey();
            endpoint = (usuario.getIaCustomEndpoint() != null && !usuario.getIaCustomEndpoint().isBlank())
                    ? usuario.getIaCustomEndpoint() : DEEPSEEK_ENDPOINT;
            modelo   = (usuario.getIaCustomModelo() != null && !usuario.getIaCustomModelo().isBlank())
                    ? usuario.getIaCustomModelo() : DEEPSEEK_MODEL;

        } else if (usuario.getIaModeloSeleccionado() != null
                && usuario.getIaModeloSeleccionado().getApi() != null
                && usuario.getIaModeloSeleccionado().getApi().getApiKey() != null) {
            // 2. Modelo asignado al usuario en la BD (tabla ia_modelo)
            Api api  = usuario.getIaModeloSeleccionado().getApi();
            apiKey   = api.getApiKey();
            endpoint = (api.getEndpointUrl() != null && !api.getEndpointUrl().isBlank())
                    ? api.getEndpointUrl() : DEEPSEEK_ENDPOINT;
            modelo   = usuario.getIaModeloSeleccionado().getNombreModelo();

        } else if (appDeepseekKey != null && !appDeepseekKey.isBlank()) {
            // 3. Clave DeepSeek de la aplicación (predeterminado si está en application.properties)
            apiKey   = appDeepseekKey;
            endpoint = DEEPSEEK_ENDPOINT;
            modelo   = DEEPSEEK_MODEL;

        } else {
            // 4. Fallback 100% gratuito (Pollinations.ai) - ¡Funciona sin API Key!
            apiKey   = "free-key"; // Pollinations ignora este campo
            endpoint = "https://text.pollinations.ai/openai";
            modelo   = "openai";
        }

        String ingredientesList = String.join(", ", dto.getIngredientes());
        String preferencias = dto.getPreferencias() != null ? dto.getPreferencias() : "";
        String prompt = buildPrompt(ingredientesList, preferencias);

        return callAiApi(endpoint, apiKey, modelo, prompt);
    }

    /**
     * Publica la receta generada por IA, crea la categoría si no existe y suma puntos al usuario.
     */
    @Transactional
    public PublicarRecetaResponseDTO publicar(PublicarRecetaIaRequestDTO dto, UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        // Encontrar o crear la categoría automáticamente
        TipoComida categoria = null;
        if (dto.getCategoriaNombre() != null && !dto.getCategoriaNombre().isBlank()) {
            categoria = tipoComidaRepository.findByNombreIgnoreCase(dto.getCategoriaNombre())
                    .orElseGet(() -> {
                        TipoComida nueva = new TipoComida();
                        nueva.setNombre(dto.getCategoriaNombre());
                        nueva.setIconoUrl(dto.getCategoriaEmoji() != null ? dto.getCategoriaEmoji() : "🍽️");
                        nueva.setColorHex(dto.getCategoriaColor() != null ? dto.getCategoriaColor() : "#C13E28");
                        return tipoComidaRepository.save(nueva);
                    });
        }

        Receta receta = new Receta();
        receta.setNombre(dto.getNombre());
        receta.setDescripcion(dto.getDescripcion());
        receta.setIngredientes(dto.getIngredientes());
        receta.setTiempoPreparacion(dto.getTiempoPreparacion());
        receta.setDificultad(dto.getDificultad());
        receta.setCalorias(dto.getCalorias());
        receta.setAlergenos(dto.getAlergenos());
        receta.setEsPublica(dto.getEsPublica() != null ? dto.getEsPublica() : true);
        receta.setUsuario(usuario);
        receta.setTipoComida(categoria);

        if (dto.getIaModeloId() != null) {
            iaModeloRepository.findById(dto.getIaModeloId()).ifPresent(receta::setIaModelo);
        }

        receta = recetaRepository.save(receta);

        // Sumar puntos y recalcular nivel
        usuario.setPuntos(usuario.getPuntos() + PUNTOS_POR_RECETA);
        usuario.setNivel(Math.max(1, usuario.getPuntos() / 100 + 1));
        usuarioRepository.save(usuario);

        RecetaResponseDTO recetaDTO = recetaMapper.toResponseDTO(receta);

        PublicarRecetaResponseDTO response = new PublicarRecetaResponseDTO();
        response.setReceta(recetaDTO);
        response.setPuntosGanados(PUNTOS_POR_RECETA);
        response.setNuevosTotalPuntos(usuario.getPuntos());
        return response;
    }

    private String buildPrompt(String ingredientes, String preferencias) {
        StringBuilder sb = new StringBuilder();
        sb.append("Eres un chef experto y creativo. El usuario tiene estos ingredientes: ")
          .append(ingredientes).append(".\n");
        if (!preferencias.isBlank()) {
            sb.append("Preferencias adicionales: ").append(preferencias).append(".\n");
        }
        sb.append("Genera exactamente 3 recetas diferentes y creativas usando principalmente esos ingredientes.\n");
        sb.append("Responde ÚNICAMENTE con un objeto JSON válido con esta estructura (sin texto adicional fuera del JSON):\n");
        sb.append("{\"recetas\":[{")
          .append("\"nombre\":\"Nombre de la receta\",")
          .append("\"descripcion\":\"Descripción apetitosa de 2-3 frases\",")
          .append("\"ingredientes\":\"ingrediente1 (cantidad), ingrediente2 (cantidad), ...\",")
          .append("\"instrucciones\":\"Paso 1: ... Paso 2: ... Paso 3: ...\",")
          .append("\"tiempoPreparacion\":30,")
          .append("\"dificultad\":\"BAJA\",")
          .append("\"calorias\":400,")
          .append("\"alergenos\":\"gluten, lactosa\",")
          .append("\"categoria\":\"Ensaladas\",")
          .append("\"categoriaEmoji\":\"🥗\",")
          .append("\"categoriaColor\":\"#5D9B5D\"")
          .append("}]}\n");
        sb.append("Reglas: dificultad debe ser BAJA, MEDIA o ALTA. Tiempo en minutos. Color en formato hex (#RRGGBB).");
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private List<RecetaSugerenciaDTO> callAiApi(String endpoint, String apiKey, String modelo, String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("model", modelo);
        requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt)));
        requestBody.put("response_format", Map.of("type", "json_object"));
        requestBody.put("temperature", 0.8);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(endpoint, entity, Map.class);
            Map<String, Object> body = response.getBody();
            if (body == null) {
                throw new OperacionInvalidaException("La IA no devolvió respuesta.");
            }

            List<?> choices = (List<?>) body.get("choices");
            Map<?, ?> firstChoice = (Map<?, ?>) choices.get(0);
            Map<?, ?> message = (Map<?, ?>) firstChoice.get("message");
            String content = (String) message.get("content");

            JsonNode root = objectMapper.readTree(content);
            JsonNode recetasNode = root.get("recetas");

            List<RecetaSugerenciaDTO> result = new ArrayList<>();
            if (recetasNode != null && recetasNode.isArray()) {
                for (JsonNode r : recetasNode) {
                    RecetaSugerenciaDTO s = new RecetaSugerenciaDTO();
                    s.setNombre(r.path("nombre").asText("Receta sin nombre"));
                    s.setDescripcion(r.path("descripcion").asText(""));
                    s.setIngredientes(r.path("ingredientes").asText(""));
                    s.setInstrucciones(r.path("instrucciones").asText(""));
                    s.setTiempoPreparacion(r.path("tiempoPreparacion").asInt(30));
                    s.setDificultad(r.path("dificultad").asText("MEDIA"));
                    s.setCalorias(r.path("calorias").asInt(0));
                    s.setAlergenos(r.path("alergenos").asText(""));
                    s.setCategoria(r.path("categoria").asText("General"));
                    s.setCategoriaEmoji(r.path("categoriaEmoji").asText("🍽️"));
                    s.setCategoriaColor(r.path("categoriaColor").asText("#C13E28"));
                    result.add(s);
                }
            }
            return result;

        } catch (OperacionInvalidaException e) {
            throw e;
        } catch (Exception e) {
            throw new OperacionInvalidaException("Error al comunicarse con la IA: " + e.getMessage());
        }
    }
}
