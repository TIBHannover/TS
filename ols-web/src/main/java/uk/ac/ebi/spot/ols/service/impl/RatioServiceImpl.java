package uk.ac.ebi.spot.ols.service.impl;

import uk.ac.ebi.spot.ols.controller.dto.RatioDto;
import uk.ac.ebi.spot.ols.model.ontology.CharacteristicsType;
import uk.ac.ebi.spot.ols.model.ontology.Ontology;
import uk.ac.ebi.spot.ols.model.ontology.ProcessedOntology;
import uk.ac.ebi.spot.ols.repositories.ProcessedMongoOntologyRepository;
import uk.ac.ebi.spot.ols.service.RatioService;
import uk.ac.ebi.spot.ols.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RatioServiceImpl implements RatioService {
    private final ProcessedMongoOntologyRepository ProcessedMongoOntologyRepository;

    @Autowired
    protected RatioServiceImpl(ProcessedMongoOntologyRepository ProcessedMongoOntologyRepository) {
        this.ProcessedMongoOntologyRepository = ProcessedMongoOntologyRepository;
    }

    public <T extends Ontology> RatioDto getRatio(List<T> ontologies,
                                                  CharacteristicsType characteristicsType) {
        List<ProcessedOntology> processedOntologies = getProcessedOntologies(ontologies);
        if (processedOntologies == null || processedOntologies.isEmpty()) {
            return RatioDto.builder().build();
        }

        List<Set<String>> characteristics = processedOntologies.stream()
            .map(characteristicsType::getCharacteristics)
            .collect(Collectors.toList());

        double distinctCharacteristicsNumber = characteristics.stream()
            .flatMap(Collection::stream)
            .distinct()
            .count();

        Set<String> intersection = CollectionUtils.intersection(characteristics);

        double result = distinctCharacteristicsNumber == 0 ? 0 : intersection.size() / distinctCharacteristicsNumber;

        return RatioDto.builder()
            .result(result)
            .similaritiesNumber(intersection.size())
            .distinctCharacteristicsNumber(distinctCharacteristicsNumber)
            .build();
    }

    private <T extends Ontology> List<ProcessedOntology> getProcessedOntologies(List<T> ontologies) {
        List<String> ids = ontologies.stream()
            .map(Ontology::getOntologyId)
            .collect(Collectors.toList());

        return ProcessedMongoOntologyRepository.findByOntologyIdIn(ids);
    }
}
