package seio.service;

import seio.dto.ProcessusDTO;
import seio.mapper.ProcessusMapper;
import seio.model.Acteur;
import seio.model.Arrete;
import seio.model.Noeud;
import seio.model.Processus;
import seio.model.enumeration.TypeNoeud;
import seio.repository.ProcessusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProcessusService {

    private final ProcessusRepository repo;

    public ProcessusService(ProcessusRepository repo) {
        this.repo = repo;
    }

    // ----------------- Cas d’usage (CRUD + validation) -----------------

    @Transactional
    public ProcessusDTO creer(ProcessusDTO dto) {
        // 1) Construire l’agrégat depuis le DTO (les arêtes ne sont pas encore reliées)
        Processus entity = ProcessusMapper.toEntity(dto);

        // 2) Persister pour obtenir des IDs (Cascade.PERSIST requis sur acteurs/noeuds/arêtes)
        Processus saved = repo.save(entity);
        repo.flush();

        // 3) Construire les maps par ID fraîchement générés
        Map<Long, Noeud> noeudsById = saved.getNoeuds().stream().collect(Collectors.toMap(Noeud::getId, n -> n));
        Map<Long, Acteur> acteursById = saved.getActeurs().stream().collect(Collectors.toMap(Acteur::getId, a -> a));

        // 4) Résoudre les références d’acteur sur les nœuds et relier les arêtes
        ProcessusMapper.applyActeurRefs(dto, saved, acteursById); // si ton mapper est francisé
        // ProcessusMapper.applyActorRefs(dto, saved, acteursById);   // si ton mapper est en anglais

        ProcessusMapper.linkArretes(dto, saved, noeudsById);       // si ton mapper est francisé (linkArretes)
        // ProcessusMapper.linkEdges(dto, saved, noeudsById);       // si ton mapper est en anglais

        // 5) Validation métier complète
        validerOuErreur(saved);

        // 6) Re-sauvegarder l’agrégat final
        Processus res = repo.save(saved);
        return ProcessusMapper.toDTO(res);
    }

    @Transactional
    public ProcessusDTO mettreAJour(Long id, ProcessusDTO dto) {
        Processus existing = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Processus introuvable"));

        // 1) Champs racine
        existing.setName(dto.name);
        if (dto.status != null) existing.setStatus(dto.status);

        // 2) Remplacement complet de l’agrégat: clear + rebuild
        // => Assure-toi d’avoir orphanRemoval = true et cascade = CascadeType.ALL sur les relations enfants
        existing.getActeurs().clear();
        existing.getNoeuds().clear();
        existing.getArretes().clear();

        Processus rebuilt = ProcessusMapper.toEntity(dto);
        for (Acteur a : rebuilt.getActeurs()) existing.addActeur(a);
        for (Noeud n : rebuilt.getNoeuds())  existing.addNoeud(n);
        for (Arrete e : rebuilt.getArretes()) existing.addArrete(e);

        repo.flush();

        // 3) Relier les références après que les IDs soient présents
        Map<Long, Noeud> noeudsById = existing.getNoeuds().stream().collect(Collectors.toMap(Noeud::getId, n -> n));
        Map<Long, Acteur> acteursById = existing.getActeurs().stream().collect(Collectors.toMap(Acteur::getId, a -> a));

        ProcessusMapper.applyActeurRefs(dto, existing, acteursById);
        // ProcessusMapper.applyActorRefs(dto, existing, acteursById);

        ProcessusMapper.linkArretes(dto, existing, noeudsById);
        // ProcessusMapper.linkEdges(dto, existing, noeudsById);

        // 4) Validation
        validerOuErreur(existing);

        // 5) Sauvegarde
        Processus res = repo.save(existing);
        return ProcessusMapper.toDTO(res);
    }

    @Transactional(readOnly = true)
    public ProcessusDTO obtenir(Long id) {
        Processus e = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Processus introuvable"));
        return ProcessusMapper.toDTO(e);
    }

    @Transactional(readOnly = true)
    public List<ProcessusDTO> lister() {
        return repo.findAll().stream().map(ProcessusMapper::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public void supprimer(Long id) {
        if (!repo.existsById(id)) throw new NoSuchElementException("Processus introuvable");
        repo.deleteById(id);
    }

    /**
     * Validation “à sec” d’un DTO sans persister.
     * Exige que chaque nœud du DTO possède déjà un id (utilisé par from/to des arêtes).
     */
    public void valider(ProcessusDTO dto) {
        Processus tmp = ProcessusMapper.toEntity(dto);

        Map<Long, Noeud> noeudsById = new HashMap<>();
        for (Noeud n : tmp.getNoeuds()) {
            if (n.getId() == null) {
                throw new IllegalArgumentException(
                        "Chaque nœud doit avoir un id dans le DTO pour la pré‑validation (from/to des arêtes).");
            }
            noeudsById.put(n.getId(), n);
        }

        ProcessusMapper.linkArretes(dto, tmp, noeudsById);
        // ProcessusMapper.linkEdges(dto, tmp, noeudsById);

        validerOuErreur(tmp);
    }


    private void validerOuErreur(Processus p) {
        List<Noeud> noeuds = Optional.ofNullable(p.getNoeuds()).orElseGet(List::of);
        List<Arrete> arretes = Optional.ofNullable(p.getArretes()).orElseGet(List::of);

        exiger(!noeuds.isEmpty(), "Le processus doit contenir des nœuds");

        Map<TypeNoeud, List<Noeud>> parType = noeuds.stream()
                .collect(Collectors.groupingBy(Noeud::getType, () -> new EnumMap<>(TypeNoeud.class), Collectors.toList()));

        exiger(parType.getOrDefault(TypeNoeud.START, List.of()).size() == 1, "Un seul nœud START est requis");
        exiger(!parType.getOrDefault(TypeNoeud.END, List.of()).isEmpty(), "Au moins un nœud END est requis");

        // Sanité des arêtes
        for (Arrete e : arretes) {
            exiger(e.getFrom() != null && e.getFrom().getId() != null, "Arête invalide: 'from' manquant");
            exiger(e.getTo() != null && e.getTo().getId() != null, "Arête invalide: 'to' manquant");
        }

        Map<Long, List<Arrete>> sorties = arretes.stream()
                .collect(Collectors.groupingBy(e -> e.getFrom().getId()));

        Set<Long> idsNoeuds = noeuds.stream()
                .map(Noeud::getId).filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        for (Noeud n : noeuds) {
            List<Arrete> outs = sorties.getOrDefault(n.getId(), List.of());

            switch (n.getType()) {
                case START -> exiger(outs.size() == 1, "Le nœud START doit avoir exactement 1 sortie");
                case ACTION -> exiger(outs.size() == 1, "ACTION doit avoir exactement 1 sortie");
                case DECISION -> {
                    exiger(outs.size() >= 2, "DECISION doit avoir au moins 2 sorties");
                    // labels uniques (null autorisé mais pas en double)
                    Set<String> labels = new HashSet<>();
                    for (Arrete e : outs) {
                        String lab = e.getLabel();
                        exiger(labels.add(String.valueOf(lab)), "Labels en double sur DECISION");
                    }
                    boolean aUneSortieAvant = outs.stream().anyMatch(e -> !e.isRetour());
                    exiger(aUneSortieAvant, "DECISION doit posséder au moins une issue vers l'avant");
                }
                case END -> exiger(outs.isEmpty(), "END ne peut pas avoir de sortie");
                default -> {}
            }

        }

        // Atteignabilité depuis START
        Noeud start = parType.get(TypeNoeud.START).getFirst();
        exiger(start.getId() != null, "Le nœud START doit avoir un id au moment de la validation");
        Set<Long> atteints = bfs(start, arretes);
        exiger(atteints.containsAll(idsNoeuds), "Certains nœuds ne sont pas atteignables depuis START");

        // Existence d’un chemin vers un END pour chaque nœud
        exiger(existeCheminVersEnd(noeuds, arretes), "Au moins une branche ne mène à aucun END");
    }

    private Set<Long> bfs(Noeud start, List<Arrete> arretes) {
        Map<Long, List<Arrete>> out = arretes.stream().collect(Collectors.groupingBy(e -> e.getFrom().getId()));
        Set<Long> vu = new HashSet<>();
        Deque<Long> dq = new ArrayDeque<>();
        dq.add(start.getId());
        while (!dq.isEmpty()) {
            Long cur = dq.poll();
            if (!vu.add(cur)) continue;
            for (Arrete e : out.getOrDefault(cur, List.of())) {
                dq.add(e.getTo().getId());
            }
        }
        return vu;
    }

    private boolean existeCheminVersEnd(List<Noeud> noeuds, List<Arrete> arretes) {
        Set<Long> ends = noeuds.stream()
                .filter(n -> n.getType() == TypeNoeud.END)
                .map(Noeud::getId)
                .collect(Collectors.toSet());

        Map<Long, List<Long>> out = arretes.stream()
                .collect(Collectors.groupingBy(e -> e.getFrom().getId(),
                        Collectors.mapping(e -> e.getTo().getId(), Collectors.toList())));

        Function<Long, Boolean> dfs = getLongBooleanFunction(ends, out);

        for (Noeud n : noeuds) {
            exiger(n.getId() != null, "Tous les nœuds doivent avoir un id au moment de la validation");
            if (!dfs.apply(n.getId())) return false;
        }
        return true;
    }

    private static Function<Long, Boolean> getLongBooleanFunction(Set<Long> ends, Map<Long, List<Long>> out) {
        Map<Long, Boolean> memo = new HashMap<>();

        return new Function<>() {
            @Override public Boolean apply(Long u) {
                if (ends.contains(u)) return true;
                if (memo.containsKey(u)) return memo.get(u); // évite les cycles
                memo.put(u, false); // marquage "en cours"
                for (Long v : out.getOrDefault(u, List.of())) {
                    if (Boolean.TRUE.equals(this.apply(v))) {
                        memo.put(u, true);
                        return true;
                    }
                }
                return false;
            }
        };
    }

    private void exiger(boolean condition, String message) {
        if (!condition) throw new IllegalArgumentException(message);
    }
}
