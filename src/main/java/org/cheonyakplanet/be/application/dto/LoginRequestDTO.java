package org.cheonyakplanet.be.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginRequestDTO {
    @Schema(description = "이메일",example = "test@test")
    private String email;
    @Schema(example = "1234")
    private String password;
}
