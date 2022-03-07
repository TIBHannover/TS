
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
COPY --from=build /opt/ols/ols-web/target/ols-boot.war /opt/ols-boot.war
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/opt/ols-boot.war"]
