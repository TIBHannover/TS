package uk.ac.ebi.spot.ols.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uk.ac.ebi.spot.ols.controller.dto.RestCallDto;
import uk.ac.ebi.spot.ols.controller.dto.RestCallRequest;
import uk.ac.ebi.spot.ols.entities.RestCall;
import uk.ac.ebi.spot.ols.entities.RestCallParameter;

import java.util.List;

public interface RestCallService {

    RestCall save(RestCall entity);
    
    Page<RestCallDto> getList(RestCallRequest request, List<RestCallParameter> parameters, boolean intersection, Pageable pageable);

    List<RestCall> findAll();
    
    Long count(RestCallRequest request, List<RestCallParameter> parameters, boolean intersection);
}
