package org.cheonyakplanet.be.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cheonyakplanet.be.application.dto.ApiResponse;
import org.cheonyakplanet.be.application.dto.user.LoginRequestDTO;
import org.cheonyakplanet.be.application.dto.user.SignupRequestDTO;
import org.cheonyakplanet.be.domain.repository.UserTokenRepository;
import org.cheonyakplanet.be.domain.service.UserService;
import org.cheonyakplanet.be.infrastructure.jwt.JwtUtil;
import org.cheonyakplanet.be.infrastructure.security.UserDetailsServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserTokenRepository userTokenRepository;

    /**
     * 회원가입
     *
     * @param requestDTO
     * @return
     */
    @PostMapping("/signup")
    @Operation(summary = "회원가입")
    public ResponseEntity<?> signup(@RequestBody SignupRequestDTO requestDTO) {
        userService.signup(requestDTO);
        return ResponseEntity.ok(new ApiResponse("success", requestDTO));
    }

    /**
     * 로그인
     *
     * @param requestDto
     * @return
     */
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일 입력")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO requestDto) {
        ApiResponse response = userService. login(requestDto);
        return ResponseEntity.ok().body(response);
    }

    /**
     * 로그아웃
     *
     * @param request
     * @param
     * @return
     */
    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "사용자 로그아웃 처리")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        return ResponseEntity.ok(userService.logout(request));
    }

    @GetMapping("/kako/callback")
    public void kakaoLogin(@RequestParam String code, HttpServletResponse response) throws IOException {
        String email = userService.kakaoLogin(code);

        // 2. 저장된 Access Token 가져오기
        String accessToken = jwtUtil.getAccessToken(email);

        // 3. 프론트엔드로 리다이렉트 (Access Token 포함)
        response.sendRedirect("https://frontend-domain.com/oauth/callback?accessToken=" + accessToken);

    }

    @PostMapping("/auth/refresh")
    @Operation(summary = "토큰 갱신")
    public ResponseEntity<?> refreshAccessToken(@RequestParam String refreshToken) {
        ApiResponse response = userService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(response);
    }

}
