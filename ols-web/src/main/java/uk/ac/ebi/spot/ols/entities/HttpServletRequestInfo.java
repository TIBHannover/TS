package uk.ac.ebi.spot.ols.entities;

import java.util.Set;

public class HttpServletRequestInfo {
    private String url;
    private Set<RestCallParameter> pathVariables;
    private Set<RestCallParameter> queryParameters;

    public HttpServletRequestInfo() {
    }

    public HttpServletRequestInfo(String url,
                                  Set<RestCallParameter> pathVariables,
                                  Set<RestCallParameter> queryParameters) {
        this.url = url;
        this.pathVariables = pathVariables;
        this.queryParameters = queryParameters;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Set<RestCallParameter> getPathVariables() {
        return pathVariables;
    }

    public void setPathVariables(Set<RestCallParameter> pathVariables) {
        this.pathVariables = pathVariables;
    }

    public Set<RestCallParameter> getQueryParameters() {
        return queryParameters;
    }

    public void setQueryParameters(Set<RestCallParameter> queryParameters) {
        this.queryParameters = queryParameters;
    }
}
