package uk.ac.ebi.spot.ols.service.impl;

import org.apache.commons.lang3.StringUtils;

public class OntologyNameParser {

    public String getName(String uri) {
        int lastSlashIndex = uri.lastIndexOf('/');
        int pointIndex = uri.lastIndexOf('.');

        if (StringUtils.isEmpty(uri) || lastSlashIndex < 0 || pointIndex < 0 || pointIndex < lastSlashIndex) {
            return null;
        }

        return uri.substring(lastSlashIndex + 1, pointIndex);
    }
}
