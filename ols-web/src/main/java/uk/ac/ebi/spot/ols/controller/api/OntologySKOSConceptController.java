package uk.ac.ebi.spot.ols.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import uk.ac.ebi.spot.ols.neo4j.model.Individual;
import uk.ac.ebi.spot.ols.neo4j.model.TreeNode;
import uk.ac.ebi.spot.ols.neo4j.service.OntologyIndividualService;

import javax.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Erhun Giray TUNCAY 
 * @date 18/03/2022
 * NFDI4ING Terminology Service Team, TIB
 */
@Controller
@RequestMapping("/api/ontologies")
@Api(value = "ontologyskos", description = "SKOS concept hierarchies and relations extracted from individuals (instances) from a particular ontology in this service")
public class OntologySKOSConceptController {

    @Autowired
    private OntologyIndividualService ontologyIndividualService;
    
    @ApiOperation(value = "Get complete SKOS concept hierarchy or only top concepts based on alternative top concept identification methods and concept relations", notes = "If only top concepts are identified, they can be used to extract the following levels of the concept tree one by one using the /{onto}/conceptrelations/{iri} method with broader or narrower concept relations.")
    @RequestMapping(path = "/{onto}/concepthierarchy", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<List<TreeNode<Individual>>> getSKOSConceptHierarchyByOntology(
    	    @ApiParam(value = "ontology ID", required = true)
    	    @PathVariable("onto") String ontologyId,
    	    @ApiParam(value = "infer top concepts by schema (hasTopConcept) or  TopConceptOf property or broader/narrower relationships", required = true)
            @RequestParam(value = "find_roots", required = true, defaultValue = "SCHEMA") TopConceptEnum topConceptIdentification,
            @ApiParam(value = "infer from narrower or broader relationships", required = true)
            @RequestParam(value = "narrower", required = true, defaultValue = "false") boolean narrower,
            @ApiParam(value = "Extract the whole tree with children or only the top concepts", required = true)
            @RequestParam(value = "with_children", required = true, defaultValue = "false") boolean withChildren,
            @ApiParam(value = "Page size to retrieve individuals", required = true)
            @RequestParam(value = "page_size", required = true, defaultValue = "20") Integer pageSize) {
    	ontologyId = ontologyId.toLowerCase();
    	if (TopConceptEnum.RELATIONSHIPS == topConceptIdentification)
    		return new ResponseEntity<>(ontologyIndividualService.conceptTreeWithoutTop(ontologyId,pageSize, narrower, withChildren), HttpStatus.OK);
    	else
    		return new ResponseEntity<>(ontologyIndividualService.conceptTree(ontologyId,pageSize,TopConceptEnum.SCHEMA == topConceptIdentification, narrower, withChildren), HttpStatus.OK);
    } 
    
    @ApiOperation(value = "Display complete SKOS concept hierarchy or only top concepts based on alternative top concept identification methods and concept relations", notes = "If only top concepts are identified, they can be used to extract the following levels of the concept tree one by one using the /{onto}/displayconceptrelations/{iri} method with broader or narrower concept relations.")
    @RequestMapping(path = "/{onto}/displayconcepthierarchy", produces = {MediaType.TEXT_PLAIN_VALUE}, method = RequestMethod.GET)
    @ResponseBody
    HttpEntity<String> displaySKOSConceptHierarchyByOntology(
    	    @ApiParam(value = "ontology ID", required = true)
    	    @PathVariable("onto") String ontologyId,
    		@ApiParam(value = "infer top concepts by schema (hasTopConcept) or  TopConceptOf property or broader/narrower relationships", required = true)
    	    @RequestParam(value = "find_roots", required = true, defaultValue = "SCHEMA") TopConceptEnum topConceptIdentification,
            @ApiParam(value = "infer from narrower or broader relationships", required = true)
            @RequestParam(value = "narrower", required = true, defaultValue = "false") boolean narrower,
            @ApiParam(value = "Extract the whole tree with children or only the top concepts", required = true)
            @RequestParam(value = "with_children", required = true, defaultValue = "false") boolean withChildren,
            @ApiParam(value = "display related concepts", required = true)
            @RequestParam(value = "display_related", required = true, defaultValue = "false") boolean displayRelated,
            @ApiParam(value = "Page size to retrieve individuals", required = true)
            @RequestParam(value = "page_size", required = true, defaultValue = "20") Integer pageSize) {
    	 ontologyId = ontologyId.toLowerCase();
     	 List<TreeNode<Individual>> rootIndividuals = null;
    	 if(TopConceptEnum.RELATIONSHIPS == topConceptIdentification)
    		 rootIndividuals = ontologyIndividualService.conceptTreeWithoutTop(ontologyId,pageSize, narrower, withChildren);
    	 else
    		 rootIndividuals = ontologyIndividualService.conceptTree(ontologyId,pageSize,TopConceptEnum.SCHEMA == topConceptIdentification,narrower, withChildren);
         StringBuilder sb = new StringBuilder();
         for (TreeNode<Individual> root : rootIndividuals) {
        	 sb.append(root.getIndex() + " , "+ root.getData().getLabel() + " , " + root.getData().getIri()).append("\n");
        	 sb.append(generateConceptHierarchyTextByOntology(root, displayRelated)); 
         }
         
         return new HttpEntity<String>(sb.toString());
    }  
    
    @ApiOperation(value = "Get partial SKOS concept hierarchy based on the encoded iri of the designated top concept")
    @RequestMapping(path = "/{onto}/concepthierarchy/{iri}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<TreeNode<Individual>> getSKOSConceptHierarchyByOntologyAndIri(
    	    @ApiParam(value = "ontology ID", required = true)
    	    @PathVariable("onto") String ontologyId,
            @ApiParam(value = "encoded concept IRI", required = true)
            @PathVariable("iri") String iri,
            @ApiParam(value = "infer from narrower or broader relationships", required = true)
            @RequestParam(value = "narrower", required = true, defaultValue = "false") boolean narrower,
            @ApiParam(value = "index value for the root term", required = true)
            @RequestParam(value = "index", required = true, defaultValue = "1") String index,
            @ApiParam(value = "Page size to retrieve individuals", required = true)
            @RequestParam(value = "page_size", required = true, defaultValue = "20") Integer pageSize) {
    	ontologyId = ontologyId.toLowerCase();
    	TreeNode<Individual> topConcept = new TreeNode<Individual>(new Individual());
    	String decodedIri;
		try {
			decodedIri = UriUtils.decode(iri, "UTF-8");
			topConcept = ontologyIndividualService.conceptSubTree(ontologyId, decodedIri, narrower, index, pageSize);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	topConcept = ontologyIndividualService.conceptSubTree(ontologyId, iri, narrower, index, pageSize);
        if (topConcept.getData().getIri() == null) 
            throw new ResourceNotFoundException("No roots could be found for " + ontologyId );
        return new ResponseEntity<>(topConcept, HttpStatus.OK);
    } 
    
    @ApiOperation(value = "Display partial SKOS concept hierarchy based on the encoded iri of the designated top concept")
    @RequestMapping(path = "/{onto}/displayconcepthierarchy/{iri}", produces = {MediaType.TEXT_PLAIN_VALUE}, method = RequestMethod.GET)
    @ResponseBody
    HttpEntity<String> displaySKOSConceptHierarchyByOntologyAndIri(
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
            @ApiParam(value = "Page size to retrieve individuals", required = true)
            @RequestParam(value = "page_size", required = true, defaultValue = "20") Integer pageSize) {
	    	ontologyId = ontologyId.toLowerCase();
	    	TreeNode<Individual> topConcept = new TreeNode<Individual>(new Individual());
	    	String decodedIri;
	    	StringBuilder sb = new StringBuilder();
			try {
				decodedIri = UriUtils.decode(iri, "UTF-8");
				topConcept = ontologyIndividualService.conceptSubTree(ontologyId, decodedIri, narrower, index, pageSize);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
   	        
        	sb.append(topConcept.getIndex() + " , "+ topConcept.getData().getLabel() + " , " + topConcept.getData().getIri()).append("\n");
	        sb.append(generateConceptHierarchyTextByOntology(topConcept, displayRelated));   
	        
            return new HttpEntity<String>(sb.toString());
    } 
    
    @ApiOperation(value = "Broader, Narrower and Related concept relations of a concept are listed in JSON if the concept iri is provided in encoded format.")
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
			related = ontologyIndividualService.findRelated(ontologyId, decodedIri, relationType);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        final int start = (int)pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), related.size());
        Page<Individual> conceptPage = new PageImpl<>(related.subList(start, end), pageable, related.size());
       
        return new ResponseEntity<>( assembler.toResource(conceptPage), HttpStatus.OK);    	

    }
    @ApiOperation(value = "Broader, Narrower and Related concept relations of a concept are displayed as text if the concept iri is provided in encoded format.")
    @RequestMapping(path = "/{onto}/displayconceptrelations/{iri}", produces = {MediaType.TEXT_PLAIN_VALUE}, method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<String> displayRelatedConcepts(
    		@ApiParam(value = "ontology ID", required = true)
    		@PathVariable("onto") String ontologyId,
            @ApiParam(value = "encoded concept IRI", required = true)
            @PathVariable("iri") String iri,
            @ApiParam(value = "skos based concept relation type", required = true, allowableValues = "broader, narrower, related")
            @RequestParam(value = "relation_type", required = true, defaultValue = "broader") String relationType,
            Pageable pageable,
            PagedResourcesAssembler assembler) {
    	StringBuilder sb = new StringBuilder();
    	ontologyId = ontologyId.toLowerCase();
    	List<Individual> related = new ArrayList<Individual>();
    	try {
			String decodedIri = UriUtils.decode(iri, "UTF-8");
			related = ontologyIndividualService.findRelated(ontologyId, decodedIri, relationType);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        final int start = (int)pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), related.size());
        Page<Individual> conceptPage = new PageImpl<>(related.subList(start, end), pageable, related.size());
        int count = 0;
        for (Individual individual : conceptPage.getContent())
        	sb.append(++count).append(" , ").append(individual.getLabel()).append(" , ").append(individual.getIri()).append("\n");
              
        return new HttpEntity<>( sb.toString());    	

    }
    
