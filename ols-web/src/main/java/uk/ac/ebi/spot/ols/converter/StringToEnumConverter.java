package uk.ac.ebi.spot.ols.converter;
package eu.tib.ts.converter;

import eu.tib.ts.model.ontology.CharacteristicsType;
import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.spot.ols.entities.RestCallParameterType;

public class StringToEnumConverter implements Converter<String, RestCallParameterType> {
    @Override
    public RestCallParameterType convert(String source) {
        return RestCallParameterType.valueOf(source.toUpperCase());
    }
}

public class StringToEnumConverter implements Converter<String, CharacteristicsType> {
    @Override
    public CharacteristicsType convert(String source) {
        return CharacteristicsType.valueOf(source.toUpperCase());
    }
}