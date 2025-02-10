package org.cheonyakplanet.be.presentation.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cheonyakplanet.be.application.dto.ApiResponse;
import org.cheonyakplanet.be.application.dto.LoginRequestDTO;
import org.cheonyakplanet.be.application.dto.SignupRequestDTO;
import org.cheonyakplanet.be.domain.service.UserService;
import org.cheonyakplanet.be.infrastructure.jwt.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    /**
     * 회원가입
     * @param requestDTO
     * @return
     */
    @PostMapping("/signup")
    @Operation(summary = "회원가입")
    public ResponseEntity<?> signup(@RequestBody SignupRequestDTO requestDTO) {
        userService.signup(requestDTO);
        return ResponseEntity.ok(new ApiResponse("success",requestDTO));
    }

    /**
     * 로그인
     * @param requestDto
     * @return
     */
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일 입력")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO requestDto) {
        ApiResponse response = userService.login(requestDto);
        return ResponseEntity.ok().body(response);
    }

    /**
     * 로그아웃
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "사용자 로그아웃 처리")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        String token = jwtUtil.getTokenFromRequest(request);

        if (token != null) {
            // Refresh Token 무효화 처리
            jwtUtil.invalidateRefreshToken(token);
        }

        return ResponseEntity.ok(new ApiResponse("success", "로그아웃이 완료되었습니다."));
    }

    @GetMapping("/kako/callback")
    public String kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        String token = userService.kakaoLogin(code);

        Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER, token);
        cookie.setPath("/");
        response.addCookie(cookie);

        return "redirect:/";

    }
}
