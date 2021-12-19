package uk.ac.ebi.spot.ols.controller.api;

import io.swagger.annotations.ApiOperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

//import uk.ac.ebi.spot.ols.entities.ApprovalEnum;
import uk.ac.ebi.spot.ols.entities.ReasonerEnum;
import uk.ac.ebi.spot.ols.entities.UserOntology;
import uk.ac.ebi.spot.ols.entities.UserOntologyUtilities;
import uk.ac.ebi.spot.ols.entities.YamlBasedPersistence;
import uk.ac.ebi.spot.ols.repositories.UserOntologyRepository;
import uk.ac.ebi.spot.ols.util.OLSEnv;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RestController
@RequestMapping("/api/ontology-suggestion")
public class OntologySuggestionController {

    private Logger log = LoggerFactory.getLogger(getClass());

    public Logger getLog() {
        return log;
    }

    @Autowired
    private UserOntologyRepository userOntologyRepository;
    
    
    private File getFile (String fileName) throws FileNotFoundException {
        File file = new File (OLSEnv.getOLSHome(), fileName);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        return file;
    }
    
    private boolean recordExists(String name, String preferredPrefix, UserOntologyRepository userOntologyRepository) {
    	try {
			for (UserOntology userOntology : userOntologyRepository.findAll()) {
				if (userOntology.getName().equals(name) || userOntology.getPreferredPrefix().equals(preferredPrefix))
					return true;
			}
		} catch (NullPointerException e) {
			System.out.println(e.getMessage());
		}	
    	
    	return false;
    }

    @ApiOperation(value = "Extract metadata of an ontology from its PURL in YAML format")
    @RequestMapping(path = "/plain-metadata-extractor", produces = {"text/yaml"}, method = RequestMethod.GET)
    String extractSuggestionMetaData(
            @RequestParam(value = "PURL", required = true, defaultValue = "") String PURL
    ) {
    	UserOntology userOntology = new UserOntology();
    	userOntology.setPURL(PURL);
        return YamlBasedPersistence.singleSuggestionDumpWriter(UserOntologyUtilities.extractMetaData(userOntology), false);

    }
    
