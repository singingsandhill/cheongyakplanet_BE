package org.cheonyakplanet.be.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SignupRequestDTO {

    @Pattern(regexp = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9.-]+$")
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    private boolean admin = false;

    private String username;

    private String adminToken = "";
}
