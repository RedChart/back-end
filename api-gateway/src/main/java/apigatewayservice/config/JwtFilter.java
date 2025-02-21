package apigatewayservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
class JwtFilter implements WebFilter {
    private static final String USER_SERVICE_URI = "lb://user-service";
    private static final String[] EXCLUDED_PATHS = {
            "api/users/kakao/**",
            "/api/users/emails/**",
            "/api/users/signin",
            "/api/users/signup",
            "/api/users/refresh-token",
            "/api/posts/swagger-ui/**",
            "/api/posts/v3/api-docs/**",
            "/api/users/swagger-ui/**",
            "/api/users/v3/api-docs/**",
            "/api/newsfeed/swagger-ui/**",
            "/api/newsfeed/v3/api-docs/**",
            "/",
            "/swagger-ui/**",
            "/v3/api-docs/**"};
    private final TokenService tokenService;
    private final ReactorLoadBalancerExchangeFilterFunction lbFunction;
    @Value("${jwt.secret}")
    private String secretKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String requestPath = exchange.getRequest().getURI().getPath();
        AntPathMatcher pathMatcher = new AntPathMatcher();

        for (String excludedPath : EXCLUDED_PATHS) {
            if (pathMatcher.match(excludedPath, requestPath)) {
                log.info("Path {} is excluded from JWT filtering", requestPath);
                return chain.filter(exchange);
            }
        }

        ServerHttpRequest request = exchange.getRequest();

        // 현재 사용자가 가지고있는 토큰
        String token = tokenService.getJwt(request);


        String userId = tokenService.validationToken(token);


        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .header("userId", userId)
                .build();
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                new User(userId, "", new ArrayList<>()), null, new ArrayList<>()
        );
        return chain.filter(exchange.mutate().request(modifiedRequest).build())
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
    }
}