    public StringBuilder generateConceptHierarchyTextByOntology(TreeNode<Individual> rootConcept, boolean displayRelated) {
    	StringBuilder sb = new StringBuilder();
        for (TreeNode<Individual> childConcept : rootConcept.getChildren()) {
       	     sb.append(childConcept.getIndex() + " , "+ childConcept.getData().getLabel() + " , " + childConcept.getData().getIri()).append("\n");
       	     sb.append(generateConceptHierarchyTextByOntology(childConcept,displayRelated));
        }
        if(displayRelated)
	        for (TreeNode<Individual> relatedConcept : rootConcept.getRelated()) {
	      	     sb.append(relatedConcept.getIndex() + " , "+ relatedConcept.getData().getLabel() + " , " + relatedConcept.getData().getIri()).append("\n");
	      	     sb.append(generateConceptHierarchyTextByOntology(relatedConcept,displayRelated));
	       }
        return sb;
    }
    
    @RequestMapping(method = RequestMethod.GET, produces = {MediaType.TEXT_PLAIN_VALUE}, value = "/removeConceptTreeCache")
    public HttpEntity<String> removeConceptTreeCache() {
    	return new HttpEntity<String>(ontologyIndividualService.removeConceptTreeCache());
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource not found")
    @ExceptionHandler(ResourceNotFoundException.class)
    public void handleError(HttpServletRequest req, Exception exception) {
    }

}
