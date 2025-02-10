package org.cheonyakplanet.be.infrastructure.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cheonyakplanet.be.domain.entity.RefreshToken;
import org.cheonyakplanet.be.domain.entity.UserRoleEnum;
import org.cheonyakplanet.be.domain.repository.RefreshTokenRepository;
import org.cheonyakplanet.be.presentation.exception.CustomException;
import org.cheonyakplanet.be.presentation.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    // Header Key
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // admin key
    public static final String AUTHORIZATION_KEY = "auth";
    // token prefix
    public static final String BEARER_PREFIX = "Bearer ";
    // token exp
    private final long TOKEN_TIME = 60 * 60 * 1000L; // 60min
    private final long REFRESH_TOKEN_TIME = 24 * 60 * 60 * 1000L; // 1 days

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    private final RefreshTokenRepository refreshTokenRepository;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String generateRefreshToken(String email) {
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByEmail(email);

        String newToken = createRefreshToken(email);
        if (existingToken.isPresent()) {
            existingToken.get().updateToken(newToken);
            refreshTokenRepository.save(existingToken.get());
        } else {
            refreshTokenRepository.save(new RefreshToken(email, newToken));
        }
        return newToken;
    }

    /**
     * JWT 생성
     *
     * @param email
     * @param role
     * @return
     */
    public String createToken(String email, UserRoleEnum role) {
        Date date = new Date();

        return BEARER_PREFIX + Jwts.builder()
                .setSubject(email) // ID
                .claim(AUTHORIZATION_KEY, role)
                .setExpiration(new Date(date.getTime() + TOKEN_TIME))
                .setIssuedAt(date)
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    public String createRefreshToken(String email) {
        Date now = new Date();
        return BEARER_PREFIX + Jwts.builder()
                .setSubject(email)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_TIME))
                .setIssuedAt(now)
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    public void invalidateRefreshToken(String token) {
        Claims claims = getUserInfoFromToken(token);
        String email = claims.getSubject();

        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByEmail(email);
        if (refreshToken.isPresent()) {
            refreshTokenRepository.delete(refreshToken.get());
            log.info("Refresh Token for {} 무효화 완료", email);
        } else {
            log.warn("Refresh Token for {} 가 존재하지 않습니다.", email);
        }
    }

    /**
     * 생성된 JWT를 쿠키에 저장
     *
     * @param token
     * @param response
     */
    public void addJwtToCookie(String token, HttpServletResponse response) {
        try {
            token = URLEncoder.encode(token, "UTF-8").replaceAll("\\+", "%20"); // 공백 제거를 위한 인코딩

            Cookie cookie = new Cookie(AUTHORIZATION_KEY, token);
            cookie.setPath("/");

            response.addCookie(cookie);

        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * JWT 토큰 prefix 제거
     *
     * @param token
     * @return
     */
    public String substringToken(String token) {
        if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
            return token.substring(7);
        }
        throw new CustomException(ErrorCode.AUTH010, "토큰 없음");
    }

    /**
     * JWT 검증
     *
     * @param token
     * @return
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            throw new CustomException(ErrorCode.AUTH001, "Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.AUTH002, "Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            throw new CustomException(ErrorCode.AUTH003, "Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.AUTH004, "JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
    }

    /**
     * 토큰에서 사용자 정보 가져오기
     *
     * @param token
     * @return
     */
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    /**
     * Header에사 JWT 가져오기
     *
     * @param request
     * @return
     */
    public String getTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            if (!StringUtils.hasText(header.substring(7)) || !validateToken(header.substring(7))) {
                throw new CustomException(ErrorCode.AUTH001, "Invalid or missing token");
            }
            return header.substring(7); // "Bearer " 이후의 토큰 반환
        }
        return null;
    }


}
