package seio.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import seio.dto.CreateUserRequest;
import seio.dto.UserResponse;
import seio.model.Utilisateur;
import seio.service.UtilisateurService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurService service;

    @GetMapping("/auth/me")
    public ResponseEntity<UserResponse> me(Authentication auth) {
        Utilisateur u = service.getByNom(auth.getName());
        return ResponseEntity.ok(new UserResponse(u.getId(), u.getNom(), u.getRole().name()));
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid CreateUserRequest req) {
        Utilisateur u = service.createUser(req.getNom(), req.getPassword(), req.getRole());
        return ResponseEntity.ok(new UserResponse(u.getId(), u.getNom(), u.getRole().name()));
    }
}
