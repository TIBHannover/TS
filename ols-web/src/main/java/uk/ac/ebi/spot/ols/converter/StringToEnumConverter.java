package uk.ac.ebi.spot.ols.converter;


import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.spot.ols.entities.RestCallParameterType;

public class StringToEnumConverter implements Converter<String, RestCallParameterType> {
    @Override
    public RestCallParameterType convert(String source) {
        return RestCallParameterType.valueOf(source.toUpperCase());
    }
}