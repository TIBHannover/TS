# TIB Terminology Service (TS)

TIB Terminology Service is forked from Ontology Lookup Service (OLS) by SPOT at EBI. The TS and OLS abbreviations refer to the same service throughout the code and documentation.

* TIB TS is currently live here https://service.tib.eu/ts4tib/index
* A REST API for TIB TS is described here https://service.tib.eu/ts4tib/swagger-ui.html
* Instructions on how to build a local OLS installation are here
  http://www.ebi.ac.uk/ols/docs/installation-guide
* Instructions on Vagrant and Ansible based virtual machine installation here
  https://github.com/TIBHannover/ebispot-ols-box
* Run OLS with docker here
  https://github.com/EBISPOT/ontotools-docker-config
* Further OLS documentation can be found here
  http://www.ebi.ac.uk/ols/docs

## Overview

![TS Architecture](OLS-Architecture.png)

This is the backend, API interface and Thymeleaf based frontend codebase for the TIB TS. 
There is also a new frontend available implemented with React here: 
https://git.tib.eu/terminology/tib-terminology-service-2.0 
TIB has been developed around two key ontology indexes that can be built
 and used independently from the core website. We provide services to build 
 a Solr index and a Neo4j index. The Solr index is used to provide 
 text-based queries over the ontologies while the Neo4j index
  is used to query the ontology structure and is the primary driver of the TS REST API.
 
TS has been developed with the Spring Data and Spring Boot framework.
You can build this project with Maven and the following Spring Boot
applications will be available to run.
 
All of the apps are available under the ols-apps module.

* [ols-apps/ols-solr-app](ols-apps/ols-solr-app) - Spring Boot
  application for building a Solr index for one or more ontologies.
  Requires access to a Solr server.
* [ols-apps/ols-neo4j-app](ols-apps/ols-neo4j-app) - Spring Boot
  application for building a Neo4j index for one or more ontologies.
  Builds an embedded Neo4j database. You can run a Neo4j server that
  uses the generated Neo4j database.

To run a complete local TS installation you will need a MongoDB
database. This is a lightweight database that is used to store all the
ontology configuration and application state information. See here for
more information http://www.ebi.ac.uk/ols/docs/installation-guide

* [ols-apps/ols-config-importer](ols-apps/ols-config-importer) - Spring
  Boot application for loading config files into the MongoDB database.
  This includes support for reading config files specified using the OBO
  Foundry YAML format.
* [ols-apps/ols-indexer](ols-apps/ols-indexer) - Spring Boot
  application for building the complete OLS indexes. This app fetches
  ontologies specified in the config files, checks whether they have
  changed from a previous download, and if they have changed, will
  create all the necessary Solr and Neo4j indexes.
