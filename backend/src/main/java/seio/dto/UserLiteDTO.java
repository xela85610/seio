package seio.dto;

import seio.model.enumeration.Role;

public class UserLiteDTO {
    public Long id;
    public String username;    // ex: Utilisateur.nom
    public String displayName; // si tu n’as pas de champ dédié, dupliqué depuis username
    public Role role;
    public Boolean active;     // si ton entité n’a pas ce champ, renvoie toujours true
}
