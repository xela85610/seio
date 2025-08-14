package seio.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import seio.model.enumeration.Role;

@Data
public class CreateUserRequest {
    @NotBlank private String nom;
    @NotBlank private String password;
    private Role role = Role.USER;
}

