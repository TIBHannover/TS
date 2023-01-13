package uk.ac.ebi.spot.ols.model.ontology;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class Titles {
    String firstTitle;
    String secondTitle;
}
