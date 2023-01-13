package uk.ac.ebi.spot.ols.service;

import uk.ac.ebi.spot.ols.controller.dto.KeyValueResultDto;
import uk.ac.ebi.spot.ols.model.ontology.CharacteristicsType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MostCommonlyUsedService {
    PageImpl<KeyValueResultDto> getMostCommonlyUsedCharacteristics(Optional<List<String>> ids,
                                                                   CharacteristicsType characteristicsType,
                                                                   Optional<String> collection,
                                                                   Pageable pageable);
}
