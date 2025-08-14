package seio.dto;

import lombok.Data;

@Data
public class UserResponse {
    private Integer id;
    private String nom;
    private String role;
    public UserResponse(Integer id, String nom, String role) {
        this.id = id; this.nom = nom; this.role = role;
    }
}
