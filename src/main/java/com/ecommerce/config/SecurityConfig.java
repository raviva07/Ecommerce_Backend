package com.ecommerce.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.List;
import org.springframework.http.HttpMethod;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth

                // ✅ PUBLIC ENDPOINTS
                .requestMatchers(
                        "/api/auth/**",          // register/login
                        "/v3/api-docs/**",       // OpenAPI docs
                        "/swagger-ui/**",        // Swagger UI resources
                        "/swagger-ui.html",      // Swagger UI HTML
                        "/api-docs/**"           // legacy docs path
                ).permitAll()

                // ✅ PRODUCTS
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")

                // ✅ CART (CUSTOMER ONLY)
                .requestMatchers("/api/cart/**").hasRole("CUSTOMER")

                // ✅ ORDERS
                .requestMatchers(HttpMethod.POST, "/api/orders").hasRole("CUSTOMER")
                .requestMatchers("/api/orders/my").hasRole("CUSTOMER")
                .requestMatchers("/api/orders/all").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/orders/{id}").hasAnyRole("CUSTOMER","ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/orders/**").hasAnyRole("CUSTOMER","ADMIN")

                // ✅ PAYMENT
                .requestMatchers("/api/payment/history/me").hasRole("CUSTOMER")
                .requestMatchers("/api/payment/history").hasRole("ADMIN")
                .requestMatchers("/api/payment/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/payment/create-order").hasRole("CUSTOMER")
                .requestMatchers("/api/payment/verify").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.GET, "/api/payment/{orderId}").hasRole("CUSTOMER")

                // ✅ USERS
                .requestMatchers("/api/users/profile").authenticated()
                .requestMatchers("/api/users/all").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/users/**").hasRole("ADMIN")

                // 🔒 DEFAULT RULE
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173")); // frontend origin
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
