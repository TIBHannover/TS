package uk.ac.ebi.spot.ols.entities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.ac.ebi.spot.ols.repositories.UserOntologyRepository;
import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class UniqueFieldValidator implements ConstraintValidator<UniqueField, UserOntology> {

    @Autowired UserOntologyRepository userOntologyRepository;
    
    public List<UserOntology> findByName(String name, UserOntologyRepository userOntologyRepository) {
    	List<UserOntology> temp = new ArrayList<UserOntology>();
    	try {
			for (UserOntology userOntology : userOntologyRepository.findAll()) {
				if (userOntology.getName().equals(name))
					temp.add(userOntology);
			}
		} catch (NullPointerException e) {
			System.out.println(e.getMessage());
		}	
    	
    	return temp;
    }
    
    public List<UserOntology> findByPreferredPrefix(String preferredPrefix, UserOntologyRepository userOntologyRepository) {
    	List<UserOntology> temp = new ArrayList<UserOntology>();
    	try {
			for (UserOntology userOntology : userOntologyRepository.findAll()) {
				if (userOntology.getPreferredPrefix().equals(preferredPrefix))
					temp.add(userOntology);
			}
		} catch (NullPointerException e) {
			System.out.println(e.getMessage());
		}	
    	
    	return temp;
    }

    @Override
    public boolean isValid(UserOntology uo, ConstraintValidatorContext context) {
    	List<UserOntology> listName = findByName(uo.getName(), userOntologyRepository);
    	List<UserOntology> listPreferredPrefix = findByPreferredPrefix(uo.getPreferredPrefix(), userOntologyRepository);
        if (listName.size() == 0 && listPreferredPrefix.size()== 0)
        	return true;
        
        for (UserOntology userOntology : listName) {
			if (userOntology.getId() != uo.getId())
				return false;
		}
        
        for (UserOntology userOntology : listPreferredPrefix) {
			if (userOntology.getId() != uo.getId())
				return false;
		}
    	    	
    	return true;
    }
}
