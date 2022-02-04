package uk.ac.ebi.spot.ols.controller.dto;

import org.springframework.format.annotation.DateTimeFormat;
import uk.ac.ebi.spot.ols.entities.RestCall;
import uk.ac.ebi.spot.ols.entities.RestCallParameter;

import java.time.LocalDateTime;
import java.util.Set;

public class RestCallDto {
    private Long id;
    private String url;
    private Set<RestCallParameter> parameters;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

    public RestCallDto() {
    }

    public RestCallDto(Long id,
                       String url,
                       Set<RestCallParameter> parameters,
                       LocalDateTime createdAt) {
        this.id = id;
        this.url = url;
        this.parameters = parameters;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Set<RestCallParameter> getParameters() {
        return parameters;
    }

    public void setParameters(Set<RestCallParameter> parameters) {
        this.parameters = parameters;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static RestCallDto of(RestCall restCall) {
        return new RestCallDto(
            restCall.getId(),
            restCall.getUrl(),
            restCall.getParameters(),
            restCall.getCreatedAt()
        );
    }
}
