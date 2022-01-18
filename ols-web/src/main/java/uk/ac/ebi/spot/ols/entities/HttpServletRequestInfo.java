package uk.ac.ebi.spot.ols.entities;

public class HttpServletRequestInfo {
    private String address;
    private String parameters;
    private String keyValueParameters;

    public HttpServletRequestInfo() {
    }

    public HttpServletRequestInfo(String address, String parameters, String keyValueParameters) {
        this.address = address;
        this.parameters = parameters;
        this.keyValueParameters = keyValueParameters;
    }

    public String getAddress() {
        return address;
    }

    public String getParameters() {
        return parameters;
    }

    public String getKeyValueParameters() {
        return keyValueParameters;
    }

    public void setKeyValueParameters(String keyValueParameters) {
        this.keyValueParameters = keyValueParameters;
    }
}
