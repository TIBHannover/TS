package uk.ac.ebi.spot.ols.service.impl;

import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

@Slf4j
@Builder
@Value
@Component
@ConfigurationProperties(prefix = "similarity")
public class SimilaritySettings {
    Map<String, Double> weight;

    @PostConstruct
    public void init() {
        double sum = weight.values().stream()
            .mapToDouble(value -> value)
            .sum();

        if (sum != 1.0) {
            log.error("Sum of all weights must be equal to 1.");
        }
    }
}
