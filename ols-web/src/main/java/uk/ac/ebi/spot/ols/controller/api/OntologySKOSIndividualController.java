package uk.ac.ebi.spot.ols.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.spot.ols.neo4j.model.Individual;
import uk.ac.ebi.spot.ols.neo4j.service.OntologyIndividualService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Erhun Giray TUNCAY 
 * @date 18/03/2022
 * NFDI4ING Terminology Service Team, TIB
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
            @RequestParam(value = "without_top", required = true) boolean withoutTop,
            @RequestParam(value = "individual_count", defaultValue = "1000000") Integer individualCount) {
        return new ResponseEntity<>(conceptTree(ontologyId,individualCount,schema,narrower,withoutTop), HttpStatus.OK);
    } 
    
    @RequestMapping(path = "/{onto}/skosconcepts", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<String> getSKOSConceptsByOntology(
            @PathVariable("onto") String ontologyId,
            @RequestParam(value = "schema", required = true) boolean schema,
            @RequestParam(value = "narrower", required = true) boolean narrower,
            @RequestParam(value = "without_top", required = true) boolean withoutTop,
            @RequestParam(value = "individual_count", defaultValue = "1000000") Integer individualCount) {
    	 List<SKOSConceptNode<Individual>> rootIndividuals = conceptTree(ontologyId,individualCount,schema,narrower,withoutTop);
         StringBuilder sb = new StringBuilder();
         for (SKOSConceptNode<Individual> root : rootIndividuals) {
        	 sb.append(root.getIndex() + " , "+ root.getLabel() + " , " + root.getIri()).append("\n");
        	 sb.append(generateConceptHierarchyTextByOntology(root)); 
         }
         
         return new ResponseEntity<>(sb.toString(), HttpStatus.OK);
    }    
    
    public List<SKOSConceptNode<Individual>> conceptTree (String ontologyId, Integer individualCount, boolean schema, boolean narrower, boolean withoutTop){
        Page<Individual> terms = ontologyIndividualRepository.findAllByOntology(ontologyId, new PageRequest(0, individualCount));
        List<Individual> listOfTerms = terms.getContent(); 
        List<SKOSConceptNode<Individual>> rootIndividuals = null;
        if(withoutTop) {
        	rootIndividuals = conceptTreeWithoutTop(ontologyId,individualCount, narrower);
        } else {
        	rootIndividuals = new ArrayList<SKOSConceptNode<Individual>>();
            
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
        }
            
         return rootIndividuals;
    }
    
    public List<SKOSConceptNode<Individual>> conceptTreeWithoutTop (String ontologyId, Integer individualCount, boolean narrower){
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
        	Individual topConceptIndividual = findIndividual(listOfTerms, iri);
    		SKOSConceptNode<Individual> topConcept = new SKOSConceptNode<Individual>(topConceptIndividual);
    		topConcept.setLabel(topConcept.getData().getLabel());
    		topConcept.setIri(iri);
    		topConcept.setIndex(String.valueOf(++count));
		     if(narrower)
		         populateChildrenandRelatedByNarrower(topConceptIndividual,topConcept,listOfTerms);
		     else
		    	 populateChildrenandRelatedByBroader(topConceptIndividual,topConcept,listOfTerms);
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
    
    public StringBuilder generateConceptHierarchyTextByOntology(SKOSConceptNode<Individual> rootIndividual) {
    	StringBuilder sb = new StringBuilder();
        for (SKOSConceptNode<Individual> individual : rootIndividual.getChildren()) {
       	     sb.append(individual.getIndex() + " , "+ individual.getLabel() + " , " + individual.getIri()).append("\n");
       	     sb.append(generateConceptHierarchyTextByOntology(individual));
        }
        return sb;
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource not found")
    @ExceptionHandler(ResourceNotFoundException.class)
    public void handleError(HttpServletRequest req, Exception exception) {
    }

}
