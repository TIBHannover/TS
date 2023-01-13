package uk.ac.ebi.spot.ols.model.error;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Value
@Builder
public class ErrorResponse {

    String timestamp = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now());
    int status;
    String error;
    String message;
    String path;
}
