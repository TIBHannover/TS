package uk.ac.ebi.spot.ols.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.spot.ols.exception.OntologyLoadingException;
import uk.ac.ebi.spot.ols.exception.ConfigParsingException;
import uk.ac.ebi.spot.ols.loader.*;
import uk.ac.ebi.spot.ols.util.OntologyMetaData;

/**
 * @author Simon Jupp
 * @date 09/07/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 *
 * Abstract loading service that creates an ontology loader given an Ontology configuration document (OntologyResourceLoader)
 *
 */
public abstract class AbstractLoadingService implements DocumentLoadingService {

    private Logger log = LoggerFactory.getLogger(getClass());

    public Logger getLog() {
        return log;
    }

    @Override
    public OntologyLoader getLoader() throws OntologyLoadingException {

        try {
            OntologyResourceConfig config = getConfiguration();
            getLog().info("Initial loader " + config.getId() + " - " + config.getTitle());
        // extract embedded metadata for basic config fields.
        OntologyMetaData omd = new OntologyMetaData(config.getId(),true);
        if(config.getDescription() == null)
        if(omd.getDescription() != null)
            config.setDescription(omd.getDescription());
        if(config.getHomepage() == null)
        if(omd.getHomepage() != null)
            config.setHomepage(omd.getHomepage());
        if(config.getVersion() == null)
        if(omd.getVersion() != null)
            config.setVersion(omd.getVersion());
        if(config.getTitle() == null)
        if(omd.getTitle() != null)
            config.setTitle(omd.getTitle());
        if(config.getMailingList() == null)
        if(omd.getEmail() != null)
            config.setMailingList(omd.getEmail());                  
            
            getLog().info("Starting up loader with " + config.getId() + " - " + config.getTitle());

            return  OntologyLoaderFactory.getLoader(config);
        } catch (ConfigParsingException e) {
            throw new OntologyLoadingException("Can't get configuration for loader: " + e.getMessage(), e);
        }

    }


    public abstract OntologyResourceConfig getConfiguration () throws ConfigParsingException;
}

