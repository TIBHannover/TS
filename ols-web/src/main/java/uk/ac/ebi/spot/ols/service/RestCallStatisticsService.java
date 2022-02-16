package uk.ac.ebi.spot.ols.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uk.ac.ebi.spot.ols.controller.dto.KeyValueResultDto;
import uk.ac.ebi.spot.ols.controller.dto.RestCallRequest;

public interface RestCallStatisticsService {
    Page<KeyValueResultDto> getRestCallsCountsByAddress(RestCallRequest request, Pageable pageable);

    KeyValueResultDto getRestCallsTotalCount(RestCallRequest request);

    Page<KeyValueResultDto> getStatisticsByParameter(RestCallRequest request, Pageable pageable);

    Page<KeyValueResultDto> getStatisticsByDate(RestCallRequest request, Pageable pageable);
}
