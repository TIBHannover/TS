package uk.ac.ebi.spot.ols.neo4j.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.neo4j.graphdb.GraphDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import uk.ac.ebi.spot.ols.neo4j.model.Property;
import uk.ac.ebi.spot.ols.neo4j.model.TreeNode;
import uk.ac.ebi.spot.ols.neo4j.repository.OntologyPropertyRepository;

/**
 * @author Simon Jupp
 * @date 18/08/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@Service
public class OntologyPropertyGraphService {

    @Autowired(required = false)
    OntologyPropertyRepository ontologyPropertyRepository;

    @Autowired(required = false)
    GraphDatabaseService graphDatabaseService;


    public Page<Property> findAll(Pageable pageable) {
        return ontologyPropertyRepository.findAll(pageable);
    }

    public Page<Property> findAllByIri(String iri, Pageable pageable) {
        return ontologyPropertyRepository.findAllByIri(iri, pageable);
    }

    public Page<Property> findAllByShortForm(String shortForm, Pageable pageable) {
        return ontologyPropertyRepository.findAllByShortForm(shortForm, pageable);
    }

    public Page<Property> findAllByOboId(String oboId, Pageable pageable) {
        return ontologyPropertyRepository.findAllByOboId(oboId, pageable);
    }

    
    public Page<Property> findAllByIsDefiningOntology(Pageable pageable) {
        return ontologyPropertyRepository.findAllByIsDefiningOntology(pageable);
    }

    public Page<Property> findAllByIriAndIsDefiningOntology(String iri, Pageable pageable) {
        return ontologyPropertyRepository.findAllByIriAndIsDefiningOntology(iri, pageable);
    }

    public Page<Property> findAllByShortFormAndIsDefiningOntology(String shortForm, Pageable pageable) {
        return ontologyPropertyRepository.findAllByShortFormAndIsDefiningOntology(shortForm, pageable);
    }

    public Page<Property> findAllByOboIdAndIsDefiningOntology(String oboId, Pageable pageable) {
        return ontologyPropertyRepository.findAllByOboIdAndIsDefiningOntology(oboId, pageable);
    }
    
    
    
    public Page<Property> findAllByOntology(String ontologyId, Pageable pageable) {
        return ontologyPropertyRepository.findAllByOntology(ontologyId, pageable);
    }

    public Property findByOntologyAndIri(String ontologyname, String iri) {
        return ontologyPropertyRepository.findByOntologyAndIri(ontologyname, iri);
    }

    public Page<Property> getParents(String ontologyName, String iri, Pageable pageable) {
        return ontologyPropertyRepository.getParents(ontologyName, iri, pageable);
    }

    public Page<Property> getChildren(String ontologyName, String iri, Pageable pageable) {
        return ontologyPropertyRepository.getChildren(ontologyName, iri, pageable);
    }

    public Page<Property> getDescendants(String ontologyName, String iri, Pageable pageable) {
        return ontologyPropertyRepository.getDescendants(ontologyName, iri, pageable);
    }

    public Page<Property> getAncestors(String ontologyName, String iri, Pageable pageable) {
        return ontologyPropertyRepository.getAncestors(ontologyName, iri, pageable);
    }

    public Property findByOntologyAndShortForm(String ontologyId, String shortForm) {
        return ontologyPropertyRepository.findByOntologyAndShortForm(ontologyId, shortForm);
    }
    public Property findByOntologyAndOboId(String ontologyId, String oboId) {
        return ontologyPropertyRepository.findByOntologyAndOboId(ontologyId, oboId);
    }

    public Page<Property> getRoots(String ontologyId, boolean includeObsoletes, Pageable pageable) {
        return ontologyPropertyRepository.getRoots(ontologyId, includeObsoletes, pageable);
    }
    
    @Cacheable(value = "propertytree", key="#ontologyId.concat('-').concat(#includeObsoletes)")
    public List<TreeNode<Property>> populatePropertyTree(String ontologyId, boolean includeObsoletes, Integer pageSize){
    	Pageable pageable = new PageRequest(0, pageSize);
    	Page<Property> roots = this.getRoots(ontologyId, includeObsoletes, pageable);
    	if (roots == null) 
    		return null;
    	List<Property> rootPropertyDataList = new ArrayList<Property>();
    	rootPropertyDataList.addAll(roots.getContent());
    	List<TreeNode<Property>> rootProperties = new ArrayList<TreeNode<Property>>();
    	
    	while(roots.hasNext()) {
    		roots = this.getRoots(ontologyId, includeObsoletes, roots.nextPageable());
    		rootPropertyDataList.addAll(roots.getContent());
    	}
    	
    	int count = 0;
    	for (Property rootPropertyData : rootPropertyDataList) {
    		TreeNode<Property> rootProperty =  new TreeNode<Property>(rootPropertyData);
    		rootProperty.setIndex(String.valueOf(++count));
    		populateChildren(ontologyId, rootProperty, pageable);	
    		rootProperties.add(rootProperty);
    	}
    	System.out.println("Kamil!!!");
    	return rootProperties;
    }
    
    @Cacheable(value = "propertytree", key="#ontologyId.concat('-').concat(#iri).concat('-').concat(#includeObsoletes)")
    public TreeNode<Property> populatePropertySubTree(String ontologyId, String iri,  boolean includeObsoletes, String rootIndex, Integer pageSize){
    	Pageable pageable = new PageRequest(0, pageSize);
    	TreeNode<Property> rootProperty = null;
    	try {
			String decoded = UriUtils.decode(iri, "UTF-8");
			Property root = this.findByOntologyAndIri(ontologyId, decoded);
	    	rootProperty =  new TreeNode<Property>(root);
	    	rootProperty.setIndex(rootIndex);
	    	populateChildren(ontologyId, rootProperty, pageable);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return rootProperty;
    }
    
    @CacheEvict(value="propertytree", allEntries=true)
    public String removeCache() {
    	return "All property cache removed";
    }
    
    public void populateChildren(String ontologyId, TreeNode<Property> root, Pageable pageable) {
		String decoded;
		int count = 0;
		try {
			decoded = UriUtils.decode(root.getData().getIri(), "UTF-8");
			Page<Property> children = this.getChildren(ontologyId, decoded, pageable);
			List<Property> childrenPropertyDataList = new ArrayList<Property>();
			childrenPropertyDataList.addAll(children.getContent());
	    	while(children.hasNext()) {
	    		children = this.getChildren(ontologyId, decoded, children.nextPageable());
	    		childrenPropertyDataList.addAll(children.getContent());
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
    
}
