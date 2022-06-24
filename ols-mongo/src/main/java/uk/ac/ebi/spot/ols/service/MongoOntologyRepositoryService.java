package uk.ac.ebi.spot.ols.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import com.mongodb.MongoTimeoutException;

import uk.ac.ebi.spot.ols.exception.OntologyRepositoryException;
import uk.ac.ebi.spot.ols.model.OntologyDocument;
import uk.ac.ebi.spot.ols.model.Status;
import uk.ac.ebi.spot.ols.model.SummaryInfo;
import uk.ac.ebi.spot.ols.repository.mongo.MongoOntologyRepository;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Simon Jupp
 * @date 04/03/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@Component
public class MongoOntologyRepositoryService implements OntologyRepositoryService {

    @Autowired
    MongoOntologyRepository repositoryService;

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public List<OntologyDocument> getAllDocuments() {
        return repositoryService.findAll();
    }
    
    @Override
    public List<OntologyDocument> getAllDocuments(Collection<String> schemas, Collection<String> classifications, boolean exclusive) {
       	 Set<OntologyDocument> tempSet = new HashSet<OntologyDocument>();
         if(!exclusive) {
        	 for (OntologyDocument ontologyDocument : repositoryService.findAll()) {
         		for(Map<String, Collection<String>> classificationSchema : ontologyDocument.getConfig().getClassifications()) {
         			for (String schema: schemas)
         			    if(classificationSchema.containsKey(schema))
         				    for (String classification: classifications) {
         				    	if (classificationSchema.get(schema) != null)
         				    		if (!classificationSchema.get(schema).isEmpty())
         				    	        if (classificationSchema.get(schema).contains(classification)) {
         					                tempSet.add(ontologyDocument);
         				  }
         				    }
         			    
         			}
     		} 
         } else {	 
        	 for (OntologyDocument ontologyDocument : repositoryService.findAll()) {
          		boolean toBeAdded = true;
        		 for(Map<String, Collection<String>> classificationSchema : ontologyDocument.getConfig().getClassifications()) {
          			for (String schema: schemas)
          			    if(classificationSchema.containsKey(schema)) {
          				    for (String classification: classifications) {
          				    	if (classificationSchema.get(schema) != null) {
          				    		if (!classificationSchema.get(schema).isEmpty()) {
          				    	        if (!classificationSchema.get(schema).contains(classification)) {
          				    	        	toBeAdded = false;
          				    	        }
          				    		} else toBeAdded = false;
          				         } else toBeAdded = false;
          				    }
          			    } else toBeAdded = false;
     			    
          			}
        		 if(toBeAdded)
        			 tempSet.add(ontologyDocument); 
      		} 
        	 
         }   
    	
    	return new ArrayList<>(tempSet);
    }

    @Override
    public List<OntologyDocument> getAllDocuments(Sort sort) {
        return repositoryService.findAll(sort);
    }

    @Override
    public Page<OntologyDocument> getAllDocuments(Pageable pageable) {
        return repositoryService.findAll(pageable);
    }
    
