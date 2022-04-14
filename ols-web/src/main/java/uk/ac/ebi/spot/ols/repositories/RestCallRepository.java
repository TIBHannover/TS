package uk.ac.ebi.spot.ols.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.ols.entities.RestCall;

public interface RestCallRepository extends MongoRepository<RestCall, String>, RestCallRepositoryCustom {

}
