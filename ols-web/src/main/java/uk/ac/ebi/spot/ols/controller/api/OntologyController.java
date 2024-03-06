package uk.ac.ebi.spot.ols.controller.api;

import org.apache.commons.collections.map.MultiKeyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
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
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import uk.ac.ebi.spot.ols.entities.Issue;
import uk.ac.ebi.spot.ols.entities.IssueFilterEnum;
import uk.ac.ebi.spot.ols.entities.Release;
import uk.ac.ebi.spot.ols.entities.RepoFilterEnum;
import uk.ac.ebi.spot.ols.model.OntologyDocument;
import uk.ac.ebi.spot.ols.service.OntologyRepositoryService;
import uk.ac.ebi.spot.ols.service.RepoMetadataService;
import uk.ac.ebi.spot.ols.model.SummaryInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Erhun Giray TUNCAY
 * @date 05/07/2022
 * NFDI4ING Terminology Service Team, TIB
 */
@Controller
@RequestMapping("/api/ontologies")
@Api(value = "ontology", description = "The Ontologies resources are used to list ontologies in this service")
@ExposesResourceFor(OntologyDocument.class)
public class OntologyController implements
        ResourceProcessor<RepositoryLinksResource> {

    private Logger log = LoggerFactory.getLogger(getClass());

    public Logger getLog() {
        return log;
    }

    @Autowired
    private OntologyRepositoryService ontologyRepositoryService;

    @Autowired
    RepoMetadataService repoMetadataService;

    @Autowired DocumentAssembler documentAssembler;

    @Autowired TermAssembler termAssembler;

    @InitBinder()
    public void initBinder(WebDataBinder binder) throws Exception
    {
       binder.registerCustomEditor(Collection.class, new CustomCollectionEditor(List.class));
    }

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

    @ApiOperation(value = "List available schema keys")
    @RequestMapping(path = "/schemakeys", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<PagedResources<String>> getAvailableSchemaKeys(
            @PageableDefault(size = 20, page = 0) Pageable pageable,
            PagedResourcesAssembler assembler
    ) throws ResourceNotFoundException {
    	Set<String> temp = new HashSet<String>();

        try {

        	for (OntologyDocument document : ontologyRepositoryService.getAllDocuments(new Sort(new Sort.Order(Sort.Direction.ASC, "ontologyId")))) {
				document.getConfig().getClassifications().forEach(x -> temp.addAll(x.keySet()));
			}
        } catch (Exception e) {
        }

        List<String> tempList = new ArrayList<String>();
        tempList.addAll(temp);

        final int start = (int)pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), temp.size());
        Page<String> document = new PageImpl<>(tempList.subList(start, end), pageable, temp.size());

       return new ResponseEntity<>( assembler.toResource(document), HttpStatus.OK);
    }

    @ApiOperation(value = "List available classification values for particular schema keys", notes = "Possible schema keys can be inquired with /api/ontologies/schemakeys method.")
    @RequestMapping(path = "/schemavalues", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<PagedResources<String>> getClassificationsForSchemas(
    		@RequestParam(value = "schema", required = true) Collection<String> schemas,
            @PageableDefault(size = 20, page = 0) Pageable pageable,
            PagedResourcesAssembler assembler
    ) throws ResourceNotFoundException {
    	Set<String> temp = new HashSet<String>();

        try {
        	for (OntologyDocument document : ontologyRepositoryService.getAllDocuments(new Sort(new Sort.Order(Sort.Direction.ASC, "ontologyId")))) {
				document.getConfig().getClassifications().forEach(x -> x.forEach((k, v) -> {if (schemas.contains(k)) if (v != null) if (!v.isEmpty()) temp.addAll(v);} ));
			}
        } catch (Exception e) {
        }

        List<String> tempList = new ArrayList<String>();
        tempList.addAll(temp);

        final int start = (int)pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), temp.size());
        Page<String> document = new PageImpl<>(tempList.subList(start, end), pageable, temp.size());

       return new ResponseEntity<>( assembler.toResource(document), HttpStatus.OK);
    }


    @ApiOperation(value = "Filter list of ontologies by particular schema keys and classification values", notes = "Possible schema keys and possible classification values of particular keys can be inquired with /api/ontologies/schemakeys and /api/ontologies/schemavalues methods respectively.")
    @RequestMapping(path = "/filterby", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<PagedResources<OntologyDocument>> filterOntologiesByClassification(
    		@RequestParam(value = "schema", required = true) Collection<String> schemas,
    		@RequestParam(value = "classification", required = true) Collection<String> classifications,
    		@ApiParam(value = "Set to true (default setting is false) for intersection (default behavior is union) of classifications.")
    		@RequestParam(value = "exclusive", required = false, defaultValue = "false") boolean exclusive,
            @PageableDefault(size = 20, page = 0) Pageable pageable,
            PagedResourcesAssembler assembler
    ) throws ResourceNotFoundException {

        Page<OntologyDocument> document = ontologyRepositoryService.getAllDocuments(pageable, schemas, classifications, exclusive);

        return new ResponseEntity<>( assembler.toResource(document, documentAssembler), HttpStatus.OK);
    }

    @ApiOperation(value = "Get Schema and Classifiction based Statistics", notes = "Possible schema keys and possible classification values of particular keys can be inquired with /api/ontologies/schemakeys and /api/ontologies/schemavalues methods respectively.")
    @RequestMapping(path = "/getstatisticsbyclassification", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<SummaryInfo> getStatisticsByClassification(
    		@RequestParam(value = "schema", required = true) Collection<String> schemas,
    		@RequestParam(value = "classification", required = true) Collection<String> classifications,
    		@ApiParam(value = "Set to true (default setting is false) for intersection (default behavior is union) of classifications.")
    		@RequestParam(value = "exclusive", required = false, defaultValue = "false") boolean exclusive
    ) throws ResourceNotFoundException {
       return new ResponseEntity<>( ontologyRepositoryService.getClassificationMetadata(schemas,classifications, exclusive), HttpStatus.OK);
    }


    @ApiOperation(value = "Get Schema based Statistics", notes = "All schemas with their respective classifications can be computed if a schema is not specified")
    @RequestMapping(path = "/getstatisticsbyschema", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<MultiKeyMap> getStatisticsBySchema(
    		@RequestParam(value = "schema", required = false) Collection<String> schemas
    ) throws ResourceNotFoundException {
    	MultiKeyMap summaries = new MultiKeyMap();
    	Collection<String> keys = new HashSet<String>();
    	if(schemas == null) {
    		
            try {
            	for (OntologyDocument document : ontologyRepositoryService.getAllDocuments(new Sort(new Sort.Order(Sort.Direction.ASC, "ontologyId")))) {
    				document.getConfig().getClassifications().forEach(x -> keys.addAll(x.keySet()));
    			}
            } catch (Exception e) {
            }
    	} else {
    		keys.addAll(schemas);
    	}
    	
    	for (String key : keys) {
    		Set<String> values = new HashSet<String>();

            try {
            	for (OntologyDocument document : ontologyRepositoryService.getAllDocuments(new Sort(new Sort.Order(Sort.Direction.ASC, "ontologyId")))) {
    				document.getConfig().getClassifications().forEach(x -> x.forEach((k, v) -> {if (key.equals(k)) if (v != null) if (!v.isEmpty()) values.addAll(v);} ));
    			}
            } catch (Exception e) {
            }
            
            for (String value : values) {
            	summaries.put(key,value, ontologyRepositoryService.getClassificationMetadata(Collections.singleton(key),Collections.singleton(value), false));
            }    
    	}

       return new ResponseEntity<>( summaries, HttpStatus.OK);
    }


    @ApiOperation(value = "Get Whole System Statistics", notes = "Components in all ontologies are taken into consideration")
    @RequestMapping(path = "/getstatistics", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<SummaryInfo> getStatistics() throws ResourceNotFoundException {
       return new ResponseEntity<>( new SummaryInfo(ontologyRepositoryService.getLastUpdated(),ontologyRepositoryService.getNumberOfOntologies(), ontologyRepositoryService.getNumberOfTerms(), ontologyRepositoryService.getNumberOfProperties(), ontologyRepositoryService.getNumberOfIndividuals(),"" ), HttpStatus.OK);
    }

    @ApiOperation(value = "Retrieve a particular ontology")
    @RequestMapping(path = "/{onto}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<Resource<OntologyDocument>> getOntology(@ApiParam(value = "The ontology id in this service", required = true) @PathVariable("onto") String ontologyId) throws ResourceNotFoundException {
        ontologyId = ontologyId.toLowerCase();
        OntologyDocument document = ontologyRepositoryService.get(ontologyId);
        if (document == null) throw new ResourceNotFoundException();
        return new ResponseEntity<>( documentAssembler.toResource(document), HttpStatus.OK);
    }

    @ApiOperation(value = "Retrieve the releases from the repo metadata of a particular ontology", notes = "Mapping files or ontologies are identified based on a comparison with an existing ontologyID from the terminology service. The file name and ontologyID are refined to be lowercase and alphanumeric before the comparison.")
    @RequestMapping(path = "/{onto}/releases", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<List<Release>> getOntologyReleases(
    		@ApiParam(value = "The ontology id in this service", required = true) @PathVariable("onto") String ontologyId,
    		@ApiParam(value = "External personal access token for the API call", required = false)
    		@RequestParam(value = "externalToken", required = false) String externalToken,
    		@ApiParam(value = "Filtering criteria for the files in the respective repo", required = true)
    	    @RequestParam(value = "filter", required = true, defaultValue = "ALL_FILES") RepoFilterEnum filter) throws ResourceNotFoundException {
        ontologyId = ontologyId.toLowerCase();
        OntologyDocument document = ontologyRepositoryService.get(ontologyId);
        if (document == null) throw new ResourceNotFoundException();

        if(document.getConfig().getRepoUrl() != null)
            if(document.getConfig().getRepoUrl().startsWith("http") && document.getConfig().getRepoUrl().contains("github")) {
            	return new ResponseEntity<>( repoMetadataService.releasesGithubREST(document.getConfig().getRepoUrl(),externalToken,filter, ontologyId), HttpStatus.OK);
            } else if (document.getConfig().getRepoUrl().startsWith("http"))
            	return new ResponseEntity<>( repoMetadataService.releasesGitlabREST(document.getConfig().getRepoUrl(),externalToken,filter, ontologyId), HttpStatus.OK);
        return new ResponseEntity<>( new ArrayList<Release>(), HttpStatus.OK);
    }


    @ApiOperation(value = "Retrieve the issues from the repo metadata of a particular ontology", notes = "It is further possible to filter issues based on their state (open(ed), closed, all")
    @RequestMapping(path = "/{onto}/issues", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<List<Issue>> getOntologyIssues(
            @ApiParam(value = "The ontology id in this service", required = true) @PathVariable("onto") String ontologyId,
            @ApiParam(value = "External personal access token for the API call", required = false)
    		@RequestParam(value = "externalToken", required = false) String externalToken,
            @ApiParam(value = "Filtering criteria based on the state of the issue", required = true)
            @RequestParam(value = "filter", required = true, defaultValue = "OPEN") IssueFilterEnum filter) throws ResourceNotFoundException {
        ontologyId = ontologyId.toLowerCase();
        OntologyDocument document = ontologyRepositoryService.get(ontologyId);
        if (document == null) throw new ResourceNotFoundException();

        if(document.getConfig().getRepoUrl() != null)
            if(document.getConfig().getRepoUrl().startsWith("http") && document.getConfig().getRepoUrl().contains("github")) {
            	return new ResponseEntity<>( repoMetadataService.issuesGithubREST(document.getConfig().getRepoUrl(),externalToken,filter), HttpStatus.OK);
            } else if (document.getConfig().getRepoUrl().startsWith("http"))
            	return new ResponseEntity<>( repoMetadataService.issuesGitlabREST(document.getConfig().getRepoUrl(),externalToken,filter), HttpStatus.OK);
        return new ResponseEntity<>( new ArrayList<Issue>(), HttpStatus.OK);
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource not found")
    @ExceptionHandler(ResourceNotFoundException.class)
    public void handleError(HttpServletRequest req, Exception exception) {
    }


}
