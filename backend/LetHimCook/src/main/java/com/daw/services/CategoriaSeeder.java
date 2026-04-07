package com.daw.services;

import com.daw.entities.TipoComida;
import com.daw.repositories.TipoComidaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Puebla la tabla tipo_comida con un catálogo fijo de categorías culinarias
 * al arrancar la aplicación. Solo inserta las que aún no existen por nombre.
 */
@Component
@Order(1)
@RequiredArgsConstructor
public class CategoriaSeeder implements CommandLineRunner {

    private final TipoComidaRepository repo;

    /* {nombre, emoji, colorHex, descripcion} */
    private static final List<String[]> CATALOGO = List.of(
        new String[]{ "Italiana",      "🍕", "#C13E28", "Pizza, pasta y risotto" },
        new String[]{ "Española",      "🥘", "#B87010", "Cocina tradicional española" },
        new String[]{ "Japonesa",      "🍱", "#2A5A2A", "Sushi, ramen y mucho más" },
        new String[]{ "Mexicana",      "🌮", "#C44020", "Tacos, enchiladas y guacamole" },
        new String[]{ "China",         "🥡", "#C03010", "Wok, dim sum y fideos" },
        new String[]{ "Francesa",      "🥐", "#C8A020", "Croissants, crepes y bistrot" },
        new String[]{ "India",         "🍛", "#D4700A", "Curries, naan y especias" },
        new String[]{ "Americana",     "🍔", "#963218", "Burgers, BBQ y brunch" },
        new String[]{ "Griega",        "🫒", "#4A7A28", "Tzatziki, souvlaki y moussaka" },
        new String[]{ "Tailandesa",    "🍜", "#D48010", "Pad thai, curry y lemongrass" },
        new String[]{ "Árabe",         "🧆", "#B88020", "Hummus, falafel y shawarma" },
        new String[]{ "Peruana",       "🍤", "#1A6A7A", "Ceviche, lomo saltado y pisco" },
        new String[]{ "Coreana",       "🥢", "#8A2020", "Kimchi, bibimbap y bulgogi" },
        new String[]{ "Vietnamita",    "🫕", "#2A6A3A", "Pho, banh mi y rollitos" },
        new String[]{ "Marroquí",      "🫓", "#B86818", "Tajín, cuscús y harira" },
        new String[]{ "Turca",         "🥙", "#9A3820", "Kebab, börek y baklava" },
        new String[]{ "Brasileña",     "🥩", "#2A7A3A", "Churrasco, feijoada y caipirinha" },
        new String[]{ "Alemana",       "🥨", "#B89020", "Salchichas, pretzel y strudel" },
        new String[]{ "Mediterránea",  "🫙", "#2A6A5A", "Aceite, pescado y verduras" },
        new String[]{ "Asiática",      "🍜", "#2A4A8A", "Fusión de sabores orientales" },
        new String[]{ "Postres",       "🍰", "#C85090", "Tartas, helados y dulces" },
        new String[]{ "Vegana",        "🥗", "#3A7A28", "100% de origen vegetal" },
        new String[]{ "Vegetariana",   "🥦", "#2A7A38", "Sin carne, llena de sabor" },
        new String[]{ "Mariscos",      "🦞", "#2A5A8A", "Pulpo, gambas y mejillones" },
        new String[]{ "Pescado",       "🐟", "#1A5A8A", "Salmón, merluza y atún" },
        new String[]{ "Carnes",        "🥩", "#7A2818", "Ternera, cordero y cerdo" },
        new String[]{ "Aves",          "🍗", "#B88820", "Pollo, pavo y pato" },
        new String[]{ "Cerdo",         "🥓", "#9A3828", "Panceta, costillas y embutidos" },
        new String[]{ "Sopas",         "🍲", "#9A5820", "Caldos, cremas y consomés" },
        new String[]{ "Ensaladas",     "🥗", "#3A7820", "Frescas, nutritivas y coloridas" },
        new String[]{ "Panadería",     "🍞", "#C89830", "Pan, bollería y masas" },
        new String[]{ "Pastas",        "🍝", "#B84810", "Espagueti, penne y lasaña" },
        new String[]{ "Arroces",       "🍚", "#6A8A2A", "Paella, risotto y arroz chino" },
        new String[]{ "Legumbres",     "🫘", "#7A6020", "Lentejas, garbanzos y alubias" },
        new String[]{ "Desayunos",     "🥞", "#D49020", "Tostadas, granola y smoothies" },
        new String[]{ "Aperitivos",    "🧀", "#C89020", "Tapas, quesos y embutidos" },
        new String[]{ "Bebidas",       "🧃", "#2A7A6A", "Zumos, batidos y cócteles" },
        new String[]{ "Saludable",       "🥗", "#2A7A50", "Ligero, nutritivo y equilibrado" },
        new String[]{ "Fast Food",       "🍔", "#C84820", "Rápido, sabroso y sin remordimientos" },
        new String[]{ "Ternera",         "🥩", "#6A2818", "Filete, burger y estofados" },
        /* Categorías de tipo / estilo */
        new String[]{ "Rápido",          "⚡", "#C84810", "Listo en 20 minutos o menos" },
        new String[]{ "Sin gluten",      "🌾", "#6A8A3A", "Apto para celíacos" },
        new String[]{ "Sin lactosa",     "🥛", "#7A8A30", "Sin derivados lácteos" },
        new String[]{ "Alto en proteína","💪", "#7A3020", "Para deportistas y gym" },
        new String[]{ "Bajo en calorías","🔥", "#3A7A6A", "Light y equilibrado" },
        new String[]{ "Para niños",      "👶", "#C8A020", "Aprobado por los más pequeños" },
        new String[]{ "Económico",       "💰", "#6A7A2A", "Rico y sin pasarse de presupuesto" },
        new String[]{ "Horneado",        "🫕", "#9A5810", "Del horno directo a la mesa" },
        new String[]{ "A la plancha",    "🥩", "#8A3010", "A la plancha o parrilla" },
        new String[]{ "Picante",         "🌶️","#C02010", "Con chispa y mucho sabor" },
        new String[]{ "Fusión",          "🌐", "#4A5A8A", "Mezcla de culturas y sabores" }
    );

    @Override
    public void run(String... args) {
        for (String[] datos : CATALOGO) {
            repo.findByNombre(datos[0]).ifPresentOrElse(
                existing -> {
                    /* Actualiza emoji y color aunque la categoría ya exista */
                    existing.setIconoUrl(datos[1]);
                    existing.setColorHex(datos[2]);
                    existing.setDescripcion(datos[3]);
                    repo.save(existing);
                },
                () -> {
                    TipoComida cat = new TipoComida();
                    cat.setNombre(datos[0]);
                    cat.setIconoUrl(datos[1]);
                    cat.setColorHex(datos[2]);
                    cat.setDescripcion(datos[3]);
                    repo.save(cat);
                }
            );
        }
    }
}
