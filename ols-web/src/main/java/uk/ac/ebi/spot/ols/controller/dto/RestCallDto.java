package uk.ac.ebi.spot.ols.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import uk.ac.ebi.spot.ols.entities.RestCall;

import java.time.LocalDateTime;

public class RestCallDto {
    private String address;
    private String parameters;
    private String keyValueParameters;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

    public RestCallDto() {
    }

    public RestCallDto(String address, String parameters, String keyValueParameters, LocalDateTime createdAt) {
        this.address = address;
        this.parameters = parameters;
        this.keyValueParameters = keyValueParameters;
        this.createdAt = createdAt;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getKeyValueParameters() {
        return keyValueParameters;
    }

    public void setKeyValueParameters(String keyValueParameters) {
        this.keyValueParameters = keyValueParameters;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static RestCallDto of(RestCall restCall) {
        return new RestCallDto(
            restCall.getAddress(),
            restCall.getParameters(),
            restCall.getKeyValueParameters(),
            restCall.getCreatedAt()
        );
    }
}
