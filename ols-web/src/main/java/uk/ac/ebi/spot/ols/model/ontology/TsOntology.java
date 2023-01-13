package uk.ac.ebi.spot.ols.model.ontology;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Value;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Builder
@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class TsOntology implements Ontology {
    String ontologyId;
    String loaded;
    String updated;
    String status;
    String message;
    Object version;
    String fileHash;
    int loadAttempts;
    int numberOfTerms;
    int numberOfProperties;
    int numberOfIndividuals;
    Config config;

    public Set<String> getCollection() {
        return Objects.isNull(config) || CollectionUtils.isEmpty(config.getClassifications())
            ? Collections.emptySet()
            : new HashSet<>(config.getClassifications().get(0).getCollection());
    }

    public String getUri() {
        return Objects.isNull(config)
            ? null
            : config.getFileLocation();
    }

    @Override
    public String getTitle() {
        return Objects.isNull(config)
                ? null
                : config.getTitle();
    }


}
