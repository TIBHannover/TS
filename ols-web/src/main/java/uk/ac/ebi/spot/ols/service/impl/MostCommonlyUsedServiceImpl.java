package uk.ac.ebi.spot.ols.service.impl;

import uk.ac.ebi.spot.ols.controller.dto.KeyValueResultDto;
import uk.ac.ebi.spot.ols.model.ontology.CharacteristicsType;
import uk.ac.ebi.spot.ols.model.ontology.ProcessedOntology;
import uk.ac.ebi.spot.ols.repositories.ProcessedMongoOntologyRepository;
import uk.ac.ebi.spot.ols.service.MostCommonlyUsedService;
import uk.ac.ebi.spot.ols.service.OntologyFilterService;
import uk.ac.ebi.spot.ols.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class MostCommonlyUsedServiceImpl implements MostCommonlyUsedService {
    private final ProcessedMongoOntologyRepository ProcessedMongoOntologyRepository;
    private final OntologyFilterService filterService;


    private static final Set<String> NAMESPACES_EXCLUSION_SET = Stream.of("https://w3id.org/mdo/full/",
            "http://www.w3.org/2002/07/owl",
            "http://www.w3.org/1999/02/22-rdf-syntax-ns",
            "http://www.w3.org/xml/1998/namespace",
            "http://www.w3.org/2001/xmlschema",
            "http://www.w3.org/2000/01/rdf-schema").collect(Collectors.toSet());




    @Autowired
    public MostCommonlyUsedServiceImpl(ProcessedMongoOntologyRepository ProcessedMongoOntologyRepository,
                                       OntologyFilterService filterService) {
        this.ProcessedMongoOntologyRepository = ProcessedMongoOntologyRepository;
        this.filterService = filterService;
    }

    @Override
    public PageImpl<KeyValueResultDto> getMostCommonlyUsedCharacteristics(Optional<List<String>> ids,
                                                                          CharacteristicsType characteristicsType,
                                                                          Optional<String> collection,
                                                                          Pageable pageable) {
        List<ProcessedOntology> processedOntologies = ids.isPresent()
            ? getProcessedOntologies(ids.get())
            : getProcessedOntologies();

        if (processedOntologies == null || processedOntologies.isEmpty()) {
            return PageUtils.toPage(Collections.emptyList(), pageable);
        }
        List<ProcessedOntology> filteredOntologies = filterService.filter(processedOntologies, collection);

        List<String> characteristics = new ArrayList<>();
        filteredOntologies.forEach(processedOntology -> {
            for (String characteristic : characteristicsType.getCharacteristics(processedOntology)) {
                if (CharacteristicsType.NAMESPACE.equals(characteristicsType) &&
                    NAMESPACES_EXCLUSION_SET.stream().anyMatch(characteristic::startsWith)) {
                    continue;
                }
                characteristics.add(characteristic);
            }
        });

        Map<String, Long> map = characteristics.stream()
            .collect(
                Collectors.groupingBy(
                    s -> s,
                    Collectors.counting()
                )
            )
            .entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (oldValue, newValue) -> oldValue,
                    LinkedHashMap::new
                )
            );

        List<KeyValueResultDto> list = map.entrySet().stream()
            .map(entry -> KeyValueResultDto.builder().key(entry.getKey()).value(entry.getValue()).build())
            .collect(Collectors.toList());

        return PageUtils.toPage(list, pageable);
    }

    private List<ProcessedOntology> getProcessedOntologies(List<String> ids) {

        return ProcessedMongoOntologyRepository.findByOntologyIdIn(ids);

    }

    private List<ProcessedOntology> getProcessedOntologies() {

        return StreamSupport.stream(ProcessedMongoOntologyRepository.findAll().spliterator(), false)
            .sorted(Comparator.comparing(ProcessedOntology::getOntologyId))
            .collect(Collectors.toList());
    }
}
