package uk.ac.ebi.spot.ols.entities;

import java.util.function.Predicate;

public enum RestCallParameterType {
    PATH {
        @Override
        public Predicate<RestCallParameter> getRestCallParameterPredicate() {
            return RestCallParameter::isPathType;
        }
    },
    QUERY {
        @Override
        public Predicate<RestCallParameter> getRestCallParameterPredicate() {
            return RestCallParameter::isQueryType;
        }
    };

    public abstract Predicate<RestCallParameter> getRestCallParameterPredicate();
}
