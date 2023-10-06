package uk.ac.ebi.spot.ols.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Component;

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
 * @author Erhun Giray TUNCAY
 * @date 05/07/2022
 * NFDI4ING Terminology Service Team, TIB
 */
@Component
public class MongoOntologyRepositoryService implements OntologyRepositoryService {

    @Autowired
    MongoOntologyRepository repositoryService;

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public Set<OntologyDocument> filter(Collection<String> schemas, Collection<String> classifications, boolean exclusive){
    	Set<OntologyDocument> tempSet = new HashSet<OntologyDocument>();
    	if(schemas != null && classifications != null)
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
	        } else if (exclusive && schemas != null && schemas.size() == 1 && classifications != null && classifications.size() == 1) {
                String schema = schemas.iterator().next();
                String classification = classifications.iterator().next();
                System.out.println("schema: "+schema);
                System.out.println("classification: "+classification);
                for (OntologyDocument ontologyDocument : repositoryService.findAll()){
                    for(Map<String, Collection<String>> classificationSchema : ontologyDocument.getConfig().getClassifications()){
                        if(classificationSchema.containsKey(schema))
                            if (classificationSchema.get(schema) != null)
                                if (!classificationSchema.get(schema).isEmpty()){
                                    for (String s :classificationSchema.get(schema))
                                        System.out.println(s);
                                    if(classificationSchema.get(schema).contains(classification))
                                        tempSet.add(ontologyDocument);
                                }

                    }
                }
            } else {
                for (OntologyDocument ontologyDocument : repositoryService.findAll()) {
                    Set<String> tempClassifications = new HashSet<String>();
                    if(ontologyDocument.getConfig().getClassifications() != null)
                        if (!ontologyDocument.getConfig().getClassifications().isEmpty()) {
                            for (Map<String, Collection<String>> classificationSchema : ontologyDocument.getConfig().getClassifications()) {
                                for (String schema : schemas)
                                    if (classificationSchema.containsKey(schema)) {
                                        for (String classification : classifications) {
                                            if (classificationSchema.get(schema) != null) {
                                                if (!classificationSchema.get(schema).isEmpty()) {
                                                    if (classificationSchema.get(schema).contains(classification)) {
                                                        tempClassifications.add(classification);
                                                    }
                                                }
                                            }
                                        }
                                    }
                            }
                            if (tempClassifications.containsAll(classifications))
                                tempSet.add(ontologyDocument);
                        }
                }
	        }
    	return tempSet;
    }

    @Override
    public List<OntologyDocument> getAllDocuments() {
        return repositoryService.findAll();
    }

    @Override
    public List<OntologyDocument> getAllDocuments(Collection<String> schemas, Collection<String> classifications, boolean exclusive) {
    	return new ArrayList<OntologyDocument>(filter(schemas,classifications,exclusive));
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
    	List<OntologyDocument> temp = new ArrayList<OntologyDocument>(filter(schemas,classifications,exclusive));

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

      	if(schemas == null || classifications == null)
      		return new SummaryInfo(new GregorianCalendar(1900, Calendar.JANUARY, 1).getTime(),ontologies,terms,properties,individuals,"");

    	Set<OntologyDocument> tempSet = filter(schemas,classifications,exclusive);
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
