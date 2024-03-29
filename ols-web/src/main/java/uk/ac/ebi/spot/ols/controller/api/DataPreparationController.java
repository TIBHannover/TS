package uk.ac.ebi.spot.ols.controller.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import uk.ac.ebi.spot.ols.model.OntologyDocument;
import uk.ac.ebi.spot.ols.neo4j.model.Individual;
import uk.ac.ebi.spot.ols.neo4j.model.Property;
import uk.ac.ebi.spot.ols.neo4j.model.Term;
import uk.ac.ebi.spot.ols.neo4j.service.OntologyIndividualService;
import uk.ac.ebi.spot.ols.neo4j.service.OntologyPropertyGraphService;
import uk.ac.ebi.spot.ols.neo4j.service.OntologyTermGraphService;
import uk.ac.ebi.spot.ols.service.OntologyRepositoryService;


/**
 * @author Erhun Giray TUNCAY 
 * @date 06/07/2022
 * NFDI4ING Terminology Service Team, TIB
 */
@Controller
@RequestMapping("/api/datapreparation")
@Api(value = "datapreparation", description = "The Properties, Terms and Individuals in a particular context such as an ontology or a classification")
public class DataPreparationController {
	
    @Autowired
    private OntologyRepositoryService ontologyRepositoryService;

    @Autowired
    private OntologyPropertyGraphService ontologyPropertyGraphService;
    
    @Autowired
    private OntologyIndividualService ontologyIndividualService;
    
    @Autowired
    private OntologyTermGraphService ontologyTermGraphService;
    
    @Autowired
    PropertyAssembler termAssembler;

