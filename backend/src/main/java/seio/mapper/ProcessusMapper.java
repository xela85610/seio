package seio.mapper;

import seio.dto.*;
import seio.model.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Rôles:
 * 1) Entity -> DTO pour renvoyer un graphe complet au front.
 * 2) DTO -> Entity pour reconstruire l'agrégat Processus depuis un payload unique.
 * 3) Post-traitement pour lier les arêtes (from/to) après persist, quand les IDs des nœuds existent.
 */
public class ProcessusMapper {

    // =======================
    // ENTITY -> DTO
    // =======================
    public static ProcessusDTO toDTO(Processus e) {
        ProcessusDTO d = new ProcessusDTO();
        d.id = e.getId();
        d.name = e.getName();
        d.status = e.getStatus();

        d.acteurs = e.getActeurs().stream().map(ProcessusMapper::toDTO).collect(Collectors.toList());
        d.noeuds  = e.getNoeuds().stream().map(ProcessusMapper::toDTO).collect(Collectors.toList());
        d.arretes = e.getArretes().stream().map(ProcessusMapper::toDTO).collect(Collectors.toList());
        return d;
    }

    private static ActeurDTO toDTO(Acteur e) {
        ActeurDTO d = new ActeurDTO();
        d.id = e.getId();
        d.name = e.getName();
        d.color = e.getColor();
        return d;
    }

    private static NoeudDTO toDTO(Noeud e) {
        NoeudDTO d = new NoeudDTO();
        d.id = e.getId();
        d.type = e.getType();
        d.description = e.getDescription();
        d.posX = e.getPosX();
        d.posY = e.getPosY();
        d.acteurId = e.getActeur() != null ? e.getActeur().getId() : null;
        d.temporalite = e.getTemporalite(); // si Temporalite est @Embeddable, Jackson le sérialisera
        return d;
    }

    private static ArreteDTO toDTO(Arrete e) {
        ArreteDTO d = new ArreteDTO();
        d.id = e.getId();
        d.from = e.getFrom().getId();
        d.to = e.getTo().getId();
        d.label = e.getLabel();
        d.retour = e.isRetour();
        d.orderIndex = e.getOrderIndex();
        d.uiHintsJson = e.getUiHintsJson();
        return d;
    }

    // =======================
    // DTO -> ENTITY
    // =======================
    public static Processus toEntity(ProcessusDTO d) {
        Objects.requireNonNull(d, "ProcessusDTO null");
        Processus e = new Processus();
        e.setName(Objects.requireNonNull(d.name, "name requis"));
        if (d.status != null) e.setStatus(d.status);

        // Acteurs
        // Remarque: on crée les entités; les IDs peuvent être null à la création, JPA les génèrera.
        Map<Long, Acteur> acteursById = new LinkedHashMap<>();
        if (d.acteurs != null) {
            for (ActeurDTO a : d.acteurs) {
                Acteur ae = new Acteur();
                ae.setId(a.id);
                ae.setName(Objects.requireNonNull(a.name, "acteur.name requis"));
                ae.setColor(Objects.requireNonNull(a.color, "acteur.color requis"));
                e.addActeur(ae);
                if (ae.getId() != null) acteursById.put(ae.getId(), ae);
            }
        }

        // Noeuds (sans setter l'acteur ici si l'id acteur n'est pas encore persisté)
        Map<Long, Noeud> noeudsById = new LinkedHashMap<>();
        if (d.noeuds != null) {
            for (NoeudDTO n : d.noeuds) {
                Noeud ne = new Noeud();
                ne.setId(n.id);
                ne.setType(Objects.requireNonNull(n.type, "noeud.type requis"));
                ne.setDescription(n.description);
                ne.setPosX(n.posX);
                ne.setPosY(n.posY);
                // on posera l'acteur après persist si nécessaire
                ne.setTemporalite(n.temporalite);
                e.addNoeud(ne);
                if (ne.getId() != null) noeudsById.put(ne.getId(), ne);
            }
        }

        // Arêtes (création sans lier from/to ici; on le fera via linkArretes)
        if (d.arretes != null) {
            for (ArreteDTO ad : d.arretes) {
                Arrete ee = new Arrete();
                ee.setId(ad.id);
                ee.setLabel(ad.label);
                ee.setRetour(ad.retour);
                ee.setOrderIndex(ad.orderIndex);
                ee.setUiHintsJson(ad.uiHintsJson);
                e.addArrete(ee);
            }
        }
        return e;
    }

    /**
     * Rattache les acteurs sur les nœuds après un persist/flush (quand les IDs sont connus).
     * @param dto payload d'origine (porte acteurId sur les nœuds)
     * @param entity agrégat Processus déjà persisté (ou reconstruit)
     * @param persistedActeurs map optionnelle id->Acteur (si déjà construite par l'appelant)
     */
    public static void applyActeurRefs(ProcessusDTO dto, Processus entity, Map<Long, Acteur> persistedActeurs) {
        if (dto == null || dto.noeuds == null) return;

        Map<Long, Acteur> acteurs = persistedActeurs != null ? persistedActeurs :
                entity.getActeurs().stream()
                        .filter(a -> a.getId() != null)
                        .collect(Collectors.toMap(Acteur::getId, Function.identity()));

        Map<Long, Noeud> noeuds = entity.getNoeuds().stream()
                .filter(n -> n.getId() != null)
                .collect(Collectors.toMap(Noeud::getId, Function.identity()));

        for (NoeudDTO nd : dto.noeuds) {
            if (nd.acteurId == null || nd.id == null) continue;
            Noeud cible = noeuds.get(nd.id);
            if (cible == null) continue;
            Acteur a = acteurs.get(nd.acteurId);
            if (a == null)
                throw new IllegalArgumentException("Acteur introuvable pour noeud " + nd.id + " (acteurId=" + nd.acteurId + ")");
            cible.setActeur(a);
        }
    }

    /**
     * Lie les arêtes (from/to) vers les bons nœuds en s'appuyant sur les IDs présents dans le DTO.
     * Doit être appelé après persist/flush pour s'assurer que entity.getNoeuds() porte des IDs.
     */
    public static void linkArretes(ProcessusDTO dto, Processus entity, Map<Long, Noeud> persistedNoeuds) {
        if (dto == null || dto.arretes == null) return;

        Map<Long, Noeud> noeuds = persistedNoeuds != null ? persistedNoeuds :
                entity.getNoeuds().stream()
                        .filter(n -> n.getId() != null)
                        .collect(Collectors.toMap(Noeud::getId, Function.identity()));

        // Hypothèse: l'ordre des arretes dans l'entité correspond à dto.arretes
        List<Arrete> entEdges = entity.getArretes();
        if (entEdges.size() != dto.arretes.size()) {
            throw new IllegalStateException("Incohérence arretes: entity=" + entEdges.size() + " dto=" + dto.arretes.size());
        }

        for (int i = 0; i < entEdges.size(); i++) {
            Arrete ee = entEdges.get(i);
            ArreteDTO ed = dto.arretes.get(i);

            if (ed.from == null || ed.to == null) {
                throw new IllegalArgumentException("Arrete fromNoeudId/toNoeudId requis");
            }
            Noeud from = noeuds.get(ed.from);
            Noeud to   = noeuds.get(ed.to);
            if (from == null || to == null) {
                throw new IllegalArgumentException("Arrete références invalides (from/to introuvables): " + ed);
            }
            ee.setFrom(from);
            ee.setTo(to);
        }
    }
}
