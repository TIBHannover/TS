package uk.ac.ebi.spot.ols.controller.dto;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class RatioDto {
    double result;
    int similaritiesNumber;
    double distinctCharacteristicsNumber;
}
