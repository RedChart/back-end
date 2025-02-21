package userservice.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import userservice.domain.User;
import userservice.exception.CustomException;
import userservice.exception.ErrorCode;

import java.security.Key;
import java.util.Date;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretKey;
    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }


    public String createAccessToken(User user, HttpServletResponse response) {
        log.error("💡 Access Token 이 만료되었습니다.");
        Claims claims = Jwts.claims().setSubject(String.valueOf(user.getId()));
//        String authorities = authentication.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.joining(","));
        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME))
                .claim(JwtProperties.AUTHORITIES_KEY, user.getAuthority())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        response.addHeader(JwtProperties.ACCESS_HEADER, JwtProperties.TOKEN_PREFIX + accessToken);
        return accessToken;
    }

    public TokenDto generateToken(User user, HttpServletResponse response) {
        Claims claims = Jwts.claims().setSubject(user.getId().toString());
        // 권한들
//        String authorities = authentication.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.joining(","));
        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME))
                .claim(JwtProperties.AUTHORITIES_KEY, user.getAuthority())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JwtProperties.REFRESH_TOKEN_EXPIRATION_TIME))
                .claim(JwtProperties.AUTHORITIES_KEY, user.getAuthority())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
//        response.addHeader(JwtProperties.ACCESS_HEADER, JwtProperties.TOKEN_PREFIX + accessToken);
        return new TokenDto(accessToken, refreshToken);
    }

    public void validateRefreshToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    public String getJwtToken(String token) throws RuntimeException {
        if (!StringUtils.hasText(token) && !token.startsWith(JwtProperties.TOKEN_PREFIX)) {
            throw new CustomException(ErrorCode.TOKEN_NOT_FOUND, token);
        }
        return token.substring(7);
    }

    public String getIdByAccessToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        }
    }

}
