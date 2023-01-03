package uk.ac.ebi.spot.ols.neo4j.service;

import org.neo4j.graphdb.GraphDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import uk.ac.ebi.spot.ols.neo4j.model.Individual;
import uk.ac.ebi.spot.ols.neo4j.model.Term;
import uk.ac.ebi.spot.ols.neo4j.model.TreeNode;
import uk.ac.ebi.spot.ols.neo4j.repository.OntologyIndividualRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Simon Jupp
 * @date 18/08/2015
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@Service
public class OntologyIndividualService {

    @Autowired(required = false)
    OntologyIndividualRepository individualRepository;

    @Autowired(required = false)
    GraphDatabaseService graphDatabaseService;

    public Page<Individual> findAll(Pageable pageable) {
        return individualRepository.findAll(pageable);
    }

    public Page<Individual> findAllByIsDefiningOntology(Pageable pageable) {
        return individualRepository.findAllByIsDefiningOntology(pageable);
    }
    
    public Page<Individual> findAllByIri(String iri, Pageable pageable) {
        return individualRepository.findAllByIri(iri, pageable);
    }

    public Page<Individual> findAllByIriAndIsDefiningOntology(String iri, Pageable pageable) {
        return individualRepository.findAllByIriAndIsDefiningOntology(iri, pageable);
    }
    
    public Page<Individual> findAllByShortForm(String shortForm, Pageable pageable) {
        return individualRepository.findAllByShortForm(shortForm, pageable);
    }

    public Page<Individual> findAllByShortFormAndIsDefiningOntology(String shortForm, Pageable pageable) {
        return individualRepository.findAllByShortFormAndIsDefiningOntology(shortForm, pageable);
    }    
    
    public Page<Individual> findAllByOboId(String oboId, Pageable pageable) {
        return individualRepository.findAllByOboId(oboId, pageable);
    }

    public Page<Individual> findAllByOboIdAndIsDefiningOntology(String oboId, Pageable pageable) {
        return individualRepository.findAllByOboIdAndIsDefiningOntology(oboId, pageable);
    }    
    
    public Page<Individual> findAllByOntology(String ontologyId, Pageable pageable) {
        return individualRepository.findAllByOntology(ontologyId, pageable);
    }

    public Individual findByOntologyAndIri(String ontologyname, String iri) {
        return individualRepository.findByOntologyAndIri(ontologyname, iri);
    }

    public Page<Term> getDirectTypes(String ontologyName, String iri, Pageable pageable) {
        return individualRepository.getDirectTypes(ontologyName, iri, pageable);
    }

    public Page<Term> getAllTypes(String ontologyName, String iri, Pageable pageable) {
        return individualRepository.getAllTypes(ontologyName, iri, pageable);
    }

    public Individual findByOntologyAndShortForm(String ontologyId, String shortForm) {
        return individualRepository.findByOntologyAndShortForm(ontologyId, shortForm);
    }
    public Individual findByOntologyAndOboId(String ontologyId, String oboId) {
        return individualRepository.findByOntologyAndOboId(ontologyId, oboId);
    }
    
    @Cacheable(value = "concepttree", key="#ontologyId.concat('-').concat(#schema).concat('-').concat(#narrower).concat('-').concat(#withChildren).concat('-').concat(#lang)")   
    public List<TreeNode<Individual>> conceptTree (String ontologyId, Integer pageSize, boolean schema, boolean narrower, boolean withChildren, String lang){
        Page<Individual> terms = this.findAllByOntology(ontologyId, new PageRequest(0, pageSize));
        List<Individual> listOfTerms = new ArrayList<Individual>();
        listOfTerms.addAll(terms.getContent()); 
        
    	while(terms.hasNext()) {
    		terms = this.findAllByOntology(ontologyId, terms.nextPageable());
    		listOfTerms.addAll(terms.getContent());
    	}       
        
        List<TreeNode<Individual>> rootIndividuals = new ArrayList<TreeNode<Individual>>();      
        int count = 0;
        
        if(schema) {
            for (Individual indiv : listOfTerms)
           	    if (indiv.getAnnotationByLang(lang).get("hasTopConcept") != null) {
        		 for (String iriTopConcept : (String[]) indiv.getAnnotationByLang(lang).get("hasTopConcept")) {
        			 Individual topConceptIndividual = findIndividual(listOfTerms,iriTopConcept);
        			 TreeNode<Individual> topConcept =  new TreeNode<Individual>(topConceptIndividual);
        		     topConcept.setIndex(String.valueOf(++count));
        		     if(withChildren) {
            		     if(narrower)
            		         populateChildrenandRelatedByNarrower(topConceptIndividual,topConcept,listOfTerms,lang);
            		     else
            		    	 populateChildrenandRelatedByBroader(topConceptIndividual,topConcept,listOfTerms,lang);
        		     }
        			 rootIndividuals.add(topConcept);
        		 }
           	    }  
        } else for (Individual individual : listOfTerms) {
        	 TreeNode<Individual> tree = new TreeNode<Individual>(individual);
        	 
        	 if (tree.isRoot() && individual.getAnnotationByLang(lang).get("topConceptOf") != null) {
				tree.setIndex(String.valueOf(++count));
				if(withChildren) {
					if(narrower)
	                    populateChildrenandRelatedByNarrower(individual,tree,listOfTerms,lang);
					else
						populateChildrenandRelatedByBroader(individual,tree,listOfTerms,lang);
				}
				rootIndividuals.add(tree);
			}
		}    
             
         return rootIndividuals;
    }
    
