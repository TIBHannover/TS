package uk.ac.ebi.spot.ols.service;

import uk.ac.ebi.spot.ols.controller.dto.OntologyDto;
import uk.ac.ebi.spot.ols.model.ontology.ProcessedOntology;

import java.util.List;

public interface ProcessedOntologyService {
    List<ProcessedOntology> findAll();

    List<OntologyDto> getOntologies();

    List<String> getOntologyIds();

    ProcessedOntology save(ProcessedOntology processedOntology);
}
