package uk.ac.ebi.spot.ols.service;

import uk.ac.ebi.spot.ols.model.ontology.ProcessedOntology;

import java.util.List;
import java.util.Optional;

public interface OntologyFilterService {
    List<ProcessedOntology> filter(List<ProcessedOntology> processedOntologies, Optional<String> collection);
}
