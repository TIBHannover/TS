package uk.ac.ebi.spot.ols.model.ontology;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class OntologyPair {
    String ontologyId1;
    String ontologyId2;

    public OntologyPair inverted() {
        return OntologyPair.builder()
            .ontologyId1(ontologyId2)
            .ontologyId2(ontologyId1)
            .build();
    }

    public static OntologyPair of(ProcessedOntology ontology1, ProcessedOntology ontology2) {
        return OntologyPair.builder()
            .ontologyId1(ontology1.getOntologyId())
            .ontologyId2(ontology2.getOntologyId())
            .build();
    }
}
