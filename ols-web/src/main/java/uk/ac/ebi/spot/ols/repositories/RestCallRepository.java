package uk.ac.ebi.spot.ols.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.ols.entities.RestCall;

public interface RestCallRepository extends JpaRepository<RestCall, Long>, RestCallRepositoryCustom {

}
