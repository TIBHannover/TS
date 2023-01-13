package uk.ac.ebi.spot.ols.config;

import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import uk.ac.ebi.spot.ols.repositories.ProcessedMongoOntologyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

@EnableMongoRepositories(basePackageClasses = ProcessedMongoOntologyRepository.class)
@Configuration
public class MongoConfig {

    @Value("${ols.mongo.readpreference:}")
    String readPreference ="";

    @Value("${ols.mongo.seedlist:}")
    String seedList = "";

    @Autowired
    MongoProperties properties;

    @Bean
    MongoClientFactoryBean mongoFactory() throws UnknownHostException {

        MongoClientFactoryBean mongoClientFactoryBean = new MongoClientFactoryBean();

        if (properties.getAuthenticationDatabase() != null) {
            MongoCredential credential = MongoCredential.createCredential(properties.getUsername(), properties.getAuthenticationDatabase(), properties.getPassword());
            mongoClientFactoryBean.setCredential(new MongoCredential[]{credential});
        }


        if (!("").equals(readPreference) && !("").equals(seedList)) {
            List<ServerAddress> seedListArray = new ArrayList<ServerAddress>();

            for (String seed : seedList.split(",")) {
                seedListArray.add(new ServerAddress(seed));
            }

            mongoClientFactoryBean.setReplicaSet(seedListArray.toArray(new ServerAddress[seedListArray.size()]).toString());
            ReadPreference preference = ReadPreference.valueOf(readPreference);

        }
        else {
            mongoClientFactoryBean.setHost(properties.getHost());
            if (properties.getPort() != null) {
                mongoClientFactoryBean.setPort(properties.getPort());
            }
        }
        return mongoClientFactoryBean;

    }
}
