package uk.ac.ebi.spot.ols.service;

import uk.ac.ebi.spot.ols.entities.HttpServletRequestInfo;

import javax.servlet.http.HttpServletRequest;

public interface RestCallParserService {
    HttpServletRequestInfo parse(HttpServletRequest request);
}
