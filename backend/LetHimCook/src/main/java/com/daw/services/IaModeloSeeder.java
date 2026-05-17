package com.daw.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import com.daw.entities.Api;
import com.daw.entities.IaModelo;
import com.daw.repositories.ApiRepository;
import com.daw.repositories.IaModeloRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Seeder para inicializar las APIs y Modelos de IA predeterminados en la base de datos.
 */
@Service
@RequiredArgsConstructor
public class IaModeloSeeder implements CommandLineRunner {

    private final ApiRepository apiRepository;
    private final IaModeloRepository iaModeloRepository;
    private final JdbcTemplate jdbcTemplate;

    @Value("${deepseek.api.key:}")
    private String appDeepseekKey;

    @Value("${openrouter.api.key:}")
    private String appOpenRouterKey;

    @Override
    public void run(String... args) throws Exception {
        
        // 1. DeepSeek API
        Api deepseekApi = apiRepository.findByEndpointUrl("https://api.deepseek.com/v1/chat/completions").orElse(null);
        if (deepseekApi == null) {
            deepseekApi = new Api();
            deepseekApi.setNombreServicio("DeepSeek (Oficial)");
            deepseekApi.setEndpointUrl("https://api.deepseek.com/v1/chat/completions");
            deepseekApi.setApiKey(appDeepseekKey != null && !appDeepseekKey.isBlank() ? appDeepseekKey : "MISSING_KEY");
            deepseekApi = apiRepository.save(deepseekApi);

            IaModelo dsModelo = new IaModelo();
            dsModelo.setNombreModelo("deepseek-chat");
            dsModelo.setApi(deepseekApi);
            iaModeloRepository.save(dsModelo);
        } else if ("MISSING_KEY".equals(deepseekApi.getApiKey()) && appDeepseekKey != null && !appDeepseekKey.isBlank()) {
            deepseekApi.setApiKey(appDeepseekKey);
            apiRepository.save(deepseekApi);
        }

        // 2. OpenRouter API
        Api openrouterApi = apiRepository.findByEndpointUrl("https://openrouter.ai/api/v1/chat/completions").orElse(null);
        if (openrouterApi == null) {
            openrouterApi = new Api();
            openrouterApi.setNombreServicio("OpenRouter");
            openrouterApi.setEndpointUrl("https://openrouter.ai/api/v1/chat/completions");
            openrouterApi.setApiKey(appOpenRouterKey != null && !appOpenRouterKey.isBlank() ? appOpenRouterKey : "MISSING_KEY");
            openrouterApi = apiRepository.save(openrouterApi);

            // Modelos gratuitos vigentes en OpenRouter (Mayo 2026)
            String[][] modelos = {
                { "openrouter/free" },
                { "qwen/qwen3-coder:free" },
                { "openai/gpt-oss-120b:free" },
                { "nvidia/nemotron-3-super-120b-a12b:free" }
            };
            for (String[] m : modelos) {
                IaModelo mod = new IaModelo();
                mod.setNombreModelo(m[0]);
                mod.setApi(openrouterApi);
                iaModeloRepository.save(mod);
            }
        } else {
            // Actualizar API key si era MISSING_KEY
            if ("MISSING_KEY".equals(openrouterApi.getApiKey()) && appOpenRouterKey != null && !appOpenRouterKey.isBlank()) {
                openrouterApi.setApiKey(appOpenRouterKey);
                apiRepository.save(openrouterApi);
            }
            // Asegurarnos de que los nuevos modelos estén creados
            String[][] nuevosModelos = {
                { "openrouter/free" }
            };
            for (String[] m : nuevosModelos) {
                if (iaModeloRepository.findByNombreModelo(m[0]).isEmpty()) {
                    IaModelo mod = new IaModelo();
                    mod.setNombreModelo(m[0]);
                    mod.setApi(openrouterApi);
                    iaModeloRepository.save(mod);
                }
            }
            
            // Migrar modelos obsoletos a los nuevos disponibles
            java.util.Map<String, String> migraciones = java.util.Map.of(
                "deepseek/deepseek-chat:free", "openrouter/free",
                "deepseek/deepseek-chat-v3-0324:free", "openrouter/free",
                "deepseek/deepseek-v4-flash:free", "openrouter/free",
                "meta-llama/llama-3-8b-instruct:free", "nvidia/nemotron-3-super-120b-a12b:free",
                "meta-llama/llama-3.3-70b-instruct:free", "nvidia/nemotron-3-super-120b-a12b:free",
                "mistralai/mistral-7b-instruct:free", "openai/gpt-oss-120b:free",
                "google/gemma-7b-it:free", "qwen/qwen3-coder:free",
                "google/gemma-3-1b-it:free", "qwen/qwen3-coder:free",
                "google/gemma-4-31b-it:free", "qwen/qwen3-coder:free"
            );
            migraciones.forEach((oldModel, newModel) -> {
                iaModeloRepository.findByNombreModelo(oldModel).ifPresent(m -> {
                    m.setNombreModelo(newModel);
                    iaModeloRepository.save(m);
                });
            });
        }

        // 3. Pollinations API (Free Fallback)
        Api pollinationsApi = apiRepository.findByEndpointUrl("https://text.pollinations.ai/openai").orElse(null);
        if (pollinationsApi == null) {
            pollinationsApi = new Api();
            pollinationsApi.setNombreServicio("Pollinations (Gratis)");
            pollinationsApi.setEndpointUrl("https://text.pollinations.ai/openai");
            pollinationsApi.setApiKey("free-key"); // No requiere key real
            pollinationsApi = apiRepository.save(pollinationsApi);

            IaModelo pollModelo = new IaModelo();
            pollModelo.setNombreModelo("openai");
            pollModelo.setApi(pollinationsApi);
            iaModeloRepository.save(pollModelo);
        } else if (iaModeloRepository.findByNombreModelo("openai").isEmpty()) {
            IaModelo pollModelo = new IaModelo();
            pollModelo.setNombreModelo("openai");
            pollModelo.setApi(pollinationsApi);
            iaModeloRepository.save(pollModelo);
        }

        // 4. Limpiar la base de datos de modelos no deseados para dejar solo el automático y pollinations
        jdbcTemplate.update("UPDATE usuario SET ia_modelo_seleccionado_id = NULL WHERE ia_modelo_seleccionado_id IN (SELECT id FROM ia_modelo WHERE nombre_modelo NOT IN ('openrouter/free', 'openai'))");
        jdbcTemplate.update("UPDATE receta SET ia_modelo_id = NULL WHERE ia_modelo_id IN (SELECT id FROM ia_modelo WHERE nombre_modelo NOT IN ('openrouter/free', 'openai'))");
        jdbcTemplate.update("DELETE FROM ia_modelo WHERE nombre_modelo NOT IN ('openrouter/free', 'openai')");

        System.out.println("✅ APIs y Modelos de IA sincronizados y limpiados correctamente.");
    }
}
