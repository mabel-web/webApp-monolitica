package com.usach.mytingeso.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${CORS_ORIGIN:http://localhost:5173}")
    private String corsOrigin;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws  Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/swagger-ui/**","/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/students/*/periods/*/enrollments")
                        .hasAnyRole("STUDENT", "ACADEMIC_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/students/*/enrollments/*")
                        .hasAnyRole("STUDENT", "ACADEMIC_ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/sections/*/enrollments/*/grade")
                        .hasRole("PROFESSOR")
                        .requestMatchers(HttpMethod.POST, "/api/**").hasRole("ACADEMIC_ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/**").hasRole("ACADEMIC_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/apí/**").hasRole("ACADEMIC_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/**").authenticated()
                        .anyRequest().denyAll())
                .oauth2ResourceServer(o -> o.jwt(j -> j.jwtAuthenticationConverter(converter())));
                return http.build();
    }

    //keycloak puts realm roles in realm access roles
    private Converter<Jwt, AbstractAuthenticationToken> converter(){
        JwtAuthenticationConverter c = new JwtAuthenticationConverter();
        c.setJwtGrantedAuthoritiesConverter(jwt -> {
            Object realm = jwt.getClaim("realm_access");
            if (realm instanceof Map<?, ?> m && m.get("roles") instanceof Collection<?> roles){
                return roles.stream()
                        .map(r -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + r))
                        .toList();
            }
            return List.of();
        });
        return c;
    }
    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of(corsOrigin));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return src;
    }

}
