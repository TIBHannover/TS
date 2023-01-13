package uk.ac.ebi.spot.ols.repositories;

import uk.ac.ebi.spot.ols.model.ontology.ProcessedOntology;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProcessedMongoOntologyRepository extends MongoRepository<ProcessedOntology, Long> {
    List<ProcessedOntology> findByOntologyIdIn(List<String> ids);
}
