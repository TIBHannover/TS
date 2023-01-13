package uk.ac.ebi.spot.ols.model.ontology;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Builder
@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalOntology implements ExtendedOntology {
    String ontologyId;
    String uri;
    String title;
    Set<String> properties;
    Set<String> classes;
    Set<String> imports;
    Set<String> namespaces;
    Set<String> individuals;
    Set<String> collection;
}
