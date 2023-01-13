package uk.ac.ebi.spot.ols.model.ontology;

import uk.ac.ebi.spot.ols.controller.dto.OntologyDto;
import lombok.*;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Relation(collectionRelation = "similarities", itemRelation = "similarity")
public class SimilarityModel extends ResourceSupport<SimilarityModel> {
    private String name;
    private List<OntologyDto> ontologies;
}