    @Cacheable(value = "concepttree", key="#ontologyId.concat('-').concat(#narrower).concat('-').concat(#withChildren).concat('-').concat(#lang)")
    public List<TreeNode<Individual>> conceptTreeWithoutTop (String ontologyId, Integer pageSize, boolean narrower, boolean withChildren, String lang){
        Page<Individual> terms = this.findAllByOntology(ontologyId, new PageRequest(0, pageSize));
        List<Individual> listOfTerms = new ArrayList<Individual>();
        listOfTerms.addAll(terms.getContent()); 
        
    	while(terms.hasNext()) {
    		terms = this.findAllByOntology(ontologyId, terms.nextPageable());
    		listOfTerms.addAll(terms.getContent());
    	}  
    	
        Set<String> rootIRIs = new HashSet<String>();
        List<TreeNode<Individual>> rootIndividuals = new ArrayList<TreeNode<Individual>>();
        int count = 0;
        if(!narrower) {
            for (Individual individual : listOfTerms) {
    			if (individual.getAnnotationByLang(lang).get("broader") != null) {
    				for (String iriBroader : (String[]) individual.getAnnotationByLang(lang).get("broader")) {
    					Individual broaderIndividual = findIndividual(listOfTerms,iriBroader);
    					if (broaderIndividual.getAnnotationByLang(lang).get("broader") == null) {
    						rootIRIs.add(iriBroader);
    					}	
    				}
    			}
            }
            
            for (String iri : rootIRIs) {
            	Individual topConceptIndividual = findIndividual(listOfTerms, iri);
        		TreeNode<Individual> topConcept = new TreeNode<Individual>(topConceptIndividual);
        		topConcept.setIndex(String.valueOf(++count));
        		if(withChildren)
    		        populateChildrenandRelatedByBroader(topConceptIndividual,topConcept,listOfTerms,lang);
        		rootIndividuals.add(topConcept);
            }
            
        } else {
        	for (Individual individual : listOfTerms) {
        		if (individual.getAnnotationByLang(lang).get("narrower") != null) {
        			boolean root = true;
        			for (Individual indiv : listOfTerms) {
        				if (indiv.getAnnotationByLang(lang).get("narrower") != null) {
        					for (String iriNarrower : (String[]) indiv.getAnnotationByLang(lang).get("narrower")) {
        						if (individual.getIri().equals(iriNarrower))
        								root = false;
        					}
        				} 
        			}
        			
        			if(root) {
                		TreeNode<Individual> topConcept = new TreeNode<Individual>(individual);
                		topConcept.setIndex(String.valueOf(++count));
                		if(withChildren)
        		            populateChildrenandRelatedByNarrower(individual,topConcept,listOfTerms,lang);
        		        rootIndividuals.add(topConcept);
        			}
        		}
        	}
        }
      
         return rootIndividuals;
    }
    
    @Cacheable(value = "concepttree", key="#ontologyId.concat('-').concat('s').concat('-').concat(#iri).concat('-').concat(#narrower).concat('-').concat(#index)")
    public TreeNode<Individual> conceptSubTree(String ontologyId, String iri, boolean narrower, String lang,String index, Integer pageSize){
        Page<Individual> terms = this.findAllByOntology(ontologyId, new PageRequest(0, pageSize));
        List<Individual> listOfTerms = new ArrayList<Individual>();
        listOfTerms.addAll(terms.getContent()); 
        
    	while(terms.hasNext()) {
    		terms = this.findAllByOntology(ontologyId, terms.nextPageable());
    		listOfTerms.addAll(terms.getContent());
    	}

		Individual topConceptIndividual = findIndividual(listOfTerms,iri);	        
		TreeNode<Individual> topConcept =  new TreeNode<Individual>(topConceptIndividual);
		topConcept.setIndex(index);
		 if(narrower)
		     populateChildrenandRelatedByNarrower(topConceptIndividual,topConcept,listOfTerms,lang);
		 else
			 populateChildrenandRelatedByBroader(topConceptIndividual,topConcept,listOfTerms,lang);

	     return topConcept;
    }
    
