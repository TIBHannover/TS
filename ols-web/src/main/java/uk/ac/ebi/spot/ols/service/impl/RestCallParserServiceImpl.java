package uk.ac.ebi.spot.ols.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.HandlerMapping;
import uk.ac.ebi.spot.ols.entities.HttpServletRequestInfo;
import uk.ac.ebi.spot.ols.entities.RestCallParameter;
import uk.ac.ebi.spot.ols.entities.RestCallParameterType;
import uk.ac.ebi.spot.ols.service.RestCallParserService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class RestCallParserServiceImpl implements RestCallParserService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final UrlCyclicDecoder decoder = new UrlCyclicDecoder();

    private final Set<String> frontends = readFrontends();

    @Override
    public HttpServletRequestInfo parse(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        requestURI = decoder.decode(requestURI);

        Map<String, String> pathVariablesMap = (Map<String, String>) request
            .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        Set<RestCallParameter> pathVariables = new HashSet<>();

        if (pathVariablesMap != null)
            for (Map.Entry<String, String> entry : pathVariablesMap.entrySet()) {
                String parameterName = entry.getKey();
                String parameterValue = decoder.decode(entry.getValue());

                int startIndex = requestURI.indexOf(parameterValue) - 1;
                int endIndex = startIndex + parameterValue.length() + 1;

                if (startIndex >= 0 && requestURI.charAt(startIndex) == '/') {
                    requestURI = doReplacement(requestURI, parameterName, startIndex, endIndex);
                    pathVariables.add(new RestCallParameter(parameterName, parameterValue, RestCallParameterType.PATH));
                }
            }

        Set<RestCallParameter> queryParameters = new HashSet<>();
        try {
            queryParameters = getQueryParameters(request);
        } catch (UnsupportedEncodingException e) {
            logger.error("Could not get query parameters: {}", e.getLocalizedMessage());
        }

        Set<RestCallParameter> headers = new HashSet<RestCallParameter>();
        for (Enumeration<?> names = request.getHeaderNames(); names.hasMoreElements();) {
            String headerName = (String) names.nextElement();
            if (!headerName.equals("user-agent"))
                continue;

            for(Enumeration<?> values = request.getHeaders(headerName); values.hasMoreElements();){
                String headerValue = (String) values.nextElement();
                if(frontends.contains(headerValue))
                    headers.add(new RestCallParameter(headerName,headerValue, RestCallParameterType.HEADER));
            }

        }

        return new HttpServletRequestInfo(requestURI, pathVariables, queryParameters, headers);
    }

    private String doReplacement(String str, String parameterName, int startIndex, int endIndex) {
        return str.substring(0, startIndex + 1) +
            String.format("{%s}", parameterName) +
            str.substring(endIndex);
    }

    private Set<RestCallParameter> getQueryParameters(HttpServletRequest request) throws UnsupportedEncodingException {
        Set<RestCallParameter> queryParameters = new HashSet<>();

        String queryString = request.getQueryString();
        if (StringUtils.isEmpty(queryString)) {
            return queryParameters;
        }

        queryString = URLDecoder.decode(queryString, StandardCharsets.UTF_8.toString());
        String[] parameters = queryString.split("&");
        for (String parameter : parameters) {
            String[] keyValuePair = parameter.split("=");
            String[] values = null;
            if(keyValuePair.length >1)
	            if(keyValuePair[1] != null)
	            	if (!keyValuePair[1].isEmpty())
	                    values = keyValuePair[1].split(",");
            if (values != null)
	            for (String value : values) {
	                queryParameters.add(new RestCallParameter(keyValuePair[0], value, RestCallParameterType.QUERY));
	            }
        }

        return queryParameters;
    }

    private Set<String> readFrontends(){
        Set<String> frontends = new HashSet<String>();
        // reads from src/main/resource
        try (InputStream is = new ClassPathResource("/frontends.txt").getInputStream()) {
            String contents = new String(FileCopyUtils.copyToByteArray(is), StandardCharsets.UTF_8);

            for (String frontend : contents.split("\n")){
                frontends.add(frontend);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return frontends;
    }
}
