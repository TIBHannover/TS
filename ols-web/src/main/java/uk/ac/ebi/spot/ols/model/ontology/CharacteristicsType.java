package uk.ac.ebi.spot.ols.model.ontology;

import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public enum CharacteristicsType implements MostCommonlyUsable {
    PROPERTY {
        @Override
        public <T extends ExtendedOntology> Set<String> getOntologyCharacteristics(T ontology) {
            return ontology.getProperties();
        }
    },
    CLASS {
        @Override
        public <T extends ExtendedOntology> Set<String> getOntologyCharacteristics(T ontology) {
            return ontology.getClasses();
        }
    },
    NAMESPACE {
        @Override
        public <T extends ExtendedOntology> Set<String> getOntologyCharacteristics(T ontology) {
            return ontology.getNamespaces();
        }
    },
    IMPORT {
        @Override
        public <T extends ExtendedOntology> Set<String> getOntologyCharacteristics(T ontology) {
            return ontology.getImports();
        }
    },
    INDIVIDUAL {
        @Override
        public <T extends ExtendedOntology> Set<String> getOntologyCharacteristics(T ontology) {
            return ontology.getIndividuals();
        }

        @Override
        public boolean consideredForCommonlyUsed() {
            return false;
        }
    };

    public abstract <T extends ExtendedOntology> Set<String> getOntologyCharacteristics(T ontology);

    public <T extends ExtendedOntology> Set<String> getCharacteristics(T ontology) {
        Set<String> ontologyCharacteristics = getOntologyCharacteristics(ontology);

        return CollectionUtils.isEmpty(ontologyCharacteristics)
            ? Collections.emptySet()
            : ontologyCharacteristics;
    }

    public <T extends ExtendedOntology> Set<String> getCharacteristicsToLowerCase(T ontology) {
        return getOntologyCharacteristics(ontology).stream()
            .map(String::toLowerCase)
            .collect(Collectors.toSet());
    }
}
