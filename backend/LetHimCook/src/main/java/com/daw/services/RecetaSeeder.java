package com.daw.services;

import com.daw.entities.Dificultad;
import com.daw.entities.Receta;
import com.daw.entities.TipoComida;
import com.daw.entities.Usuario;
import com.daw.repositories.RecetaRepository;
import com.daw.repositories.TipoComidaRepository;
import com.daw.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Puebla la tabla receta con un conjunto de recetas de ejemplo variadas
 * al arrancar la aplicación. Solo se ejecuta si hay menos de 20 recetas.
 */
@Component
@Order(2)
@RequiredArgsConstructor
public class RecetaSeeder implements CommandLineRunner {

    private final RecetaRepository recetaRepository;
    private final TipoComidaRepository tipoComidaRepository;
    private final UsuarioRepository usuarioRepository;

    /*
     * {nombre, descripcion, ingredientes, tiempoMin, dificultad, calorias,
     *  cat1, cat2, cat3}
     * cat2 y cat3 pueden ser null
     */
    private static final List<Object[]> RECETAS = List.of(
        new Object[]{ "Carbonara Clásica",         "La pasta italiana más cremosa",                                "espaguetis,panceta,huevo,parmesano,pimienta negra",                                      25,  Dificultad.MEDIA,   650, "Italiana",    "Pastas",            null },
        new Object[]{ "Pizza Margherita",            "Base crujiente, tomate y mozzarella fresca",                  "harina,levadura,tomate triturado,mozzarella,albahaca,aceite",                            50,  Dificultad.MEDIA,   720, "Italiana",    "Horneado",          null },
        new Object[]{ "Risotto de Gambas",           "Arroz cremoso con gambas al ajillo",                          "arroz arbóreo,gambas,caldo de marisco,cebolla,vino blanco,parmesano",                   40,  Dificultad.ALTA, 580, "Italiana",    "Arroces",           "Mariscos" },
        new Object[]{ "Tacos de Pollo",              "Tacos jugosos con pico de gallo casero",                      "tortillas de maíz,pechuga,cebolla,tomate,cilantro,lima,jalapeño",                       30,  Dificultad.BAJA,   490, "Mexicana",    "Aves",              "Rápido" },
        new Object[]{ "Guacamole Fresco",            "Aguacate, lima y un punto de picante",                        "aguacate,lime,tomate,cilantro,sal,cebolla morada,jalapeño",                              10,  Dificultad.BAJA,   210, "Mexicana",    "Vegana",            "Rápido" },
        new Object[]{ "Enchiladas Verdes",           "Pollo desmenuzado en salsa tomatillo",                        "tortillas,pollo cocido,tomatillo,chile serrano,crema,queso fresco",                     45,  Dificultad.MEDIA,   620, "Mexicana",    "Aves",              null },
        new Object[]{ "Sushi Variado",               "Nigiris y makis con salmón, atún y pepino",                   "arroz sushi,nori,salmón,atún,pepino,wasabi,salsa de soja",                              90,  Dificultad.ALTA, 410, "Japonesa",    "Pescado",           "Saludable" },
        new Object[]{ "Ramen de Miso",               "Caldo profundo con huevo marinado y chashu",                  "fideos ramen,cerdo chashu,huevo,nori,cebollino,pasta de miso,caldo dashi",              120,  Dificultad.ALTA, 710, "Japonesa",    "Sopas",             null },
        new Object[]{ "Edamame con Sal",             "Aperitivo clásico japonés",                                   "edamame congelado,sal gruesa",                                                            8,  Dificultad.BAJA,   120, "Japonesa",    "Aperitivos",        "Vegana" },
        new Object[]{ "Paella Valenciana",           "La reina de los arroces españoles",                           "arroz bomba,pollo,conejo,judía verde,garrafón,tomate,pimentón,azafrán",                 60,  Dificultad.ALTA, 680, "Española",    "Arroces",           "Aves" },
        new Object[]{ "Tortilla de Patatas",         "La tortilla perfecta, jugosa por dentro",                     "patata,huevo,cebolla,aceite de oliva,sal",                                               35,  Dificultad.MEDIA,   420, "Española",    "Vegetariana",       null },
        new Object[]{ "Gazpacho Andaluz",            "Sopa fría de verano con todas las verduras",                  "tomate,pepino,pimiento rojo,ajo,pan del día anterior,vinagre,aceite",                   15,  Dificultad.BAJA,   130, "Española",    "Vegana",            "Bajo en calorías" },
        new Object[]{ "Pollo al Curry",              "Curry suave con leche de coco y especias aromáticas",         "pechuga,cebolla,ajo,jengibre,curry en polvo,leche de coco,tomate",                     35,  Dificultad.MEDIA,   540, "India",       "Aves",              null },
        new Object[]{ "Dal de Lentejas",             "Guiso indio de lentejas con especias",                        "lentejas rojas,cebolla,ajo,jengibre,comino,cúrcuma,tomate,cilantro",                   30,  Dificultad.BAJA,   320, "India",       "Legumbres",         "Vegana" },
        new Object[]{ "Naan de Ajo",                 "Pan plano indio con mantequilla y ajo",                       "harina,yogur,levadura,ajo,mantequilla,sal,cilantro",                                    45,  Dificultad.MEDIA,   290, "India",       "Panadería",         "Horneado" },
        new Object[]{ "Pad Thai",                    "Fideos salteados con gambas y cacahuetes",                    "fideos de arroz,gambas,huevo,brotes de soja,cebollino,cacahuetes,salsa de tamarindo",  25,  Dificultad.MEDIA,   560, "Tailandesa",  "Mariscos",          null },
        new Object[]{ "Tom Kha Gai",                 "Sopa de pollo con leche de coco y lemongrass",                "pollo,leche de coco,lemongrass,galangal,kaffir lime,champiñones,chile",                 30,  Dificultad.MEDIA,   380, "Tailandesa",  "Sopas",             "Aves" },
        new Object[]{ "Croissants Caseros",          "Hojaldrado y mantecoso, de panadería francesa",               "harina,mantequilla,leche,levadura,azúcar,sal",                                         180, Dificultad.ALTA, 450, "Francesa",    "Panadería",         "Horneado" },
        new Object[]{ "Crema Brûlée",                "Crema vainilla con costra de caramelo crujiente",             "nata,yema de huevo,azúcar,vainilla",                                                    50,  Dificultad.MEDIA,   380, "Francesa",    "Postres",           null },
        new Object[]{ "Hamburguesa Clásica",         "Burger jugosa con doble queso cheddar",                       "carne picada,queso cheddar,lechuga,tomate,pepinillo,cebolla,pan brioche,ketchup",       20,  Dificultad.BAJA,   820, "Americana",   "Fast Food",         "Carnes" },
        new Object[]{ "Mac & Cheese",                "La reconfortante pasta americana con salsa de queso",         "macarrones,queso cheddar,leche,mantequilla,harina,sal,pimienta",                        30,  Dificultad.BAJA,   750, "Americana",   "Pastas",            "Horneado" },
        new Object[]{ "Tzatziki con Pita",           "Yogur griego con pepino y ajo fresco",                        "yogur griego,pepino,ajo,eneldo,aceite de oliva,limón,pan de pita",                     15,  Dificultad.BAJA,   220, "Griega",      "Aperitivos",        "Vegetariana" },
        new Object[]{ "Moussaka",                    "Lasaña griega de berenjena con carne y bechamel",             "berenjenas,carne picada de cordero,tomate,cebolla,bechamel,canela,queso",              80,  Dificultad.ALTA, 680, "Griega",      "Carnes",            "Horneado" },
        new Object[]{ "Bowl de Açaí",                "Bowl energético con frutas y granola",                        "pulpa de açaí,plátano,fresas,granola,miel,coco rallado",                                10,  Dificultad.BAJA,   310, "Americana",   "Desayunos",         "Saludable" },
        new Object[]{ "Shakshuka",                   "Huevos pochados en salsa de tomate especiada",                "huevos,tomate,pimiento,cebolla,ajo,comino,pimentón,cilantro",                           25,  Dificultad.BAJA,   290, "Árabe",       "Desayunos",         "Vegetariana" },
        new Object[]{ "Falafel con Tahini",          "Croquetas de garbanzo con salsa de sésamo",                   "garbanzos secos,perejil,cilantro,ajo,comino,harina,tahini,limón",                       30,  Dificultad.MEDIA,   380, "Árabe",       "Legumbres",         "Vegana" },
        new Object[]{ "Bibimbap",                    "Arroz coreano con verduras, huevo y gochujang",               "arroz,zanahoria,espinacas,brotes de soja,champiñones,huevo,gochujang,aceite de sésamo", 40, Dificultad.MEDIA,   490, "Coreana",     "Arroces",           "Saludable" },
        new Object[]{ "Brownie de Chocolate",        "Denso, húmedo y chocolatoso por dentro",                      "chocolate negro,mantequilla,azúcar,huevo,harina,cacao,nueces",                          45,  Dificultad.BAJA,   480, "Postres",     "Horneado",          "Americana" },
        new Object[]{ "Tarta de Manzana",            "Clásica tarta de manzana con masa casera",                    "manzana,harina,mantequilla,azúcar,canela,limón,huevo",                                  75,  Dificultad.MEDIA,   420, "Postres",     "Horneado",          "Francesa" },
        new Object[]{ "Ensalada César",              "Lechuga romana, crutones y aderezo César clásico",            "lechuga romana,parmesano,pan,anchoa,ajo,limón,mayonesa,mostaza",                        15,  Dificultad.BAJA,   340, "Ensaladas",   "Americana",         "Rápido" },
        new Object[]{ "Smoothie Verde",              "Batido energizante con espinacas y mango",                    "espinacas,mango,plátano,leche de coco,jengibre,limón",                                  5,   Dificultad.BAJA,   180, "Bebidas",     "Saludable",         "Vegana" },
        new Object[]{ "Sopa de Tomate",              "Cremosa y reconfortante con albahaca fresca",                 "tomate rama,cebolla,ajo,nata,caldo vegetal,albahaca,aceite",                            35,  Dificultad.BAJA,   210, "Sopas",       "Vegetariana",       "Bajo en calorías" },
        new Object[]{ "Wok de Ternera",              "Ternera salteada con verduras al wok estilo chino",           "solomillo de ternera,pimiento,zanahoria,brócoli,salsa de ostras,soja,ajo,jengibre",    20,  Dificultad.MEDIA,   460, "China",       "Carnes",            "Rápido" },
        new Object[]{ "Pho Bo",                      "Sopa vietnamita de ternera con fideos de arroz",              "fideos de arroz,caldo de ternera,anís estrellado,canela,jengibre,hierba limón",        180, Dificultad.ALTA, 390, "Vietnamita",  "Sopas",             "Ternera" },
        new Object[]{ "Ceviche Peruano",             "Pescado marinado en leche de tigre con ají",                  "corvina,lima,ají amarillo,cebolla morada,cilantro,choclo,cancha",                       20,  Dificultad.MEDIA,   220, "Peruana",     "Pescado",           "Bajo en calorías" }
    );

    @Override
    @Transactional
    public void run(String... args) {
        if (recetaRepository.count() > 15) return;

        Usuario creador = usuarioRepository.findAll().stream().findFirst().orElse(null);
        if (creador == null) return;

        Map<String, TipoComida> cats = new HashMap<>();
        tipoComidaRepository.findAll().forEach(c -> cats.put(c.getNombre(), c));

        for (Object[] d : RECETAS) {
            Receta r = new Receta();
            r.setNombre((String)  d[0]);
            r.setDescripcion((String)  d[1]);
            r.setIngredientes((String)  d[2]);
            r.setTiempoPreparacion((Integer) d[3]);
            r.setDificultad((Dificultad) d[4]);
            r.setCalorias((Integer) d[5]);
            r.setTipoComida(cats.get(d[6]));
            if (d[7] != null) r.setTipoComida2(cats.get((String) d[7]));
            if (d[8] != null) r.setTipoComida3(cats.get((String) d[8]));
            r.setUsuario(creador);
            r.setEsPublica(true);
            recetaRepository.save(r);
        }
    }
}
