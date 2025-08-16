package seio.dto;

import seio.model.enumeration.TypeNoeud;

public class NoeudDTO {
    public Long id;                 // peut être fourni par le front (sinon généré)
    public TypeNoeud type;
    public String title;
    public String description;
    public Double posX;
    public Double posY;
    public Long acteurId;
    public String temporalite;
}
