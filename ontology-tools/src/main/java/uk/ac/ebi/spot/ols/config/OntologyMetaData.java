package uk.ac.ebi.spot.ols.config;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;


public class OntologyMetaData {
	
	private String purl;
	private String title;
	private String description;
	private String homepage;
	private String email;
	private String version;

    public OntologyMetaData(String purl) {
		super();
		this.purl = purl;
	}

	public String getPurl() {
		return purl;
	}

	public void setPurl(String purl) {
		this.purl = purl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getHomepage() {
		return homepage;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

    public OntologyMetaData extract() {
    	
    	org.apache.jena.rdf.model.Model modelQuery = null;
    	Query query;

    	try {
    		modelQuery = FileManager.get().loadModel(this.getPurl());
			if(modelQuery.isEmpty()) {
				System.out.println("No valid statements");
				return this;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("query::: "+e.getMessage());
			return this;
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

        	"SELECT ?title ?description ?homepage ?contact_email ?versionInfo" +  "\n" + 
        	"# ?license_url ?creator ?IRI" +  "\n" +
        	"WHERE {" +  "\n" +
        	        "?ontology a owl:Ontology . # Does not exclude imported ontologies." + "\n" +
        	        "# OPTIONAL{?ontology terms:license|terms:rights|dc:rights ?license_url .}" + "\n" +
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
        	        "# OPTIONAL{?ontology dc:contributor|dc:creator ?creator .}" + "\n" +

        	        "# FILTER (LANG(?title) = 'en')" + "\n" +
        	        "# FILTER (LANG(?description) = 'en')" + "\n" +
        	"}";
      
        query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, modelQuery);
        try {
            ResultSet results = qexec.execSelect();
            if ( results.hasNext() ) {
                QuerySolution soln = results.nextSolution();
                Literal title = soln.getLiteral("title");
                Literal description = soln.getLiteral("description");

                Resource homepage_resource = null;
                Literal homepage_literal = null;
                if(soln.get("homepage")!=null)
                    if(soln.get("homepage").isResource()) {
  	                homepage_resource = soln.getResource("homepage");
                    } else
                  	  homepage_literal = soln.getLiteral("homepage");
                
                Literal contact_email = soln.getLiteral("contact_email");
                Literal versionInfo = soln.getLiteral("versionInfo");            
                if(title != null) {
                	System.out.println("title: "+title);
                	this.setTitle(title.toString());
                }
                    
                if(description != null) {
                    System.out.println("description: "+description);
                    this.setDescription(description.toString());
                }
                
                if(homepage_resource != null) {
                	System.out.println("homepage resource: "+homepage_resource);
                    this.setHomepage(homepage_resource.toString());
                } else 
                	if(homepage_literal != null) {
                	System.out.println("homepage literal: "+homepage_literal);
                    this.setHomepage(homepage_literal.toString());
                } 

                System.out.println("contact email: "+contact_email);
                
                if (contact_email != null) {
                	System.out.println("versionInfo: "+contact_email);
                	this.setEmail(contact_email.toString());
                }
                
                if (versionInfo != null) {
                	System.out.println("versionInfo: "+versionInfo);
                	this.setVersion(versionInfo.toString());
                }
                     
            }  
            
        } finally {
            qexec.close();
        }
    	
    	return this;
    }
}
