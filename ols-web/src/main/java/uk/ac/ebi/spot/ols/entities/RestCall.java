package uk.ac.ebi.spot.ols.entities;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "rest_call")
public class RestCall {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "parameters")
    private String parameters;

    @Column(name = "key_value_parameters")
    private String keyValueParameters;

    @Column(name = "created_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

    public RestCall() {
    }

    public RestCall(String address, String parameters, String keyValueParameters) {
        this.address = address;
        this.parameters = parameters;
        this.keyValueParameters = keyValueParameters;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "RestCall{" +
            "id=" + id +
            ", address='" + address + '\'' +
            ", parameters='" + parameters + '\'' +
            ", createdAt=" + createdAt +
            '}';
    }
}
