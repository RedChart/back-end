package apigatewayservice.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import apigatewayservice.exception.CustomException;
import apigatewayservice.exception.ErrorCode;

import java.security.Key;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    @Value("${jwt.secret}")
    private String secretKey;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public String validationToken(String token) {
        return parseClaims(token).getSubject();
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.TOKEN_NOT_FOUND);
        }
    }

    public String getJwt(ServerHttpRequest request) {
        String jwt = Optional.ofNullable(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .orElseThrow(NullPointerException::new);

        if (!StringUtils.hasText(jwt) && !jwt.startsWith("Bearer ")) {
            throw new CustomException(ErrorCode.TOKEN_NOT_FOUND);
        }

        return jwt.substring(7).trim();
    }

//    public SimpleGrantedAuthority isValidToken(String token) {
//
//        try{
//        Claims claims = parseClaims(token);
//        //todo: exception 설정
//
//            String auth = claims.get("auth", String.class);
//
//            return new SimpleGrantedAuthority(auth);
//        } catch (Exception ignored){
//          //todo: exception Throw 해야함
//            System.out.println("ㅎㅎ");
//        }
//
//    }

}
