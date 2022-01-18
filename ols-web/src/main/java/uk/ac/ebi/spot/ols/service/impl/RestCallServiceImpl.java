package uk.ac.ebi.spot.ols.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ols.controller.dto.RestCallDto;
import uk.ac.ebi.spot.ols.controller.dto.RestCallRequest;
import uk.ac.ebi.spot.ols.entities.RestCall;
import uk.ac.ebi.spot.ols.repositories.RestCallRepository;
import uk.ac.ebi.spot.ols.service.RestCallService;

@Service
public class RestCallServiceImpl implements RestCallService {
    private final RestCallRepository restCallRepository;

    @Autowired
    public RestCallServiceImpl(RestCallRepository restCallRepository) {
        this.restCallRepository = restCallRepository;
    }

    @Override
    public RestCall save(RestCall entity) {

        return restCallRepository.save(entity);
    }

    @Override
    public Page<RestCallDto> getList(RestCallRequest request, Pageable pageable) {

        return restCallRepository.query(request, pageable);
    }
}
