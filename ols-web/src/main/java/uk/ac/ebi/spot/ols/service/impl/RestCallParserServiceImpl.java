package uk.ac.ebi.spot.ols.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerMapping;
import uk.ac.ebi.spot.ols.entities.HttpServletRequestInfo;
import uk.ac.ebi.spot.ols.service.RestCallParserService;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Service
public class RestCallParserServiceImpl implements RestCallParserService {
    @Override
    public HttpServletRequestInfo parse(HttpServletRequest request) {
        String requestURI = request.getRequestURI();

        Map<String, String> pathVariables = (Map<String, String>) request
            .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        StringBuilder keyValueParametersBuilder = new StringBuilder();

        for (Map.Entry<String, String> entry : pathVariables.entrySet()) {
            requestURI = requestURI.replace(entry.getValue(), String.format("{%s}", entry.getKey()));
            keyValueParametersBuilder.append(String.format("%s:%s;", entry.getKey(), entry.getValue()));
        }

        String parameters = String.join(",", pathVariables.values());

        return new HttpServletRequestInfo(requestURI, parameters, keyValueParametersBuilder.toString());
    }
}
