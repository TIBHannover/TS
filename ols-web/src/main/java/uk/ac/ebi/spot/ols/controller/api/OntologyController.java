package uk.ac.ebi.spot.ols.controller.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.RepositoryLinksResource;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.*;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import io.swagger.annotations.ApiOperation;
import uk.ac.ebi.spot.ols.exception.ErrorMessage;
import uk.ac.ebi.spot.ols.model.OntologyDocument;
import uk.ac.ebi.spot.ols.neo4j.model.Term;
import uk.ac.ebi.spot.ols.neo4j.service.OntologyTermGraphService;
import uk.ac.ebi.spot.ols.service.OntologyRepositoryService;
import uk.ac.ebi.spot.ols.util.OLSEnv;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Simon Jupp
 * @date 19/08/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@Controller
@RequestMapping("/api/ontologies")
@ExposesResourceFor(OntologyDocument.class)
public class OntologyController implements
        ResourceProcessor<RepositoryLinksResource> {

    private Logger log = LoggerFactory.getLogger(getClass());

    public Logger getLog() {
        return log;
    }

    @Autowired
    private OntologyRepositoryService ontologyRepositoryService;

    @Autowired DocumentAssembler documentAssembler;

    @Autowired TermAssembler termAssembler;

    @Override
    public RepositoryLinksResource process(RepositoryLinksResource resource) {
        resource.add(ControllerLinkBuilder.linkTo(OntologyController.class).withRel("ontologies"));
        return resource;
    }
    @ApiOperation(value = "List all ontologies")
    @RequestMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<PagedResources<OntologyDocument>> getOntologies(
            @PageableDefault(size = 20, page = 0) Pageable pageable,
            PagedResourcesAssembler assembler
    ) throws ResourceNotFoundException {
        Page<OntologyDocument> document = ontologyRepositoryService.getAllDocuments(pageable);
        return new ResponseEntity<>( assembler.toResource(document, documentAssembler), HttpStatus.OK);
    }
    
    @ApiOperation(value = "Filter list of ontologies by subject")
    @RequestMapping(path = "/subject/{subj}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<PagedResources<OntologyDocument>> filterOntologiesBySubject(@PathVariable("subj") String subject,
            @PageableDefault(size = 20, page = 0) Pageable pageable,
            PagedResourcesAssembler assembler
    ) throws ResourceNotFoundException {
    	List<OntologyDocument> temp = new ArrayList<OntologyDocument>();
    	 for (OntologyDocument ontologyDocument : ontologyRepositoryService.getAllDocuments(new Sort(new Sort.Order(Sort.Direction.ASC, "ontologyId")))) {
    		if(ontologyDocument.getConfig().getSubjects().contains(subject))
    			temp.add(ontologyDocument);
		}
        
        final int start = (int)pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), temp.size());
        Page<OntologyDocument> document = new PageImpl<>(temp.subList(start, end), pageable, temp.size());
       
       return new ResponseEntity<>( assembler.toResource(document, documentAssembler), HttpStatus.OK);
    }
    
    @ApiOperation(value = "Filter list of ontologies by a particular classification schema")
    @RequestMapping(path = "/classification/{schema}/{classification}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<PagedResources<OntologyDocument>> filterOntologiesByClassification(@PathVariable("schema") String schema, @PathVariable("classification") String classification,
            @PageableDefault(size = 20, page = 0) Pageable pageable,
            PagedResourcesAssembler assembler
    ) throws ResourceNotFoundException {
    	
    	String[] cls = classification.split(",");    	
    	
    	List<OntologyDocument> temp = new ArrayList<OntologyDocument>();
    	 for (OntologyDocument ontologyDocument : ontologyRepositoryService.getAllDocuments(new Sort(new Sort.Order(Sort.Direction.ASC, "ontologyId")))) {
    		for(HashMap<String, ArrayList<String>> classificationSchema : ontologyDocument.getConfig().getClassifications()) {
    			if(classificationSchema.containsKey(schema))
    				for (int i = 0;i<cls.length;i++)
    				  if (classificationSchema.get(schema).contains(cls[i])) {
    					  temp.add(ontologyDocument);
    					  break;
    				  }
    			    
    			}
		}
        
        final int start = (int)pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), temp.size());
        Page<OntologyDocument> document = new PageImpl<>(temp.subList(start, end), pageable, temp.size());
       
       return new ResponseEntity<>( assembler.toResource(document, documentAssembler), HttpStatus.OK);
    }

    @ApiOperation(value = "Retrieve a particular ontology")
    @RequestMapping(path = "/{onto}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<Resource<OntologyDocument>> getOntology(@PathVariable("onto") String ontologyId) throws ResourceNotFoundException {
        ontologyId = ontologyId.toLowerCase();
        OntologyDocument document = ontologyRepositoryService.get(ontologyId);
        if (document == null) throw new ResourceNotFoundException();
        return new ResponseEntity<>( documentAssembler.toResource(document), HttpStatus.OK);
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource not found")
    @ExceptionHandler(ResourceNotFoundException.class)
    public void handleError(HttpServletRequest req, Exception exception) {
    }



}
