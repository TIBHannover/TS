package uk.ac.ebi.spot.ols.service;

import org.apache.jena.ontology.OntModel;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public interface OntologyReadService {
    OWLOntology readOntologyWithOwlApi(String uri) throws OWLOntologyCreationException;

    OntModel readOntologyWithJenaApi(String uri) throws OWLOntologyCreationException;
}