    public Individual findIndividual(List<Individual> wholeList, String iri) {
    	for (Individual individual : wholeList)
    		if(individual.getIri().equals(iri))
    			return individual;
    	return new Individual();
    }
    
    public List<Individual> findRelated(String ontologyId, String iri, String relationType, String lang) {
    	List<Individual> related = new ArrayList<Individual>();	
		Individual individual = this.findByOntologyAndIri(ontologyId, iri);
		if (individual != null)
			if (individual.getAnnotationByLang(lang).get(relationType) != null)
				for (String iriBroader : (String[]) individual.getAnnotationByLang(lang).get(relationType)) 
					related.add(this.findByOntologyAndIri(ontologyId, iriBroader));
    	
    	return related;
    }
    
    public List<Individual>findRelatedIndirectly(String ontologyId, String iri, String relationType,  String lang, Integer pageSize){
    	List<Individual> related = new ArrayList<Individual>();	
    	
    	Individual individual = this.findByOntologyAndIri(ontologyId, iri);
    	if(individual == null)
    		return related;
    	if(individual.getIri() == null)
    		return related;
    	
        Page<Individual> terms = this.findAllByOntology(ontologyId, new PageRequest(0, pageSize));
        List<Individual> listOfTerms = new ArrayList<Individual>();
        listOfTerms.addAll(terms.getContent()); 
        
    	while(terms.hasNext()) {
    		terms = this.findAllByOntology(ontologyId, terms.nextPageable());
    		listOfTerms.addAll(terms.getContent());
    	}   
    		
    	for (Individual term : listOfTerms) {
    		if (term != null)
    			if (term.getAnnotationByLang(lang).get(relationType) != null)
    				for (String iriRelated : (String[]) term.getAnnotationByLang(lang).get(relationType)) 
    					if(iriRelated.equals(iri))
    					    related.add(term);
    	}
    	    	
    	return related;
    }
    
    public void populateChildrenandRelatedByNarrower(Individual individual, TreeNode<Individual> tree, List<Individual> listOfTerms, String lang) {
		
		if (individual.getAnnotationByLang(lang).get("related") != null)
		for (String iriRelated : (String[]) individual.getAnnotationByLang(lang).get("related")) {
			TreeNode<Individual> related = new TreeNode<Individual>(findIndividual(listOfTerms,iriRelated));
			related.setIndex(tree.getIndex()+ ".related");
			tree.addRelated(related);
		}
    	int count = 0;
    	if (individual.getAnnotationByLang(lang).get("narrower") != null)
		for (String iriChild : (String[]) individual.getAnnotationByLang(lang).get("narrower")) {
			Individual childIndividual = findIndividual(listOfTerms,iriChild);
			TreeNode<Individual> child = new TreeNode<Individual>(childIndividual);
			child.setIndex(tree.getIndex()+"."+ ++count);			
			populateChildrenandRelatedByNarrower(childIndividual,child,listOfTerms,lang);
			tree.addChild(child);
		}
    }
    
    public void populateChildrenandRelatedByBroader(Individual individual, TreeNode<Individual> tree, List<Individual> listOfTerms, String lang) {
		if (individual.getAnnotationByLang(lang).get("related") != null)
		for (String iriRelated : (String[]) individual.getAnnotationByLang(lang).get("related")) {
			TreeNode<Individual> related = new TreeNode<Individual>(findIndividual(listOfTerms,iriRelated));
			related.setIndex(tree.getIndex()+ ".related");
			tree.addRelated(related);
		}
		int count = 0;
		for ( Individual indiv : listOfTerms) {
			if (indiv.getAnnotationByLang(lang).get("broader") != null)
				for (String iriBroader : (String[]) indiv.getAnnotationByLang(lang).get("broader"))
					if(individual.getIri() != null)
						if (individual.getIri().equals(iriBroader)) {
							TreeNode<Individual> child = new TreeNode<Individual>(indiv);
							child.setIndex(tree.getIndex()+"."+ ++count);	
							populateChildrenandRelatedByBroader(indiv,child,listOfTerms,lang);
							tree.addChild(child);
						}	
		}
    }
    
    @CacheEvict(value="concepttree", allEntries=true)
    public String removeConceptTreeCache() {
    	return "All concept tree cache removed!";
    }
    
}
