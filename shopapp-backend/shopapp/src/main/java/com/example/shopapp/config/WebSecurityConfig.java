package com.example.shopapp.config;

import com.example.shopapp.components.JwtAuthenticationFilter;
import com.example.shopapp.models.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    @Value("${api.prefix}")
    private String apiPrefix;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(requests -> {
                    requests
                            // USER
                            .requestMatchers(
                                    String.format("%s/users/register", apiPrefix),
                                    String.format("%s/users/login", apiPrefix)
                            )
                            .permitAll()
                            // ROLES
                            .requestMatchers(
                                    HttpMethod.GET,
                                    String.format("%s/roles/**", apiPrefix)).permitAll()

                            // CATEGORY
                            .requestMatchers(
                                    HttpMethod.POST,
                                    String.format("%s/categories/**", apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(
                                    HttpMethod.GET,
                                    String.format("%s/categories/**", apiPrefix)).permitAll()
                            .requestMatchers(
                                    HttpMethod.PUT,
                                    String.format("%s/categories/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(
                                    HttpMethod.DELETE,
                                    String.format("%s/categories/**", apiPrefix)).hasRole(Role.ADMIN)
                            // PRODUCT
                            .requestMatchers(
                                    HttpMethod.POST,
                                    String.format("%s/products/**", apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(
                                    HttpMethod.GET,
                                    String.format("%s/products/**", apiPrefix)).permitAll()
                            .requestMatchers(
                                    HttpMethod.GET,
                                    String.format("%s/products/images/**", apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(
                                    HttpMethod.PUT,
                                    String.format("%s/products/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(
                                    HttpMethod.DELETE,
                                    String.format("%s/products/**", apiPrefix)).hasRole(Role.ADMIN)
                            // ORDER
                            .requestMatchers(
                                    HttpMethod.POST,
                                    String.format("%s/orders/**", apiPrefix)).hasAnyRole(Role.USER)
                            .requestMatchers(
                                    HttpMethod.GET,
                                    String.format("%s/orders/**", apiPrefix)).permitAll()
                            .requestMatchers(
                                    HttpMethod.PUT,
                                    String.format("%s/orders/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(
                                    HttpMethod.DELETE,
                                    String.format("%s/orders/**", apiPrefix)).hasRole(Role.ADMIN)
                            // ORDER DETAIL
                            .requestMatchers(
                                    HttpMethod.POST,
                                    String.format("%s/order_details/**", apiPrefix)).hasAnyRole(Role.USER)
                            .requestMatchers(
                                    HttpMethod.GET,
                                    String.format("%s/order_details/**", apiPrefix)).hasAnyRole(Role.USER, Role.ADMIN)
                            .requestMatchers(
                                    HttpMethod.PUT,
                                    String.format("%s/order_details/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(
                                    HttpMethod.DELETE,
                                    String.format("%s/order_details/**", apiPrefix)).hasRole(Role.ADMIN)

                            .anyRequest().authenticated();
                });
        // Cors configuration
        http.cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Access-Control-Allow-Origin",
                "x-auth-token"
        ));
        configuration.setExposedHeaders(List.of("x-auth-token"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
