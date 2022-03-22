package uk.ac.ebi.spot.ols.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import io.swagger.annotations.ApiParam;
import uk.ac.ebi.spot.ols.neo4j.model.Individual;
import uk.ac.ebi.spot.ols.neo4j.service.OntologyIndividualService;

import javax.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;
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
public class OntologySKOSConceptController {

    @Autowired
    private OntologyIndividualService ontologyIndividualRepository;
    
    @RequestMapping(path = "/{onto}/concepthierarchy", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<List<SKOSConceptNode<Individual>>> getSKOSConceptHierarchyByOntology(
    	    @ApiParam(value = "ontology ID", required = true)
    	    @PathVariable("onto") String ontologyId,
    	    @ApiParam(value = "infer top concepts by schema (hasTopConcept) or  TopConceptOf property or broader/narrower relationships", required = true)
            @RequestParam(value = "find_roots", required = true, defaultValue = "SCHEMA") TopConceptEnum topConceptIdentification,
            @ApiParam(value = "infer from narrower or broader relationships", required = true)
            @RequestParam(value = "narrower", required = true, defaultValue = "false") boolean narrower,
            @ApiParam(value = "Maximum number of concepts", required = true)
            @RequestParam(value = "individual_count", required = true, defaultValue = "1000000") Integer individualCount) {
    	ontologyId = ontologyId.toLowerCase();
    	if (TopConceptEnum.RELATIONSHIPS == topConceptIdentification)
    		return new ResponseEntity<>(conceptTreeWithoutTop(ontologyId,individualCount, narrower), HttpStatus.OK);
    	else
    		return new ResponseEntity<>(conceptTree(ontologyId,individualCount,TopConceptEnum.SCHEMA == topConceptIdentification, narrower), HttpStatus.OK);
    } 
    
    @RequestMapping(path = "/{onto}/displayconcepthierarchy", method = RequestMethod.GET)
    @ResponseBody
    String displaySKOSConceptHierarchyByOntology(
    	    @ApiParam(value = "ontology ID", required = true)
    	    @PathVariable("onto") String ontologyId,
    		@ApiParam(value = "infer top concepts by schema (hasTopConcept) or  TopConceptOf property or broader/narrower relationships", required = true)
    	    @RequestParam(value = "find_roots", required = true, defaultValue = "SCHEMA") TopConceptEnum topConceptIdentification,
            @ApiParam(value = "infer from narrower or broader relationships", required = true)
            @RequestParam(value = "narrower", required = true, defaultValue = "false") boolean narrower,
            @ApiParam(value = "display related concepts", required = true)
            @RequestParam(value = "display_related", required = true, defaultValue = "false") boolean displayRelated,
            @ApiParam(value = "Maximum number of concepts", required = true)
            @RequestParam(value = "individual_count", required = true, defaultValue = "1000000") Integer individualCount) {
    	 ontologyId = ontologyId.toLowerCase();
     	 List<SKOSConceptNode<Individual>> rootIndividuals = null;
    	 if(TopConceptEnum.RELATIONSHIPS == topConceptIdentification)
    		 rootIndividuals = conceptTreeWithoutTop(ontologyId,individualCount, narrower);
    	 else
    		 rootIndividuals = conceptTree(ontologyId,individualCount,TopConceptEnum.SCHEMA == topConceptIdentification,narrower);
         StringBuilder sb = new StringBuilder();
         for (SKOSConceptNode<Individual> root : rootIndividuals) {
        	 sb.append(root.getIndex() + " , "+ root.getData().getLabel() + " , " + root.getData().getIri()).append("\n");
        	 sb.append(generateConceptHierarchyTextByOntology(root, displayRelated)); 
         }
         
         return sb.toString();
    }  
    
    @RequestMapping(path = "/{onto}/concepthierarchy/{iri}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<SKOSConceptNode<Individual>> getSKOSConceptHierarchyByOntologyAndIri(
    	    @ApiParam(value = "ontology ID", required = true)
    	    @PathVariable("onto") String ontologyId,
            @ApiParam(value = "encoded concept IRI", required = true)
            @PathVariable("iri") String iri,
            @ApiParam(value = "infer from narrower or broader relationships", required = true)
            @RequestParam(value = "narrower", required = true, defaultValue = "false") boolean narrower,
            @ApiParam(value = "index value for the root term", required = true)
            @RequestParam(value = "index", required = true, defaultValue = "1") String index,
            @ApiParam(value = "Maximum number of concepts", required = true)
            @RequestParam(value = "individual_count", required = true, defaultValue = "1000000") Integer individualCount) {
    	ontologyId = ontologyId.toLowerCase();
        Page<Individual> terms = ontologyIndividualRepository.findAllByOntology(ontologyId, new PageRequest(0, individualCount));
        List<Individual> listOfTerms = terms.getContent(); 
        SKOSConceptNode<Individual> topConcept = new SKOSConceptNode<Individual>(new Individual());
        try {
			String decodedIri = UriUtils.decode(iri, "UTF-8");	        
	        Individual topConceptIndividual = findIndividual(listOfTerms,decodedIri);
	        topConcept =  new SKOSConceptNode<Individual>(topConceptIndividual);
		    topConcept.setIndex(index);
		    if(narrower)
		        populateChildrenandRelatedByNarrower(topConceptIndividual,topConcept,listOfTerms);
		    else
		        populateChildrenandRelatedByBroader(topConceptIndividual,topConcept,listOfTerms);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return new ResponseEntity<>(topConcept, HttpStatus.OK);
    } 
    
    @RequestMapping(path = "/{onto}/displayconcepthierarchy/{iri}", method = RequestMethod.GET)
    @ResponseBody
    String displaySKOSConceptHierarchyByOntologyAndIri(
    	    @ApiParam(value = "ontology ID", required = true)
    	    @PathVariable("onto") String ontologyId,
            @ApiParam(value = "encoded concept IRI", required = true)
            @PathVariable("iri") String iri,
            @ApiParam(value = "infer from narrower or broader relationships", required = true)
            @RequestParam(value = "narrower", required = true, defaultValue = "false") boolean narrower,
            @ApiParam(value = "display related concepts", required = true)
            @RequestParam(value = "display_related", required = true, defaultValue = "false") boolean displayRelated,
            @ApiParam(value = "index value for the root term", required = true)
            @RequestParam(value = "index", required = true, defaultValue = "1") String index,
            @ApiParam(value = "Maximum number of concepts", required = true)
            @RequestParam(value = "individual_count", required = true, defaultValue = "1000000") Integer individualCount) {
    	ontologyId = ontologyId.toLowerCase();
        Page<Individual> terms = ontologyIndividualRepository.findAllByOntology(ontologyId, new PageRequest(0, individualCount));
        List<Individual> listOfTerms = terms.getContent();
        StringBuilder sb = new StringBuilder();
        try {
        	String decodedIri = UriUtils.decode(iri, "UTF-8");
			Individual topConceptIndividual = findIndividual(listOfTerms,decodedIri);	        
	        SKOSConceptNode<Individual> topConcept =  new SKOSConceptNode<Individual>(topConceptIndividual);
		     topConcept.setIndex(index);
		     if(narrower)
		         populateChildrenandRelatedByNarrower(topConceptIndividual,topConcept,listOfTerms);
		     else
		    	 populateChildrenandRelatedByBroader(topConceptIndividual,topConcept,listOfTerms);      
	         sb.append(topConcept.getIndex() + " , "+ topConcept.getData().getLabel() + " , " + topConcept.getData().getIri()).append("\n");
	         sb.append(generateConceptHierarchyTextByOntology(topConcept, displayRelated)); 
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}   
         	     
        return sb.toString();
    } 
    
    @RequestMapping(path = "/{onto}/conceptrelations/{iri}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    public HttpEntity<PagedResources<Individual>> findRelatedConcepts(
    		@ApiParam(value = "ontology ID", required = true)
    		@PathVariable("onto") String ontologyId,
            @ApiParam(value = "encoded concept IRI", required = true)
            @PathVariable("iri") String iri,
            @ApiParam(value = "skos based concept relation type", required = true, allowableValues = "broader, narrower, related")
            @RequestParam(value = "relation_type", required = true, defaultValue = "broader") String relationType,
            Pageable pageable,
            PagedResourcesAssembler assembler) {
    	
    	ontologyId = ontologyId.toLowerCase();
    	List<Individual> related = new ArrayList<Individual>();
    	try {	
    		String decodedIri = UriUtils.decode(iri, "UTF-8");
			Individual individual = ontologyIndividualRepository.findByOntologyAndIri(ontologyId, decodedIri);
			if (individual != null)
				if (individual.getAnnotation().get(relationType) != null)
					for (String iriBroader : (String[]) individual.getAnnotation().get(relationType)) 
						related.add(ontologyIndividualRepository.findByOntologyAndIri(ontologyId, iriBroader));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        final int start = (int)pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), related.size());
        Page<Individual> conceptPage = new PageImpl<>(related.subList(start, end), pageable, related.size());
       
       return new ResponseEntity<>( assembler.toResource(conceptPage), HttpStatus.OK);    	

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
        			 Individual topConceptIndividual = findIndividual(listOfTerms,iriTopConcept);
        			 SKOSConceptNode<Individual> topConcept =  new SKOSConceptNode<Individual>(topConceptIndividual);
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
    
    public List<SKOSConceptNode<Individual>> conceptTreeWithoutTop (String ontologyId, Integer individualCount, boolean narrower){
        Page<Individual> terms = ontologyIndividualRepository.findAllByOntology(ontologyId, new PageRequest(0, individualCount));
        List<Individual> listOfTerms = terms.getContent();       
        Set<String> rootIRIs = new HashSet<String>();
        List<SKOSConceptNode<Individual>> rootIndividuals = new ArrayList<SKOSConceptNode<Individual>>();
        int count = 0;
        if(!narrower) {
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
            
            for (String iri : rootIRIs) {
            	Individual topConceptIndividual = findIndividual(listOfTerms, iri);
        		SKOSConceptNode<Individual> topConcept = new SKOSConceptNode<Individual>(topConceptIndividual);
        		topConcept.setIndex(String.valueOf(++count));
    		    populateChildrenandRelatedByBroader(topConceptIndividual,topConcept,listOfTerms);
        		rootIndividuals.add(topConcept);
            }
            
        } else {
        	for (Individual individual : listOfTerms) {
        		if (individual.getAnnotation().get("narrower") != null) {
        			boolean root = true;
        			for (Individual indiv : listOfTerms) {
        				if (indiv.getAnnotation().get("narrower") != null) {
        					for (String iriNarrower : (String[]) indiv.getAnnotation().get("narrower")) {
        						if (individual.getIri().equals(iriNarrower))
        								root = false;
        					}
        				} 
        			}
        			
        			if(root) {
                		SKOSConceptNode<Individual> topConcept = new SKOSConceptNode<Individual>(individual);
                		topConcept.setIndex(String.valueOf(++count));
        		        populateChildrenandRelatedByNarrower(individual,topConcept,listOfTerms);
        		        rootIndividuals.add(topConcept);
        			}
        		}
        	}
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
			related.setIndex(tree.getIndex()+ ".related");
			tree.addRelated(related);
		}
    	int count = 0;
    	if (individual.getAnnotation().get("narrower") != null)
		for (String iriChild : (String[]) individual.getAnnotation().get("narrower")) {
			Individual childIndividual = findIndividual(listOfTerms,iriChild);
			SKOSConceptNode<Individual> child = new SKOSConceptNode<Individual>(childIndividual);
			child.setIndex(tree.getIndex()+"."+ ++count);			
			populateChildrenandRelatedByNarrower(childIndividual,child,listOfTerms);
			tree.addChild(child);
		}
    }
    
    public void populateChildrenandRelatedByBroader(Individual individual, SKOSConceptNode<Individual> tree, List<Individual> listOfTerms) {
		if (individual.getAnnotation().get("related") != null)
		for (String iriRelated : (String[]) individual.getAnnotation().get("related")) {
			SKOSConceptNode<Individual> related = new SKOSConceptNode<Individual>(findIndividual(listOfTerms,iriRelated));
			related.setIndex(tree.getIndex()+ ".related");
			tree.addRelated(related);
		}
		int count = 0;
		for ( Individual indiv : listOfTerms) {
			if (indiv.getAnnotation().get("broader") != null)
				for (String iriBroader : (String[]) indiv.getAnnotation().get("broader"))
					if(individual.getIri() != null)
						if (individual.getIri().equals(iriBroader)) {
							SKOSConceptNode<Individual> child = new SKOSConceptNode<Individual>(indiv);
							child.setIndex(tree.getIndex()+"."+ ++count);	
							populateChildrenandRelatedByBroader(indiv,child,listOfTerms);
							tree.addChild(child);
						}	
		}
    }
    
    public StringBuilder generateConceptHierarchyTextByOntology(SKOSConceptNode<Individual> rootConcept, boolean displayRelated) {
    	StringBuilder sb = new StringBuilder();
        for (SKOSConceptNode<Individual> childConcept : rootConcept.getChildren()) {
       	     sb.append(childConcept.getIndex() + " , "+ childConcept.getData().getLabel() + " , " + childConcept.getData().getIri()).append("\n");
       	     sb.append(generateConceptHierarchyTextByOntology(childConcept,displayRelated));
        }
        if(displayRelated)
	        for (SKOSConceptNode<Individual> relatedConcept : rootConcept.getRelated()) {
	      	     sb.append(relatedConcept.getIndex() + " , "+ relatedConcept.getData().getLabel() + " , " + relatedConcept.getData().getIri()).append("\n");
	      	     sb.append(generateConceptHierarchyTextByOntology(relatedConcept,displayRelated));
	       }
        return sb;
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource not found")
    @ExceptionHandler(ResourceNotFoundException.class)
    public void handleError(HttpServletRequest req, Exception exception) {
    }

}
