package uk.ac.ebi.spot.ols.service;

import uk.ac.ebi.spot.ols.entities.RestCallParameter;

import java.util.Set;

public interface RestCallParameterService {
    RestCallParameter save(RestCallParameter entity);

    Set<RestCallParameter> save(Set<RestCallParameter> entities);
}
