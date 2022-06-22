package uk.ac.ebi.spot.ols.controller.api;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.nd4j.common.io.ClassPathResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import uk.ac.ebi.spot.ols.model.OntologyDocument;
import uk.ac.ebi.spot.ols.neo4j.model.Individual;
import uk.ac.ebi.spot.ols.neo4j.model.Property;
import uk.ac.ebi.spot.ols.neo4j.model.Term;
import uk.ac.ebi.spot.ols.neo4j.service.OntologyIndividualService;
import uk.ac.ebi.spot.ols.neo4j.service.OntologyPropertyGraphService;
import uk.ac.ebi.spot.ols.neo4j.service.OntologyTermGraphService;
import uk.ac.ebi.spot.ols.service.OntologyRepositoryService;
import uk.ac.ebi.spot.ols.word2vec.ReverseDictionary;
import uk.ac.ebi.spot.ols.word2vec.preprocessing.Word2VecPreProc;
import uk.ac.ebi.spot.ols.word2vec.training.TrainW2V;


/**
 * @author Simon Jupp
 * @date 23/06/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
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
    
    @Autowired
    public Word2VecPreProc wpp;
    @Autowired
    public TrainW2V tw2v;
    @Autowired
    public ReverseDictionary rd;

    @RequestMapping(path = "/extendedsearch", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<List<Object>> getEverythingByOntology(
            @RequestParam(value = "ontology_id", required = false) Collection<String> ontologies,
    		@RequestParam(value = "schema", required = false) Collection<String> schemas,
    		@RequestParam(value = "classification", required = false) Collection<String> classifications,
            @ApiParam(value = "Page Size", required = true)
            @RequestParam(value = "page_size", required = true, defaultValue = "20") Integer pageSize) {

    	Pageable pageable = new PageRequest(0, pageSize);
    	List<Object> everything = new ArrayList<Object>();
    	
    	Set<String> tempSet = new HashSet<String>();
    	if (ontologies != null)
    	if(ontologies.size()>=1)
    		for (String ontology : ontologies)
    	{
    		ontology = ontology.toLowerCase();
    		tempSet.add(ontology);
    	}
    	
	    if (classifications != null && schemas != null)	    
	    if (classifications.size() >= 1 && schemas.size()>=1)
	   	 for (OntologyDocument ontologyDocument : ontologyRepositoryService.getAllDocuments(new Sort(new Sort.Order(Sort.Direction.ASC, "ontologyId")))) {
	   		for(Map<String, Collection<String>> classificationSchema : ontologyDocument.getConfig().getClassifications()) {
	   			for (String schema: schemas)
	   			    if(classificationSchema.containsKey(schema))
	   				    for (String classification: classifications) {
	   				    	if (classificationSchema.get(schema) != null)
	   				    		if (!classificationSchema.get(schema).isEmpty())
	   				    	        if (classificationSchema.get(schema).contains(classification)) {
	   					                tempSet.add(ontologyDocument.getOntologyId());
	   				  }
	   				    }
	   			    
	   			}
			} 

	  	 for (String oid : tempSet ) {
	   		 oid = oid.toLowerCase();
	   		 
	         Page<Property> properties = ontologyPropertyGraphService.findAllByOntology(oid, pageable);
	         
	     	everything.addAll(properties.getContent());
	     	
	     	while(properties.hasNext()) {
	     		properties = ontologyPropertyGraphService.findAllByOntology(oid, pageable);
	     		everything.addAll(properties.getContent());
	     	}
	
	         Page<Term> terms = ontologyTermGraphService.findAllByOntology(oid, pageable);
	         
	     	everything.addAll(terms.getContent());
	     	
	     	while(terms.hasNext()) {
	     		terms = ontologyTermGraphService.findAllByOntology(oid, pageable);
	     		everything.addAll(terms.getContent());
	     	}
	         
	         Page<Individual> individuals = ontologyIndividualService.findAllByOntology(oid, pageable);  
	         
	     	everything.addAll(individuals.getContent());
	     	
	     	while(individuals.hasNext()) {
	     		individuals = ontologyIndividualService.findAllByOntology(oid, pageable);
	     		everything.addAll(individuals.getContent());
	     	}
	  	 
	  	 }

    	return new ResponseEntity<>(everything, HttpStatus.OK);
    }
    
    @RequestMapping(path = "/basictermsearch", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<List<BasicTerm>> getBasicTermsByOntology(
            @RequestParam(value = "ontology_id", required = false) Collection<String> ontologies,
    		@RequestParam(value = "schema", required = false) Collection<String> schemas,
    		@RequestParam(value = "classification", required = false) Collection<String> classifications,
            @ApiParam(value = "Page Size", required = true)
            @RequestParam(value = "page_size", required = false, defaultValue = "20") Integer pageSize) {
    	
    	Pageable pageable = new PageRequest(0, pageSize);
    	List<BasicTerm> everything = new ArrayList<BasicTerm>();
    	
    	Set<String> tempSet = new HashSet<String>();
    	if (ontologies != null)
    	if(ontologies.size()>=1)
    		for (String ontology : ontologies)
    	{
    		ontology = ontology.toLowerCase();
    		tempSet.add(ontology);
    	}
    	
	    if (classifications != null && schemas != null)	    
	    if (classifications.size() >= 1 && schemas.size()>=1)
	   	 for (OntologyDocument ontologyDocument : ontologyRepositoryService.getAllDocuments(new Sort(new Sort.Order(Sort.Direction.ASC, "ontologyId")))) {
	   		for(Map<String, Collection<String>> classificationSchema : ontologyDocument.getConfig().getClassifications()) {
	   			for (String schema: schemas)
	   			    if(classificationSchema.containsKey(schema))
	   				    for (String classification: classifications) {
	   				    	if (classificationSchema.get(schema) != null)
	   				    		if (!classificationSchema.get(schema).isEmpty())
	   				    	        if (classificationSchema.get(schema).contains(classification)) {
	   					                tempSet.add(ontologyDocument.getOntologyId());
	   				  }
	   				    }
	   			    
	   			}
			} 

    	
	   	 for (String oid : tempSet ) {
	   		 oid = oid.toLowerCase();
	   		 System.out.println("starting ontology: "+oid);
	         Page<Property> properties = ontologyPropertyGraphService.findAllByOntology(oid, pageable);
	         
	 		for (Property property : properties.getContent()) {
	 			everything.add(new BasicTerm(property));
	 		}
	     	
	     	while(properties.hasNext()) {
	     		properties = ontologyPropertyGraphService.findAllByOntology(oid, pageable);
	     		
	     		for (Property property : properties.getContent()) {
	     			everything.add(new BasicTerm(property));
	     		}
	
	     	}
	     	System.out.println("properties of "+oid+" finished!");
	         Page<Term> terms = ontologyTermGraphService.findAllByOntology(oid, pageable);
	         
	 		for (Term term : terms.getContent()) {
	 			everything.add(new BasicTerm(term));
	 		}
	     	
	     	while(terms.hasNext()) {
	     		terms = ontologyTermGraphService.findAllByOntology(oid, pageable);
	     		for (Term term : terms.getContent()) {
	     			everything.add(new BasicTerm(term));
	     		}
	     	}
	     	System.out.println("terms of "+oid+" finished!");
	         Page<Individual> individuals = ontologyIndividualService.findAllByOntology(oid, pageable);  
	         
	 		for (Individual individual : individuals.getContent()) {
	 			everything.add(new BasicTerm(individual));
	 		}
	     	
	     	while(individuals.hasNext()) {
	     		individuals = ontologyIndividualService.findAllByOntology(oid, pageable);
	     		for (Individual individual : individuals.getContent()) {
	     			everything.add(new BasicTerm(individual));
	     		}
	     	}
	     	System.out.println("individuals  of "+oid+" finished!");
	   	 }

        return new ResponseEntity<>(everything, HttpStatus.OK);
    }
    @RequestMapping(path = "/displaysentences", produces = {MediaType.TEXT_PLAIN_VALUE}, method = RequestMethod.GET)
    public HttpEntity<String> getSentences(            
    		@RequestParam(value = "ontology_id", required = false) Collection<String> ontologies,
    		@RequestParam(value = "schema", required = false) Collection<String> schemas,
    		@RequestParam(value = "classification", required = false) Collection<String> classifications,
            @ApiParam(value = "Page Size", required = true)
            @RequestParam(value = "page_size", required = false, defaultValue = "20") Integer pageSize) throws IOException{

    	String sentences = getRawSentences(ontologies, schemas, classifications, pageSize);	
    	StringBuilder sb = new StringBuilder();
    	if(ontologies != null)
    	for (String ontology : ontologies)
    	    sb.append("_").append(ontology);
    	if(schemas != null)
    	for (String schema : schemas)
    	    sb.append("_").append(schema);
    	if(classifications != null)
    	for (String classification : classifications)
    	    sb.append("_").append(classification);
    	    	
//        String filePath = new ClassPathResource("raw_sentences"+sb.toString()+".txt").getFile().getAbsolutePath();
        
        try (PrintWriter out = new PrintWriter("raw_sentences"+sb.toString()+".txt")) {
            out.println(sentences);
        } catch (Exception e) {
        	e.printStackTrace();
        }
    	
    	
    	return new HttpEntity<String>(sentences);
    }
    
    public String getRawSentences(Collection<String> ontologies,Collection<String> schemas,Collection<String> classifications, Integer pageSize) {
    	StringBuilder sb = new StringBuilder();
    	Pageable pageable = new PageRequest(0, pageSize);
    	
    	Set<String> tempSet = new HashSet<String>();
    	if (ontologies != null)
    	if(ontologies.size()>=1)
    		for (String ontology : ontologies)
    	{
    		ontology = ontology.toLowerCase();
    		tempSet.add(ontology);
    	}
    	
	    if (classifications != null && schemas != null)	    
	    if (classifications.size() >= 1 && schemas.size()>=1)
	   	 for (OntologyDocument ontologyDocument : ontologyRepositoryService.getAllDocuments(new Sort(new Sort.Order(Sort.Direction.ASC, "ontologyId")))) {
	   		for(Map<String, Collection<String>> classificationSchema : ontologyDocument.getConfig().getClassifications()) {
	   			for (String schema: schemas)
	   			    if(classificationSchema.containsKey(schema))
	   				    for (String classification: classifications) {
	   				    	if (classificationSchema.get(schema) != null)
	   				    		if (!classificationSchema.get(schema).isEmpty())
	   				    	        if (classificationSchema.get(schema).contains(classification)) {
	   					                tempSet.add(ontologyDocument.getOntologyId());
	   				  }
	   				    }
	   			    
	   			}
			} 

    	
	   	 for (String oid : tempSet ) {
	   		 oid = oid.toLowerCase();
	   		 System.out.println("starting ontology: "+oid);
	         Page<Property> properties = ontologyPropertyGraphService.findAllByOntology(oid, pageable);
	         
	 		for (Property property : properties.getContent()) {
	 			if(property.getDescription() != null)
		 			for (String description : property.getDescription())
		 			    sb.append(description).append("\n");
	 		}
	     	
	     	while(properties.hasNext()) {
	     		properties = ontologyPropertyGraphService.findAllByOntology(oid, pageable);
	     		
	     		for (Property property : properties.getContent()) {
	     			if(property.getDescription() != null)
			 			for (String description : property.getDescription())
			 			    sb.append(description).append("\n");
	     		}
	
	     	}
	     	System.out.println("properties of "+oid+" finished!");
	         Page<Term> terms = ontologyTermGraphService.findAllByOntology(oid, pageable);
	         
	 		for (Term term : terms.getContent()) {
	 			if(term.getDescription() != null)
		 			for (String description : term.getDescription())
		 			    sb.append(description).append("\n");
	 		}
	     	
	     	while(terms.hasNext()) {
	     		terms = ontologyTermGraphService.findAllByOntology(oid, pageable);
	     		for (Term term : terms.getContent()) {
	     			if(term.getDescription() != null)
			 			for (String description : term.getDescription())
			 			    sb.append(description).append("\n");
	     		}
	     	}
	     	System.out.println("terms of "+oid+" finished!");
	         Page<Individual> individuals = ontologyIndividualService.findAllByOntology(oid, pageable);  
	         
	 		for (Individual individual : individuals.getContent()) {
	 			if(individual.getDescription() != null)
		 			for (String description : individual.getDescription())
		 			    sb.append(description).append("\n");
	 		}
	     	
	     	while(individuals.hasNext()) {
	     		individuals = ontologyIndividualService.findAllByOntology(oid, pageable);
	     		for (Individual individual : individuals.getContent()) {
	     			if(individual.getDescription() != null)
			 			for (String description : individual.getDescription())
			 			    sb.append(description).append("\n");
	     		}
	     	}
	     	System.out.println("individuals  of "+oid+" finished!");
	   	 }
	   	 return sb.toString();
    }
    
    @RequestMapping(path="/load", produces = {MediaType.TEXT_PLAIN_VALUE}, method = RequestMethod.GET)
    public HttpEntity<String> load(@RequestParam String word, @RequestParam int count,    		
    		@RequestParam(value = "ontology_id", required = false) Collection<String> ontologies,
    		@RequestParam(value = "schema", required = false) Collection<String> schemas,
    		@RequestParam(value = "classification", required = false) Collection<String> classifications,
    		@RequestParam(value = "path", required = false, defaultValue = "") String path,
            @ApiParam(value = "Page Size", required = true)
            @RequestParam(value = "page_size", required = false, defaultValue = "20" ) Integer pageSize) throws IOException {
       	
    	StringBuilder sb = new StringBuilder();
    	if(ontologies != null)
    	for (String ontology : ontologies)
    	    sb.append("_").append(ontology);
    	if(schemas != null)
    	for (String schema : schemas)
    	    sb.append("_").append(schema);
    	if(classifications != null)
    	for (String classification : classifications)
    	    sb.append("_").append(classification);

        wpp.processing(path+"raw_sentences"+sb.toString()+".txt");

        return new HttpEntity<String>(path+"raw_sentences"+sb.toString()+".txt");
    }
    
    @RequestMapping(path="/train", produces = {MediaType.TEXT_PLAIN_VALUE}, method = RequestMethod.GET)
    public HttpEntity<String> word2vec(@RequestParam String word, @RequestParam int count,    		
    		@RequestParam(value = "ontology_id", required = false) Collection<String> ontologies,
    		@RequestParam(value = "schema", required = false) Collection<String> schemas,
    		@RequestParam(value = "classification", required = false) Collection<String> classifications,
            @ApiParam(value = "Page Size", required = true)
            @RequestParam(value = "page_size", required = false, defaultValue = "20" ) Integer pageSize) throws IOException {
       	
    	StringBuilder sb = new StringBuilder();
    	if(ontologies != null)
    	for (String ontology : ontologies)
    	    sb.append("_").append(ontology);
    	if(schemas != null)
    	for (String schema : schemas)
    	    sb.append("_").append(schema);
    	if(classifications != null)
    	for (String classification : classifications)
    	    sb.append("_").append(classification);

        System.out.println("before train");
        tw2v.trainSerialise(wpp.getT(), wpp.getIter());
        System.out.println("after train");
        Collection<String> results = rd.dict(tw2v.getVec(), word, count);
        System.out.println("way after train");

        Iterator<String> iterator = results.iterator();

        Collection<JSONObject> items = new ArrayList<JSONObject>();

        Integer key = 0;


        while (iterator.hasNext()) {
            key++;
            JSONObject object = new JSONObject();
            String currentWord = iterator.next();

            object.put(key.toString(), currentWord);
            items.add(object);
        }

        return new HttpEntity<String>(items.toString());
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
