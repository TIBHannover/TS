package uk.ac.ebi.spot.ols.service.impl;

import uk.ac.ebi.spot.ols.model.ontology.OntologyType;
import uk.ac.ebi.spot.ols.service.OntologyReadService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.jfree.util.Log;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OntologyReadServiceImpl implements OntologyReadService {
    private static final int FILE_EXTENSION_LENGTH = 3;

    public OntModel readOntologyWithJenaApi(String uri) {
        OntModel model = ModelFactory.createOntologyModel();
        OntologyType type = OntologyType.get(StringUtils.right(uri, FILE_EXTENSION_LENGTH));
        model.read(uri, type.getName());
        Log.info("starting main service");

        return model;
    }

    @Override
    public OWLOntology readOntologyWithOwlApi(String uri) throws OWLOntologyCreationException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

        return manager.loadOntology(IRI.create(uri));
    }
}
