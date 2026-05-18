package com.daw.services;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.daw.entities.Logro;
import com.daw.repositories.LogroRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Puebla la tabla de logros al arrancar si aún está vacía.
 */
@Component
@Order(4)
@RequiredArgsConstructor
@Slf4j
public class LogroSeeder implements CommandLineRunner {

    private final LogroRepository logroRepository;

    @Override
    public void run(String... args) {
        if (logroRepository.existsByNombre("Primera receta")) {
            log.info("Los logros ya están poblados.");
            return;
        }

        log.info("Poblando logros iniciales...");

        List<Logro> logros = List.of(
            crear("Primera receta",        "🍳", "Completa tu primera receta siguiendo todos los pasos."),
            crear("Cocinero en prácticas", "🔥", "Completa 5 recetas distintas."),
            crear("Chef experimentado",    "👨‍🍳", "Completa 10 recetas distintas."),
            crear("Foodie",                "❤️", "Dale un like a tu primera receta."),
            crear("Escritor",              "📝", "Publica tu primer post en el foro."),
            crear("Primer comentario",     "💬", "Responde por primera vez en el foro."),
            crear("Primera tirada",        "🎰", "Consigue tu primer premio en la tragaperras."),
            crear("Coleccionista",         "🏆", "Consigue 5 premios en la tragaperras."),
            crear("Sociable",              "🤝", "Añade a tu primer amigo.")
        );

        logroRepository.saveAll(logros);
        log.info("{} logros creados.", logros.size());
    }

    private Logro crear(String nombre, String icono, String descripcion) {
        Logro l = new Logro();
        l.setNombre(nombre);
        l.setIconoUrl(icono);
        l.setDescripcion(descripcion);
        return l;
    }
}
