package uk.ac.ebi.spot.ols.model;

import java.util.Date;

public class SummaryInfo {
    Date lastUpdated;
    int numberOfOntologies;
    int numberOfTerms;
    int numberOfProperties;
    int numberOfIndividuals;
    String softwareVersion;

    public SummaryInfo(Date lastUpdated, int numberOfOntologies, int numberOfTerms, int numberOfProperties, int numberOfIndividuals, String softwareVersion) {
        this.lastUpdated = lastUpdated;
        this.numberOfOntologies = numberOfOntologies;
        this.numberOfTerms = numberOfTerms;
        this.numberOfProperties = numberOfProperties;
        this.numberOfIndividuals = numberOfIndividuals;
        this.softwareVersion = softwareVersion;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getNumberOfOntologies() {
        return numberOfOntologies;
    }

    public void setNumberOfOntologies(int numberOfOntologies) {
        this.numberOfOntologies = numberOfOntologies;
    }

    public int getNumberOfTerms() {
        return numberOfTerms;
    }

    public void setNumberOfTerms(int numberOfTerms) {
        this.numberOfTerms = numberOfTerms;
    }

    public int getNumberOfProperties() {
        return numberOfProperties;
    }

    public void setNumberOfProperties(int numberOfProperties) {
        this.numberOfProperties = numberOfProperties;
    }

    public int getNumberOfIndividuals() {
        return numberOfIndividuals;
    }

    public void setNumberOfIndividuals(int numberOfIndividuals) {
        this.numberOfIndividuals = numberOfIndividuals;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }
}