    @Override
    public Page<OntologyDocument> getAllDocuments(Pageable pageable, Collection<String> schemas, Collection<String> classifications, boolean exclusive) {
    	Set<OntologyDocument> tempSet = new HashSet<OntologyDocument>();
    	Page<OntologyDocument> documents = getAllDocuments(pageable);
     	
     	while(documents.hasNext()) {
     		documents = getAllDocuments(pageable);
     		
            if(!exclusive) {
              	 for (OntologyDocument ontologyDocument : documents.getContent()) {
               		for(Map<String, Collection<String>> classificationSchema : ontologyDocument.getConfig().getClassifications()) {
               			for (String schema: schemas)
               			    if(classificationSchema.containsKey(schema))
               				    for (String classification: classifications) {
               				    	if (classificationSchema.get(schema) != null)
               				    		if (!classificationSchema.get(schema).isEmpty())
               				    	        if (classificationSchema.get(schema).contains(classification)) {
               					                tempSet.add(ontologyDocument);
               				  }
               				    }
               			    
               			}
           		} 
               } else {	 
              	 for (OntologyDocument ontologyDocument : documents.getContent()) {
                		boolean toBeAdded = true;
              		 for(Map<String, Collection<String>> classificationSchema : ontologyDocument.getConfig().getClassifications()) {
                			for (String schema: schemas)
                			    if(classificationSchema.containsKey(schema)) {
                				    for (String classification: classifications) {
                				    	if (classificationSchema.get(schema) != null) {
                				    		if (!classificationSchema.get(schema).isEmpty()) {
                				    	        if (!classificationSchema.get(schema).contains(classification)) {
                				    	        	toBeAdded = false;
                				    	        }
                				    		} else toBeAdded = false;
                				         } else toBeAdded = false;
                				    }
                			    } else toBeAdded = false;
           			    
                			}
              		 if(toBeAdded)
              			 tempSet.add(ontologyDocument); 
            		} 
              	 
               }   
     		
     	}
     	
     	List<OntologyDocument> temp = new ArrayList<>(tempSet);
     	
     	final int start = (int)pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), temp.size());
        Page<OntologyDocument> tempDocuments = new PageImpl<>(temp.subList(start, end), pageable, temp.size());
  	
        return tempDocuments;
    }

    @Override
    public List<OntologyDocument> getAllDocumentsByStatus(Status status) {
        return getAllDocumentsByStatus(status, new Sort(new Sort.Order(Sort.Direction.ASC, "ontologyId")));
    }

    @Override
    public List<OntologyDocument> getAllDocumentsByStatus(Status status, Sort sort) {
        return repositoryService.findByStatus(status, sort);
    }

    @Override
    public void delete(OntologyDocument document) throws OntologyRepositoryException {
        repositoryService.delete(document);
    }

    @Override
    public OntologyDocument create(OntologyDocument document) throws OntologyRepositoryException {
        return repositoryService.save(document);
    }

    @Override
    public OntologyDocument update(OntologyDocument document) throws OntologyRepositoryException {
        return repositoryService.save(document);
    }

    @Override
    public OntologyDocument get(String documentId) {
        return repositoryService.findByOntologyId(documentId);
    }

    @Override
    public Date getLastUpdated() {
        OntologyDocument document = repositoryService.findAll(new Sort(new Sort.Order(Sort.Direction.DESC, "updated"))).get(0);
        return document.getUpdated();
    }

    @Override
    public int getNumberOfOntologies() {
        return repositoryService.findAll().size();
    }

    @Override
    public int getNumberOfTerms() {
        Aggregation agg =
                Aggregation.newAggregation(
                        group("ANYTHING").sum("numberOfTerms").as("total"),
                        project("total")
                );
        //Convert the aggregation result into a List
        AggregationResults<AggregateResult> groupResults
                = mongoTemplate.aggregate(agg, "olsadmin", AggregateResult.class);
        AggregateResult result = groupResults.getUniqueMappedResult();
        return result.getTotal();
    }
    
    @Override
    public SummaryInfo getClassificationMetadata(Collection<String> schemas, Collection<String> classifications, boolean exclusive) {
      	int ontologies = 0;
      	int terms = 0;
      	int properties = 0;
      	int individuals = 0;
      	
    	Set<OntologyDocument> tempSet = new HashSet<OntologyDocument>();
        if(!exclusive) {
       	 for (OntologyDocument ontologyDocument : repositoryService.findAll()) {
        		for(Map<String, Collection<String>> classificationSchema : ontologyDocument.getConfig().getClassifications()) {
        			for (String schema: schemas)
        			    if(classificationSchema.containsKey(schema))
        				    for (String classification: classifications) {
        				    	if (classificationSchema.get(schema) != null)
        				    		if (!classificationSchema.get(schema).isEmpty())
        				    	        if (classificationSchema.get(schema).contains(classification)) {
        					                tempSet.add(ontologyDocument);
        				  }
        				    }
        			    
        			}
    		} 
        } else {	 
       	 for (OntologyDocument ontologyDocument : repositoryService.findAll()) {
         		boolean toBeAdded = true;
       		 for(Map<String, Collection<String>> classificationSchema : ontologyDocument.getConfig().getClassifications()) {
         			for (String schema: schemas)
         			    if(classificationSchema.containsKey(schema)) {
         				    for (String classification: classifications) {
         				    	if (classificationSchema.get(schema) != null) {
         				    		if (!classificationSchema.get(schema).isEmpty()) {
         				    	        if (!classificationSchema.get(schema).contains(classification)) {
         				    	        	toBeAdded = false;
         				    	        }
         				    		} else toBeAdded = false;
         				         } else toBeAdded = false;
         				    }
         			    } else toBeAdded = false;
    			    
         			}
       		 if(toBeAdded)
       			 tempSet.add(ontologyDocument); 
     		} 
       	 
        }   
	   	 Date lastUpdated = new GregorianCalendar(1900, Calendar.JANUARY, 1).getTime();
	   	 for (OntologyDocument document : tempSet) {
	   		 ontologies+=1;
	   		 terms+=document.getNumberOfTerms();
	   		 properties+=document.getNumberOfProperties();
	   		 individuals+=document.getNumberOfIndividuals();
	   		 if(document.getLoaded()!= null)
	   		     if(document.getLoaded().after(lastUpdated))
	   			     lastUpdated = document.getLoaded();
	   	 }
	   	 
	   	 return new SummaryInfo(lastUpdated,ontologies,terms,properties,individuals,"");
    }

    @Override
    public int getNumberOfProperties() {
        Aggregation agg =
                Aggregation.newAggregation(
                        group("ANYTHING").sum("numberOfProperties").as("total"),
                        project("total")
                );
        //Convert the aggregation result into a List
        AggregationResults<AggregateResult> groupResults
                = mongoTemplate.aggregate(agg, "olsadmin", AggregateResult.class);
        AggregateResult result = groupResults.getUniqueMappedResult();
        return result.getTotal();

    }

    @Override
    public int getNumberOfIndividuals() {
        Aggregation agg =
                Aggregation.newAggregation(
                        group("ANYTHING").sum("numberOfIndividuals").as("total"),
                        project("total")
                );
        //Convert the aggregation result into a List
        AggregationResults<AggregateResult> groupResults
                = mongoTemplate.aggregate(agg, "olsadmin", AggregateResult.class);
        AggregateResult result = groupResults.getUniqueMappedResult();
        return result.getTotal();
    }

    private class AggregateResult {
        int total;

        public int getTotal() {
            return total;
        }
    }

}
