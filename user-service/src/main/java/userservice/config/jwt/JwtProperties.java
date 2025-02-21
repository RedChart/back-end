package userservice.config.jwt;


public interface JwtProperties {
         long ACCESS_TOKEN_EXPIRATION_TIME = 6000000; // 10분 (1/1000초)-3
//    long ACCESS_TOKEN_EXPIRATION_TIME = 600000; // 10분 (1/1000초)-3
    long REFRESH_TOKEN_EXPIRATION_TIME = 864000000; // 10일 (1/1000초)
//    long REFRESH_TOKEN_EXPIRATION_TIME = 180000; // 5일 (1/1000초)
    String TOKEN_PREFIX = "Bearer ";
    String ACCESS_HEADER = "Authorization";
    String AUTHORITIES_KEY = "auth";
}