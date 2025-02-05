package org.cheonyakplanet.be.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SignupRequestDTO {

    @Schema(description = "이메일, 로그인에 이용", example = "user@example.com")
    @Pattern(regexp = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9.-]+$")
    @NotBlank
    private String email;

    @Schema(description = "비밀번호", example = "1234")
    @NotBlank
    private String password;

    @Schema(description = "관리자 여부", example = "false")
    private boolean admin = false;

    @Schema(description = "게시판에 쓸 아이디", example = "tester")
    private String username;

    @Schema(description = "관리자 계정 생성시 토큰 입력 (생략가능)",example = "")
    private String adminToken = "";
}
