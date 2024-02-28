package uk.ac.ebi.spot.ols.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ols.controller.dto.RestCallDto;
import uk.ac.ebi.spot.ols.controller.dto.RestCallRequest;
import uk.ac.ebi.spot.ols.entities.RestCall;
import uk.ac.ebi.spot.ols.entities.RestCallParameter;
import uk.ac.ebi.spot.ols.repositories.RestCallRepository;
import uk.ac.ebi.spot.ols.service.RestCallService;

import java.util.List;
import java.util.stream.Collectors;

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
    public Page<RestCallDto> getList(RestCallRequest request, List<RestCallParameter> parameters, boolean intersection, Pageable pageable) {
        List<RestCall> list = restCallRepository.query(request, parameters, intersection, pageable);

        List<RestCallDto> dtos = list.stream()
            .map(RestCallDto::of)
            .collect(Collectors.toList());

        Long count = restCallRepository.count(request, parameters, intersection);

        return new PageImpl<>(dtos, pageable, count);
    }
    @Override
    public List<RestCall> findAll() {
        return restCallRepository.findAll();
    }
    
    @Override
    public Long count(RestCallRequest request, List<RestCallParameter> parameters, boolean intersection) {

        return restCallRepository.count(request,parameters,intersection);
    }
}
