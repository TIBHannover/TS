
FROM maven:3.6-jdk-8 AS build
RUN mkdir /opt/ols
COPY . /opt/ols/ 
<<<<<<< HEAD
COPY build-fix/org/neo4j/neo4j-cypher-dsl/2.0.1/. /root/.m2/repository/org/neo4j/neo4j-cypher-dsl/2.0.1/
COPY build-fix/com/google/guava/guava/22.0/. /root/.m2/repository/com/google/guava/guava/22.0/
=======
COPY build-fix/. /root/.m2/repository/
>>>>>>> 6b26b5e43ada0ebc714898f7a81a1620b94f0802
RUN cd /opt/ols && ls && mvn clean package -DskipTests

FROM openjdk:8-jre-alpine
RUN apk add bash
COPY --from=build /opt/ols/ols-apps/ols-config-importer/target/ols-config-importer.jar /opt/ols-config-importer.jar
EXPOSE 8080
<<<<<<< HEAD
ENTRYPOINT ["java", "-jar", "/opt/ols-config-importer.jar", "--ols.ontology.config=ols-config.yaml","--ols.obofoundry.ontology.config=obo-config.yaml"]
=======
ENTRYPOINT ["java", "-jar", "/opt/ols-config-importer.jar", "--ols.ontology.config=file:///config/ols-config.yaml","--ols.obofoundry.ontology.config=file:///config/obo-config.yaml"]
>>>>>>> 6b26b5e43ada0ebc714898f7a81a1620b94f0802
