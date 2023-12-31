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
import java.util.Set;

/**
 * @author Erhun Giray TUNCAY 
 * @date 05/07/2022
 * NFDI4ING Terminology Service Team, TIB
 */
public interface OntologyRepositoryService {
	
	Set<OntologyDocument> filter(Collection<String> schemas, Collection<String> classifications, boolean exclusive);

    List<OntologyDocument> getAllDocuments();
    
    List<OntologyDocument> getAllDocuments(Collection<String> schemas, Collection<String> classifications, boolean exclusive);

    List<OntologyDocument> getAllDocuments(Sort sort);

    Page<OntologyDocument> getAllDocuments(Pageable pageable);
    
    Page<OntologyDocument> getAllDocuments(Pageable pageable, Collection<String> schemas, Collection<String> classifications, boolean exclusive);

    List<OntologyDocument> getAllDocumentsByStatus(Status status);

    List<OntologyDocument> getAllDocumentsByStatus(Status status, Sort sort);

    void delete(OntologyDocument document) throws OntologyRepositoryException;

    OntologyDocument create(OntologyDocument document) throws OntologyRepositoryException;

    OntologyDocument update(OntologyDocument document) throws OntologyRepositoryException;

    OntologyDocument get(String documentId);

    Date getLastUpdated();

    int getNumberOfOntologies();
    
    SummaryInfo getClassificationMetadata(Collection<String> schemas, Collection<String> classifications, boolean exclusive);

    int getNumberOfTerms();

    int getNumberOfProperties();

    int getNumberOfIndividuals();
}
