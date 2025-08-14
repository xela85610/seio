package seio.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 2000)
    @Column(name = "enonce", nullable = false)
    private String enonce;

    @Size(max = 2000)
    @Column(name = "reponse", nullable = false)
    private String reponse;

    @Size(min=1, max = 5)
    @Column(name = "difficulte", nullable = false)
    private Integer difficulte;

}
