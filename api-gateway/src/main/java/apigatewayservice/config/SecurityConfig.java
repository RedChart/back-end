package apigatewayservice.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;


@Slf4j
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    //    private final CorsConfig corsConfig;
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        return httpSecurity
                // REST API이므로 basic auth 및 csrf 보안을 사용하지 않음

                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(corsSpec -> corsSpec.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowCredentials(true);
                    config.setAllowedMethods(Collections.singletonList("*"));
                    config.setAllowedHeaders(Collections.singletonList("*"));
                    config.setAllowedOrigins(Collections.singletonList("*"));
                    config.setMaxAge(3600L);
                    return config;
                }))
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(authorize -> authorize
                        .pathMatchers("api/users/kakao/**").permitAll()
                        .pathMatchers("/api/users/emails/**").permitAll()
                        .pathMatchers("/api/users/signin", "/api/users/signup").permitAll()
                        .pathMatchers("/api/users/refresh-token").permitAll()
                        .pathMatchers("/api/posts/swagger-ui/**", "/api/posts/v3/api-docs/**", "/api/users/swagger-ui/**",
                                "/api/users/v3/api-docs/**","/api/newsfeed/swagger-ui/**", "/api/newsfeed/v3/api-docs/**").permitAll()
                        .pathMatchers("/", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        .anyExchange().authenticated()
                )
                .exceptionHandling(ex ->
                        ex.accessDeniedHandler(new JwtAccessDeniedHandler()))
                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();

    }
}


