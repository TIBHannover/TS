package uk.ac.ebi.spot.ols.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ols.entities.HttpServletRequestInfo;
import uk.ac.ebi.spot.ols.entities.RestCall;
import uk.ac.ebi.spot.ols.service.RestCallHandlerService;
import uk.ac.ebi.spot.ols.service.RestCallParserService;
import uk.ac.ebi.spot.ols.service.RestCallService;

import javax.servlet.http.HttpServletRequest;

@Service
public class RestCallHandlerServiceImpl implements RestCallHandlerService {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final RestCallParserService restCallParserService;
    private final RestCallService restCallService;

    @Autowired
    public RestCallHandlerServiceImpl(RestCallParserService restCallParserService,
                                      RestCallService restCallService) {
        this.restCallParserService = restCallParserService;
        this.restCallService = restCallService;
    }

    @Override
    public void handle(HttpServletRequest request) {
        HttpServletRequestInfo requestInfo = restCallParserService.parse(request);

        RestCall restCall = new RestCall(requestInfo.getUrl());
        restCall.addParameters(requestInfo.getPathVariables());
        restCall.addParameters(requestInfo.getQueryParameters());

        RestCall saved = restCallService.save(restCall);

        log.debug("REST Call: {}", saved);
    }
}
