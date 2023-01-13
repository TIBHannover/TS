package uk.ac.ebi.spot.ols.service;

import uk.ac.ebi.spot.ols.controller.dto.RatioDto;
import uk.ac.ebi.spot.ols.model.ontology.CharacteristicsType;
import uk.ac.ebi.spot.ols.model.ontology.Ontology;

import java.util.List;

public interface RatioService {
    <T extends Ontology> RatioDto getRatio(List<T> ontologies, CharacteristicsType characteristicsType);
}
