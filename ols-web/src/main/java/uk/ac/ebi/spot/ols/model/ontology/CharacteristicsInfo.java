package uk.ac.ebi.spot.ols.model.ontology;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Builder
@Value
public class CharacteristicsInfo {
    long size;
    long maxSimilaritiesSize;
    double percent;
    List<String> list;

    public static CharacteristicsInfo of(List<String> similarities, long maxSimilaritiesSize, double percent) {
        return CharacteristicsInfo.builder()
            .size(similarities.size())
            .maxSimilaritiesSize(maxSimilaritiesSize)
            .percent(percent)
            .list(similarities)
            .build();
    }
}
