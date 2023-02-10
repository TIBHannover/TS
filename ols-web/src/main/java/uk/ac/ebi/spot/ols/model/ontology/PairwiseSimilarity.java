package uk.ac.ebi.spot.ols.model.ontology;

import lombok.Builder;
import lombok.Value;
import uk.ac.ebi.spot.ols.controller.dto.Pair;

import java.util.Map;

@Builder
@Value
public class PairwiseSimilarity {
    Pair<String, String> pair;
    double sum;
    double totalSum;
    double percent;
    Pair<String, String> titles;
    Map<String, CharacteristicsInfo> characteristics;
}
