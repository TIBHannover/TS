package uk.ac.ebi.spot.ols.entities;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "rest_call")
public class RestCall {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "url", nullable = false)
    private String url;

    @OneToMany(mappedBy = "restCall", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<RestCallParameter> parameters = new HashSet<>();

    @Column(name = "created_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

    public RestCall() {
    }

    public RestCall(String url) {
        this.url = url;
        this.createdAt = LocalDateTime.now();
    }

    public RestCall(String url,
                    Set<RestCallParameter> parameters) {
        this.url = url;
        this.parameters = parameters;
        this.createdAt = LocalDateTime.now();
    }

    public void addParameters(Set<RestCallParameter> set) {
        parameters.addAll(set);
        set.forEach(parameter -> parameter.setRestCall(this));
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<RestCallParameter> getParameters() {
        return parameters;
    }

    public void setParameters(Set<RestCallParameter> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "RestCall{" +
            "id=" + id +
            ", url='" + url + '\'' +
            ", parameters=" + parameters +
            ", createdAt=" + createdAt +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestCall restCall = (RestCall) o;
        return id.equals(restCall.id) && url.equals(restCall.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url);
    }
}
