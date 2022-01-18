package uk.ac.ebi.spot.ols.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ols.controller.dto.KeyValueResultDto;
import uk.ac.ebi.spot.ols.controller.dto.RestCallCountResultDto;
import uk.ac.ebi.spot.ols.controller.dto.RestCallDto;
import uk.ac.ebi.spot.ols.controller.dto.RestCallRequest;
import uk.ac.ebi.spot.ols.service.RestCallService;
import uk.ac.ebi.spot.ols.service.RestCallStatisticsService;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RestCallStatisticsServiceImpl implements RestCallStatisticsService {
    public static final int MAX_PAGE_SIZE = 2147483647;
    private final RestCallService restCallService;

    @Autowired
    public RestCallStatisticsServiceImpl(RestCallService restCallService) {
        this.restCallService = restCallService;
    }

    @Override
    public RestCallCountResultDto getRestCallsCountsByAddress(RestCallRequest request) {
        PageRequest pageRequest = new PageRequest(0, MAX_PAGE_SIZE);
        Page<RestCallDto> page = restCallService.getList(request, pageRequest);

        Map<String, Long> countsMap = getCountsMap(page);

        List<KeyValueResultDto> list = countsMap.entrySet().stream()
            .map(entry -> new KeyValueResultDto(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());

        return new RestCallCountResultDto(list);
    }

    @Override
    public KeyValueResultDto getRestCallsTotalCount(RestCallRequest request) {
        PageRequest pageRequest = new PageRequest(0, MAX_PAGE_SIZE);
        Page<RestCallDto> page = restCallService.getList(request, pageRequest);

        Map<String, Long> countsMap = getCountsMap(page);

        long sum = countsMap.values().stream()
            .mapToLong(value -> value)
            .sum();

        return new KeyValueResultDto("total", sum);
    }

    @Override
    public RestCallCountResultDto getRestCallsCountsByParameter(RestCallRequest request) {
        PageRequest pageRequest = new PageRequest(0, MAX_PAGE_SIZE);
        Page<RestCallDto> page = restCallService.getList(request, pageRequest);

        Map<String, Long> parametersWithCountsMap = page.getContent().stream()
            .flatMap(restCallDto -> Arrays.stream(restCallDto.getParameters().split(",")))
            .sorted()
            .collect(
                Collectors.groupingBy(
                    s -> s,
                    Collectors.counting()
                )
            )
            .entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (oldValue, newValue) -> oldValue,
                    LinkedHashMap::new
                )
            );

        List<KeyValueResultDto> list = parametersWithCountsMap.entrySet().stream()
            .map(entry -> new KeyValueResultDto(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());

        return new RestCallCountResultDto(list);
    }

    private Map<String, Long> getCountsMap(Page<RestCallDto> page) {
        Map<String, Long> addressesWithCountsMap = page.getContent().stream()
            .collect(
                Collectors.groupingBy(
                    RestCallDto::getAddress,
                    Collectors.counting()
                )
            );

        return addressesWithCountsMap.entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (oldValue, newValue) -> oldValue,
                    LinkedHashMap::new
                )
            );
    }
}
