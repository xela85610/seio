package seio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seio.model.Utilisateur;
import seio.model.enumeration.Role;
import seio.repository.UtilisateurRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UtilisateurService {

    private final UtilisateurRepository repo;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Utilisateur createUser(String nom, String rawPassword, Role role) {
        if (repo.existsByNom(nom)) {
            throw new IllegalArgumentException("Ce nom d'utilisateur existe déjà");
        }
        Utilisateur u = new Utilisateur();
        u.setNom(nom);
        u.setMdp(passwordEncoder.encode(rawPassword)); // BCrypt
        u.setRole(role);
        return repo.save(u);
    }

    public Utilisateur getByNom(String nom) {
        return repo.findByNom(nom).orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
    }

    @Transactional(readOnly = true)
    public List<Utilisateur> findAll() {
        return repo.findAll();
    }
}
