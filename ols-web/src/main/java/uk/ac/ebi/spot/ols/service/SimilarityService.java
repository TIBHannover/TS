package uk.ac.ebi.spot.ols.service;

import uk.ac.ebi.spot.ols.model.ontology.CharacteristicsType;
import uk.ac.ebi.spot.ols.model.ontology.ExtendedOntology;
import uk.ac.ebi.spot.ols.model.ontology.PairwiseSimilarity;
import uk.ac.ebi.spot.ols.model.ontology.Similarity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SimilarityService {
    Page<Similarity> getSimilarities(List<String> ids,
                                     CharacteristicsType characteristicsType,
                                     Optional<String> collection,
                                     Pageable pageable);

    Page<Similarity> getSimilarities(String id,
                                     CharacteristicsType characteristicsType,
                                     Optional<String> collection,
                                     Pageable pageable);

    <T extends ExtendedOntology> Page<Similarity> getSimilarities(T ontology,
                                                                  CharacteristicsType characteristicsType,
                                                                  Optional<String> collection,
                                                                  Pageable pageable);

    Page<PairwiseSimilarity> getPairwiseSimilarity(String id,
                                                   Optional<List<String>> ids,
                                                   Optional<String> collection,
                                                   Pageable pageable);

    Page<PairwiseSimilarity> getPairwiseSimilarity(Optional<List<String>> ids,
                                                   Optional<String> collection,
                                                   Pageable pageable);

    Page<PairwiseSimilarity> getPairwiseSimilarity(String id,
                                                   Optional<String> collection,
                                                   Pageable pageable);

    <T extends ExtendedOntology> Page<PairwiseSimilarity> getPairwiseSimilarity(T ontology,
                                                                                Optional<String> collection,
                                                                                Pageable pageable);

    <T extends ExtendedOntology> Page<PairwiseSimilarity> getPairwiseSimilarity(T ontology,
                                                                                Optional<List<String>> ids,
                                                                                Optional<String> collection,
                                                                                Pageable pageable);
}
