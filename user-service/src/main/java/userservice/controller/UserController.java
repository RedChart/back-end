package userservice.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import userservice.config.jwt.JwtUtil;
import userservice.config.jwt.TokenDto;
import userservice.dto.user.*;
import userservice.service.UserService;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "회원가입", description = "회원가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    })
    @PostMapping(value = "/signup")
    public ResponseEntity<SignUpResponseDto> singUp(@RequestBody SignUpDto signUpDto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(userService.signUp(signUpDto));
    }

    @Operation(summary = "로그인", description = "로그인후, access/refresh Token 발행")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    })
    @PostMapping("/signin")
    public ResponseEntity<TokenDto> signIn(@RequestBody SignInRequestDto signInRequestDto, HttpServletResponse response) throws Exception {
        return ResponseEntity.ok(userService.signIn(signInRequestDto, response));
    }

    @Operation(summary = "이메일 인증 요청", description = "이메일 인증 코드 발송")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    })
    @PostMapping("/emails/verification-requests")
    public ResponseEntity sendMessage(@RequestParam("email") @Valid String email) throws NoSuchAlgorithmException, MessagingException, UnsupportedEncodingException {
        userService.sendCodeToEmail(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "유저 이름 중복 확인", description = "username 중복 확인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    })
    @GetMapping("/check-username")
    public ResponseEntity checkUsername(@RequestParam("username") @Valid String username) {
        userService.checkDuplicatedUsername(username);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "이메일 인증 코드 입력", description = "이메일로 전송된 코드 작성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    })
    @GetMapping("/emails/verifications")
    public ResponseEntity verificationEmail(@RequestParam("email") @Valid String email,
                                            @RequestParam("code") String authCode) {
        userService.verifiedCode(email, authCode);
        return new ResponseEntity<>(HttpStatus.OK);
    }
//    // 사실 원래 Post 여야함
//    @GetMapping("/refresh-token")
//    public String refresh(@RequestHeader("Authorization")String token, HttpServletResponse response) {
//        String accessToken = jwtUtil.validateRefreshToken(token, response);
//
//        return accessToken;
//    }

    @Operation(summary = "회원 정보 수정", description = "회원 정보 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Accepted")
    })
    @PatchMapping("/update")
    public ResponseEntity<UserDto> updateUser(@RequestHeader("userId") Long userId, @RequestBody UpdateUserRequest request) {
        UserDto user = userService.updateUser(userId, request);
        // dto 만들어서 조건 추가하기
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "프로필 이미지 업로드", description = "프로필 이미지 업로드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Accepted")
    })
    @PutMapping("/update/profileImage")
    public ResponseEntity<UserDto> updateProfileImage(@RequestHeader("userId") Long userId, @RequestBody UpdateUserRequest request) {
        UserDto user = userService.updateUser(userId, request);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "로그아웃", description = "Acceess Token 인증 후, 현재 로그인중인 사용자 로그아웃")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Accepted")
    })
    @PatchMapping("/logout")
    public void logout(HttpServletResponse response, HttpServletRequest request) {
        userService.logout(request);
        response.setHeader("Authorization", "");
    }

    @Operation(summary = "에세스 토큰 재발급", description = "에세스 토큰 재발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Accepted")
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<TokenDto> reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {

        return ResponseEntity.ok(userService.reissueAccessToken(request, response));
    }

}
