package uk.ac.ebi.spot.ols.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import uk.ac.ebi.spot.ols.controller.api.TreeNode;
import uk.ac.ebi.spot.ols.neo4j.model.Term;
import uk.ac.ebi.spot.ols.neo4j.service.OntologyTermGraphService;
  
@Service
public class TreePopulateService {
	
    @Cacheable(value = "termtree", key="#ontologyId.concat('-').concat(#includeObsoletes)")
    public List<TreeNode<Term>> populateTermTree(OntologyTermGraphService ontologyTermGraphService, String ontologyId, boolean includeObsoletes, Integer pageSize){
    	Pageable pageable = new PageRequest(0, pageSize);
    	Page<Term> roots = ontologyTermGraphService.getRoots(ontologyId, includeObsoletes, pageable);
    	if (roots == null) 
    		return null;
    	List<Term> rootTermDataList = new ArrayList<Term>();
    	rootTermDataList.addAll(roots.getContent());
    	List<TreeNode<Term>> rootTerms = new ArrayList<TreeNode<Term>>();
    	
    	while(roots.hasNext()) {
    		roots = ontologyTermGraphService.getRoots(ontologyId, includeObsoletes, roots.nextPageable());
    		rootTermDataList.addAll(roots.getContent());
    	}
    	
    	int count = 0;
    	for (Term rootTermData : rootTermDataList) {
    		TreeNode<Term> rootTerm =  new TreeNode<Term>(rootTermData);
    		rootTerm.setIndex(String.valueOf(++count));
    		populateChildren(ontologyTermGraphService, ontologyId, rootTerm, pageable);	
    		rootTerms.add(rootTerm);
    	}
    	System.out.println("Kamil!!!");
    	return rootTerms;
    }
    
    @CacheEvict(value="termtree", allEntries=true)
    public String removeCache() {
    	return "All cache removed";
    }
    
    public void populateChildren(OntologyTermGraphService ontologyTermGraphService, String ontologyId, TreeNode<Term> root, Pageable pageable) {
		String decoded;
		int count = 0;
		try {
			decoded = UriUtils.decode(root.getData().getIri(), "UTF-8");
			Page<Term> children = ontologyTermGraphService.getChildren(ontologyId, decoded, pageable);
			List<Term> childrenTermDataList = new ArrayList<Term>();
			childrenTermDataList.addAll(children.getContent());
	    	while(children.hasNext()) {
	    		children = ontologyTermGraphService.getChildren(ontologyId, decoded, children.nextPageable());
	    		childrenTermDataList.addAll(children.getContent());
	    	}			
					
			for (Term term : childrenTermDataList) {
				TreeNode<Term> child =  new TreeNode<Term>(term);
				child.setIndex(root.getIndex()+"."+ ++count);
				populateChildren(ontologyTermGraphService, ontologyId, child, pageable);
				root.addChild(child);
			}
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
