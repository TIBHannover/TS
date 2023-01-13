package uk.ac.ebi.spot.ols.model.ontology;

import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum OntologyType {
    TTL("TTL"),
    OWL("RDF/XML");

    @Getter
    private final String name;
    private static final Map<String, OntologyType> ENUM_MAP;

    OntologyType(String name) {
        this.name = name;
    }

    static {
        Map<String, OntologyType> map = new ConcurrentHashMap<>();
        for (OntologyType instance : OntologyType.values()) {
            map.put(instance.getName().toLowerCase(), instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public static OntologyType get(String name) {
        return ENUM_MAP.getOrDefault(name.toLowerCase(), OntologyType.OWL);
    }
}
