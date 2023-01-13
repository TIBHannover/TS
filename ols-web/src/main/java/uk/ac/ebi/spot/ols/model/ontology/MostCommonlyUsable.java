package uk.ac.ebi.spot.ols.model.ontology;

public interface MostCommonlyUsable {
    default boolean consideredForCommonlyUsed() {
        return true;
    }
}
