package uk.ac.ebi.spot.ols.controller.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.semanticweb.elk.owl.implementation.ElkIndividualListObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;
import uk.ac.ebi.spot.ols.neo4j.model.Individual;
import uk.ac.ebi.spot.ols.neo4j.model.Property;
import uk.ac.ebi.spot.ols.neo4j.model.Term;
import uk.ac.ebi.spot.ols.neo4j.service.IndividualJsTreeBuilder;
import uk.ac.ebi.spot.ols.neo4j.service.JsTreeBuilder;
import uk.ac.ebi.spot.ols.neo4j.service.OntologyIndividualService;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Giray
 * @date 2022
 * TIB
 */
@Controller
@RequestMapping("/api/ontologies")
public class OntologySKOSIndividualController {

    @Autowired
    private OntologyIndividualService ontologyIndividualRepository;
    
    @RequestMapping(path = "/{onto}/skosconcepthierarchy", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<List<SKOSConceptNode<Individual>>> getSKOSConceptHierarchyByOntology(
            @PathVariable("onto") String ontologyId,
            @RequestParam(value = "schema", required = true) boolean schema,
            @RequestParam(value = "narrower", required = true) boolean narrower,
            @RequestParam(value = "individual_count", defaultValue = "1000000") Integer individualCount) {
        return new ResponseEntity<>(conceptTree(ontologyId,individualCount,schema,narrower), HttpStatus.OK);
    } 
    
    @RequestMapping(path = "/{onto}/skosconcepts", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<String> getSKOSConceptsByOntology(
            @PathVariable("onto") String ontologyId,
            @RequestParam(value = "schema", required = true) boolean schema,
            @RequestParam(value = "narrower", required = true) boolean narrower,
            @RequestParam(value = "individual_count", defaultValue = "1000000") Integer individualCount) {
    	 List<SKOSConceptNode<Individual>> rootIndividuals = conceptTree(ontologyId,individualCount,schema,narrower);
         StringBuilder sb = new StringBuilder();
         for (SKOSConceptNode<Individual> root : rootIndividuals) {
        	 sb.append(root.getIndex() + " , "+ root.getLabel() + " , " + root.getIri()).append("\n");
        	 sb.append(getYAMLSKOSConceptHierarchyByOntology(root)); 
         }
         
         return new ResponseEntity<>(sb.toString(), HttpStatus.OK);
    }   
    @RequestMapping(path = "/{onto}/skostopconcepts", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<String> getSKOSTopConceptsByOntology(
            @PathVariable("onto") String ontologyId,
            @RequestParam(value = "individual_count", defaultValue = "1000000") Integer individualCount) {
    	 List<SKOSConceptNode<Individual>> rootIndividuals = identifyTopConcepts(ontologyId,individualCount);
         StringBuilder sb = new StringBuilder();
         for (SKOSConceptNode<Individual> root : rootIndividuals) {
        	 sb.append(root.getIndex() + " , "+ root.getLabel() + " , " + root.getIri()).append("\n");
        	 sb.append(getYAMLSKOSConceptHierarchyByOntology(root)); 
         }
         
         return new ResponseEntity<>(sb.toString(), HttpStatus.OK);
    }  
    
    public List<SKOSConceptNode<Individual>> conceptTree (String ontologyId, Integer individualCount, boolean schema, boolean narrower){
        Page<Individual> terms = ontologyIndividualRepository.findAllByOntology(ontologyId, new PageRequest(0, individualCount));
        List<Individual> listOfTerms = terms.getContent();       

        List<SKOSConceptNode<Individual>> rootIndividuals = new ArrayList<SKOSConceptNode<Individual>>();
        
        int count = 0;
        
        if(schema) {
            for (Individual indiv : listOfTerms)
           	    if (indiv.getAnnotation().get("hasTopConcept") != null) {
        		 for (String iriTopConcept : (String[]) indiv.getAnnotation().get("hasTopConcept")) {
        			 System.out.println(iriTopConcept);
        			 Individual topConceptIndividual = findIndividual(listOfTerms,iriTopConcept);
        			 SKOSConceptNode<Individual> topConcept =  new SKOSConceptNode<Individual>(findIndividual(listOfTerms,iriTopConcept));
        			 topConcept.setIri(topConcept.getData().getIri());
        		     topConcept.setLabel(topConcept.getData().getLabel());
        		     topConcept.setIndex(String.valueOf(++count));
        		     if(narrower)
        		         populateChildrenandRelatedByNarrower(topConceptIndividual,topConcept,listOfTerms);
        		     else
        		    	 populateChildrenandRelatedByBroader(topConceptIndividual,topConcept,listOfTerms);
        			 rootIndividuals.add(topConcept);
        		 }
           	    }  
        } else for (Individual individual : listOfTerms) {
        	 SKOSConceptNode<Individual> tree = new SKOSConceptNode<Individual>(individual);
        	 
        	 if (tree.isRoot() && individual.getAnnotation().get("topConceptOf") != null) {
				tree.setIri(individual.getIri());
				tree.setLabel(individual.getLabel());
				tree.setIndex(String.valueOf(++count));
				if(narrower)
                    populateChildrenandRelatedByNarrower(individual,tree,listOfTerms);
				else
					populateChildrenandRelatedByBroader(individual,tree,listOfTerms);
				rootIndividuals.add(tree);
			}
		}          
         return rootIndividuals;
    }
    
    public List<SKOSConceptNode<Individual>> identifyTopConcepts (String ontologyId, Integer individualCount){
        Page<Individual> terms = ontologyIndividualRepository.findAllByOntology(ontologyId, new PageRequest(0, individualCount));
        List<Individual> listOfTerms = terms.getContent();       
        int count = 0;
        Set<String> rootIRIs = new HashSet<String>();
        for (Individual individual : listOfTerms) {
			if (individual.getAnnotation().get("broader") != null) {
				for (String iriBroader : (String[]) individual.getAnnotation().get("broader")) {
					Individual broaderIndividual = findIndividual(listOfTerms,iriBroader);
					if (broaderIndividual.getAnnotation().get("broader") == null) {
						rootIRIs.add(iriBroader);
					}	
				}
			}
        }  
        List<SKOSConceptNode<Individual>> rootIndividuals = new ArrayList<SKOSConceptNode<Individual>>();
        for (String iri : rootIRIs) {
        	
    		SKOSConceptNode<Individual> topConcept = new SKOSConceptNode<Individual>(findIndividual(listOfTerms, iri));
    		topConcept.setLabel(topConcept.getData().getLabel());
    		topConcept.setIri(iri);
    		topConcept.setIndex(String.valueOf(++count));
    		rootIndividuals.add(topConcept);
        }

        
         return rootIndividuals;
    }
    
    public Individual findIndividual(List<Individual> wholeList, String iri) {
    	for (Individual individual : wholeList)
    		if(individual.getIri().equals(iri))
    			return individual;
    	return new Individual();
    }
    
    public void populateChildrenandRelatedByNarrower(Individual individual, SKOSConceptNode<Individual> tree, List<Individual> listOfTerms ) {
		
		if (individual.getAnnotation().get("related") != null)
		for (String iriRelated : (String[]) individual.getAnnotation().get("related")) {
			SKOSConceptNode<Individual> related = new SKOSConceptNode<Individual>(findIndividual(listOfTerms,iriRelated));
			related.setLabel(related.getData().getLabel());
			related.setIri(iriRelated);
			related.setIndex(tree.getIndex()+ ".related");
			tree.addRelated(related);
		}
    	int count = 0;
    	if (individual.getAnnotation().get("narrower") != null)
		for (String iriChild : (String[]) individual.getAnnotation().get("narrower")) {
			Individual childIndividual = findIndividual(listOfTerms,iriChild);
			SKOSConceptNode<Individual> child = new SKOSConceptNode<Individual>(childIndividual);
			child.setLabel(child.getData().getLabel());
			child.setIri(iriChild);
			child.setIndex(tree.getIndex()+"."+ ++count);			
			populateChildrenandRelatedByNarrower(childIndividual,child,listOfTerms);
			tree.addChild(child);
		}
    }
    
    public void populateChildrenandRelatedByBroader(Individual individual, SKOSConceptNode<Individual> tree, List<Individual> listOfTerms) {
		if (individual.getAnnotation().get("related") != null)
		for (String iriRelated : (String[]) individual.getAnnotation().get("related")) {
			SKOSConceptNode<Individual> related = new SKOSConceptNode<Individual>(findIndividual(listOfTerms,iriRelated));
			related.setLabel(related.getData().getLabel());
			related.setIri(iriRelated);
			related.setIndex(tree.getIndex()+ ".related");
			tree.addRelated(related);
		}
		int count = 0;
		for ( Individual indiv : listOfTerms) {
			if (indiv.getAnnotation().get("broader") != null)
				for (String iriBroader : (String[]) indiv.getAnnotation().get("broader")) 
					if (individual.getIri().equals(iriBroader)) {
						SKOSConceptNode<Individual> child = new SKOSConceptNode<Individual>(indiv);
						child.setLabel(child.getData().getLabel());
						child.setIri(iriBroader);
						child.setIndex(tree.getIndex()+"."+ ++count);	
						populateChildrenandRelatedByBroader(indiv,child,listOfTerms);
						tree.addChild(child);
					}	
		}
    }
    
    public StringBuilder getYAMLSKOSConceptHierarchyByOntology(SKOSConceptNode<Individual> rootIndividual) {
    	StringBuilder sb = new StringBuilder();
        for (SKOSConceptNode<Individual> individual : rootIndividual.getChildren()) {
       	     sb.append(individual.getIndex() + " , "+ individual.getLabel() + " , " + individual.getIri()).append("\n");
       	     sb.append(getYAMLSKOSConceptHierarchyByOntology(individual));
        }
        return sb;
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource not found")
    @ExceptionHandler(ResourceNotFoundException.class)
    public void handleError(HttpServletRequest req, Exception exception) {
    }

}
