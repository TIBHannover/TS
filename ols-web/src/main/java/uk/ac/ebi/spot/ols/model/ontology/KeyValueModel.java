package uk.ac.ebi.spot.ols.model.ontology;

import lombok.*;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.server.core.Relation;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Relation(collectionRelation = "list", itemRelation = "item")
public class KeyValueModel extends ResourceSupport<KeyValueModel> {
    private String key;
    private long value;
}
