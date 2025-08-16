package seio.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import seio.model.enumeration.TypeNoeud;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Noeud {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id", nullable = false)
    private Processus process;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeNoeud type;

    @Column(columnDefinition = "text")
    private String description;

    private Double posX;
    private Double posY;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acteur_id")
    private Acteur acteur;

    @Column(nullable = false)
    private String temporalite;
}
