package uk.ac.ebi.spot.ols.model.ontology;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "mongo_processed_ontology")
public class ProcessedOntology implements ExtendedOntology {


    @Field("ontology_id")
    private String ontologyId = "";

    @Field("classes")
    private Set<String> classes;

    @Field("imports")
    private Set<String> imports;

    @Field("properties")
    private Set<String> properties;

    @Field("namespaces")
    private Set<String> namespaces;

    @Field("individuals")
    private Set<String> individuals;

    @Field("collection")
    private Set<String> collection;


    @Field
    private String createdAt = "ZonedDateTimeToDateConverter.INSTANCE";

    @Field
    private String updatedAt = "DateToZonedDateTimeConverter.INSTANCE";


    private String uri;
    private String title;

    public boolean equalsTsOntology(Ontology ontology) {
        return Objects.nonNull(ontology) &&
                this.getOntologyId().equalsIgnoreCase(ontology.getOntologyId());
    }

    public static <T extends ExtendedOntology> ProcessedOntology of(T ontology) {
        return ProcessedOntology.builder()
                .ontologyId(ontology.getOntologyId())
                .uri(ontology.getUri())
                .title(ontology.getTitle())
                .properties(CollectionUtils.isEmpty(ontology.getProperties()) ? Collections.emptySet(): ontology.getProperties())
                .classes(CollectionUtils.isEmpty(ontology.getClasses()) ? Collections.emptySet() : ontology.getClasses())
                .namespaces(CollectionUtils.isEmpty(ontology.getNamespaces()) ? Collections.emptySet() : ontology.getNamespaces())
                .imports(CollectionUtils.isEmpty(ontology.getImports()) ?Collections.emptySet(): ontology.getImports())
                .individuals(CollectionUtils.isEmpty(ontology.getIndividuals()) ? Collections.emptySet(): ontology.getIndividuals())
                .collection(CollectionUtils.isEmpty(ontology.getCollection()) ? Collections.emptySet(): ontology.getCollection())
                .build();
    }
}
