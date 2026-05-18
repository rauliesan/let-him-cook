package com.daw.services;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.daw.entities.Recompensa;
import com.daw.repositories.RecompensaRepository;
import com.daw.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Seeder para poblar la base de datos con 50 insignias de comida.
 */
@Component
@Order(3)
@RequiredArgsConstructor
@Slf4j
public class RecompensaSeeder implements CommandLineRunner {

    private final RecompensaRepository recompensaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) {
        usuarioRepository.findByNombre("prueba1").ifPresent(u -> {
            u.setPuntos(0);
            usuarioRepository.save(u);
            log.info("DEBUG: Monedas de prueba1 reseteadas a 0 en el Seeder");
        });

        if (recompensaRepository.existsByNombre("Dios Pastafarista")) {
            log.info("Las recompensas ya están pobladas.");
            return;
        }

        log.info("Poblando 50 insignias de comida...");

        List<Recompensa> recompensas = Arrays.asList(
                // Legendario
                crear("Dios Pastafarista", "La deidad máxima de la pasta. Ramen.", "🍝", "legendario", 0.001),

                // Épicos
                crear("Sushi Imperial", "El honor del emperador en un bocado.", "🍣", "epico", 0.002),
                crear("Ramen Sagrado", "Fideos bendecidos por los dioses.", "🍜", "epico", 0.002),
                crear("Pizza Suprema", "Con todos los ingredientes del universo.", "🍕", "epico", 0.002),
                crear("Hamburguesa de Oro", "Brilla tanto como sabe.", "🍔", "epico", 0.002),
                crear("Tarta de Bodas", "Un monumento al dulce.", "🎂", "epico", 0.002),

                // Raros
                crear("Tacos al Pastor", "Sabor auténtico de la calle.", "🌮", "raro", 0.005),
                crear("Paella Real", "Directamente de la Albufera.", "🥘", "raro", 0.005),
                crear("Curry de la India", "Especias que despiertan el alma.", "🍛", "raro", 0.005),
                crear("Dim Sum", "Pequeños tesoros al vapor.", "🥟", "raro", 0.005),
                crear("Croissant de París", "Mantequilla y elegancia.", "🥐", "raro", 0.005),
                crear("Langosta", "El lujo del océano.", "🦞", "raro", 0.005),
                crear("Bistec de Wagyu", "Carne que se derrite en la boca.", "🥩", "raro", 0.005),
                crear("Helado Galáctico", "Frío como el espacio, dulce como una estrella.", "🍦", "raro", 0.005),
                crear("Café de Especialidad", "Solo para paladares refinados.", "☕", "raro", 0.005),
                crear("Dona Glaseada", "El círculo perfecto de felicidad.", "🍩", "raro", 0.005),

                // Comunes
                crear("Manzana Roja", "Salud pura y crujiente.", "🍎", "comun", 0.02),
                crear("Plátano", "Energía instantánea.", "🍌", "comun", 0.02),
                crear("Huevo Frito", "El rey del desayuno.", "🥚", "comun", 0.02),
                crear("Pan Recién Hecho", "Huele a hogar.", "🍞", "comun", 0.02),
                crear("Queso Curado", "Un clásico indispensable.", "🧀", "comun", 0.02),
                crear("Patatas Fritas", "Nadie puede comer solo una.", "🍟", "comun", 0.02),
                crear("Ensalada Fresca", "Ligera y nutritiva.", "🥗", "comun", 0.02),
                crear("Sopa de Pollo", "Cura hasta el alma.", "🥣", "comun", 0.02),
                crear("Arroz Blanco", "La base de toda buena comida.", "🍚", "comun", 0.02),
                crear("Sandía", "Frescura veraniega.", "🍉", "comun", 0.02),
                crear("Zanahoria", "Buena para la vista.", "🥕", "comun", 0.02),
                crear("Brócoli", "Pequeños arbolitos de salud.", "🥦", "comun", 0.02),
                crear("Berenjena", "Versátil y deliciosa.", "🍆", "comun", 0.02),
                crear("Aguacate", "Oro verde.", "🥑", "comun", 0.02),
                crear("Cereza", "Dulces tentaciones.", "🍒", "comun", 0.02),
                crear("Uvas", "Un brindis por el sabor.", "🍇", "comun", 0.02),
                crear("Pera", "Dulce y jugosa.", "🍐", "comun", 0.02),
                crear("Kiwi", "Exótico y vitamínico.", "🥝", "comun", 0.02),
                crear("Maíz", "Dorado y dulce.", "🌽", "comun", 0.02),
                crear("Champiñón", "El sabor del bosque.", "🍄", "comun", 0.02),
                crear("Cacahuete", "El snack perfecto.", "🥜", "comun", 0.02),
                crear("Castaña", "Calor de invierno.", "🌰", "comun", 0.02),
                crear("Galleta", "Un capricho dulce.", "🍪", "comun", 0.02),
                crear("Caramelo", "Explosión de azúcar.", "🍬", "comun", 0.02),
                crear("Chocolate", "Pura pasión.", "🍫", "comun", 0.02),
                crear("Palomitas", "Noche de cine.", "🍿", "comun", 0.02),
                crear("Vino", "El compañero ideal.", "🍷", "comun", 0.02),
                crear("Cerveza", "Refrescante y social.", "🍺", "comun", 0.02),
                crear("Zumo de Naranja", "Vitamina C pura.", "🍹", "comun", 0.02),
                crear("Té Verde", "Paz y antioxidantes.", "🍵", "comun", 0.02),
                crear("Leche", "Fortaleza blanca.", "🥛", "comun", 0.02),
                crear("Miel", "Dulzura natural.", "🍯", "comun", 0.02),
                crear("Bacon", "Crujiente y ahumado.", "🥓", "comun", 0.02),
                crear("Pollo Asado", "Cena de domingo.", "🍗", "comun", 0.02));

        recompensaRepository.saveAll(recompensas);
        log.info("50 insignias creadas con éxito.");
    }

    private Recompensa crear(String nombre, String desc, String emoji, String rareza, Double prob) {
        Recompensa r = new Recompensa();
        r.setNombre(nombre);
        r.setDescripcion(desc);
        r.setEmoji(emoji);
        r.setRareza(rareza);
        r.setProbabilidad(prob);
        return r;
    }
}
