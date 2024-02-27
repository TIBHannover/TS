package uk.ac.ebi.spot.ols.repositories;

import org.springframework.data.domain.Pageable;
import uk.ac.ebi.spot.ols.controller.dto.RestCallRequest;
import uk.ac.ebi.spot.ols.entities.RestCall;
import uk.ac.ebi.spot.ols.entities.RestCallParameter;

import java.util.List;

public interface RestCallRepositoryCustom {
    
    List<RestCall> query(RestCallRequest request, List<RestCallParameter> parameters, Pageable pageable);
    
    Long count(RestCallRequest request, List<RestCallParameter> parameters);
}
