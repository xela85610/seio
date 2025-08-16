package seio.dto;

import seio.model.enumeration.ProcessStatus;

import java.util.ArrayList;
import java.util.List;

public class ProcessusDTO {
    public Long id;                       // null à la création
    public String name;
    public ProcessStatus status = ProcessStatus.DRAFT;

    public List<ActeurDTO> acteurs = new ArrayList<>();
    public List<NoeudDTO> noeuds = new ArrayList<>();
    public List<ArreteDTO> arretes = new ArrayList<>();
}
