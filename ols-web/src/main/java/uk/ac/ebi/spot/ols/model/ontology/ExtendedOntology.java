package uk.ac.ebi.spot.ols.model.ontology;

import java.util.Set;

public interface ExtendedOntology extends Ontology {
    Set<String> getProperties();

    Set<String> getClasses();

    Set<String> getImports();

    Set<String> getNamespaces();

    Set<String> getIndividuals();

    Set<String> getCollection();
}
