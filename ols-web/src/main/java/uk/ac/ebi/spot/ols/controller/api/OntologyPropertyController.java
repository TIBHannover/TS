package uk.ac.ebi.spot.ols.controller.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import io.swagger.annotations.ApiParam;

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
import uk.ac.ebi.spot.ols.neo4j.model.Property;
import uk.ac.ebi.spot.ols.neo4j.model.Term;
import uk.ac.ebi.spot.ols.neo4j.service.JsTreeBuilder;
import uk.ac.ebi.spot.ols.neo4j.service.OntologyPropertyGraphService;
import uk.ac.ebi.spot.ols.neo4j.service.PropertyJsTreeBuilder;
import uk.ac.ebi.spot.ols.neo4j.service.ViewMode;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Simon Jupp
 * @date 02/11/15
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@Controller
@RequestMapping("/api/ontologies")
public class OntologyPropertyController {

    @Autowired
    private OntologyPropertyGraphService ontologyPropertyGraphService;

    @Autowired
    PropertyAssembler termAssembler;

    @Autowired
    PropertyJsTreeBuilder jsTreeBuilder;

    @RequestMapping(path = "/{onto}/properties", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<PagedResources<Property>> getAllPropertiesByOntology(
            @PathVariable("onto") String ontologyId,
            @RequestParam(value = "iri", required = false) String iri,
            @RequestParam(value = "short_form", required = false) String shortForm,
            @RequestParam(value = "obo_id", required = false) String oboId,
            Pageable pageable,
            PagedResourcesAssembler assembler) {

        Page<Property> terms = null;

        ontologyId = ontologyId.toLowerCase();
        if (iri != null) {
            Property term = ontologyPropertyGraphService.findByOntologyAndIri(ontologyId, iri);
            if (term != null) {
                terms =  new PageImpl<Property>(Arrays.asList(term));
            }
        }
        else if (shortForm != null) {
            Property term = ontologyPropertyGraphService.findByOntologyAndShortForm(ontologyId, shortForm);
            if (term != null) {
                terms =  new PageImpl<Property>(Arrays.asList(term));
            }
        }
        else if (oboId != null) {
            Property term = ontologyPropertyGraphService.findByOntologyAndOboId(ontologyId, oboId);
            if (term != null) {
                terms =  new PageImpl<Property>(Arrays.asList(term));
            }
        }
        else {
            terms = ontologyPropertyGraphService.findAllByOntology(ontologyId, pageable);
        }

        return new ResponseEntity<>( assembler.toResource(terms, termAssembler), HttpStatus.OK);
    }

    @RequestMapping(path = "/{onto}/properties/roots", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<PagedResources<Property>> getRoots(
            @PathVariable("onto") String ontologyId,
            @RequestParam(value = "includeObsoletes", defaultValue = "false", required = false) boolean includeObsoletes,
            Pageable pageable,
            PagedResourcesAssembler assembler
    ) throws ResourceNotFoundException {
        ontologyId = ontologyId.toLowerCase();

        Page<Property> roots = ontologyPropertyGraphService.getRoots(ontologyId, includeObsoletes, pageable);
        if (roots == null) throw  new ResourceNotFoundException();
        return new ResponseEntity<>( assembler.toResource(roots, termAssembler), HttpStatus.OK);
    }

    @RequestMapping(path = "/{onto}/properties/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<Resource<Property>> getProperty(@PathVariable("onto") String ontologyId, @PathVariable("id") String termId) throws ResourceNotFoundException {
        ontologyId = ontologyId.toLowerCase();

        try {
            String decoded = UriUtils.decode(termId, "UTF-8");
            Property term = ontologyPropertyGraphService.findByOntologyAndIri(ontologyId, decoded);
            return new ResponseEntity<>( termAssembler.toResource(term), HttpStatus.OK);
        } catch (UnsupportedEncodingException e) {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(path = "/{onto}/properties/{id}/parents", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<PagedResources<Property>> getParents(@PathVariable("onto") String ontologyId, @PathVariable("id") String termId, Pageable pageable,
                                                    PagedResourcesAssembler assembler) {
        ontologyId = ontologyId.toLowerCase();

        try {
            String decoded = UriUtils.decode(termId, "UTF-8");
            Page<Property> parents = ontologyPropertyGraphService.getParents(ontologyId, decoded, pageable);
            return new ResponseEntity<>( assembler.toResource(parents, termAssembler), HttpStatus.OK);
        }
        catch (UnsupportedEncodingException e) {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(path = "/{onto}/properties/{id}/children", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<PagedResources<Property>> children(@PathVariable("onto") String ontologyId, @PathVariable("id") String termId, Pageable pageable,
                                                  PagedResourcesAssembler assembler) {
        ontologyId = ontologyId.toLowerCase();

        try {
            String decoded = UriUtils.decode(termId, "UTF-8");
            Page<Property> children = ontologyPropertyGraphService.getChildren(ontologyId, decoded, pageable);
            return new ResponseEntity<>( assembler.toResource(children, termAssembler), HttpStatus.OK);
        }
        catch (UnsupportedEncodingException e) {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(path = "/{onto}/properties/{id}/descendants", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<PagedResources<Property>> descendants(@PathVariable("onto") String ontologyId, @PathVariable("id") String termId, Pageable pageable,
                                                     PagedResourcesAssembler assembler) {
        ontologyId = ontologyId.toLowerCase();

        try {
            String decoded = UriUtils.decode(termId, "UTF-8");
            Page<Property> descendants = ontologyPropertyGraphService.getDescendants(ontologyId, decoded, pageable);
            return new ResponseEntity<>( assembler.toResource(descendants, termAssembler), HttpStatus.OK);
        }
        catch (UnsupportedEncodingException e) {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(path = "/{onto}/properties/{id}/ancestors", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<PagedResources<Property>> ancestors(@PathVariable("onto") String ontologyId, @PathVariable("id") String termId, Pageable pageable,
                                                   PagedResourcesAssembler assembler) {
        ontologyId = ontologyId.toLowerCase();

        try {
            String decoded = UriUtils.decode(termId, "UTF-8");
            Page<Property> ancestors = ontologyPropertyGraphService.getAncestors(ontologyId, decoded, pageable);
            return new ResponseEntity<>( assembler.toResource(ancestors, termAssembler), HttpStatus.OK);
        }
        catch (UnsupportedEncodingException e) {
            throw new ResourceNotFoundException();
        }
    }


    @RequestMapping(path = "/{onto}/properties/{id}/jstree/children/{nodeid}", produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<String> graphJsTreeChildren(
            @PathVariable("onto") String ontologyId,
            @PathVariable("id") String termId,
            @PathVariable("nodeid") String nodeId
    ) {
        ontologyId = ontologyId.toLowerCase();

        try {
            String decoded = UriUtils.decode(termId, "UTF-8");

            Object object= jsTreeBuilder.getJsTreeChildren(ontologyId, decoded, nodeId);
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            return new HttpEntity<String>(ow.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        throw new ResourceNotFoundException();
    }

    @RequestMapping(path = "/{onto}/properties/{id}/jstree",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE},
            method = RequestMethod.GET)
    HttpEntity<String> getJsTree(
            @PathVariable("onto") String ontologyId,
            @PathVariable("id") String termId,
            @RequestParam(value = "siblings", defaultValue = "false", required = false) boolean siblings,
            @RequestParam(value = "viewMode", defaultValue = "PreferredRoots", required = false) String viewMode)
    {
        ontologyId = ontologyId.toLowerCase();

        try {
            String decoded = UriUtils.decode(termId, "UTF-8");

            Object object= jsTreeBuilder.getJsTree(ontologyId, decoded, siblings, ViewMode.getFromShortName(viewMode));
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            return new HttpEntity<String>(ow.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        throw new ResourceNotFoundException();
    }
    
    @RequestMapping(path = "/{onto}/propertytree", produces = {MediaType.APPLICATION_JSON_VALUE, 
            MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<List<TreeNode<Property>>> getPropertyHierarchyByOntology(  @PathVariable("onto") String ontologyId,
    @RequestParam(value = "includeObsoletes", defaultValue = "false", required = false) boolean includeObsoletes, 
    @ApiParam(value = "Page Size", required = true)
    @RequestParam(value = "page_size", required = true, defaultValue = "20") Integer pageSize,
    PagedResourcesAssembler assembler){
    	
    	Pageable pageable = new PageRequest(0, pageSize);
    	Page<Property> roots = ontologyPropertyGraphService.getRoots(ontologyId, includeObsoletes, pageable);
    	List<Property> rootPropertyDataList = new ArrayList<Property>();
    	rootPropertyDataList.addAll(roots.getContent());
//    	roots.getContent().forEach(x -> rootPropertyDataList.add(x));
    	List<TreeNode<Property>> rootProperties = new ArrayList<TreeNode<Property>>();
    	
    	while(roots.hasNext()) {
    		roots = ontologyPropertyGraphService.getRoots(ontologyId, includeObsoletes, roots.nextPageable());
    		rootPropertyDataList.addAll(roots.getContent());
//    		roots.getContent().forEach(x -> rootPropertyDataList.add(x));
    	}
    	
    	
    	int count = 0;
    	for (Property rootPropertyData : rootPropertyDataList) {
    		TreeNode<Property> rootProperty =  new TreeNode<Property>(rootPropertyData);
    		rootProperty.setIndex(String.valueOf(++count));
    		populateChildren(ontologyId, rootProperty, pageable);	
    		rootProperties.add(rootProperty);
    	}
    	
        if (roots == null) 
            throw new ResourceNotFoundException("No roots could be found for " + ontologyId );
          return new ResponseEntity<>( rootProperties, HttpStatus.OK);
    }
    
    public void populateChildren(String ontologyId, TreeNode<Property> root, Pageable pageable) {
		String decoded;
		int count = 0;
		try {
			decoded = UriUtils.decode(root.getData().getIri(), "UTF-8");
			Page<Property> children = ontologyPropertyGraphService.getChildren(ontologyId, decoded, pageable);
			
			List<Property> childrenPropertyDataList = new ArrayList<Property>();
			childrenPropertyDataList.addAll(children.getContent());
//			children.getContent().forEach(x -> childrenPropertyDataList.add(x));
	    	while(children.hasNext()) {
	    		children = ontologyPropertyGraphService.getChildren(ontologyId, decoded, children.nextPageable());
	    		childrenPropertyDataList.addAll(children.getContent());
//	    		children.getContent().forEach(x -> childrenPropertyDataList.add(x));
	    	}		
			
			for (Property property : childrenPropertyDataList) {
				TreeNode<Property> child =  new TreeNode<Property>(property);
				child.setIndex(root.getIndex()+"."+ ++count);
				populateChildren(ontologyId, child, pageable);
				root.addChild(child);
			}
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource not found")
    @ExceptionHandler(ResourceNotFoundException.class)
    public void handleError(HttpServletRequest req, Exception exception) {
    }
}
