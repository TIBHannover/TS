package uk.ac.ebi.spot.ols.model.ontology;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Builder
@Value
public class Classification {
    List<String> collection;
    List<String> dfg;
}
