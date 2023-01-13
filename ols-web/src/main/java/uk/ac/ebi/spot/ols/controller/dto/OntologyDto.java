package uk.ac.ebi.spot.ols.controller.dto;

import uk.ac.ebi.spot.ols.model.ontology.ProcessedOntology;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class OntologyDto {
    long id;
    String ontologyId;
    String uri;
    String title;

    public static OntologyDto of(ProcessedOntology processedOntology) {
        return OntologyDto.builder()
            .ontologyId(processedOntology.getOntologyId())
            .uri(processedOntology.getUri())
            .title(processedOntology.getTitle())
            .build();
    }
}
