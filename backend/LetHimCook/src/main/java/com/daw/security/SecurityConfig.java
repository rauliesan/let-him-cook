package com.daw.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

/**
 * Configuración central de Spring Security para la aplicación.
 *
 * Aspectos clave:
 * <ul>
 * Sesiones stateless (API REST pura con JWT).
 * CORS habilitado para desarrollo frontend.
 * Rutas públicas: {@code /auth/**}, Swagger, OpenAPI docs.
 * Rutas de administrador: {@code /admin/**}.
 * Filtro JWT registrado antes de
 * {@code UsernamePasswordAuthenticationFilter}.
 * Manejo de errores 401/403 con handlers personalizados.
 *
 * @author IES Almudeyne - Let Him Cook
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Habilita @PreAuthorize, @Secured, etc.
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    // ========================
    // Security Filter Chain
    // ========================

    /**
     * Define la cadena de filtros de seguridad HTTP.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitar CSRF (no necesario en APIs stateless con JWT)
                .csrf(csrf -> csrf.disable())

                // Configuración CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Gestión de sesiones stateless
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Reglas de autorización de rutas
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas (autenticación, documentación)
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/error").permitAll()

                        // Rutas exclusivas de ADMIN
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Permitir OPTIONS para preflight CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Todas las demás rutas requieren autenticación
                        .anyRequest().authenticated())

                // Manejo de excepciones con respuestas JSON
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))

                // Registrar el proveedor de autenticación
                .authenticationProvider(authenticationProvider())

                // Registrar el filtro JWT antes del filtro de autenticación estándar
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ========================
    // CORS
    // ========================

    /**
     * Configuración CORS permisiva para desarrollo.
     * En producción se debe restringir {@code allowedOrigins} al dominio del
     * frontend.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // ========================
    // Authentication Beans
    // ========================

    /**
     * Proveedor de autenticación basado en DAO que usa:
     * <ul>
     * <li>{@link CustomUserDetailsService} para cargar usuarios.</li>
     * <li>{@link BCryptPasswordEncoder} para verificar contraseñas.</li>
     * </ul>
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Expone el {@link AuthenticationManager} como bean para inyección
     * en controllers de autenticación (login, registro, etc.).
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Encoder de contraseñas BCrypt.
     * Se usa tanto para codificar contraseñas en el registro como para verificarlas
     * en el login.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}