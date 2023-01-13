package uk.ac.ebi.spot.ols.service;

import org.apache.jena.ontology.OntModel;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Set;

public interface OntologyTraverseService {
    Set<String> getImports(OWLOntology ontology);

    Set<String> getNamespaces(OWLOntology ontology);

    Set<String> getProperties(OntModel model);

    Set<String> getIndividuals(OWLOntology ontology);

    Set<String> getClasses(OntModel model);

    Set<String> getClasses(OWLOntology ontology);
}
