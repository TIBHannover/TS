package uk.ac.ebi.spot.ols.service;

import uk.ac.ebi.spot.ols.model.chart.ChartData;
import uk.ac.ebi.spot.ols.model.chart.ChartRequest;
import uk.ac.ebi.spot.ols.model.ontology.ExtendedOntology;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ChartService {
    ChartData chart(Optional<List<String>> ids, Optional<String> collection, ChartRequest request, Pageable pageable);

    <T extends ExtendedOntology> ChartData chart(T ontology, Optional<String> collection, ChartRequest request, Pageable pageable);
}
