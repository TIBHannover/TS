package uk.ac.ebi.spot.ols.controller.api;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.spot.ols.controller.dto.KeyValueResultDto;
import uk.ac.ebi.spot.ols.controller.dto.RestCallDto;
import uk.ac.ebi.spot.ols.controller.dto.RestCallRequest;
import uk.ac.ebi.spot.ols.entities.RestCallParameter;
import uk.ac.ebi.spot.ols.entities.RestCallParameterList;
import uk.ac.ebi.spot.ols.entities.RestCallParameterType;
import uk.ac.ebi.spot.ols.service.RestCallService;
import uk.ac.ebi.spot.ols.service.RestCallStatisticsService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rest/statistics")
public class RestCallStatisticsController {
    public static final int DEFAULT_PAGE_SIZE = 20;

    private final RestCallService restCallService;
    private final RestCallStatisticsService restCallStatisticsService;
    private final RestCallAssembler restCallAssembler;
    private final KeyValueResultAssembler keyValueResultAssembler;

    @Autowired
    public RestCallStatisticsController(RestCallService restCallService,
                                        RestCallStatisticsService restCallStatisticsService,
                                        RestCallAssembler restCallAssembler,
                                        KeyValueResultAssembler keyValueResultAssembler) {
        this.restCallService = restCallService;
        this.restCallStatisticsService = restCallStatisticsService;
        this.restCallAssembler = restCallAssembler;
        this.keyValueResultAssembler = keyValueResultAssembler;
    }

    @ApiOperation(value = "REST Calls List")
    @RequestMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public HttpEntity<PagedResources<RestCallDto>> getList(
        PagedResourcesAssembler assembler,
        @PageableDefault(size = DEFAULT_PAGE_SIZE)
        @SortDefault.SortDefaults({
            @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC)
        }) Pageable pageable,

