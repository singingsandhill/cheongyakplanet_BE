package org.cheonyakplanet.be.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cheonyakplanet.be.application.dto.ApiResponse;
import org.cheonyakplanet.be.application.dto.LoginRequestDTO;
import org.cheonyakplanet.be.application.dto.SignupRequestDTO;
import org.cheonyakplanet.be.domain.entity.User;
import org.cheonyakplanet.be.domain.entity.UserRoleEnum;
import org.cheonyakplanet.be.domain.repository.UserRepository;
import org.cheonyakplanet.be.infrastructure.jwt.JwtUtil;
import org.cheonyakplanet.be.infrastructure.security.UserDetailsImpl;
import org.cheonyakplanet.be.presentation.exception.CustomException;
import org.cheonyakplanet.be.presentation.exception.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Slf4j(topic = "회원 가입 및 로그인")
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    /**
     * 회원가입
     * @param requestDTO
     */
    public void signup(SignupRequestDTO requestDTO){
        String useremail = requestDTO.getEmail();
        String password = passwordEncoder.encode(requestDTO.getPassword());
        String username = requestDTO.getUsername();

        // 중복 확인
        Optional<User> checkEmail = userRepository.findByEmail(useremail);
        if(checkEmail.isPresent()){
            throw new CustomException(ErrorCode.SIGN002,"Email already in use");
        }

        //  사용자 role 확인
        UserRoleEnum role = UserRoleEnum.USER;
        if (requestDTO.isAdmin()){
            if(!ADMIN_TOKEN.equals(requestDTO.getAdminToken())){
                throw new CustomException(ErrorCode.SIGN003,"관리자 가입 토큰 불일치");
            }
            role = UserRoleEnum.ADMIN;
        }

        // 사용자 등록
        User user = new User(useremail,password,role,username);
        userRepository.save(user);
    }

    /**
     * 로그인
     * @param requestDTO
     * @return
     */
    public ApiResponse login(LoginRequestDTO requestDTO){

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
            String accessToken = jwtUtil.createToken(principal.getUsername(), role);
            String refreshToken = jwtUtil.generateRefreshToken(principal.getUsername());

            ApiResponse apiResponse = new ApiResponse("success", Map.of(
                    "accessToken", accessToken,
                    "refreshToken", refreshToken
            ));

            return apiResponse;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.SIGN004,"로그인 정보 불일치");
        }
    }
}
