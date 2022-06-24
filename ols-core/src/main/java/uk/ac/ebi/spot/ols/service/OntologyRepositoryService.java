package uk.ac.ebi.spot.ols.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import uk.ac.ebi.spot.ols.exception.OntologyRepositoryException;
import uk.ac.ebi.spot.ols.model.OntologyDocument;
import uk.ac.ebi.spot.ols.model.Status;
import uk.ac.ebi.spot.ols.model.SummaryInfo;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author Simon Jupp
 * @date 29/01/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 *
 * Provides access to a repository of ontology documents
 *
 */
public interface OntologyRepositoryService {

    List<OntologyDocument> getAllDocuments();
    
    List<OntologyDocument> getAllDocuments(Collection<String> schemas, Collection<String> classifications);

    List<OntologyDocument> getAllDocuments(Sort sort);

    Page<OntologyDocument> getAllDocuments(Pageable pageable);
    
    Page<OntologyDocument> getAllDocuments(Pageable pageable, Collection<String> schemas, Collection<String> classifications);

    List<OntologyDocument> getAllDocumentsByStatus(Status status);

    List<OntologyDocument> getAllDocumentsByStatus(Status status, Sort sort);

    void delete(OntologyDocument document) throws OntologyRepositoryException;

    OntologyDocument create(OntologyDocument document) throws OntologyRepositoryException;

    OntologyDocument update(OntologyDocument document) throws OntologyRepositoryException;

    OntologyDocument get(String documentId);

    Date getLastUpdated();

    int getNumberOfOntologies();
    
    SummaryInfo getClassificationMetadata(Collection<String> schemas, Collection<String> classifications);

    int getNumberOfTerms();

    int getNumberOfProperties();

    int getNumberOfIndividuals();
}
