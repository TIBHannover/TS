package uk.ac.ebi.spot.ols.service.impl;

import uk.ac.ebi.spot.ols.model.ontology.ProcessedOntology;
import uk.ac.ebi.spot.ols.service.OntologyFilterService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OntologyFilterServiceImpl implements OntologyFilterService {

    /**
     * Filters list of ontologies if collection is present, otherwise does not
     *
     * @param processedOntologies list of ontologies to be filtered
     * @param collection          optional collection value
     * @return list of ontologies
     */
    @Override
    public List<ProcessedOntology> filter(List<ProcessedOntology> processedOntologies, Optional<String> collection) {
        return collection
            .map(s ->
                processedOntologies.stream()
                    .filter(processedOntology -> processedOntology.getCollection().stream()
                        .anyMatch(s::equalsIgnoreCase))
                    .collect(Collectors.toList()))
            .orElse(processedOntologies);
    }
}
