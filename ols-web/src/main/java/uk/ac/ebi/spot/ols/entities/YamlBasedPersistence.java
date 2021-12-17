package uk.ac.ebi.spot.ols.entities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class YamlBasedPersistence {
	
	public static String allSuggestionsDumpWriter(List<UserOntology> userOntologies, boolean includeUserData) {

		   List<Map<String, Object>> ontologyList = new ArrayList<Map<String, Object>> ();
		   for (UserOntology userOntology : userOntologies) {	   
			   ontologyList.add(putUserOntologyToMap(userOntology,includeUserData));	  
		   }
		   
		   Map<String, Object> ontologies = new HashMap<String, Object>();
		   Map<String, String> author = new HashMap<String, String>();
		   author.put("name", "OBO Technical WG");
           ontologies.put("name", "OBO Foundry");
           ontologies.put("title", "The OBO Foundry");
           ontologies.put("markdown", "kramdown");
           ontologies.put("highlighter", "rouge");
           ontologies.put("baseurl", "/");
           ontologies.put("imgurl", "/images");
           ontologies.put("repo", "https://github.com/OBOFoundry/OBOFoundry.github.io/");
           ontologies.put("repo_src", "https://github.com/OBOFoundry/OBOFoundry.github.io/blob/master/");
           ontologies.put("author",author);
		   ontologies.put("ontologies", ontologyList);
		   
		   DumperOptions options = new DumperOptions();
		   options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		   Yaml yaml = new Yaml(options);
		   return yaml.dump(ontologies);
		}
	
	public static String singleSuggestionDumpWriter(UserOntology userOntology, boolean includeUserData) {
		   DumperOptions options = new DumperOptions();
		   options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		   Yaml yaml = new Yaml(options);
		   return yaml.dump(putUserOntologyToMap(userOntology,includeUserData));
	}
	
	public static String genericDumpWriter(Map<String, Object> data) {
		   DumperOptions options = new DumperOptions();
		   options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		   Yaml yaml = new Yaml(options);
		   return yaml.dump(data);
	}
	
	public static Map<String, Object> putUserOntologyToMap (UserOntology userOntology, boolean includeUserData ) {
		
		Map<String, Object> data = new HashMap<String, Object>();
		
		   data = new HashMap<String, Object>();
		   if(userOntology.getName() != null)
		   if(!userOntology.getName().isEmpty())
		      data.put("id", userOntology.getName());
		   if(!userOntology.getPURL().isEmpty())
		      data.put("ontology_purl", userOntology.getPURL());
		   if(userOntology.getURI() != null)
		   if(!userOntology.getURI().isEmpty())
		      data.put("uri", userOntology.getURI());
		   
		   Map<String, Object> license = new HashMap<String, Object>();
		     
		   if(userOntology.getLicenseURL() != null)
		   if(!userOntology.getLicenseURL().isEmpty()) {
			   license.put("url",userOntology.getLicenseURL());
		   }
		      
		   if(userOntology.getLicenseLogo() != null)
		   if(!userOntology.getLicenseLogo().isEmpty()) {
			   license.put("logo",userOntology.getLicenseLogo());
		   }
		        
		   if(userOntology.getLicenseLabel() != null)
		   if(!userOntology.getLicenseLabel().isEmpty()) {
			   license.put("label", userOntology.getLicenseLabel()); 
		   }
		   
		   if(!license.isEmpty())
			   data.put("license", license);
		      
		   if(userOntology.getTitle() != null)
		   if(!userOntology.getTitle().isEmpty())
		      data.put("title", userOntology.getTitle());
		   if(userOntology.getDescription() != null)
		   if(!userOntology.getDescription().isEmpty())
		      data.put("description", userOntology.getDescription());
		   if(userOntology.getHomePage() != null)
		   if(!userOntology.getHomePage().isEmpty())
		      data.put("homepage", userOntology.getHomePage());
		   if(userOntology.getTracker() != null)
		   if(!userOntology.getTracker().isEmpty())
		      data.put("tracker", userOntology.getTracker());
		   if(userOntology.getMailingList() != null)
		   if(!userOntology.getMailingList().isEmpty())
		      data.put("mailing_list", userOntology.getMailingList());
		   if(userOntology.getPreferredPrefix() != null)
		   if(!userOntology.getPreferredPrefix().isEmpty())
		      data.put("preferredPrefix", userOntology.getPreferredPrefix());
		   if(userOntology.getBaseURI() != null)
		   if(!userOntology.getBaseURI().isEmpty())
		      data.put("base_uri", userOntology.getBaseURI());
		   if(userOntology.getCreator() != null)
		   if(!userOntology.getCreator().isEmpty())
		      data.put("creator", userOntology.getCreator());
		   if(userOntology.getLabelProperty() != null)
		   if(!userOntology.getLabelProperty().isEmpty())
		      data.put("label_property", userOntology.getLabelProperty());
		   if(userOntology.getDefinitionProperty() != null)
		   if(!userOntology.getDefinitionProperty().isEmpty())
		      data.put("definition_property", userOntology.getDefinitionProperty());
		   if(userOntology.getSynonymProperty() != null)
		   if(!userOntology.getSynonymProperty().isEmpty())
		      data.put("synonym_property", userOntology.getSynonymProperty());
		   if(userOntology.getHierarchicalProperty() != null)
		   if(!userOntology.getHierarchicalProperty().isEmpty())
		      data.put("hierarchical_property", userOntology.getHierarchicalProperty());
		   if(userOntology.getHiddenProperty() != null)
		   if(!userOntology.getHiddenProperty().isEmpty())
		      data.put("hidden_property", userOntology.getHiddenProperty());
		   if(userOntology.getReasoner() != null)
		   if(!userOntology.getReasoner().getPropertyName().isEmpty()) 
			   if(!userOntology.getReasoner().getPropertyName().equals("none"))
				   data.put("reasoner", userOntology.getReasoner().getPropertyName());
		   if(userOntology.isOboSlims() == true)
		      data.put("oboSlims", userOntology.isOboSlims());
		   if(userOntology.getPreferredRootTerm() != null)
		   if(!userOntology.getPreferredRootTerm().isEmpty())
		      data.put("preferred_root_term", userOntology.getPreferredRootTerm());
		   if(userOntology.getLogo() != null)
		   if(!userOntology.getLogo().isEmpty())
		      data.put("logo", userOntology.getLogo());
		   if( userOntology.isFoundary() == true)
		      data.put("is_foundary", userOntology.isFoundary());
		   
		   if(includeUserData) {
			   if(userOntology.getApproval() != null)
				   if(!userOntology.getApproval().getPropertyName().isEmpty())
				      data.put("approval", userOntology.getApproval().getPropertyName());
			   
			   if(userOntology.getAddedBy() != null)
				   if(!userOntology.getAddedBy().isEmpty())
				      data.put("added_by", userOntology.getAddedBy());
		   }
		
		return data;	
	}
	
	public static List<UserOntology> yamlReader(File ontologies) {
		List<UserOntology> userOntologies = new ArrayList<UserOntology>();
		Yaml yaml = new Yaml();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
    try {
		InputStream inputStream = new FileInputStream(ontologies);
		Map<String, Object> data = (Map<String, Object>) yaml.load(inputStream);
		List<Map<String, Object>> ontologyList = (List<Map<String, Object>>) data.get("ontologies");
		
		for (Map<String, Object> map : ontologyList) {
			UserOntology uo = new UserOntology();
			uo.setName((String) map.get("id"));
			uo.setPURL((String) map.get("ontology_purl"));
			uo.setURI((String) map.get("uri"));
			
			if (map.get("license") != null) {
				uo.setLicenseURL((String) ((Map<String, Object>) map.get("license")).get("url"));
				uo.setLicenseLogo((String) ((Map<String, Object>) map.get("license")).get("logo"));
				uo.setLicenseLabel((String) ((Map<String, Object>) map.get("license")).get("label"));
			}
			
			uo.setTitle((String) map.get("title"));
			uo.setDescription((String) map.get("description"));
			uo.setHomePage((String) map.get("homepage"));
			uo.setTracker((String) map.get("tracker"));
			uo.setMailingList((String) map.get("mailing_list"));
			if(((String) map.get("preferredPrefix")) != null) {
				if(!((String) map.get("preferredPrefix")).isEmpty())
				    uo.setPreferredPrefix((String) map.get("preferredPrefix"));
			} else
				uo.setPreferredPrefix((String) map.get("id"));
			uo.setBaseURI((String) map.get("base_uri"));
			uo.setCreator((List<String>) map.get("creator"));
			uo.setLabelProperty((String) map.get("label_property"));
			uo.setDefinitionProperty((List<String>) map.get("definition_property"));
			uo.setSynonymProperty((List<String>) map.get("synonym_property"));
			uo.setHierarchicalProperty((List<String>) map.get("hierarchical_property"));
			uo.setHiddenProperty((List<String>) map.get("hidden_property"));
			if (map.get("reasoner") != null)
			    uo.setReasoner(ReasonerEnum.valueOf((String) map.get("reasoner")));
			if(map.get("oboSlims")!=null) {
				if(map.get("oboSlims") instanceof java.lang.Integer) {
					if ((int) map.get("oboSlims") ==1)
						uo.setOboSlims(true);
					else if ((int) map.get("oboSlims") ==0)
						uo.setOboSlims(false);
				} else if (map.get("oboSlims") instanceof java.lang.Boolean)
					uo.setOboSlims((boolean) map.get("oboSlims"));
			}
			uo.setPreferredRootTerm((List<String>) map.get("preferred_root_term"));
			uo.setLogo((String) map.get("logo"));
			if(map.get("is_foundary")!=null) {
				if(map.get("is_foundary") instanceof java.lang.Integer) {
					if ((int) map.get("is_foundary") ==1)
						uo.setFoundary(true);
					else if ((int) map.get("is_foundary") ==0)
						uo.setFoundary(false);
				} else if (map.get("is_foundary") instanceof java.lang.Boolean)
					uo.setFoundary((boolean) map.get("is_foundary"));
			}	
			
			if (map.get("approval") != null)
			    uo.setApproval(ApprovalEnum.valueOf((String) map.get("approval")));
			uo.setAddedBy((String) map.get("added_by"));
			
			if(validator.validate(uo).isEmpty())
				userOntologies.add(uo);	
			else {
				System.out.println(uo.getName()+" - "+uo.getPreferredPrefix()+" : ");
                System.out.println("Violations Exist: "+!validator.validate(uo).isEmpty());	
			}
		}
		
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
		return userOntologies;
	}
	
	public static int noofOntologiesInYAML(File ontologies) {
		List<UserOntology> userOntologies = new ArrayList<UserOntology>();
		Yaml yaml = new Yaml();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		int ontologyListSize = 0;
    try {
		InputStream inputStream = new FileInputStream(ontologies);
		Map<String, Object> data = (Map<String, Object>) yaml.load(inputStream);
		ontologyListSize = ((List<Map<String, Object>>) data.get("ontologies")).size();
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
		return ontologyListSize ;
	}
	
}
