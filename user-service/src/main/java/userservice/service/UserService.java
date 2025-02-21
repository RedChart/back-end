package userservice.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.MessagingException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import userservice.config.email.EmailService;
import userservice.config.jwt.JwtUtil;
import userservice.config.jwt.TokenDto;
import userservice.config.oauth2.OAuthAttributes;
import userservice.config.RedisService;
import userservice.domain.Authority;
import userservice.domain.User;
import userservice.dto.user.*;
import userservice.exception.CustomException;
import userservice.exception.ErrorCode;
import userservice.feign.dto.ClientUserDto;
import userservice.repository.UserRepository;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Optional;
import java.util.Random;

import static userservice.config.jwt.JwtProperties.REFRESH_TOKEN_EXPIRATION_TIME;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private static final String AUTH_CODE_PREFIX = "AuthCode ";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RedisService redisService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final JwtUtil jwtUtil;
    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;

    public Long join(User user) {
        userRepository.save(user);
        return user.getId();
    }

    @Transactional
    public SignUpResponseDto signUp(SignUpDto signUpDto) {
        signUpDto.setAuthority(Authority.valueOf("ROLE_USER"));
        User user = signUpDto.toEntity();
        user.passwordEncoding(passwordEncoder);
        userRepository.save(user);

        // 뉴스피드 redis key 생성
        kafkaTemplate.send("user-event",user.getId().toString());

        return SignUpResponseDto.toDto(user);
    }

    // 소셜 로그인
    @Transactional
    public User signUp(OAuthAttributes oAuthAttributes) {
//        oAuthAttributes.setAuthority(Authority.valueOf("ROLE_USER"));
        User user = oAuthAttributes.toEntity();
        userRepository.save(user);
        return user;
    }


    public void sendCodeToEmail(String toEmail) throws NoSuchAlgorithmException, MessagingException, UnsupportedEncodingException, jakarta.mail.MessagingException {
        this.checkDuplicatedEmail(toEmail);
        String title = "[RedChart] 이메일 인증 번호";
        String authCode = this.createCode();
        emailService.sendEmail(toEmail, title, authCode);
        // 이메일 인증 요청 시 인증 번호 Redis에 저장 ( key = "AuthCode " + Email / value = AuthCode )
        redisService.setValues(AUTH_CODE_PREFIX + toEmail,
                authCode, Duration.ofMillis(this.authCodeExpirationMillis));
    }

    public void verifiedCode(String email, String authCode) {
        String redisAuthCode = redisService.getValues(AUTH_CODE_PREFIX + email);
        boolean authResult = redisService.checkExistsValue(redisAuthCode) && redisAuthCode.equals(authCode);
        if (!authResult) {
            log.debug("UserService.createCode() exception occur");
        }
    }

    private void checkDuplicatedEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATED_EMAIL, email);
        }
    }

    public void checkDuplicatedUsername(String username) {
        Optional<User> user = userRepository.findByusername(username);
        if (user.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATED_USER_NAME, username);
        }
    }

    private String createCode() {
        int lenth = 6;
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < lenth; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.CANNOT_CREATE_CODE, e);
        }
    }

    public UserDto updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (request.getUsername() != null) {
            Optional<User> checkUser = userRepository.findByusername(request.getUsername());
            if (checkUser.isPresent()) {
                throw new CustomException(ErrorCode.DUPLICATED_USER_NAME, request.getUsername());
            }
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null) {
            Optional<User> checkUser = userRepository.findByEmail(request.getEmail());
            if (checkUser.isPresent()) {
                throw new CustomException(ErrorCode.DUPLICATED_EMAIL, request.getUsername());
            }
            user.setEmail(request.getEmail());
        }
        if (request.getPassword() != null) {
            user.setPassword(request.getPassword());
            user.passwordEncoding(passwordEncoder);
        }
        userRepository.save(user);
        return new UserDto(user);
    }

    public ClientUserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUNDED, userId));
        return new ClientUserDto(user);
    }

    public TokenDto signIn(SignInRequestDto signInRequestDto, HttpServletResponse response) throws Exception {
        User user = userRepository.findByusername(signInRequestDto.getUsername()).orElseThrow(() -> new
                CustomException(ErrorCode.USER_NOT_FOUNDED, signInRequestDto.getUsername()));
        if (user == null || !passwordEncoder.matches(signInRequestDto.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.USER_NOT_FOUNDED, user);
        }
        TokenDto tokenDto = jwtUtil.generateToken(user, response);
        redisService.setValues(user.getId().toString(), tokenDto.getRefreshToken(), Duration.ofDays(REFRESH_TOKEN_EXPIRATION_TIME));
        return tokenDto;
    }

    public void logout(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String accessToken = jwtUtil.getJwtToken(authorizationHeader);
        String id = jwtUtil.getIdByAccessToken(accessToken);
        redisService.deleteValues(id);
    }

    @Transactional
    public TokenDto reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {

        String accessToken = jwtUtil.getJwtToken(request.getHeader(HttpHeaders.AUTHORIZATION));

        String userId = jwtUtil.getIdByAccessToken(accessToken);

        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUNDED));

        String refreshToken = redisService.getValues(userId);

        jwtUtil.validateRefreshToken(refreshToken);

        String newToken = jwtUtil.createAccessToken(user, response);

        return new TokenDto(newToken, refreshToken);
    }
}