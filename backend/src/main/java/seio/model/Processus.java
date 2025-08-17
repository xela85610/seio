package seio.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "processus")
public class Processus {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "process", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Acteur> acteurs = new ArrayList<>();

    @OneToMany(mappedBy = "process", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Noeud> noeuds = new ArrayList<>();

    @OneToMany(mappedBy = "process", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Arrete> arretes = new ArrayList<>();

    public void addActeur(Acteur a){ a.setProcess(this); this.acteurs.add(a); }
    public void addNoeud(Noeud n){ n.setProcess(this); this.noeuds.add(n); }
    public void addArrete(Arrete e){ e.setProcess(this); this.arretes.add(e); }

}
