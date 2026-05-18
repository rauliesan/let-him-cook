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
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio que gestiona la generación de recetas mediante IA y su publicación.
 */
@Slf4j
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

    /** Clave DeepSeek de la aplicación, cargada desde application-secret.properties */
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
            modelo   = "openai"; // único modelo anónimo disponible en Pollinations
        }

        String ingredientesList = String.join(", ", dto.getIngredientes());
        String preferencias = dto.getPreferencias() != null ? dto.getPreferencias() : "";
        // Para Pollinations usamos prompt corto: deja más tokens para la respuesta
        boolean esPollinationsEndpoint = endpoint.contains("pollinations");
        String prompt = esPollinationsEndpoint
                ? buildPromptCorto(ingredientesList, preferencias)
                : buildPrompt(ingredientesList, preferencias);

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
            modelo   = "openai"; // único modelo anónimo disponible en Pollinations
        }

        String prompt = "Receta: \"" + recetaNombre + "\". Ingredientes: " + ingredientes +
                ". Escribe los pasos de preparacion numerados. Solo JSON: {\"instrucciones\":\"1. Paso...\\n2. Paso...\"}";

        boolean esPollinationsInstr = endpoint.contains("pollinations");

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("model", modelo);
            requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt)));
            if (!esPollinationsInstr) {
                requestBody.put("response_format", Map.of("type", "json_object"));
            }
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
            // Fallback: si content viene vacío (modelos reasoning) intentar el campo reasoning
            if (content.isBlank()) {
                content = apiResponse.path("choices").get(0).path("message").path("reasoning").asText("");
            }
            content = content.trim();
            if (content.startsWith("```")) {
                content = content.replaceFirst("```(?:json)?\\s*", "").replaceAll("\\s*```\\s*$", "").trim();
            }
            int jStart = content.indexOf('{');
            int jEnd   = content.lastIndexOf('}');
            if (jStart >= 0 && jEnd > jStart) content = content.substring(jStart, jEnd + 1);

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
        sb.append("Eres un chef experto. Ingredientes disponibles: ").append(ingredientes).append(".");
        if (!preferencias.isBlank()) {
            sb.append(" Preferencias del usuario: ").append(preferencias).append(".");
        }
        sb.append("\n\nDEBES responder ÚNICAMENTE con un objeto JSON válido. Sin texto antes ni después. Sin markdown. Sin explicaciones.");
        sb.append("\nEl JSON debe contener EXACTAMENTE 3 recetas diferentes en el array 'recetas'.");
        sb.append("\n\nEstructura obligatoria:\n");
        sb.append("{\"recetas\":[\n");
        sb.append("  {\"nombre\":\"Receta 1\",\"descripcion\":\"Una frase corta\",\"tiempoPreparacion\":20,\"dificultad\":\"BAJA\",\"calorias\":350,\"alergenos\":\"Ninguno\",\"categoria\":\"Ensalada\",\"categoriaEmoji\":\"🥗\",\"categoriaColor\":\"#4CAF50\",\"ingredientes\":\"1. 200g de ingrediente A. 2. 1 unidad de ingrediente B.\",\"instrucciones\":\"1. Primer paso. 2. Segundo paso. 3. Tercer paso.\"},\n");
        sb.append("  {\"nombre\":\"Receta 2\",\"descripcion\":\"Una frase corta\",\"tiempoPreparacion\":35,\"dificultad\":\"MEDIA\",\"calorias\":500,\"alergenos\":\"Gluten\",\"categoria\":\"Principal\",\"categoriaEmoji\":\"🍳\",\"categoriaColor\":\"#FF9800\",\"ingredientes\":\"1. 300g de ingrediente C. 2. 2 cucharadas de ingrediente D.\",\"instrucciones\":\"1. Primer paso. 2. Segundo paso. 3. Tercer paso.\"},\n");
        sb.append("  {\"nombre\":\"Receta 3\",\"descripcion\":\"Una frase corta\",\"tiempoPreparacion\":50,\"dificultad\":\"ALTA\",\"calorias\":650,\"alergenos\":\"Lactosa\",\"categoria\":\"Postre\",\"categoriaEmoji\":\"🍰\",\"categoriaColor\":\"#E91E63\",\"ingredientes\":\"1. 150g de ingrediente E. 2. 3 unidades de ingrediente F.\",\"instrucciones\":\"1. Primer paso. 2. Segundo paso. 3. Tercer paso.\"}\n");
        sb.append("]}\n");
        sb.append("\nReglas: dificultad solo puede ser BAJA, MEDIA o ALTA. tiempoPreparacion en minutos (número entero). colorHex válido. Incluye ingredientes con cantidades exactas e instrucciones detalladas paso a paso.");
        return sb.toString();
    }

    /**
     * Versión compacta del prompt para modelos con pocos tokens disponibles (Pollinations.ai).
     * Usa un solo ejemplo en línea para reducir tokens del prompt y dejar más espacio para la respuesta.
     */
    private String buildPromptCorto(String ingredientes, String preferencias) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a chef. Ingredients: ").append(ingredientes).append(".");
        if (!preferencias.isBlank()) sb.append(" Preferences: ").append(preferencias).append(".");
        sb.append("\nReply ONLY with a JSON object containing exactly 3 recipes. No text outside the JSON.");
        sb.append("\nFormat (repeat 3 times in the array):\n");
        sb.append("{\"recetas\":[");
        sb.append("{\"nombre\":\"Name\",\"descripcion\":\"Short description\",\"tiempoPreparacion\":20,\"dificultad\":\"BAJA\",\"calorias\":300,\"alergenos\":\"None\",\"categoria\":\"Category\",\"categoriaEmoji\":\"🍳\",\"categoriaColor\":\"#FF9800\",\"ingredientes\":\"1. 200g X. 2. 1 unit Y.\",\"instrucciones\":\"1. Step one. 2. Step two. 3. Step three.\"}");
        sb.append("]}");
        sb.append("\nRules: dificultad must be BAJA, MEDIA or ALTA. tiempoPreparacion is an integer (minutes). Return exactly 3 items.");
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

        log.info("[IA] Llamando a endpoint={} modelo={} jsonMode={}", endpoint, modelo, soportaJsonMode);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> rawResponse = restTemplate.postForEntity(endpoint, entity, String.class);
            String rawBody = rawResponse.getBody();
            log.info("[IA] Respuesta HTTP status={} body={}", rawResponse.getStatusCode(), rawBody);
            if (rawBody == null || rawBody.isBlank()) {
                throw new OperacionInvalidaException("La IA no devolvió respuesta.");
            }

            JsonNode apiResponse = objectMapper.readTree(rawBody);
            String content = apiResponse
                    .path("choices").get(0)
                    .path("message")
                    .path("content").asText("");
            // Fallback: modelos reasoning (gpt-oss-20b de Pollinations) escriben en reasoning cuando content viene vacío
            if (content.isBlank()) {
                content = apiResponse.path("choices").get(0).path("message").path("reasoning").asText("");
                if (!content.isBlank()) {
                    log.info("[IA] content vacío → usando campo reasoning como fallback ({} chars)", content.length());
                }
            }
            log.info("[IA] Content extraído: {}", content.length() > 500 ? content.substring(0, 500) + "..." : content);

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
