package uk.ac.ebi.spot.ols.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ols.entities.RestCallParameter;
import uk.ac.ebi.spot.ols.repositories.RestCallParameterRepository;
import uk.ac.ebi.spot.ols.service.RestCallParameterService;

import java.util.HashSet;
import java.util.Set;

@Service
public class RestCallParameterServiceImpl implements RestCallParameterService {
    private final RestCallParameterRepository parameterRepository;

    @Autowired
    public RestCallParameterServiceImpl(RestCallParameterRepository parameterRepository) {
        this.parameterRepository = parameterRepository;
    }

    @Override
    public RestCallParameter save(RestCallParameter entity) {
        return parameterRepository.save(entity);
    }

    @Override
    public Set<RestCallParameter> save(Set<RestCallParameter> entities) {
        return new HashSet<>(parameterRepository.save(entities));
    }
}
