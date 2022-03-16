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
import java.util.Iterator;
import java.util.List;

/**
 * @author Simon Jupp
 * @date 02/11/15
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@Controller
@RequestMapping("/api/ontologies")
public class OntologyIndividualController {

    @Autowired
    private OntologyIndividualService ontologyIndividualRepository;

    @Autowired
    IndividualAssembler individualAssembler;

    @Autowired
    TermAssembler termAssembler;

    @Autowired
    IndividualJsTreeBuilder jsTreeBuilder;

    @RequestMapping(path = "/{onto}/individuals", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<PagedResources<Individual>> getAllIndividualsByOntology(
            @PathVariable("onto") String ontologyId,
            @RequestParam(value = "iri", required = false) String iri,
            @RequestParam(value = "short_form", required = false) String shortForm,
            @RequestParam(value = "obo_id", required = false) String oboId,
            Pageable pageable,
            PagedResourcesAssembler assembler) {

        Page<Individual> terms = null;

        ontologyId = ontologyId.toLowerCase();
        if (iri != null) {
            Individual term = ontologyIndividualRepository.findByOntologyAndIri(ontologyId, iri);
            if (term != null) {
                terms = new PageImpl<Individual>(Arrays.asList(term));
            }
        } else if (shortForm != null) {
            Individual term = ontologyIndividualRepository.findByOntologyAndShortForm(ontologyId, shortForm);
            if (term != null) {
                terms = new PageImpl<Individual>(Arrays.asList(term));
            }
        } else if (oboId != null) {
            Individual term = ontologyIndividualRepository.findByOntologyAndOboId(ontologyId, oboId);
            if (term != null) {
                terms = new PageImpl<Individual>(Arrays.asList(term));
            }
        } else {
            terms = ontologyIndividualRepository.findAllByOntology(ontologyId, pageable);
            
        }

        return new ResponseEntity<>(assembler.toResource(terms, individualAssembler), HttpStatus.OK);
    }
    
    @RequestMapping(path = "/{onto}/skosconcepthierarchy", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<List<SKOSConceptNode<Individual>>> getSKOSConceptHierarchyByOntology(
            @PathVariable("onto") String ontologyId,
            @RequestParam(value = "individual_count", defaultValue = "1000000") Integer individualCount) {
        return new ResponseEntity<>(conceptTree(ontologyId,individualCount), HttpStatus.OK);
    } 
    
    @RequestMapping(path = "/{onto}/skosconcepts", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<String> getSKOSConceptsByOntology(
            @PathVariable("onto") String ontologyId,
            @RequestParam(value = "individual_count", defaultValue = "1000000") Integer individualCount) {
    	 List<SKOSConceptNode<Individual>> rootIndividuals = conceptTree(ontologyId,individualCount);
         StringBuilder sb = new StringBuilder();
         for (SKOSConceptNode<Individual> root : rootIndividuals) {
        	 sb.append(root.getIndex() + " , "+ root.getLabel() + " , " + root.getIri()).append("\n");
        	 sb.append(getYAMLSKOSConceptHierarchyByOntology(root)); 
         }
         
         return new ResponseEntity<>(sb.toString(), HttpStatus.OK);
    }   
    
    public List<SKOSConceptNode<Individual>> conceptTree (String ontologyId, Integer individualCount){
        Page<Individual> terms = ontologyIndividualRepository.findAllByOntology(ontologyId, new PageRequest(0, individualCount));
        List<Individual> listOfTerms = terms.getContent();       

        List<SKOSConceptNode<Individual>> rootIndividuals = new ArrayList<SKOSConceptNode<Individual>>();
        
        int count = 0;
         for (Individual individual : listOfTerms) {
        	 SKOSConceptNode<Individual> tree = new SKOSConceptNode<Individual>(individual);
        	if (tree.isRoot() && individual.getAnnotation().get("topConceptOf") != null) {
				tree.setIri(individual.getIri());
				tree.setLabel(individual.getLabel());
				tree.setIndex(String.valueOf(++count));
				
				if (individual.getAnnotation().get("broader") != null) {
					for (String iriBroader : (String[]) individual.getAnnotation().get("broader")) {
						SKOSConceptNode<Individual> parent = new SKOSConceptNode<Individual>(findIndividual(listOfTerms,iriBroader));
						parent.setLabel(parent.getData().getLabel());
						parent.setIri(iriBroader);
						tree.addParent(parent);
					}
				}
			
                populateChildrenandRelated(individual,tree,listOfTerms);								
				rootIndividuals.add(tree);
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
    
    public void populateChildrenandRelated(Individual individual, SKOSConceptNode<Individual> tree, List<Individual> listOfTerms ) {
		
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
			populateChildrenandRelated(childIndividual,child,listOfTerms);
			tree.addChild(child);
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

    @RequestMapping(path = "/{onto}/individuals/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<Resource<Individual>> getIndividual(@PathVariable("onto") String ontologyId, @PathVariable("id") String termId) throws ResourceNotFoundException {
        ontologyId = ontologyId.toLowerCase();

        try {
            String decoded = UriUtils.decode(termId, "UTF-8");
            Individual term = ontologyIndividualRepository.findByOntologyAndIri(ontologyId, decoded);
            return new ResponseEntity<>(individualAssembler.toResource(term), HttpStatus.OK);
        } catch (UnsupportedEncodingException e) {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(path = "/{onto}/individuals/{id}/types", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<PagedResources<Term>> getDirectTypes(@PathVariable("onto") String ontologyId, @PathVariable("id") String termId, Pageable pageable,
                                                    PagedResourcesAssembler assembler) {
        ontologyId = ontologyId.toLowerCase();

        try {
            String decoded = UriUtils.decode(termId, "UTF-8");
            Page<Term> parents = ontologyIndividualRepository.getDirectTypes(ontologyId, decoded, pageable);
            return new ResponseEntity<>(assembler.toResource(parents, termAssembler), HttpStatus.OK);
        } catch (UnsupportedEncodingException e) {
            throw new ResourceNotFoundException();
        }
    }


    @RequestMapping(path = "/{onto}/individuals/{id}/alltypes", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<PagedResources<Property>> ancestors(@PathVariable("onto") String ontologyId, @PathVariable("id") String termId, Pageable pageable,
                                                   PagedResourcesAssembler assembler) {
        ontologyId = ontologyId.toLowerCase();

        try {
            String decoded = UriUtils.decode(termId, "UTF-8");
            Page<Term> ancestors = ontologyIndividualRepository.getAllTypes(ontologyId, decoded, pageable);
            return new ResponseEntity<>(assembler.toResource(ancestors, termAssembler), HttpStatus.OK);
        } catch (UnsupportedEncodingException e) {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(path = "/{onto}/individuals/{id}/jstree", produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<String> getJsTree(@PathVariable("onto") String ontologyId, @PathVariable("id") String termId) {
        ontologyId = ontologyId.toLowerCase();

        try {
            String decoded = UriUtils.decode(termId, "UTF-8");

            Object object = jsTreeBuilder.getJsTree(ontologyId, decoded, false);
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            return new HttpEntity<String>(ow.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        throw new ResourceNotFoundException();
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource not found")
    @ExceptionHandler(ResourceNotFoundException.class)
    public void handleError(HttpServletRequest req, Exception exception) {
    }

}
