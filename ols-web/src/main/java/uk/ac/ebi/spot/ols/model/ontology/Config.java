package uk.ac.ebi.spot.ols.model.ontology;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Builder
@Value
public class Config {
    String id;
    String versionIri;
    String title;
    String namespace;
    String preferredPrefix;
    String description;
    String homepage;
    Object version;
    Object mailingList;
    Object tracker;
    Object logo;
    List<String> creators;
    Annotation annotation;
    String fileLocation;
    String reasonerType;
    boolean oboSlims;
    String labelProperty;
    List<String> definitionProperties;
    List<String> synonymProperties;
    List<String> hierarchicalProperties;
    List<Object> baseUris;
    List<Object> hiddenProperties;
    List<Object> preferredRootTerms;
    boolean allowDownload;
    List<Classification> classifications;
    List<Object> internalMetadataProperties;
    boolean skos;
}
