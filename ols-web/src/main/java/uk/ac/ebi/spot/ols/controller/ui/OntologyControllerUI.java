package uk.ac.ebi.spot.ols.controller.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.ac.ebi.spot.ols.model.OntologyDocument;
import uk.ac.ebi.spot.ols.neo4j.model.Individual;
import uk.ac.ebi.spot.ols.neo4j.service.OntologyIndividualService;

import uk.ac.ebi.spot.ols.neo4j.service.OntologyTermGraphService;
import uk.ac.ebi.spot.ols.service.OntologyRepositoryService;
import uk.ac.ebi.spot.ols.util.OLSEnv;

import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Simon Jupp
 * @date 15/07/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@Controller
@RequestMapping("/ontologies")
public class OntologyControllerUI {

    @Autowired
    private HomeController homeController;

    @Autowired
    OntologyRepositoryService repositoryService;

    @Autowired
    private OntologyTermGraphService ontologyTermGraphService;

    @Autowired
    private OntologyIndividualService ontologyIndividualService;

    @Autowired
    private CustomisationProperties customisationProperties;

    // Reading these from application.properties
    @Value("${ols.downloads.folder:}")
    private String downloadsFolder;
       
    public Set<String> getClassificationsForSchema(String key){
    	
        try {
        	Set<String> classifications = new TreeSet<String>();
        	for (OntologyDocument document : repositoryService.getAllDocuments(new Sort(new Sort.Order(Sort.Direction.ASC, "ontologyId")))) {
				document.getConfig().getClassifications().forEach(x -> x.forEach((k, v) -> {if (x.get(k)!=null) if (!x.get(k).isEmpty()) if (key.equals(k)) classifications.addAll(x.get(k));} ));
			}
            return classifications;
        } catch (Exception e) {
        	return Collections.emptySet();
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

    @RequestMapping(path = "", method = RequestMethod.GET)
    String getAll(
    		@RequestParam(value = "classification", required = false) Collection<String> classifications,
    		@RequestParam(value = "lang", required = false, defaultValue = "en") String lang,
    		Model model) {
    	model.addAttribute("lang", lang);
    	if(classifications != null) {
    		Set<OntologyDocument> set = filterOntologiesByClassification(new ArrayList<String>(Arrays.asList("collection")), classifications);
    		model.addAttribute("all_ontologies", set);
    		model.addAttribute("scope", String.join(", ", classifications).replaceAll("(.*), (.*)", "$1 and $2"));
    	} else {
    		List<OntologyDocument> list = repositoryService.getAllDocuments(new Sort(new Sort.Order(Sort.Direction.ASC, "ontologyId")));
            model.addAttribute("all_ontologies", list);
            model.addAttribute("scope", "all");
    	}
    	if(getClassificationsForSchema("collection").size()>0)  	
           model.addAttribute("collectionValues", getClassificationsForSchema("collection"));
        customisationProperties.setCustomisationModelAttributes(model);
        return "browse";
    }

    @RequestMapping(path = "/{onto}", method = RequestMethod.GET)
    String getTerm(
            @PathVariable("onto") String ontologyId,
            @RequestParam(value = "lang", required = false, defaultValue = "en") String lang,
            @PageableDefault(page = 0, size = 1000, sort="n.label") Pageable pageable,
            Model model) throws ResourceNotFoundException {

        ontologyId = ontologyId.toLowerCase();
        if (ontologyId != null) {
            OntologyDocument document = repositoryService.get(ontologyId);
            if (document == null) {
                throw new ResourceNotFoundException("Ontology called " + ontologyId + " not found");
            }

            String contact = document.getConfig().getMailingList();
            try {
                InternetAddress address = new InternetAddress(contact, true);
                contact = "mailto:" + contact;
            } catch (Exception e) {
                // only thrown if not valid e-mail, so contact must be URL of some sort
            }
            model.addAttribute("lang", lang);
	        model.addAttribute("ontologyLanguages", document.getConfig().getLanguages());
            model.addAttribute("contact", contact);
            
            if (pageable.getSort() == null) 
                pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), new Sort(new Sort.Order(Sort.Direction.DESC, "n.label")));
 
            Page<Individual> individuals = ontologyIndividualService.findAllByOntology(ontologyId, pageable);

            model.addAttribute("contact", contact);
            model.addAttribute("ontologyDocument", document);
            model.addAttribute("ontologyIndividuals", individuals);
            

            customisationProperties.setCustomisationModelAttributes(model);
            DisplayUtils.setPreferredRootTermsModelAttributes(ontologyId, document, ontologyTermGraphService, model);
        }
        else {
            return homeController.doSearch(
                    "*",
                    null,
                    null,null,null, null, null, false, null, false, false, null, 10,0,model);
        }
        return "ontology";
    }

    @RequestMapping(path = "/{onto}", produces = "application/rdf+xml", method = RequestMethod.GET)
    public @ResponseBody
    FileSystemResource getOntologyDirectDownload(@PathVariable("onto") String ontologyId, HttpServletResponse response) throws ResourceNotFoundException {
        return getDownloadOntology(ontologyId, response);
    }


    @RequestMapping(path = "/{onto}/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, method = RequestMethod.GET)
    public @ResponseBody  FileSystemResource getDownloadOntology(@PathVariable("onto") String ontologyId, HttpServletResponse response) throws ResourceNotFoundException {

        ontologyId = ontologyId.toLowerCase();

        OntologyDocument document = repositoryService.get(ontologyId);

        if (document == null) {
            throw new ResourceNotFoundException("Ontology called " + ontologyId + " not found");
        }

        if(document.getConfig().getAllowDownload() == false) {
            throw new ResourceNotFoundException("This ontology is not available for download");
        }

        try {
            response.setHeader("Content-Disposition", "filename=" + ontologyId + ".owl");
            return new FileSystemResource(getDownloadFile(ontologyId));
        } catch (FileNotFoundException e) {
            throw new ResourceNotFoundException("This ontology is not available for download");
        }
    }


    private File getDownloadFile(String ontologyId) throws FileNotFoundException {
        File file = new File(getDownloadsFolder(), ontologyId.toLowerCase());
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        return file;
    }


    private String getDownloadsFolder() {
        if (downloadsFolder.equals("")) {
            return OLSEnv.getOLSHome() + File.separator + "downloads";
        }
        return downloadsFolder;
    }

}