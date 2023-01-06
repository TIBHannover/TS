package uk.ac.ebi.spot.ols.loader;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import uk.ac.ebi.spot.ols.config.OntologyLoadingConfiguration;
import uk.ac.ebi.spot.ols.config.OntologyResourceConfig;
import uk.ac.ebi.spot.ols.exception.OntologyLoadingException;
import uk.ac.ebi.spot.ols.xrefs.DatabaseService;

/**
 * @author Simon Jupp
 * @date 03/02/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public class SKOSLoader extends AbstractOWLOntologyLoader {
	OWLReasoner reasoner = null;
    public SKOSLoader(OntologyResourceConfig config, DatabaseService databaseService,
    		OntologyLoadingConfiguration ontologyLoadingConfiguration) throws OntologyLoadingException {
        super(config, databaseService, ontologyLoadingConfiguration);
    }
    public SKOSLoader(OntologyResourceConfig config) throws OntologyLoadingException {
        super(config);
    }
    @Override
    protected void discardReasoner(OWLOntology ontology) throws OWLOntologyCreationException {
    	reasoner = null;
    	System.gc();
    }
    @Override
    protected OWLReasoner getOWLReasoner(OWLOntology owlOntology) {
        if (reasoner == null) {
            getLogger().debug("Trying to create a reasoner over ontology '" + getOntologyIRI() + "'");
            OWLReasonerFactory factory = new StructuralReasonerFactory();
            reasoner = factory.createReasoner(owlOntology);
        }

         return reasoner;
     }
    }

