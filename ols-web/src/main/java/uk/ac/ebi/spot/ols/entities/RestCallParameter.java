package uk.ac.ebi.spot.ols.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "rest_call_parameter")
public class RestCallParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;
    private String value;

    @Enumerated(EnumType.STRING)
    private RestCallParameterType parameterType;

    @ManyToOne
    @JoinColumn(name = "rest_call_id")
    private RestCall restCall;

    public RestCallParameter() {
    }

    public RestCallParameter(String name, String value, RestCallParameterType parameterType) {
        this.name = name;
        this.value = value;
        this.parameterType = parameterType;
    }

    public RestCallParameter(Long id, String name, String value, RestCallParameterType parameterType, RestCall restCall) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.parameterType = parameterType;
        this.restCall = restCall;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public RestCall getRestCall() {
        return restCall;
    }

    public void setRestCall(RestCall restCall) {
        this.restCall = restCall;
    }

    public RestCallParameterType getParameterType() {
        return parameterType;
    }

    public void setParameterType(RestCallParameterType parameterType) {
        this.parameterType = parameterType;
    }

    @Override
    public String toString() {
        return "RestCallParameter{" +
            "id='" + id + '\'' +
            ", parameterType='" + parameterType + '\'' +
            ", name='" + name + '\'' +
            ", value='" + value + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestCallParameter that = (RestCallParameter) o;
        return id.equals(that.id) && name.equals(that.name) && Objects.equals(value, that.value) && parameterType == that.parameterType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, value, parameterType);
    }

    @Transient
    @JsonIgnore
    public boolean isPathType() {
        return RestCallParameterType.PATH.equals(this.parameterType);
    }

    @Transient
    @JsonIgnore
    public boolean isQueryType() {
        return RestCallParameterType.QUERY.equals(this.parameterType);
    }
}
