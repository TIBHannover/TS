package uk.ac.ebi.spot.ols.model.chart;

import lombok.Builder;
import lombok.Value;

import java.util.Optional;

@Builder
@Value
public class ChartRequest {
    Optional<Boolean> horizontal;
    Optional<Integer> height;
    Optional<Integer> width;
}
