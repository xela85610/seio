package seio.mapper;

import seio.dto.UserLiteDTO;
import seio.model.Utilisateur;

public final class UtilisateurMapper {
    private UtilisateurMapper() {}

    public static UserLiteDTO toLite(Utilisateur u) {
        if (u == null) return null;
        UserLiteDTO dto = new UserLiteDTO();
        dto.id = Long.valueOf(u.getId());
        dto.username = u.getNom();
        dto.displayName = u.getNom(); // adapte si tu as prenom/nom affichable
        dto.role = u.getRole();
        dto.active = true; // ou u.getActive() si tu as ce champ
        return dto;
    }
}