    @ApiOperation(value = "Extract metadata of an ontology from its PURL as a Resource in JSON format")
    @RequestMapping(path = "/metadata-extractor", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<Resource<UserOntology>> extractSuggestionMetaDataJSON(
            @RequestParam(value = "PURL", required = true, defaultValue = "") String PURL
    ) {
    	UserOntology userOntology = new UserOntology();
    	userOntology.setPURL(PURL);
    	
    	Resource<UserOntology> resourceUserOntology = new Resource<UserOntology>(UserOntologyUtilities.extractMetaData(userOntology));

        final ControllerLinkBuilder lb = ControllerLinkBuilder.linkTo(
                ControllerLinkBuilder.methodOn(OntologySuggestionController.class).extractSuggestionMetaDataJSON(PURL));
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
            @RequestParam(value = "reasoner", required = false) ReasonerEnum reasoner,
    		@RequestParam(value = "labelProperty", required = false) String labelProperty,
    		@RequestParam(value = "definitionProperty", required = false) List<String> definitionProperty,
    		@RequestParam(value = "synanymProperty", required = false) List<String> synonymProperty,
    		@RequestParam(value = "hierarchicalProperty", required = false) List<String> hierarchicalProperty,
    		@RequestParam(value = "hiddenProperty", required = false) List<String> hiddenProperty,
    		@RequestParam(value = "oboSlims", required = false) boolean oboSlims,
    		@RequestParam(value = "preferredRootTerm", required = false) List<String> preferredRootTerm,
    		@RequestParam(value = "logo", required = false) String logo,
    		@RequestParam(value = "foundary", required = false) boolean foundary,
//    		@RequestParam(value = "approval", required = false) ApprovalEnum approval,
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
//    	userOntology.setApproval(approval);
//    	userOntology.setAddedBy(addedBy);    
    	
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		
		if (extractMetaData && validator.validate(userOntology).isEmpty())
		    userOntology = UserOntologyUtilities.extractMetaData(userOntology);
		
		Map<String,Object> error = new HashMap<String,Object>();
		userOntologyRepository.findAll().forEach(x ->  {if (x.getName().equals(name)) error.put("Existing name",x.getName());if (x.getPreferredPrefix().equals(preferredPrefix)) error.put("Existing preferredPrefix",x.getPreferredPrefix());} );
			
		if(error.isEmpty() && validator.validate(userOntology).isEmpty()) {
		    userOntologyRepository.save(userOntology);
			return YamlBasedPersistence.singleSuggestionDumpWriter(userOntology, false);
		} 	
		
		validator.validate(userOntology).forEach(x->error.put(x.getMessage(), x.getInvalidValue()));    	    
    	return YamlBasedPersistence.genericDumpWriter(error);		    
    }
     
    @ApiOperation(value = "Generate config file for a particular ontology suggestion in YAML format")
    @RequestMapping(path = "/generate-config/{name}", produces = {"text/yaml"}, method = RequestMethod.GET)
    public String extractSingleSuggestionsMetaData(@PathVariable("name") String name ) {
           List<UserOntology> results = userOntologyRepository.findByName(name);
           if(!results.isEmpty())
        	  return YamlBasedPersistence.singleSuggestionDumpWriter(results.get(0), false); 
           Map<String,Object> error = new HashMap<String,Object>();
           error.put("No such record", name);
           return YamlBasedPersistence.genericDumpWriter(error);
    }
       
    @ApiOperation(value = "Generate config file for all ontology suggestions in YAML format")
    @RequestMapping(path = "/generate-config-allsuggestions", produces = {"text/yaml"}, method = RequestMethod.GET)
    public String extractAllSuggestionsMetaData() {
           List<UserOntology> ontologyList = new ArrayList<UserOntology>();
           userOntologyRepository.findAll().forEach(ontologyList::add);
          return YamlBasedPersistence.allSuggestionsDumpWriter(ontologyList, false);  
    }

    @ApiOperation(value = "List all ontology suggestions")
    @RequestMapping(path = "/list-suggestions", produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE}, method = RequestMethod.GET)
    HttpEntity<PagedResources<UserOntology>> listUserOntologies(@PageableDefault(size = 20, page = 0) Pageable pageable, PagedResourcesAssembler assembler   ) {
    	List<UserOntology> temp = new ArrayList<UserOntology>();
    	
    	for (UserOntology userOntology : userOntologyRepository.findAll()) {
		    temp.add(userOntology);
		}
    	
        final int start = (int)pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), temp.size());
        Page<UserOntology> suggestionsPage = new PageImpl<>(temp.subList(start, end), pageable, temp.size());
       
       return new ResponseEntity<>( assembler.toResource(suggestionsPage), HttpStatus.OK);
    }
    
    @ApiOperation(value = "Load a YAML format ontology suggestions list in addition to the existing suggestions and return it in the same format")
    @RequestMapping(path = "/load-suggestion-list", produces = {"text/yaml"}, method = RequestMethod.POST)
    String loadSuggestionListfromYAML(@RequestParam(value = "file", required = true) MultipartFile file) throws FileNotFoundException {
    	
        Map<String,Object> error = new HashMap<String,Object>();

    	// check if file is empty
        if (file.isEmpty()) {
      	  error.put("exceptionMessage", "Please select a file to upload.");

      	return YamlBasedPersistence.genericDumpWriter(error);
        }

        // normalize the file path
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        System.out.println("File Name: "+fileName);
        // save the file on the local file system
        try {
            Path path = Paths.get(OLSEnv.getOLSHome() + fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("copied to "+path.getFileName());
        } catch (IOException e) {
      	  e.printStackTrace();
        }
        
        List<UserOntology> temp =YamlBasedPersistence.yamlReader(getFile(fileName));

        if (temp == null || temp.isEmpty()) {
  	     error.put("exceptionMessage", "Upload Operation is unsuccessfull due to Validation Errors in the YAML file");
  	     return YamlBasedPersistence.genericDumpWriter(error);
        }

  	  for (UserOntology uo : temp) {
  		  if (!recordExists(uo.getName(), uo.getPreferredPrefix(), userOntologyRepository)) {
  			  userOntologyRepository.save(uo);
  		  } else {
  			  System.out.println(uo.getName()+" is not added due to an existing record with the same id or same preferred prefix.");
  			  System.out.println("A record exists with either id: "+uo.getName()+" or preferredPrefix: "+uo.getPreferredPrefix());
  		  }		  
  	  }
	
    	return YamlBasedPersistence.allSuggestionsDumpWriter(temp, false);
    }

}
