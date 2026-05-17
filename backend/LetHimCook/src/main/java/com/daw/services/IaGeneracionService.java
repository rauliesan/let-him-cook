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

    @Value("${openrouter.api.key:}")
    private String appOpenRouterKey;

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
            
            if ("MISSING_KEY".equals(apiKey)) {
                throw new OperacionInvalidaException("Este modelo requiere una API Key global que no está configurada. Usa la IA Automática o introduce tu propia clave en el Perfil.");
            }
            
            endpoint = (api.getEndpointUrl() != null && !api.getEndpointUrl().isBlank())
                    ? api.getEndpointUrl() : DEEPSEEK_ENDPOINT;
            modelo   = usuario.getIaModeloSeleccionado().getNombreModelo();

        } else if (appDeepseekKey != null && !appDeepseekKey.isBlank()) {
            // 3. Clave DeepSeek de la aplicación (predeterminado si está en application.properties)
            apiKey   = appDeepseekKey;
            endpoint = DEEPSEEK_ENDPOINT;
            modelo   = DEEPSEEK_MODEL;

        } else if (appOpenRouterKey != null && !appOpenRouterKey.isBlank()) {
            // 4. Clave OpenRouter global de la aplicación
            apiKey   = appOpenRouterKey;
            endpoint = "https://openrouter.ai/api/v1/chat/completions";
            modelo   = "openrouter/free";

        } else {
            // 5. Fallback 100% gratuito (Pollinations.ai) - ¡Funciona sin API Key!
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
        receta.setInstrucciones(dto.getInstrucciones());
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

    @Transactional(readOnly = true)
    public Map<String, String> generarInstrucciones(String recetaNombre, String ingredientes, UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        String apiKey, endpoint, modelo;
        if (usuario.getIaCustomApiKey() != null && !usuario.getIaCustomApiKey().isBlank()) {
            apiKey   = usuario.getIaCustomApiKey();
            endpoint = (usuario.getIaCustomEndpoint() != null && !usuario.getIaCustomEndpoint().isBlank())
                    ? usuario.getIaCustomEndpoint() : DEEPSEEK_ENDPOINT;
            modelo   = (usuario.getIaCustomModelo() != null && !usuario.getIaCustomModelo().isBlank())
                    ? usuario.getIaCustomModelo() : DEEPSEEK_MODEL;
        } else if (usuario.getIaModeloSeleccionado() != null
                && usuario.getIaModeloSeleccionado().getApi() != null
                && usuario.getIaModeloSeleccionado().getApi().getApiKey() != null) {
            Api api  = usuario.getIaModeloSeleccionado().getApi();
            apiKey   = api.getApiKey();
            
            if ("MISSING_KEY".equals(apiKey)) {
                throw new OperacionInvalidaException("Este modelo requiere una API Key global que no está configurada. Usa la IA Automática o introduce tu propia clave en el Perfil.");
            }
            
            endpoint = (api.getEndpointUrl() != null && !api.getEndpointUrl().isBlank())
                    ? api.getEndpointUrl() : DEEPSEEK_ENDPOINT;
            modelo   = usuario.getIaModeloSeleccionado().getNombreModelo();
        } else if (appDeepseekKey != null && !appDeepseekKey.isBlank()) {
            apiKey   = appDeepseekKey;
            endpoint = DEEPSEEK_ENDPOINT;
            modelo   = DEEPSEEK_MODEL;
        } else if (appOpenRouterKey != null && !appOpenRouterKey.isBlank()) {
            apiKey   = appOpenRouterKey;
            endpoint = "https://openrouter.ai/api/v1/chat/completions";
            modelo   = "openrouter/free";
        } else {
            apiKey   = "free-key";
            endpoint = "https://text.pollinations.ai/openai";
            modelo   = "openai";
        }

        String prompt = "Receta: \"" + recetaNombre + "\". Ingredientes: " + ingredientes +
                ". Escribe los pasos de preparacion numerados. Solo JSON: {\"instrucciones\":\"1. Paso...\\n2. Paso...\"}";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("model", modelo);
            requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt)));
            requestBody.put("response_format", Map.of("type", "json_object"));
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 512);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> rawResponse = restTemplate.postForEntity(endpoint, entity, String.class);
            String rawBody = rawResponse.getBody();
            if (rawBody == null || rawBody.isBlank()) {
                throw new OperacionInvalidaException("La IA no devolvio respuesta.");
            }

            JsonNode apiResponse = objectMapper.readTree(rawBody);
            String content = apiResponse.path("choices").get(0).path("message").path("content").asText("");
            content = content.trim();
            if (content.startsWith("```")) {
                content = content.replaceFirst("```(?:json)?\\s*", "").replaceAll("\\s*```\\s*$", "").trim();
            }

            JsonNode root = objectMapper.readTree(content);
            String instrucciones = root.path("instrucciones").asText("");
            return Map.of("instrucciones", instrucciones);

        } catch (OperacionInvalidaException e) {
            throw e;
        } catch (org.springframework.web.client.RestClientResponseException e) {
            if (e.getStatusCode().value() == 429) {
                throw new OperacionInvalidaException("El modelo seleccionado está temporalmente saturado (Error 429). Por favor, intenta de nuevo en unos segundos o ve a tu perfil y selecciona otro modelo.");
            } else if (e.getStatusCode().value() == 404) {
                throw new OperacionInvalidaException("El modelo seleccionado no está disponible (Error 404). Por favor, ve a tu perfil y selecciona un modelo diferente.");
            } else if (e.getStatusCode().value() == 402) {
                throw new OperacionInvalidaException("El proveedor de IA requiere pago o se ha quedado sin saldo (Error 402). Por favor, usa un modelo gratuito (como el Auto-router de OpenRouter) en tu perfil.");
            } else if (e.getStatusCode().value() == 502 || e.getStatusCode().value() == 503 || e.getStatusCode().value() == 504) {
                throw new OperacionInvalidaException("El proveedor de IA está inactivo (" + e.getStatusCode().value() + "). Por favor, selecciona otro modelo.");
            } else {
                throw new OperacionInvalidaException("El proveedor de IA devolvió un error (" + e.getStatusCode().value() + "). Intenta con otro modelo.");
            }
        } catch (Exception e) {
            throw new OperacionInvalidaException("Error al generar instrucciones: " + e.getMessage());
        }
    }

    private String buildPrompt(String ingredientes, String preferencias) {
        StringBuilder sb = new StringBuilder();
        sb.append("Eres un chef. Ingredientes disponibles: ").append(ingredientes).append(".");
        if (!preferencias.isBlank()) {
            sb.append(" Preferencias: ").append(preferencias).append(".");
        }
        sb.append(" Genera 3 recetas COMPLETAS y DETALLADAS en JSON con esta estructura exacta, sin texto fuera del JSON:\n");
        sb.append("{\"recetas\":[");
        sb.append("{\"nombre\":\"Nombre de la receta\", \"descripcion\":\"Descripción atractiva de 2 líneas\", \"tiempoPreparacion\":30, \"dificultad\":\"MEDIA\", \"calorias\":400, \"alergenos\":\"Ninguno\", \"categoria\":\"Principal\", \"categoriaEmoji\":\"🍳\", \"categoriaColor\":\"#FFA500\", \"ingredientes\":\"1. Ingrediente con cantidad exacta. 2. Otro ingrediente...\", \"instrucciones\":\"1. Paso inicial detallado. 2. Siguiente paso...\"}");
        sb.append("]}");
        sb.append("\nIMPORTANTE: Los campos 'ingredientes' e 'instrucciones' no deben ser un resumen. Incluye TODOS los ingredientes con sus cantidades exactas y TODOS los pasos detallados para cocinar el plato.");
        sb.append("Reglas: dificultad=BAJA|MEDIA|ALTA, tiempo en minutos, color hex. Descripcion maxima 1 frase corta.");
        return sb.toString();
    }

    private List<RecetaSugerenciaDTO> callAiApi(String endpoint, String apiKey, String modelo, String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("model", modelo);
        requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt)));
        requestBody.put("temperature", 0.8);
        requestBody.put("max_tokens", 4096);

        // response_format solo para proveedores que lo soportan (OpenAI, DeepSeek, OpenRouter)
        boolean soportaJsonMode = !endpoint.contains("pollinations");
        if (soportaJsonMode) {
            requestBody.put("response_format", Map.of("type", "json_object"));
        }

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> rawResponse = restTemplate.postForEntity(endpoint, entity, String.class);
            String rawBody = rawResponse.getBody();
            if (rawBody == null || rawBody.isBlank()) {
                throw new OperacionInvalidaException("La IA no devolvió respuesta.");
            }

            JsonNode apiResponse = objectMapper.readTree(rawBody);
            String content = apiResponse
                    .path("choices").get(0)
                    .path("message")
                    .path("content").asText("");

            // Limpiar code fences: ```json ... ``` o ``` ... ```
            content = content.trim();
            if (content.startsWith("```")) {
                content = content.replaceFirst("```(?:json)?\\s*", "").replaceAll("\\s*```\\s*$", "").trim();
            }

            // Extraer el primer bloque JSON válido si hay texto antes/después
            int jsonStart = content.indexOf('{');
            int jsonEnd   = content.lastIndexOf('}');
            if (jsonStart >= 0 && jsonEnd > jsonStart) {
                content = content.substring(jsonStart, jsonEnd + 1);
            }

            JsonNode root = objectMapper.readTree(content);

            // Soportar tanto {"recetas":[...]} como {"suggestions":[...]} o array directo [...]
            JsonNode recetasNode = null;
            if (root.isArray()) {
                recetasNode = root;
            } else {
                for (String key : new String[]{"recetas", "recipes", "suggestions", "items"}) {
                    if (root.has(key) && root.get(key).isArray()) {
                        recetasNode = root.get(key);
                        break;
                    }
                }
            }

            if (recetasNode == null || !recetasNode.isArray() || recetasNode.isEmpty()) {
                throw new OperacionInvalidaException("La IA no devolvió recetas en el formato esperado. Inténtalo de nuevo.");
            }

            List<RecetaSugerenciaDTO> result = new ArrayList<>();
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
            return result;

        } catch (OperacionInvalidaException e) {
            throw e;
        } catch (org.springframework.web.client.RestClientResponseException e) {
            if (e.getStatusCode().value() == 429) {
                throw new OperacionInvalidaException("El modelo está saturado (429). Espera unos segundos e inténtalo de nuevo.");
            } else if (e.getStatusCode().value() == 404) {
                throw new OperacionInvalidaException("El modelo no está disponible (404). Ve a tu perfil y selecciona otro modelo.");
            } else if (e.getStatusCode().value() == 402) {
                throw new OperacionInvalidaException("El proveedor requiere pago o se ha quedado sin saldo (402). Usa un modelo gratuito en tu perfil.");
            } else if (e.getStatusCode().value() == 502 || e.getStatusCode().value() == 503 || e.getStatusCode().value() == 504) {
                throw new OperacionInvalidaException("El proveedor de IA está caído (" + e.getStatusCode().value() + "). Selecciona otro modelo.");
            } else {
                throw new OperacionInvalidaException("Error del proveedor de IA (" + e.getStatusCode().value() + "). Intenta con otro modelo.");
            }
        } catch (Exception e) {
            throw new OperacionInvalidaException("Error al comunicarse con la IA: " + e.getMessage());
        }
    }
}
