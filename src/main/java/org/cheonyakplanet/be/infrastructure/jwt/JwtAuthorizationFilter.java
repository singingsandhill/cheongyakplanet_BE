package org.cheonyakplanet.be.infrastructure.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cheonyakplanet.be.infrastructure.security.UserDetailsServiceImpl;
import org.cheonyakplanet.be.presentation.exception.CustomException;
import org.cheonyakplanet.be.presentation.exception.ErrorCode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {

        try {
            String tokenValue = jwtUtil.getTokenFromRequest(req);

            log.info("Extracted Token: {}", tokenValue);
            if (StringUtils.hasText(tokenValue)) {
                // JWT 토큰 substring
                //tokenValue = jwtUtil.substringToken(tokenValue);
                log.info(tokenValue);

                if (!jwtUtil.validateToken(tokenValue)) {
                    throw new CustomException(ErrorCode.AUTH005, "Token validation failed");
                }

                Claims info = jwtUtil.getUserInfoFromToken(tokenValue);
                log.info("Claims: {}", info);
                setAuthentication(info.getSubject());
            }
            filterChain.doFilter(req, res);

        } catch (CustomException e) {
            throw e;
        }
    }

    // 인증 처리
    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs");
    }

}