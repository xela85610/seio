package seio.controller;

import seio.dto.ProcessCategoryDTO;
import seio.dto.ProcessusDTO;
import seio.dto.UserLiteDTO;
import seio.mapper.UtilisateurMapper;
import seio.model.Utilisateur;
import seio.service.ProcessusService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import seio.service.UtilisateurService;

import java.util.List;

@RestController
@RequestMapping("/api/processes")
public class ProcessusController {

    private final ProcessusService service;
    private final UtilisateurService utilisateurService;

    public ProcessusController(ProcessusService service, UtilisateurService utilisateurService) {
        this.service = service;
        this.utilisateurService = utilisateurService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProcessusDTO create(@RequestBody ProcessusDTO dto) {
        return service.creer(dto);
    }

    @PutMapping("/{id}")
    public ProcessusDTO update(@PathVariable Long id, @RequestBody ProcessusDTO dto) {
        dto.id = id; // pour info
        return service.mettreAJour(id, dto);
    }

    @GetMapping("/{id}")
    public ProcessusDTO get(@PathVariable Long id) {
        return service.obtenir(id);
    }

    @GetMapping
    public List<ProcessusDTO> list() {
        return service.lister();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.supprimer(id);
    }

    @PostMapping("/validate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void validate(@RequestBody ProcessusDTO dto) {
        service.valider(dto);
    }

    // ====== Référentiel Users pour le front (chemin absolu) ======
    @GetMapping(path = "/api/users")
    public List<UserLiteDTO> listUsers(@RequestParam(name = "active", required = false) Boolean active) {
        List<Utilisateur> users = utilisateurService.findAll();
        return users.stream().map(UtilisateurMapper::toLite).toList();
    }

    @GetMapping(path = "/api/process-categories")
    public List<ProcessCategoryDTO> listProcessCategories(
            @RequestParam(name = "active", required = false) Boolean active) {

        ProcessCategoryDTO hr = new ProcessCategoryDTO();
        hr.id = 1L; hr.code = "HR"; hr.label = "Ressources humaines"; hr.active = true;

        ProcessCategoryDTO it = new ProcessCategoryDTO();
        it.id = 2L; it.code = "IT"; it.label = "Informatique"; it.active = true;

        // Filtre simple si active != null
        List<ProcessCategoryDTO> all = List.of(hr, it);
        if (active == null) return all;
        return all.stream().filter(c -> Boolean.TRUE.equals(c.active) == active).toList();
    }
}
