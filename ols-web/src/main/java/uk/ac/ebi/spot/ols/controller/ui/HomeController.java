package uk.ac.ebi.spot.ols.controller.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Simon Jupp
 * @date 08/07/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import uk.ac.ebi.spot.ols.model.OntologyDocument;
import uk.ac.ebi.spot.ols.model.SummaryInfo;
import uk.ac.ebi.spot.ols.service.OntologyRepositoryService;
import uk.ac.ebi.spot.ols.util.OLSEnv;

@Controller
@RequestMapping("")
public class HomeController {

    @Autowired
    OntologyRepositoryService repositoryService;

    @Autowired
    Environment environment;

    @Autowired
    private CustomisationProperties customisationProperties;

    @Value("${ols.maintenance.start:#{null}}")
    String start = null;

    @Value("${ols.maintenance.end:#{null}}")
    String end = null;

    @Value("${ols.maintenance.message:#{null}}")
    String message;

    // Reading these from application.properties
    @Value("${ols.sitemap.folder:}")
    private String sitemapFolder;
    
    @ModelAttribute("all_ontologies")
    public List<OntologyDocument> getOntologies() {
        try {
            return repositoryService.getAllDocuments(new Sort(new Sort.Order(Sort.Direction.ASC, "ontologyId")));
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
    
    @InitBinder()
    public void initBinder(WebDataBinder binder) throws Exception
    {
       binder.registerCustomEditor(Collection.class, new CustomCollectionEditor(List.class));
    }
    
    public Set<String> getClassificationsForSchemas(Collection<String> keys){
    	
        try {
        	Set<String> temp = new HashSet<String>();
        	for (OntologyDocument document : repositoryService.getAllDocuments(new Sort(new Sort.Order(Sort.Direction.ASC, "ontologyId")))) {
				document.getConfig().getClassifications().forEach(x -> x.forEach((k, v) -> {if (x.get(k)!=null) if (!x.get(k).isEmpty()) if (keys.contains(k)) temp.addAll(x.get(k));} ));
			}
            return temp;
        } catch (Exception e) {
        	return Collections.emptySet();
        }
    }
    
    public Set<String> getClassificationsForSchema(String key){
    	
        try {
        	Set<String> classifications = new HashSet<String>();
        	for (OntologyDocument document : repositoryService.getAllDocuments(new Sort(new Sort.Order(Sort.Direction.ASC, "ontologyId")))) {
				document.getConfig().getClassifications().forEach(x -> x.forEach((k, v) -> {if (x.get(k)!=null) if (!x.get(k).isEmpty()) if (key.equals(k)) classifications.addAll(x.get(k));} ));
			}
            return classifications;
        } catch (Exception e) {
        	return Collections.emptySet();
        }
    }
    
    @ModelAttribute("availableSchemas")
    public List<Schema> getAvailableSchemas(){
        try {
        	Set<String> schemaKeys = new HashSet<String>();
        	List<Schema> schemas = new ArrayList<Schema>();
        	for (OntologyDocument document : repositoryService.getAllDocuments(new Sort(new Sort.Order(Sort.Direction.ASC, "ontologyId")))) {
				document.getConfig().getClassifications().forEach(x -> schemaKeys.addAll(x.keySet()));
			}
        	
        	for (String key : schemaKeys) {
        		Schema schema = new Schema(key,getClassificationsForSchema(key));
        		schemas.add(schema);
        	}
        	schemas.sort(Comparator.comparing(Schema::getKey));
        	
            return schemas;
        } catch (Exception e) {
        	return Collections.emptyList();
        }
    }
    
    public Set<OntologyDocument> filterOntologiesByClassification(Collection<String> schemas, Collection<String> classifications){ 	
    	Set<OntologyDocument> temp = new HashSet<OntologyDocument>();
    	 for (OntologyDocument ontologyDocument : repositoryService.getAllDocuments(new Sort(new Sort.Order(Sort.Direction.ASC, "ontologyId")))) {
    		 for(Map<String, Collection<String>> classificationSchema : ontologyDocument.getConfig().getClassifications()) {
    			for (Map.Entry<String, Collection<String>> entry : classificationSchema.entrySet()) {
					if (schemas.contains(entry.getKey()))
						if(entry.getValue() != null)
							if(!entry.getValue().isEmpty())
						        for (String classification : entry.getValue()) {
							        if (classifications.contains(classification)) {
								      temp.add(ontologyDocument);
							}		
						}		
				}
            }
    	 }
    	 
    	 return temp;
    }
    
    public Set<String> findDuplicates(Collection<String> listContainingDuplicates) {
        final Set<String> setToReturn = new HashSet<>(); 
        final Set<String> set1 = new HashSet<>();
        for (String string : listContainingDuplicates)
    	    if (!set1.add(string))
    		    setToReturn.add(string);
        return setToReturn;
    }
    	 

    @RequestMapping({"", "/"})
    public String goHome () {
        return "redirect:index";
    }

    @RequestMapping({"/index"})
    public String showHome(Model model) {

        Date lastUpdated = repositoryService.getLastUpdated();
        int numberOfOntologies = repositoryService.getNumberOfOntologies();
        int numberOfTerms = repositoryService.getNumberOfTerms();
        int numberOfProperties = repositoryService.getNumberOfProperties();
        int numberOfIndividuals = repositoryService.getNumberOfIndividuals();

        SummaryInfo summaryInfo = new SummaryInfo(lastUpdated, numberOfOntologies, numberOfTerms, numberOfProperties, numberOfIndividuals, getClass().getPackage().getImplementationVersion());

        try {
            if (isMaintenancePlanned(start)) {
                model.addAttribute("start", start);
                model.addAttribute("end", end);
                model.addAttribute("message", message);
            }
        } catch (Exception e) {
            // couldn't determine whether we are in maintenance mode..
        }
        model.addAttribute("summary", summaryInfo);

        customisationProperties.setCustomisationModelAttributes(model);

        return "index";
    }

    @RequestMapping("/browse.do")
    public ModelAndView redirectOldUrls (
            @RequestParam(value = "ontName", required = false) String ontologyName,
            @RequestParam(value = "termId", required = false) String termId,
            Model model
    )  {
        String url = "";

        if (ontologyName == null && termId != null) {
            url = "terms?obo_id=" + termId;
        }  else if (termId != null) {
            url = "ontologies/" + ontologyName + "/terms?obo_id=" + termId;
        }
        else  {
            ontologyName = ontologyName.toLowerCase();
            url = "ontologies/" + ontologyName;
        }
        RedirectView rv = new RedirectView(url);
        rv.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        rv.setUrl(url);
        return new ModelAndView(rv);
    }

    @RequestMapping("/v2")
    public ModelAndView redirectV2Urls2 (
            HttpServletRequest request,
            Model model
    )  {
        return redirectV2Urls("browse.do", request, model);
    }

    @RequestMapping("/v2/{path}")
    public ModelAndView redirectV2Urls (
            @PathVariable String path,
            HttpServletRequest request,
            Model model
    )  {
        if (path == null) { path = "";}
        String url = "../" + path + "?" + request.getQueryString();
        RedirectView rv = new RedirectView(url);
        rv.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        rv.setUrl(url);
        return new ModelAndView(rv);
    }

    @RequestMapping("/search")
    public String doSearch(
            @RequestParam(value = "q", defaultValue = "*") String query,
            @RequestParam(value = "ontology", required = false) Collection<String> ontologies,
            @RequestParam(value = "schema", required = false) Collection<String> schemas,
            @RequestParam(value = "classification", required = false) Collection<String> classifications,
            @RequestParam(value = "type", required = false) Collection<String> types,
            @RequestParam(value = "slim", required = false) Collection<String> slims,
            @RequestParam(value = "queryFields", required = false) Collection<String> queryFields,
            @RequestParam(value = "exact", required = false) boolean exact,
            @RequestParam(value = "groupField", required = false) String groupField,
            @RequestParam(value = "obsoletes", defaultValue = "false") boolean queryObsoletes,
            @RequestParam(value = "local", defaultValue = "false") boolean isLocal,
            @RequestParam(value = "childrenOf", required = false) Collection<String> childrenOf,
            @RequestParam(value = "rows", defaultValue = "10") Integer rows,
            @RequestParam(value = "start", defaultValue = "0") Integer start,
            Model model

    ) {

        AdvancedSearchOptions searchOptions = new AdvancedSearchOptions(
                query,
                queryObsoletes,
                exact,
                isLocal,
                rows,
                start
        );
        
        if ( schemas != null && classifications != null) {
        	Set<String> filteredOntologies = new HashSet<String>();	
        	filterOntologiesByClassification(schemas, classifications).forEach(x -> filteredOntologies.add(x.getOntologyId()));
        	String filterMessage = "Displaying results for schemas: "+String.join(",", schemas)+" and respective classifications: "+String.join(",",classifications);
        	model.addAttribute("filterMessage",filterMessage);
        	ontologies = new HashSet<String>();
    		ontologies.addAll(filteredOntologies);
    		schemas = new HashSet<>();	
        } 
        
        if(ontologies != null) {
            searchOptions.setOntologies(ontologies);
        }
        
        if (schemas != null) {
            searchOptions.setSchemas(schemas);
        }

        if (queryFields != null) {
            searchOptions.setQueryField(queryFields);
        }

        if (types != null) {
            searchOptions.setTypes(types);
        }

        if (slims != null) {
            searchOptions.setSlims(slims);
        }

        if (groupField != null) {
            searchOptions.setGroupField(groupField);
        }
        
        model.addAttribute("searchOptions", searchOptions);
        model.addAttribute("availableSchemaValues",getClassificationsForSchemas(searchOptions.getSchemas()));
        customisationProperties.setCustomisationModelAttributes(model);
        return "search";
    }

    @RequestMapping({"contact"})
    public String showContact() {
        return "contact";
    }

    @RequestMapping({"maintenance"})
    public String showMaintenance(Model model) {

        try {
            model.addAttribute("scheduled", isMaintenancePlanned(start));
            model.addAttribute("maintenance", isMaintenanceMode(start, end));
        } catch (Exception e) {
            // can't determine if scheduled
        }
        model.addAttribute("start", start);
        model.addAttribute("end", end);
        model.addAttribute("message", message);
        customisationProperties.setCustomisationModelAttributes(model);
        return "maintenance";
    }

    @RequestMapping({"sparql"})
    public String showSparql() {
        return "comingsoon";
    }
	
	@RequestMapping({"dataprotection"})
    public String showDataProtection(Model model) {
    	customisationProperties.setCustomisationModelAttributes(model);
        return "dataprotection";
    }
    
    @RequestMapping({"imprint"})
    public String showImprint(Model model) {
    	customisationProperties.setCustomisationModelAttributes(model);
        return "imprint";
    }

    @RequestMapping({"about"})
    public String showAbout(Model model) {
    	customisationProperties.setCustomisationModelAttributes(model);
        return "about";
    }
    
    @RequestMapping({"graphview-help"})
    public String showGraphViewHelp(Model model) {
    	customisationProperties.setCustomisationModelAttributes(model);
        return "graphview-help";
    }
    
    @RequestMapping({"apidoc"})
    public String showAPI(Model model) {
    	customisationProperties.setCustomisationModelAttributes(model);
        return "apidoc";
    }
    
    @RequestMapping({"documentation"})
    public String showDocumentation(Model model) {
    	customisationProperties.setCustomisationModelAttributes(model);
        return "documentation";
    }
    
    @RequestMapping({"google056bffc3c1713b87.html"})
    public String showGoogleVerification(Model model) {
    	customisationProperties.setCustomisationModelAttributes(model);
        return "google056bffc3c1713b87";
    }
        
    @RequestMapping({"faq"})
    public String showFAQ(Model model) {
    	customisationProperties.setCustomisationModelAttributes(model);
        return "faq";
    }

    @RequestMapping({"docs"})
    public String showDocsIndex(Model model) {
        return "redirect:docs/index";
    }

    // ok, this is bad, need to find a way to deal with trailing slashes and constructing relative URLs in the thymeleaf template...
    @RequestMapping({"docs/"})
    public String showDocsIndex2(Model model) {
        return "redirect:index";
    }

    @RequestMapping({"docs/{page}"})
    public String showDocs(@PathVariable("page") String pageName, Model model) {

        if(customisationProperties.getDebrand()) {
            return "redirect:../index";
        }

        model.addAttribute("page", pageName);
        customisationProperties.setCustomisationModelAttributes(model);
        return "docs-template";
    }

    @RequestMapping(path = "sitemap/{file}", method = RequestMethod.GET)
    public @ResponseBody
    FileSystemResource getDownloadOntology(@PathVariable("file") String map, HttpServletResponse response) throws ResourceNotFoundException {
        File sitemapFile = new File (getSitemapFolder(), map);
        if (sitemapFile.exists()) {
            return new FileSystemResource(sitemapFile);
        }
        throw new ResourceNotFoundException("This file is not available");
    }

    private String getSitemapFolder ( ) {
        if (sitemapFolder.equals("")) {
            return OLSEnv.getOLSHome() + File.separator + "sitemap";
        }
        return sitemapFolder;
    }

    private boolean isMaintenancePlanned
            (String start) throws Exception {
        if (start != null) {

            try {
                Calendar cal = Calendar.getInstance();
                Date currenTime = cal.getTime();
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date maintenanceStartTime = format1.parse( start );
                return currenTime.before(maintenanceStartTime);
            } catch (Exception e) {
                throw new Exception("Can't parse maintenance mode start or end time");
            }
        }
        return false;

    }

    private boolean isMaintenanceMode
            (String start, String end) throws Exception {
        if (start != null && end != null) {

            try {
                Calendar cal = Calendar.getInstance();
                Date currenTime = cal.getTime();
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date maintenanceStartTime = format1.parse( start );
                Date maintenanceEndTime = format1.parse( end );
                return (currenTime.after(maintenanceStartTime) && currenTime.before(maintenanceEndTime));
            } catch (Exception e) {
                throw new Exception("Can't parse maintenance mode start or end time");
            }
        }
        return false;

    }

}
