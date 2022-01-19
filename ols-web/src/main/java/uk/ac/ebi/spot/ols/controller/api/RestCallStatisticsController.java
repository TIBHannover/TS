package uk.ac.ebi.spot.ols.controller.api;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.data.web.SortDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.spot.ols.controller.dto.KeyValueResultDto;
import uk.ac.ebi.spot.ols.controller.dto.RestCallCountResultDto;
import uk.ac.ebi.spot.ols.controller.dto.RestCallDto;
import uk.ac.ebi.spot.ols.controller.dto.RestCallRequest;
import uk.ac.ebi.spot.ols.service.RestCallService;
import uk.ac.ebi.spot.ols.service.RestCallStatisticsService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/rest/statistics")
public class RestCallStatisticsController {
    public static final int DEFAULT_PAGE_SIZE = 20;

    private final RestCallService restCallService;
    private final RestCallStatisticsService restCallStatisticsService;
    private final RestCallAssembler restCallAssembler;

    @Autowired
    public RestCallStatisticsController(RestCallService restCallService,
                                        RestCallStatisticsService restCallStatisticsService,
                                        RestCallAssembler restCallAssembler) {
        this.restCallService = restCallService;
        this.restCallStatisticsService = restCallStatisticsService;
        this.restCallAssembler = restCallAssembler;
    }

    @ApiOperation(value = "REST Calls List")
    @RequestMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public HttpEntity<PagedResources<RestCallDto>> getList(
        PagedResourcesAssembler assembler,
        @PageableDefault(size = DEFAULT_PAGE_SIZE)
        @SortDefault.SortDefaults({
            @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC)
        }) Pageable pageable,

        @RequestParam(name = "address", required = false) String address,
        @RequestParam(name = "dateTimeFrom", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTimeFrom,
        @RequestParam(name = "dateTimeTo", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTimeTo
    ) {
        RestCallRequest request = new RestCallRequest(
            address,
            dateTimeFrom,
            dateTimeTo
        );

        Page<RestCallDto> page = restCallService.getList(request, pageable);

        return new ResponseEntity<>(assembler.toResource(page, restCallAssembler), HttpStatus.OK);
    }

    @ApiOperation(value = "REST Calls statistics by address")
    @RequestMapping(value = "/byAddress", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public HttpEntity<RestCallCountResultDto> getStatisticsByAddress(
        @RequestParam(name = "address", required = false) String address,
        @RequestParam(name = "dateTimeFrom", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTimeFrom,
        @RequestParam(name = "dateTimeTo", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTimeTo
    ) {
        RestCallRequest request = new RestCallRequest(
            address,
            dateTimeFrom,
            dateTimeTo
        );

        RestCallCountResultDto counts = restCallStatisticsService.getRestCallsCountsByAddress(request);

        return new ResponseEntity<>(counts, HttpStatus.OK);
    }

    @ApiOperation(value = "REST Calls total count")
    @RequestMapping(value = "/totalCount", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public HttpEntity<KeyValueResultDto> getTotalCount(
        @RequestParam(name = "address", required = false) String address,
        @RequestParam(name = "dateTimeFrom", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTimeFrom,
        @RequestParam(name = "dateTimeTo", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTimeTo
    ) {
        RestCallRequest request = new RestCallRequest(
            address,
            dateTimeFrom,
            dateTimeTo
        );

        KeyValueResultDto counts = restCallStatisticsService.getRestCallsTotalCount(request);

        return new ResponseEntity<>(counts, HttpStatus.OK);
    }

    @ApiOperation(value = "REST Calls statistics by parameter")
    @RequestMapping(value = "/byParameter", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public HttpEntity<RestCallCountResultDto> getStatisticsByParameter(
        @RequestParam(name = "address", required = false) String address,
        @RequestParam(name = "dateTimeFrom", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTimeFrom,
        @RequestParam(name = "dateTimeTo", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTimeTo
    ) {
        RestCallRequest request = new RestCallRequest(
            address,
            dateTimeFrom,
            dateTimeTo
        );

        RestCallCountResultDto counts = restCallStatisticsService.getRestCallsCountsByParameter(request);

        return new ResponseEntity<>(counts, HttpStatus.OK);
    }

}
