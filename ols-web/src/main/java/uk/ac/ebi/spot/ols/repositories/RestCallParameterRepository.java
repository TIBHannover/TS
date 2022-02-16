package uk.ac.ebi.spot.ols.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.ols.entities.RestCallParameter;

public interface RestCallParameterRepository extends JpaRepository<RestCallParameter, Long> {

}
