package seio.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "utilisateur")
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 32)
    @Column(name = "role", nullable = false)
    private String role;

    @Size(max = 255)
    @Column(name = "nom", nullable = false)
    private String nom;

    @Size(max = 255)
    @Column(name = "mdp", nullable = false)
    private String mdp;

}