    @ApiOperation(value = "Retrieves all terms, properties and individuals in JSON format.", notes = "Possible schema keys and possible classification values of particular keys can be inquired with /api/ontologies/schemakeys and /api/ontologies/schemavalues methods respectively. If no ontology, schema and classification is specified, everything is retrieved without any filter.")
    @RequestMapping(path = "/extendedsearch", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<List<Object>> getEverythingByOntology(
            @RequestParam(value = "ontology_id", required = false) Collection<String> ontologies,
    		@RequestParam(value = "schema", required = false) Collection<String> schemas,
    		@RequestParam(value = "classification", required = false) Collection<String> classifications,
    		@ApiParam(value = "Set to true (default setting is false) for intersection (default behavior is union) of classifications.")
    		@RequestParam(value = "exclusive", required = false, defaultValue = "false") boolean exclusive,
            @ApiParam(value = "Page Size", required = true)
            @RequestParam(value = "page_size", required = true, defaultValue = "20") Integer pageSize) {

    	Pageable pageable = new PageRequest(0, pageSize);
    	List<Object> everything = new ArrayList<Object>();
    	
    	Set<OntologyDocument> tempSet;
    	if (ontologies == null && schemas == null && classifications == null) {
    		tempSet = new HashSet<OntologyDocument>(ontologyRepositoryService.getAllDocuments());
    	} else {
        	tempSet = ontologyRepositoryService.filter(schemas, classifications, exclusive);

        	if (ontologies != null)
        	if(ontologies.size()>=1)
        		for (String ontology : ontologies)
        	{
        		ontology = ontology.toLowerCase();
        		tempSet.add(ontologyRepositoryService.get(ontology));
        	}	
    	}	

	  	 for (OntologyDocument document : tempSet ) {
	   		 
	         Page<Property> properties = ontologyPropertyGraphService.findAllByOntology(document.getOntologyId(), pageable);
	         
	     	everything.addAll(properties.getContent());
	     	
	     	while(properties.hasNext()) {
	     		properties = ontologyPropertyGraphService.findAllByOntology(document.getOntologyId(), pageable);
	     		everything.addAll(properties.getContent());
	     	}
	
	         Page<Term> terms = ontologyTermGraphService.findAllByOntology(document.getOntologyId(), pageable);
	         
	     	everything.addAll(terms.getContent());
	     	
	     	while(terms.hasNext()) {
	     		terms = ontologyTermGraphService.findAllByOntology(document.getOntologyId(), pageable);
	     		everything.addAll(terms.getContent());
	     	}
	         
	         Page<Individual> individuals = ontologyIndividualService.findAllByOntology(document.getOntologyId(), pageable);  
	         
	     	everything.addAll(individuals.getContent());
	     	
	     	while(individuals.hasNext()) {
	     		individuals = ontologyIndividualService.findAllByOntology(document.getOntologyId(), pageable);
	     		everything.addAll(individuals.getContent());
	     	}
	  	 
	  	 }

    	return new ResponseEntity<>(everything, HttpStatus.OK);
    }
    
    @ApiOperation(value = "Retrieves basic information about all terms, properties and individuals including their annotations in JSON format.", notes = "Possible schema keys and possible classification values of particular keys can be inquired with /api/ontologies/schemakeys and /api/ontologies/schemavalues methods respectively. If no ontology, schema and classification is specified, everything is retrieved without any filter.")
    @RequestMapping(path = "/basictermsearch", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<List<BasicTerm>> getBasicTermsByOntology(
            @RequestParam(value = "ontology_id", required = false) Collection<String> ontologies,
    		@RequestParam(value = "schema", required = false) Collection<String> schemas,
    		@RequestParam(value = "classification", required = false) Collection<String> classifications,
    		@ApiParam(value = "Set to true (default setting is false) for intersection (default behavior is union) of classifications.")
    		@RequestParam(value = "exclusive", required = false, defaultValue = "false") boolean exclusive,
            @ApiParam(value = "Page Size", required = true)
            @RequestParam(value = "page_size", required = false, defaultValue = "20") Integer pageSize) {
    	
    	Pageable pageable = new PageRequest(0, pageSize);
    	List<BasicTerm> everything = new ArrayList<BasicTerm>();
    	Set<OntologyDocument> tempSet;
    	if (ontologies == null && schemas == null && classifications == null) {
    		tempSet = new HashSet<OntologyDocument>(ontologyRepositoryService.getAllDocuments());
    	} else {
        	tempSet = ontologyRepositoryService.filter(schemas, classifications, exclusive);

        	if (ontologies != null)
        	if(ontologies.size()>=1)
        		for (String ontology : ontologies)
        	{
        		ontology = ontology.toLowerCase();
        		tempSet.add(ontologyRepositoryService.get(ontology));
        	}	
    	}	
    	
	   	 for (OntologyDocument document : tempSet ) {
	   		 System.out.println("starting ontology: "+document.getOntologyId());
	         Page<Property> properties = ontologyPropertyGraphService.findAllByOntology(document.getOntologyId(), pageable);
	         
	 		for (Property property : properties.getContent()) {
	 			everything.add(new BasicTerm(property));
	 		}
	     	
	     	while(properties.hasNext()) {
	     		properties = ontologyPropertyGraphService.findAllByOntology(document.getOntologyId(), pageable);
	     		
	     		for (Property property : properties.getContent()) {
	     			everything.add(new BasicTerm(property));
	     		}
	
	     	}
	     	System.out.println("properties of "+document.getOntologyId()+" finished!");
	         Page<Term> terms = ontologyTermGraphService.findAllByOntology(document.getOntologyId(), pageable);
	         
	 		for (Term term : terms.getContent()) {
	 			everything.add(new BasicTerm(term));
	 		}
	     	
	     	while(terms.hasNext()) {
	     		terms = ontologyTermGraphService.findAllByOntology(document.getOntologyId(), pageable);
	     		for (Term term : terms.getContent()) {
	     			everything.add(new BasicTerm(term));
	     		}
	     	}
	     	System.out.println("terms of "+document.getOntologyId()+" finished!");
	         Page<Individual> individuals = ontologyIndividualService.findAllByOntology(document.getOntologyId(), pageable);  
	         
	 		for (Individual individual : individuals.getContent()) {
	 			everything.add(new BasicTerm(individual));
	 		}
	     	
	     	while(individuals.hasNext()) {
	     		individuals = ontologyIndividualService.findAllByOntology(document.getOntologyId(), pageable);
	     		for (Individual individual : individuals.getContent()) {
	     			everything.add(new BasicTerm(individual));
	     		}
	     	}
	     	System.out.println("individuals  of "+document.getOntologyId()+" finished!");
	   	 }

        return new ResponseEntity<>(everything, HttpStatus.OK);
    }
    
    
    @ApiOperation(value = "Retrieves the set of all annotation keys of terms, properties and individuals for the ontology and classification selection in JSON format.", notes = "Possible schema keys and possible classification values of particular keys can be inquired with /api/ontologies/schemakeys and /api/ontologies/schemavalues methods respectively. If no ontology, schema and classification is specified, everything is retrieved without any filter.")
    @RequestMapping(path = "/getannotationkeys", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<Set<String>> getAnnotationKeySetByOntology(
            @RequestParam(value = "ontology_id", required = false) Collection<String> ontologies,
    		@RequestParam(value = "schema", required = false) Collection<String> schemas,
    		@RequestParam(value = "classification", required = false) Collection<String> classifications,
    		@ApiParam(value = "Set to true (default setting is false) for intersection (default behavior is union) of classifications.")
    		@RequestParam(value = "exclusive", required = false, defaultValue = "false") boolean exclusive,
            @ApiParam(value = "Page Size", required = true)
            @RequestParam(value = "page_size", required = false, defaultValue = "20") Integer pageSize) {
    	
    	Pageable pageable = new PageRequest(0, pageSize);
    	Set<String> annotationKeys = new HashSet<String>();
    	Set<OntologyDocument> tempSet;
    	if (ontologies == null && schemas == null && classifications == null) {
    		tempSet = new HashSet<OntologyDocument>(ontologyRepositoryService.getAllDocuments());
    	} else {
        	tempSet = ontologyRepositoryService.filter(schemas, classifications, exclusive);

        	if (ontologies != null)
        	if(ontologies.size()>=1)
        		for (String ontology : ontologies)
        	{
        		ontology = ontology.toLowerCase();
        		tempSet.add(ontologyRepositoryService.get(ontology));
        	}	
    	}	
    	
	   	 for (OntologyDocument document : tempSet ) {
	   		 System.out.println("starting ontology: "+document.getOntologyId());
	         Page<Property> properties = ontologyPropertyGraphService.findAllByOntology(document.getOntologyId(), pageable);
	         
	 		for (Property property : properties.getContent()) {
	 			annotationKeys.addAll(property.getAnnotation().keySet());
	 		}
	     	
	     	while(properties.hasNext()) {
	     		properties = ontologyPropertyGraphService.findAllByOntology(document.getOntologyId(), pageable);
	     		
	     		for (Property property : properties.getContent()) {
	     			annotationKeys.addAll(property.getAnnotation().keySet());
	     		}
	
	     	}
	     	System.out.println("properties of "+document.getOntologyId()+" finished!");
	         Page<Term> terms = ontologyTermGraphService.findAllByOntology(document.getOntologyId(), pageable);
	         
	 		for (Term term : terms.getContent()) {
	 			annotationKeys.addAll(term.getAnnotation().keySet());
	 		}
	     	
	     	while(terms.hasNext()) {
	     		terms = ontologyTermGraphService.findAllByOntology(document.getOntologyId(), pageable);
	     		for (Term term : terms.getContent()) {
	     			annotationKeys.addAll(term.getAnnotation().keySet());
	     		}
	     	}
	     	System.out.println("terms of "+document.getOntologyId()+" finished!");
	         Page<Individual> individuals = ontologyIndividualService.findAllByOntology(document.getOntologyId(), pageable);  
	         
	 		for (Individual individual : individuals.getContent()) {
	 			annotationKeys.addAll(individual.getAnnotation().keySet());
	 		}
	     	
	     	while(individuals.hasNext()) {
	     		individuals = ontologyIndividualService.findAllByOntology(document.getOntologyId(), pageable);
	     		for (Individual individual : individuals.getContent()) {
	     			annotationKeys.addAll(individual.getAnnotation().keySet());
	     		}
	     	}
	     	System.out.println("individuals  of "+document.getOntologyId()+" finished!");
	   	 }

        return new ResponseEntity<>(annotationKeys, HttpStatus.OK);
    }
    
    @ApiOperation(value = "Retrieves definitions of all terms, properties and individuals as subsequent sentences of a corpus.", notes = "Possible schema keys and possible classification values of particular keys can be inquired with /api/ontologies/schemakeys and /api/ontologies/schemavalues methods respectively. If no ontology, schema and classification is specified, everything is retrieved without any filter.")
    @RequestMapping(path = "/displaysentences", produces = {MediaType.TEXT_PLAIN_VALUE}, method = RequestMethod.GET)
    public HttpEntity<String> getSentences(            
    		@RequestParam(value = "ontology_id", required = false) Collection<String> ontologies,
    		@RequestParam(value = "schema", required = false) Collection<String> schemas,
    		@RequestParam(value = "classification", required = false) Collection<String> classifications,
    		@ApiParam(value = "Set to true (default setting is false) for intersection (default behavior is union) of classifications.")
    		@RequestParam(value = "exclusive", required = false, defaultValue = "false") boolean exclusive,
    		@ApiParam(value = "The specified annotations are added as extra sentences to the corpus.")
    		@RequestParam(value = "annotation", required = false) Collection<String> annotations,
            @ApiParam(value = "Page Size", required = true)
            @RequestParam(value = "page_size", required = false, defaultValue = "20") Integer pageSize) {

    	String sentences = getRawSentences(ontologies, schemas, classifications, exclusive, annotations,pageSize);	
    	
    	return new HttpEntity<String>(sentences);
    }
    
    public String getRawSentences(Collection<String> ontologies,Collection<String> schemas,Collection<String> classifications, boolean exclusive, Collection<String> annotations,Integer pageSize) {
    	StringBuilder sb = new StringBuilder();
    	Pageable pageable = new PageRequest(0, pageSize);
    	
    	Set<OntologyDocument> tempSet;
    	if (ontologies == null && schemas == null && classifications == null) {
    		tempSet = new HashSet<OntologyDocument>(ontologyRepositoryService.getAllDocuments());
    	} else {
        	tempSet = ontologyRepositoryService.filter(schemas, classifications, exclusive);

        	if (ontologies != null)
        	if(ontologies.size()>=1)
        		for (String ontology : ontologies)
        	{
        		ontology = ontology.toLowerCase();
        		tempSet.add(ontologyRepositoryService.get(ontology));
        	}	
    	}	
    	
    	if (annotations == null)
    		annotations = new HashSet<String>();
    	
	   	 for (OntologyDocument document : tempSet ) {
	   		 System.out.println("starting ontology: "+document.getOntologyId());
	         Page<Property> properties = ontologyPropertyGraphService.findAllByOntology(document.getOntologyId(), pageable);
	         
	 		for (Property property : properties.getContent()) {
	 			if(property.getDescription() != null)
		 			for (String description : property.getDescription())
		 			    sb.append(description).append("\n");
	 			if(property.getAnnotation() != null)
	 				for (String annotation : annotations)
	 					if(property.getAnnotation().containsKey(annotation)) {
	 						if (property.getAnnotation().get(annotation) instanceof String[]) {
	 							for (String annotationItem : (String[])property.getAnnotation().get(annotation))
	 								sb.append(annotationItem).append("\n");
	 								
	 						} else if (property.getAnnotation().get(annotation) instanceof String)
	 							sb.append(property.getAnnotation().get(annotation)).append("\n");
	 					}		
	 		}
	     	
	     	while(properties.hasNext()) {
	     		properties = ontologyPropertyGraphService.findAllByOntology(document.getOntologyId(), pageable);
	     		
	     		for (Property property : properties.getContent()) {
	     			if(property.getDescription() != null)
			 			for (String description : property.getDescription())
			 			    sb.append(description).append("\n");
	     			if(property.getAnnotation() != null)
		 				for (String annotation : annotations)
		 					if(property.getAnnotation().containsKey(annotation)){
		 						if (property.getAnnotation().get(annotation) instanceof String[]) {
		 							for (String annotationItem : (String[])property.getAnnotation().get(annotation))
		 								sb.append(annotationItem).append("\n");
		 								
		 						} else if (property.getAnnotation().get(annotation) instanceof String)
		 							sb.append(property.getAnnotation().get(annotation)).append("\n");
		 					}	
	     		}
	
	     	}
	     	System.out.println("properties of "+document.getOntologyId()+" finished!");
	         Page<Term> terms = ontologyTermGraphService.findAllByOntology(document.getOntologyId(), pageable);
	         
	 		for (Term term : terms.getContent()) {
	 			if(term.getDescription() != null)
		 			for (String description : term.getDescription())
		 			    sb.append(description).append("\n");
	 			if(term.getAnnotation() != null)
	 				for (String annotation : annotations)
	 					if(term.getAnnotation().containsKey(annotation)){
	 						if (term.getAnnotation().get(annotation) instanceof String[]) {
	 							for (String annotationItem : (String[])term.getAnnotation().get(annotation))
	 								sb.append(annotationItem).append("\n");
	 								
	 						} else if (term.getAnnotation().get(annotation) instanceof String)
	 							sb.append(term.getAnnotation().get(annotation)).append("\n");
	 					}	
	 		}
	     	
	     	while(terms.hasNext()) {
	     		terms = ontologyTermGraphService.findAllByOntology(document.getOntologyId(), pageable);
	     		for (Term term : terms.getContent()) {
	     			if(term.getDescription() != null)
			 			for (String description : term.getDescription())
			 			    sb.append(description).append("\n");
	     			if(term.getAnnotation() != null)
		 				for (String annotation : annotations)
		 					if(term.getAnnotation().containsKey(annotation)){
		 						if (term.getAnnotation().get(annotation) instanceof String[]) {
		 							for (String annotationItem : (String[])term.getAnnotation().get(annotation))
		 								sb.append(annotationItem).append("\n");
		 								
		 						} else if (term.getAnnotation().get(annotation) instanceof String)
		 							sb.append(term.getAnnotation().get(annotation)).append("\n");
		 					}	
	     		}
	     	}
	     	System.out.println("terms of "+document.getOntologyId()+" finished!");
	         Page<Individual> individuals = ontologyIndividualService.findAllByOntology(document.getOntologyId(), pageable);  
	         
	 		for (Individual individual : individuals.getContent()) {
	 			if(individual.getDescription() != null)
		 			for (String description : individual.getDescription())
		 			    sb.append(description).append("\n");
	 			if(individual.getAnnotation() != null)
	 				for (String annotation : annotations)
	 					if(individual.getAnnotation().containsKey(annotation)){
	 						if (individual.getAnnotation().get(annotation) instanceof String[]) {
	 							for (String annotationItem : (String[])individual.getAnnotation().get(annotation))
	 								sb.append(annotationItem).append("\n");
	 								
	 						} else if (individual.getAnnotation().get(annotation) instanceof String)
	 							sb.append(individual.getAnnotation().get(annotation)).append("\n");
	 					}	
	 		}
	     	
	     	while(individuals.hasNext()) {
	     		individuals = ontologyIndividualService.findAllByOntology(document.getOntologyId(), pageable);
	     		for (Individual individual : individuals.getContent()) {
	     			if(individual.getDescription() != null)
			 			for (String description : individual.getDescription())
			 			    sb.append(description).append("\n");
	     			if(individual.getAnnotation() != null)
		 				for (String annotation : annotations)
		 					if(individual.getAnnotation().containsKey(annotation)){
		 						if (individual.getAnnotation().get(annotation) instanceof String[]) {
		 							for (String annotationItem : (String[])individual.getAnnotation().get(annotation))
		 								sb.append(annotationItem).append("\n");
		 								
		 						} else if (individual.getAnnotation().get(annotation) instanceof String)
		 							sb.append(individual.getAnnotation().get(annotation)).append("\n");
		 					}	
	     		}
	     	}
	     	System.out.println("individuals  of "+document.getOntologyId()+" finished!");
	   	 }
	   	 return sb.toString();
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource not found")
    @ExceptionHandler(ResourceNotFoundException.class)
    public void handleError(HttpServletRequest req, Exception exception) {
    }
    
    private class BasicTerm {
    	String iri;
    	String label;
    	Set<String> description;
    	String type;
    	Map annotation;
    	
    	BasicTerm(Property property) {
    	this.setLabel(property.getLabel());
    	this.setIri(property.getIri());
    	this.setDescription(property.getDescription());
    	this.setType("property");
    	this.setAnnotation(property.getAnnotation());
    	}
    	
    	BasicTerm(Term term) {
    	this.setLabel(term.getLabel());
    	this.setIri(term.getIri());
    	this.setDescription(term.getDescription());
    	this.setType("term");
    	this.setAnnotation(term.getAnnotation());
    	}
    	
    	BasicTerm(Individual individual) {
    	this.setLabel(individual.getLabel());
    	this.setIri(individual.getIri());
    	this.setDescription(individual.getDescription());
    	this.setType("individual");
    	this.setAnnotation(individual.getAnnotation());
    	}
    	
		public String getIri() {
			return iri;
		}
		public void setIri(String iri) {
			this.iri = iri;
		}
		public String getLabel() {
			return label;
		}
		public void setLabel(String label) {
			this.label = label;
		}
		public Set<String>  getDescription() {
			return description;
		}
		public void setDescription(Set<String>  description) {
			this.description = description;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public Map getAnnotation() {
			return annotation;
		}
		public void setAnnotation(Map annotation) {
			this.annotation = annotation;
		} 	
    }

}