* [ols-web](ols-web) - This contains the WAR file that can be deployed
  in Tomcat to launch the OLS website and REST API. It depends on
  [ols-term-type-treeview]
  (https://github.com/EBISPOT/ols-term-type-treeview) and
  [ols-tabbed-term-treeview]
  (https://github.com/EBISPOT/ols-tabbed-term-treeview).


## Deploying with Docker

The preferred method of deployment for TS is using Docker. If you would like to deploy **the entire OntoTools stack** (TS, OxO, and ZOOMA), check out the [OntoTools Docker Config](https://github.com/EBISPOT/ontotools-docker-config) repository. If you would like to deploy **TS only**, read on.

First, create the necessary volumes:

    docker volume create --name=ols-neo4j-data
    docker volume create --name=ols-mongo-data
    docker volume create --name=ols-solr-data
    docker volume create --name=ols-downloads

Then, start solr and mongodb only:

    docker-compose up -d solr mongo


Then build Docker images of config importer and indexer tools using the Dockerfiles in this repository. (Although there exists prebuilt config importer and indexer images of ebispot in Docker Hub, they cannot be used for this particular version due to their missing features.) 

    docker build -f ols-apps/ols-config-importer/Dockerfile -t ols-config-importer .
    docker build -f ols-apps/ols-indexer/Dockerfile -t ols-indexer .


Then, adjust the configuration YAML files in the `config` directory as required,
and load the configuration into the Mongo database using the config loader:

    docker run --net=host -v $(pwd)/config:/config ols-config-importer

Then, run the indexer:

    docker run --net=host -v ols-neo4j-data:/mnt/neo4j -v ols-downloads:/mnt/downloads ols-indexer

Finally, start the OLS webserver:

    docker-compose up -d ols-web

You should now be able to access a populated OLS instance at `http://localhost:8080`. 


## Building TS manually

To build TS you will need to use Java 8 and Maven 3.x.

To build TS, in the root directory of TS, run:
`mvn clean package`. Currently this will fail with the following error:

`[ERROR] Failed to execute goal on project ols-neo4j: Could not resolve dependencies for project uk.ac.ebi.spot:ols-neo4j:jar:3.2.1-SNAPSHOT: Failed to collect dependencies at org.springframework.data:spring-data-neo4j:jar:3.4.5.RELEASE -> org.neo4j:neo4j-cypher-dsl:jar:2.0.1: Failed to read artifact descriptor for org.neo4j:neo4j-cypher-dsl:jar:2.0.1: Could not transfer artifact org.neo4j:neo4j-cypher-dsl:pom:2.0.1 from/to maven-neo4j (https://m2.neo4j.org/content/repositories/releases/): Failed to transfer file https://m2.neo4j.org/content/repositories/releases/org/neo4j/neo4j-cypher-dsl/2.0.1/neo4j-cypher-dsl-2.0.1.pom with status code 502 -> [Help 1]`

To correct this, copy the contents of the `build-fix` directory into your Maven 
repository under `~/.m2/repository`.

Run `mvn clean package` again. TS should now build successfully. 

### Other build errors
Other build errors you may come across are the following:

1. Wrong version of Java used:

`[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.1:compile (default-compile) on project ols-solr: Compilation failure: Compilation failure: 
 [ERROR] /Users/james/OLS/ols-solr/src/main/java/uk/ac/ebi/spot/ols/config/SolrContext.java:[15,24] package javax.annotation does not exist
 [ERROR] /Users/james/OLS/ols-solr/src/main/java/uk/ac/ebi/spot/ols/config/SolrContext.java:[25,4] cannot find symbol
 [ERROR]  symbol:  class Resource
 [ERROR]  location: class uk.ac.ebi.spot.ols.config.SolrContext
 [ERROR] -> [Help 1]` 
 
This is the error you get when you compile TS with Java 11. The fix for this 
build error is to ensure your Maven installation is indeed using Java 8 for 
compilation.  

## Customisation

It is possible to customise several branding options in `ols-web/src/main/resources/application.properties`:

* `ols.customisation.debrand` — If set to true, removes header and footer, documentation, and about page
* `ols.customisation.ebiInfo` — If set to true, EBI specific banners are enabled
* `ols.customisation.logo` — The relative location of the logo file to the static directory
* `ols.customisation.title` — A custom title for your instance, e.g. "My OLS Instance"
* `ols.customisation.short-title` — A shorter version of the custom title, e.g. "MYOLS"
* `ols.customisation.description` — A description of the instance
* `ols.customisation.org` — The organisation hosting your instance
* `ols.customisation.web` — Web address of your organization
* `ols.customisation.twitter` — Twitter address of your organization
* `ols.customisation.backgroundImage` — The background image of page header
* `ols.customisation.backgroundColor` — The background color of page header
* `ols.customisation.issuesPage` — The issues page address for notifying problems
* `ols.customisation.supportMail` — The support mail address for notifying feedback
* `ols.customisation.hideGraphView` — Set to true to hide the graph view 
* `ols.customisation.ontologyAlias` — A custom word or phrase to use instead of "Ontology", e.g. "Data Dictionary"
* `ols.customisation.ontologyAliasPlural` — As `ontologyAlias` but plural, e.g. "Data Dictionaries"
* `ols.customisation.oxoUrl` — The URL of an OxO instance to link to with a trailing slash e.g. `https://www.ebi.ac.uk/spot/oxo/`







