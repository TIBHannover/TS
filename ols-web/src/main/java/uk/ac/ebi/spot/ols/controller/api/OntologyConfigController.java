package uk.ac.ebi.spot.ols.controller.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.swagger.annotations.ApiOperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.spot.ols.config.OntologyResourceConfig;
import uk.ac.ebi.spot.ols.entities.UserOntology;
import uk.ac.ebi.spot.ols.entities.UserOntologyUtilities;
import uk.ac.ebi.spot.ols.entities.YamlBasedPersistence;
import uk.ac.ebi.spot.ols.model.OntologyDocument;
import uk.ac.ebi.spot.ols.repositories.UserOntologyRepository;
import uk.ac.ebi.spot.ols.service.OntologyRepositoryService;
import uk.ac.ebi.spot.ols.util.ReasonerType;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.net.URI;
import java.util.*;

/**
 * @author Simon Jupp
 * @date 10/07/2019
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@RestController
@RequestMapping("/api/ols-config")
public class OntologyConfigController {

    private Logger log = LoggerFactory.getLogger(getClass());

    public Logger getLog() {
        return log;
    }

    @Autowired
    private OntologyRepositoryService ontologyRepositoryService;
    
    @Autowired
    private UserOntologyRepository userOntologyRepository;

    @RequestMapping(path = "", produces = {"text/yaml"}, method = RequestMethod.GET)
    String getOntologies(
            @RequestParam(value = "ids", required = false, defaultValue = "") Collection<String> ids
    ) {

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        List<OntologyResourceConfig> configs = new ArrayList<OntologyResourceConfig>();
        for (OntologyDocument document : ontologyRepositoryService.getAllDocuments()) {

            if (ids.isEmpty()) {
                OntologyResourceConfigFormatter ex = new OntologyResourceConfigFormatter(document.getConfig());
                configs.add(ex);
            }
            else if (ids.contains(document.getOntologyId())) {
                OntologyResourceConfigFormatter ex = new OntologyResourceConfigFormatter(document.getConfig());
                configs.add(ex);
            }
        }

        try {
            return mapper.writeValueAsString(new OntologyResourceConfigWrapper(configs));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return "";

    }

    @ApiOperation(value = "Extract metadata of an ontology from its PURL in YAML format")
    @RequestMapping(path = "/plain-metadata-extractor", produces = {"text/yaml"}, method = RequestMethod.GET)
    String extractPlainMetaData(
            @RequestParam(value = "PURL", required = true, defaultValue = "") String PURL
    ) {
    	UserOntology userOntology = new UserOntology();
    	userOntology.setPURL(PURL);
        return YamlBasedPersistence.singleSuggestionYAMLDumpWriter(UserOntologyUtilities.extractMetaData(userOntology), false);

    }
    
    @ApiOperation(value = "Extract metadata of an ontology from its PURL as a Resource in JSON format")
    @RequestMapping(path = "/metadata-extractor", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<Resource<UserOntology>> extractMetaData(
            @RequestParam(value = "PURL", required = true, defaultValue = "") String PURL
    ) {
    	UserOntology userOntology = new UserOntology();
    	userOntology.setPURL(PURL);
    	
    	Resource<UserOntology> resourceUserOntology = new Resource<UserOntology>(UserOntologyUtilities.extractMetaData(userOntology));

        final ControllerLinkBuilder lb = ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder.methodOn(OntologyConfigController.class).extractMetaData(PURL));
        resourceUserOntology.add(lb.withSelfRel());	
        return new ResponseEntity<>( resourceUserOntology, HttpStatus.OK);
    }
    
    @ApiOperation(value = "Add an ontology suggestion and return it in YAML format")
    @RequestMapping(path = "/add-suggestion", produces = {"text/yaml"}, method = RequestMethod.GET)
    String addSuggestion(
    		@RequestParam(value = "name", required = true) String name,
    		@RequestParam(value = "PURL", required = true) String PURL,
    		@RequestParam(value = "URI", required = false) String URI,
    		@RequestParam(value = "licenseURL", required = false) String licenseURL,
    		@RequestParam(value = "licenseLogo", required = false) String licenseLogo,
            @RequestParam(value = "licenseLabel", required = false) String licenseLabel,
    		@RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "creator", required = false) List<String> creator,
            @RequestParam(value = "homePage", required = false) String homePage,
    		@RequestParam(value = "tracker", required = false) String tracker,
    		@RequestParam(value = "mailingList", required = false) String mailingList,
    		@RequestParam(value = "preferredPrefix", required = true) String preferredPrefix,
    		@RequestParam(value = "baseURI", required = false) String baseURI,
            @RequestParam(value = "reasoner", required = false) String reasoner,
    		@RequestParam(value = "labelProperty", required = false) String labelProperty,
    		@RequestParam(value = "definitionProperty", required = false) List<String> definitionProperty,
    		@RequestParam(value = "synanymProperty", required = false) List<String> synonymProperty,
    		@RequestParam(value = "hierarchicalProperty", required = false) List<String> hierarchicalProperty,
    		@RequestParam(value = "hiddenProperty", required = false) List<String> hiddenProperty,
    		@RequestParam(value = "oboSlims", required = false) boolean oboSlims,
    		@RequestParam(value = "preferredRootTerm", required = false) List<String> preferredRootTerm,
    		@RequestParam(value = "logo", required = false) String logo,
    		@RequestParam(value = "foundary", required = false) boolean foundary,
    		@RequestParam(value = "approval", required = false) String approval,
//    		@RequestParam(value = "addedBy", required = false, defaultValue = "") String addedBy
    		@RequestParam(value = "extractMetaData", required = false) boolean extractMetaData
    ) {
    	UserOntology userOntology = new UserOntology();
    	userOntology.setName(name);
    	userOntology.setPURL(PURL);
    	userOntology.setURI(URI);
    	userOntology.setLicenseURL(licenseURL);
    	userOntology.setLicenseLogo(licenseLogo);
    	userOntology.setLicenseLabel(licenseLabel);
    	userOntology.setTitle(title);
    	userOntology.setDescription(description);
    	userOntology.setCreator(creator);
    	userOntology.setHomePage(homePage);
    	userOntology.setTracker(tracker);
    	userOntology.setMailingList(mailingList);
    	userOntology.setPreferredPrefix(preferredPrefix);
    	userOntology.setBaseURI(baseURI);
    	userOntology.setReasoner(reasoner);
    	userOntology.setLabelProperty(labelProperty);
    	userOntology.setDefinitionProperty(definitionProperty);
    	userOntology.setSynonymProperty(synonymProperty);
    	userOntology.setHierarchicalProperty(hierarchicalProperty);
    	userOntology.setHiddenProperty(hiddenProperty);
        userOntology.setOboSlims(oboSlims);
    	userOntology.setPreferredRootTerm(preferredRootTerm);
    	userOntology.setLogo(logo);
    	userOntology.setFoundary(foundary);
    	userOntology.setApproval(approval);
//    	userOntology.setAddedBy(addedBy);    
    	
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		
		if (extractMetaData && validator.validate(userOntology).isEmpty())
		    userOntology = UserOntologyUtilities.extractMetaData(userOntology);
		
		Map<String,Object> error = new HashMap<String,Object>();
		userOntologyRepository.findAll().forEach(x ->  {if (x.getName().equals(name)) error.put("Existing name",x.getName());if (x.getPreferredPrefix().equals(preferredPrefix)) error.put("Existing preferredPrefix",x.getPreferredPrefix());} );
			
		if(error.isEmpty() && validator.validate(userOntology).isEmpty()) {
		    userOntologyRepository.save(userOntology);
			return YamlBasedPersistence.singleSuggestionYAMLDumpWriter(userOntology, false);
		} 	
		
		validator.validate(userOntology).forEach(x->error.put(x.getMessage(), x.getInvalidValue()));    	    
    	return YamlBasedPersistence.yamlGenericDumpWriter(error);		    
    }
    
    @ApiOperation(value = "List ontologies")
    @RequestMapping(path = "/list-ontologies", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<PagedResources<UserOntology>> listUserOntologies(@PageableDefault(size = 20, page = 0) Pageable pageable,PagedResourcesAssembler assembler   ) {
    	List<UserOntology> temp = new ArrayList<UserOntology>();
    	
    	for (UserOntology userOntology : userOntologyRepository.findAll()) {
		    temp.add(userOntology);
		}
    	
        final int start = (int)pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), temp.size());
        Page<UserOntology> document = new PageImpl<>(temp.subList(start, end), pageable, temp.size());
       
       return new ResponseEntity<>( assembler.toResource(document), HttpStatus.OK);
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource not found")
    @ExceptionHandler(ResourceNotFoundException.class)
    public void handleError(HttpServletRequest req, Exception exception) {
    }

    private class OntologyResourceConfigWrapper {

        private List<OntologyResourceConfig> ontologies;

        public OntologyResourceConfigWrapper(List<OntologyResourceConfig> ontologies) {
            this.ontologies = ontologies;
        }

        public List<OntologyResourceConfig> getOntologies() {
            return ontologies;
        }

        public void setOntologies(List<OntologyResourceConfig> ontologies) {
            this.ontologies = ontologies;
        }
    }

    private class OntologyResourceConfigFormatter extends OntologyResourceConfig {

        private OntologyResourceConfigFormatter(OntologyResourceConfig config) {
            this.setId(config.getId());
            this.setVersionIri(config.getVersionIri());
            this.setTitle(config.getTitle());
            this.setNamespace(config.getNamespace());
            this.setPreferredPrefix(config.getPreferredPrefix());
            this.setDescription(config.getDescription());
            this.setHomepage(config.getHomepage());
            this.setMailingList(config.getMailingList());
            this.setFileLocation(config.getFileLocation());
            this.setReasonerType(config.getReasonerType());
            this.setLabelProperty(config.getLabelProperty());
            this.setDefinitionProperties(config.getDefinitionProperties());
            this.setSynonymProperties(config.getSynonymProperties());
            this.setHierarchicalProperties(config.getHierarchicalProperties());
            this.setBaseUris(config.getBaseUris());
        }

        @Override
        @JsonIgnore
        public String getVersionIri() {
            return super.getVersionIri();
        }

        @Override
        @JsonIgnore
        public String getVersion() {
            return super.getVersion();
        }

        @Override
        @JsonIgnore
        public Collection<String> getCreators() {
            return super.getCreators();
        }

        @Override
        @JsonIgnore
        public Collection<String> getInternalMetadataProperties() {
            return super.getInternalMetadataProperties();
        }

        @Override
        @JsonIgnore
        public Collection<URI> getHiddenProperties() {
            return super.getHiddenProperties();
        }

        @Override
        @JsonIgnore
        public boolean isSkos() {
            return super.isSkos();
        }

        @Override
        @JsonProperty("uri")
        public String getId() {
            return super.getId();
        }

        @Override
        @JsonProperty("id")
        public String getNamespace() {
            return super.getNamespace();
        }

        @Override
        @JsonProperty("ontology_purl")
        public URI getFileLocation() {
            return super.getFileLocation();
        }

        @Override
        @JsonProperty("mailing_list")
        public String getMailingList() {
            return super.getMailingList();
        }

        @Override
        @JsonProperty("label_property")
        public URI getLabelProperty() {
            return super.getLabelProperty();
        }

        @Override
        @JsonProperty("synonym_property")
        public Collection<URI> getSynonymProperties() {
            return super.getSynonymProperties();
        }

        @Override
        @JsonProperty("definition_property")
        public Collection<URI> getDefinitionProperties() {
            return super.getDefinitionProperties();
        }

        @Override
        @JsonProperty("base_uri")
        public Collection<String> getBaseUris() {
            return super.getBaseUris();
        }

        @Override
        @JsonProperty("hierarchical_property")
        public Collection<URI> getHierarchicalProperties() {
            return super.getHierarchicalProperties();
        }

        @Override
        @JsonProperty("reasoner")
        public ReasonerType getReasonerType() {
            return super.getReasonerType();
        }
    }


}