        @RequestParam(name = "url", required = false) String url,
        @RequestParam(name = "dateTimeFrom", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTimeFrom,
        @RequestParam(name = "dateTimeTo", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTimeTo,
        @RequestParam(name="intersection", required=true,defaultValue="false") boolean intersection,
        @ModelAttribute RestCallParameterList parameterList
    ) {
        RestCallRequest request = new RestCallRequest(
            url,
            dateTimeFrom,
            dateTimeTo
        );
        
        List<RestCallParameter> parameters = new ArrayList<RestCallParameter>();
        
        if(parameterList != null) {
        	if(parameterList.getParameters() != null)
	        	parameters.addAll(parameterList.getParameters());
        }     

        Page<RestCallDto> page = restCallService.getList(request, parameters, intersection, pageable);

        return new ResponseEntity<>(assembler.toResource(page, restCallAssembler), HttpStatus.OK);
    }

    @ApiOperation(value = "REST Calls statistics by URL")
    @RequestMapping(value = "/byUrl", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public HttpEntity<PagedResources<KeyValueResultDto>> getStatisticsByUrl(
        @RequestParam(name = "url", required = false) String url,
        @RequestParam(name = "dateTimeFrom", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTimeFrom,
        @RequestParam(name = "dateTimeTo", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTimeTo,
        @RequestParam(name="intersection", required=true,defaultValue="false") boolean intersection,
        @ModelAttribute RestCallParameterList parameterList,
        Pageable pageable,
        PagedResourcesAssembler assembler
    ) {
        RestCallRequest request = new RestCallRequest(
            url,
            dateTimeFrom,
            dateTimeTo
        );
        
        List<RestCallParameter> parameters = new ArrayList<RestCallParameter>();
        
        if(parameterList != null) {
        	if(parameterList.getParameters() != null)
	        	parameters.addAll(parameterList.getParameters());
        }      

        Page<KeyValueResultDto> page = restCallStatisticsService.getRestCallsCountsByAddress(request, parameters, intersection, pageable);

        return new ResponseEntity<>(assembler.toResource(page, keyValueResultAssembler), HttpStatus.OK);
    }

    @ApiOperation(value = "REST Calls total count")
    @RequestMapping(value = "/count", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public HttpEntity<KeyValueResultDto> getTotalCount(
        @RequestParam(name = "url", required = false) String url,
        @RequestParam(name = "dateTimeFrom", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTimeFrom,
        @RequestParam(name = "dateTimeTo", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTimeTo,
        @RequestParam(name="intersection", required=true,defaultValue="false") boolean intersection,
        @ModelAttribute RestCallParameterList parameterList
    ) {
        RestCallRequest request = new RestCallRequest(
            url,
            dateTimeFrom,
            dateTimeTo
        );
        
        
        List<RestCallParameter> parameters = new ArrayList<RestCallParameter>();
        
        if(parameterList != null) {
        	if(parameterList.getParameters() != null)
	        	parameters.addAll(parameterList.getParameters());
        }     

        KeyValueResultDto counts = restCallStatisticsService.getRestCallsTotalCount(request,parameters,intersection);

        return new ResponseEntity<>(counts, HttpStatus.OK);
    }
    

    @ApiOperation(value = "REST Calls statistics by query parameters and path variables")
    @RequestMapping(value = "/byParameter", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public HttpEntity<PagedResources<KeyValueResultDto>> getStatisticsByParameter(
        @ApiParam(value = "Parameter type", allowableValues = "PATH,QUERY,HEADER")
        @RequestParam(name = "type", required = false) RestCallParameterType type,
        @RequestParam(name = "url", required = false) String url,
        @ApiParam(value = "Parameter name")
        @RequestParam(name = "parameter", required = false) String parameter,
        @RequestParam(name = "dateTimeFrom", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTimeFrom,
        @RequestParam(name = "dateTimeTo", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTimeTo,
        @RequestParam(name="intersection", required=true,defaultValue="false") boolean intersection,
        @ModelAttribute RestCallParameterList parameterList,
        Pageable pageable,
        PagedResourcesAssembler assembler
    ) {
        RestCallRequest request = new RestCallRequest(
            url,
            Optional.ofNullable(type),
            Optional.ofNullable(parameter),
            dateTimeFrom,
            dateTimeTo
        );
        
        List<RestCallParameter> parameters = new ArrayList<RestCallParameter>();
        
        if(parameterList != null) {
        	if(parameterList.getParameters() != null)
	        	parameters.addAll(parameterList.getParameters());
        }     

        Page<KeyValueResultDto> page = restCallStatisticsService.getStatisticsByParameter(request, parameters, intersection,pageable);

        return new ResponseEntity<>(assembler.toResource(page, keyValueResultAssembler), HttpStatus.OK);
    }

    @ApiOperation(value = "REST Calls statistics by date")
    @RequestMapping(value = "/byDate", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public HttpEntity<PagedResources<KeyValueResultDto>> getStatisticsByDate(
        @ApiParam(value = "Parameter type", allowableValues = "PATH,QUERY,HEADER")
        @RequestParam(name = "type", required = false) RestCallParameterType type,
        @RequestParam(name = "url", required = false) String url,
        @ApiParam(value = "Parameter name")
        @RequestParam(name = "parameter", required = false) String parameter,
        @RequestParam(name = "dateTimeFrom", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTimeFrom,
        @RequestParam(name = "dateTimeTo", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTimeTo,
        @RequestParam(name="intersection", required=true,defaultValue="false") boolean intersection,
        @ModelAttribute RestCallParameterList parameterList,
        Pageable pageable,
        PagedResourcesAssembler assembler
    ) {
        RestCallRequest request = new RestCallRequest(
            url,
            Optional.ofNullable(type),
            Optional.ofNullable(parameter),
            dateTimeFrom,
            dateTimeTo
        );
        
        List<RestCallParameter> parameters = new ArrayList<RestCallParameter>();
        
        if(parameterList != null) {
        	if(parameterList.getParameters() != null)
	        	parameters.addAll(parameterList.getParameters());
        }     

        Page<KeyValueResultDto> page = restCallStatisticsService.getStatisticsByDate(request, parameters, intersection, pageable);

        return new ResponseEntity<>(assembler.toResource(page, keyValueResultAssembler), HttpStatus.OK);
    }

}
