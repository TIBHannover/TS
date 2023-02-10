package uk.ac.ebi.spot.ols.controller.dto;

import uk.ac.ebi.spot.ols.model.ontology.CharacteristicsInfo;
import lombok.*;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.server.core.Relation;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Relation(collectionRelation = "similarities", itemRelation = "similarity")
public class PairwiseSimilarityModel extends ResourceSupport<PairwiseSimilarityModel> {
    private Pair<String, String> pair;
    private double sum;
    private double totalSum;
    private double percentage;
    private Pair<String, String> titles;
    private Map<String, CharacteristicsInfo> characteristics;
}
