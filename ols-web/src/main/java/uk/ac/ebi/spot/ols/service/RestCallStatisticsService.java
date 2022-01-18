package uk.ac.ebi.spot.ols.service;

import uk.ac.ebi.spot.ols.controller.dto.KeyValueResultDto;
import uk.ac.ebi.spot.ols.controller.dto.RestCallCountResultDto;
import uk.ac.ebi.spot.ols.controller.dto.RestCallRequest;

public interface RestCallStatisticsService {
    RestCallCountResultDto getRestCallsCountsByAddress(RestCallRequest request);

    KeyValueResultDto getRestCallsTotalCount(RestCallRequest request);

    RestCallCountResultDto getRestCallsCountsByParameter(RestCallRequest request);
}
