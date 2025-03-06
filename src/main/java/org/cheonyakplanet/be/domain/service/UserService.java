package org.cheonyakplanet.be.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cheonyakplanet.be.application.dto.ApiResponse;
import org.cheonyakplanet.be.application.dto.user.KakaoUserInfoDto;
import org.cheonyakplanet.be.application.dto.user.LoginRequestDTO;
import org.cheonyakplanet.be.application.dto.user.SignupRequestDTO;
import org.cheonyakplanet.be.domain.entity.User;
import org.cheonyakplanet.be.domain.entity.UserRoleEnum;
import org.cheonyakplanet.be.domain.entity.UserToken;
import org.cheonyakplanet.be.domain.repository.UserRepository;
import org.cheonyakplanet.be.domain.repository.UserTokenRepository;
import org.cheonyakplanet.be.infrastructure.jwt.JwtUtil;
import org.cheonyakplanet.be.infrastructure.security.UserDetailsImpl;
import org.cheonyakplanet.be.presentation.exception.CustomException;
import org.cheonyakplanet.be.presentation.exception.ErrorCode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

@Slf4j(topic = "회원 가입 및 로그인")
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;

    private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    /**
     * 회원가입
     *
     * @param requestDTO
     */
    public void signup(SignupRequestDTO requestDTO) {
        String useremail = requestDTO.getEmail();
        String password = passwordEncoder.encode(requestDTO.getPassword());
        String username = requestDTO.getUsername();

        // 중복 확인
        if (userRepository.findByEmail(useremail).isPresent()) {
            throw new CustomException(ErrorCode.SIGN002, "중복된 이메일 존재");
        }

        //  사용자 role 확인
        UserRoleEnum role = UserRoleEnum.USER;
        if (requestDTO.isAdmin()) {
            if (!ADMIN_TOKEN.equals(requestDTO.getAdminToken())) {
                throw new CustomException(ErrorCode.SIGN003, "관리자 가입 토큰 불일치");
            }
            role = UserRoleEnum.ADMIN;
        }

        // 사용자 등록
        User user = new User(useremail, password, role, username);
        userRepository.save(user);
    }

    /**
     * 로그인
     *
     * @param requestDTO
     * @return
     */
    public Object login(LoginRequestDTO requestDTO) {
        try {
            // 1) Spring Security AuthenticationManager 로 인증 시도
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDTO.getEmail(),
                            requestDTO.getPassword()
                    )
            );

            // 2) 인증 성공 시 JWT 생성
            UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
            UserRoleEnum role = principal.getUser().getRole();
            String accessToken = jwtUtil.createAccessToken(principal.getUsername(), role);
            String refreshToken = jwtUtil.createRefreshToken(principal.getUsername(), principal.getUser().getRole());
            jwtUtil.storeTokens(principal.getUsername(), accessToken, refreshToken);

            return Map.of(
                    "accessToken", accessToken,
                    "refreshToken", refreshToken
            );
        } catch (Exception e) {
            log.info(e.getMessage());
            log.error("Authentication failed", e);
            throw new CustomException(ErrorCode.SIGN004, "로그인 정보 불일치");
        }
    }

    public Object logout(HttpServletRequest request) {
        // 요청 헤더에서 토큰 추출 (Bearer 접두어 제외)
        String token = jwtUtil.getTokenFromRequest(request);
        if (!StringUtils.hasText(token)) {
            throw new CustomException(ErrorCode.AUTH001, "토큰이 존재하지 않습니다.");
        }

        // "Bearer " 접두어를 포함하여 토큰 조회
        Optional<UserToken> tokenEntityOpt = userTokenRepository.findByAccessToken(JwtUtil.BEARER_PREFIX + token);
        if (tokenEntityOpt.isPresent()) {
            UserToken userToken = tokenEntityOpt.get();
            userToken.blacklist(); // 토큰을 블랙리스트에 등록
            userTokenRepository.save(userToken);
            return "로그아웃 성공";
        } else {
            throw new CustomException(ErrorCode.AUTH002, "토큰 정보가 존재하지 않습니다.");
        }
    }

    public String kakaoLogin(String code) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getToken(code);

        // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
        KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);
        // TODO: 사용자 존재 여부 확인 후 신규 등록 또는 연동 처리

        return kakaoUserInfo.getEmail();
    }

    private String getToken(String code) throws JsonProcessingException {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com")
                .path("/oauth/token")
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "b3ffa0766c60125572ebdc645fceb9c6");
        body.add("redirect_uri", "http://localhost:8080/api/user/kakao/callback");
        body.add("code", code);

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(body);

        // HTTP 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        return jsonNode.get("access_token").asText();
    }

    private KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://kapi.kakao.com")
                .path("/v2/user/me")
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(new LinkedMultiValueMap<>());

        // HTTP 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();
        String email = jsonNode.get("kakao_account")
                .get("email").asText();

        log.info("카카오 사용자 정보: " + id + ", " + nickname + ", " + email);
        return new KakaoUserInfoDto(id, nickname, email);
    }

    public Object refreshAccessToken(String refreshToken) {
        String pureToken = jwtUtil.substringToken(refreshToken);

        String email = jwtUtil.getUserInfoFromToken(pureToken).getSubject();
        Optional<?> storedTokenOpt = userTokenRepository.findByEmail(email);
        if (storedTokenOpt.isEmpty() ||
                !jwtUtil.substringToken(((org.cheonyakplanet.be.domain.entity.UserToken) storedTokenOpt.get()).getRefreshToken())
                        .equals(pureToken)) {
            throw new CustomException(ErrorCode.AUTH005, "Refresh Token이 유효하지 않습니다.");
        }

        String newAccessToken = jwtUtil.createAccessToken(email, UserRoleEnum.USER);
        jwtUtil.storeTokens(email, newAccessToken, pureToken);

        return Map.of("accessToken", newAccessToken);
    }
}
