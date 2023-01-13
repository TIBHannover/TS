package uk.ac.ebi.spot.ols.repositories;

import uk.ac.ebi.spot.ols.model.ontology.TsOntology;

import java.util.List;

public interface TsRepository {
    List<TsOntology> getOntologies();
}
