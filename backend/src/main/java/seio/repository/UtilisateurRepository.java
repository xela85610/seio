package seio.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import seio.model.Utilisateur;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {

    Optional<Utilisateur> findByNom(String nom);

    boolean existsByNom(String nom);
}