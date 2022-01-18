package uk.ac.ebi.spot.ols.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uk.ac.ebi.spot.ols.controller.dto.RestCallDto;
import uk.ac.ebi.spot.ols.controller.dto.RestCallRequest;

public interface RestCallRepositoryCustom {
    Page<RestCallDto> query(RestCallRequest request, Pageable pageable);
}
