package uk.ac.ebi.spot.ols.model.ontology;

import uk.ac.ebi.spot.ols.controller.dto.OntologyDto;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class Similarity {
    String name;
    List<OntologyDto> ontologies;
}
