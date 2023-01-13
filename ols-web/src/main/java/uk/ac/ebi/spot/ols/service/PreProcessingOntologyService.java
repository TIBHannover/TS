package uk.ac.ebi.spot.ols.service;

import uk.ac.ebi.spot.ols.model.ontology.ProcessedOntology;
import uk.ac.ebi.spot.ols.model.ontology.TsOntology;

import java.util.Optional;

public interface PreProcessingOntologyService {
    ProcessedOntology preProcess(Optional<TsOntology> tsOntology, String fileLocation,String title);
}
