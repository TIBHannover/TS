package uk.ac.ebi.spot.ols.entities;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;

import uk.ac.ebi.spot.ols.controller.api.OntologySuggestionController;

public class UserOntologyUtilities {

	   public static UserOntology extractMetaData(UserOntology userOntology) {
	    	
	    	FileManager.get().addLocatorClassLoader(OntologySuggestionController.class.getClassLoader());
	    	org.apache.jena.rdf.model.Model modelQuery = null;
	    	Query query;

	    	try {
	    		modelQuery = FileManager.get().loadModel(userOntology.getPermanenturl());
				if(modelQuery.isEmpty()) {
					System.out.println("No valid statements");
					return userOntology;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("query::: "+e.getMessage());
				return userOntology;
			}
	    	
	        String queryString = 
	            "PREFIX owl: <http://www.w3.org/2002/07/owl#>" + "\n" +
	        	"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +  "\n" +
	        	"PREFIX xml: <http://www.w3.org/XML/1998/namespace>" +  "\n" +
	        	"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>" +  "\n" +
	        	"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +  "\n" +
	        	"PREFIX terms: <http://purl.org/dc/terms/>" +  "\n" +
	        	"PREFIX dc: <http://purl.org/dc/elements/1.1/>" +  "\n" +
	        	"PREFIX foaf: <http://xmlns.com/foaf/0.1/>" +  "\n" +

	        	"SELECT ?title ?description ?license_url ?homepage ?contact_email ?creator ?versionInfo" +  "\n" + 
	        	"# ?IRI" +  "\n" +
	        	"WHERE {" +  "\n" +
	        	        "?ontology a owl:Ontology . # Does not exclude imported ontologies." + "\n" +
	        	        "OPTIONAL{?ontology terms:license|terms:rights|dc:rights ?license_url .}" + "\n" +
	        	        "OPTIONAL{?ontology terms:title|dc:title|rdfs:label ?title .}" + "\n" +
	        	        "OPTIONAL{?ontology terms:description|dc:description ?description .}" + "\n" +
	        	        "OPTIONAL{?ontology owl:versionInfo ?versionInfo .}" + "\n" +
	        	        "# OPTIONAL{?ontology owl:versionIRI  ?IRI .}" + "\n" +
	        	        "OPTIONAL{?ontology foaf:homepage ?homepage .}" + "\n" +
	        	        "OPTIONAL{" + "\n" +
	        	                "?ontology foaf:mbox ?contact_email_tmp ." + "\n" +
	        	                "BIND(xsd:string(?contact_email_tmp) AS ?contact_email_tmp_str) ." + "\n" +
	        	                "BIND(REPLACE(?contact_email_tmp_str, 'mailto:', '') AS ?contact_email) ."  + "\n" +
	        	        "}" + "\n" +
	        	        "OPTIONAL{?ontology dc:contributor|dc:creator ?creator .}" + "\n" +

	        	        "# FILTER (LANG(?title) = 'en')" + "\n" +
	        	        "# FILTER (LANG(?description) = 'en')" + "\n" +
	        	"}";
	      
	        query = QueryFactory.create(queryString);
	        QueryExecution qexec = QueryExecutionFactory.create(query, modelQuery);
	        try {
	            ResultSet results = qexec.execSelect();
	            List<String> creatorList = new ArrayList<String>();
	            String firstCreator = "";
	            if ( results.hasNext() ) {
	                QuerySolution soln = results.nextSolution();
	                Literal title = soln.getLiteral("title");
	                Literal description = soln.getLiteral("description");
	                Resource license_url_resource = null;
	                Literal license_url_literal = null;
	                if(soln.get("license_url")!=null)
	                  if(soln.get("license_url").isResource()) {
		                license_url_resource = soln.getResource("license_url");
	                  } else
	                	  license_url_literal = soln.getLiteral("license_url");
	                Resource homepage_resource = null;
	                Literal homepage_literal = null;
	                if(soln.get("homepage")!=null)
	                    if(soln.get("homepage").isResource()) {
	  	                homepage_resource = soln.getResource("homepage");
	                    } else
	                  	  homepage_literal = soln.getLiteral("homepage");
	                
	                Literal contact_email = soln.getLiteral("contact_email");
	                Literal creator = soln.getLiteral("creator");
	                Literal versionInfo = soln.getLiteral("versionInfo");
	                Literal IRI = soln.getLiteral("IRI");             
	                if(title != null) {
	                	System.out.println("title: "+title);
	                	userOntology.setTitle(title.toString());
	                }
	                    
	                if(description != null) {
	                    System.out.println("description: "+description);
	                    userOntology.setDescription(description.toString());
	                }

	                if(license_url_resource != null) {
	                	System.out.println("license url resource: "+license_url_resource);
	                    userOntology.setLicenseURL(license_url_resource.toString());
	                } else 
	                	if(license_url_literal != null) {
	                	System.out.println("license url literal: "+license_url_literal);
	                    userOntology.setLicenseURL(license_url_literal.toString());
	                }  
	                
	                if(homepage_resource != null) {
	                	System.out.println("homepage resource: "+homepage_resource);
	                    userOntology.setHomePage(homepage_resource.toString());
	                } else 
	                	if(homepage_literal != null) {
	                	System.out.println("homepage literal: "+homepage_literal);
	                    userOntology.setHomePage(homepage_literal.toString());
	                } 

	                System.out.println("contact email: "+contact_email);
	                
	                if(creator != null) 
	                {
	                	System.out.println("creator: "+creator.toString());
	                	firstCreator = creator.toString();
	                	creatorList.add(creator.toString());
	                	userOntology.setCreator(creatorList);
	                }
	                
	                if (IRI != null) {
	                	System.out.println("IRI: "+IRI);
	                	userOntology.setURI(IRI.toString());
	                }
	                
	                if (versionInfo != null) {
	                	System.out.println("versionInfo: "+versionInfo);
	                	userOntology.setVersionInfo(versionInfo.toString());
	                }
	                     
	            }
	            
	            while ( results.hasNext() ) {
	                QuerySolution soln = results.nextSolution();

	                Literal creator = soln.getLiteral("creator");
	                            
	                if(firstCreator.equals(creator.toString()))
	                	break;
	                
	                if(creator != null) 
	                {
	                	System.out.println("creator: "+creator.toString());
	                	creatorList.add(creator.toString());
	                	userOntology.setCreator(creatorList);
	                }        
	            }    
	            
	        } finally {
	            qexec.close();
	        }
	    	
	    	return userOntology;
	    }
	
}
