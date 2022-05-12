package uk.ac.ebi.spot.ols.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.HandlerMapping;
import uk.ac.ebi.spot.ols.entities.HttpServletRequestInfo;
import uk.ac.ebi.spot.ols.service.RestCallParserService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RestCallParserServiceImplTest {
    @Mock
    private HttpServletRequest request;
    private RestCallParserService restCallParserService;

    @Before
    public void setUp() {
        restCallParserService = new RestCallParserServiceImpl();
    }

    @Test
    public void givenHttpServletRequest_ontologiesMsTermsRoots_shouldReturnValidRequestURI() {
        String requestURI = "/ts4tib/api/ontologies/ms/terms/roots";

        when(request.getRequestURI())
            .thenReturn(requestURI);

        Map<String, String> pathVariablesMap = new HashMap<>();
        pathVariablesMap.put("onto", "ms");

        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
            .thenReturn(pathVariablesMap);

        HttpServletRequestInfo actual = restCallParserService.parse(request);

        assertEquals("/ts4tib/api/ontologies/{onto}/terms/roots", actual.getUrl());
    }

    @Test
    public void givenHttpServletRequest_ontologiesAeon_shouldReturnValidRequestURI() {
        String requestURI = "/ts4tib/api/ontologies/aeon";

        when(request.getRequestURI())
            .thenReturn(requestURI);

        Map<String, String> pathVariablesMap = new HashMap<>();
        pathVariablesMap.put("onto", "aeon");

        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
            .thenReturn(pathVariablesMap);

        HttpServletRequestInfo actual = restCallParserService.parse(request);

        assertEquals("/ts4tib/api/ontologies/{onto}", actual.getUrl());
    }

    @Test
    public void givenHttpServletRequest_ontologiesOrdoTermsIdJstree_shouldReturnValidRequestURI() {
        String requestURI = "/ts4tib/api/ontologies/ordo/terms/http%253A%252F%252Fwww.orpha.net%252FORDO%252FOrphanet_122043/jstree";

        when(request.getRequestURI())
            .thenReturn(requestURI);

        Map<String, String> pathVariablesMap = new HashMap<>();
        pathVariablesMap.put("onto", "ordo");
        pathVariablesMap.put("id", "http%3A%2F%2Fwww.orpha.net%2FORDO%2FOrphanet_122043");

        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
            .thenReturn(pathVariablesMap);

        HttpServletRequestInfo actual = restCallParserService.parse(request);

        assertEquals("/ts4tib/api/ontologies/{onto}/terms/{id}/jstree", actual.getUrl());
    }

    @Test
    public void givenHttpServletRequest_ontologiesRoPropertiesJstree_shouldReturnValidRequestURI() {
        String requestURI = "/ts4tib/api/ontologies/ro/properties/http%253A%252F%252Fpurl.obolibrary.org%252Fobo%252FRO_0000058/jstree";

        when(request.getRequestURI())
            .thenReturn(requestURI);

        Map<String, String> pathVariablesMap = new HashMap<>();
        pathVariablesMap.put("onto", "ro");
        pathVariablesMap.put("id", "http%3A%2F%2Fpurl.obolibrary.org%2Fobo%2FRO_0000058");

        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
            .thenReturn(pathVariablesMap);

        HttpServletRequestInfo actual = restCallParserService.parse(request);

        assertEquals("/ts4tib/api/ontologies/{onto}/properties/{id}/jstree", actual.getUrl());
    }

    @Test
    public void givenHttpServletRequest_ontologiesOeoTermsGraph_shouldReturnValidRequestURI() {
        String requestURI = "/ts4tib/api/ontologies/oeo/terms/http%253A%252F%252Fopenenergy-platform.org%252Fontology%252Foeo%252FOEO_00140068/graph";

        when(request.getRequestURI())
            .thenReturn(requestURI);

        Map<String, String> pathVariablesMap = new HashMap<>();
        pathVariablesMap.put("onto", "oeo");
        pathVariablesMap.put("id", "http%3A%2F%2Fopenenergy-platform.org%2Fontology%2Foeo%2FOEO_00140068");

        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
            .thenReturn(pathVariablesMap);

        HttpServletRequestInfo actual = restCallParserService.parse(request);

        assertEquals("/ts4tib/api/ontologies/{onto}/terms/{id}/graph", actual.getUrl());
    }

    @Test
    public void givenHttpServletRequest_ontologiesUberonTermsIdJstreeChildrenNodeid_shouldReturnValidRequestURI() {
        String requestURI = "/ts4tib/api/ontologies/uberon/terms/http%253A%252F%252Fpurl.obolibrary.org%252Fobo%252FUBERON_0000104/jstree/children/2";

        when(request.getRequestURI())
            .thenReturn(requestURI);

        Map<String, String> pathVariablesMap = new LinkedHashMap<>();
        pathVariablesMap.put("nodeid", "2");
        pathVariablesMap.put("onto", "uberon");
        pathVariablesMap.put("id", "http%3A%2F%2Fpurl.obolibrary.org%2Fobo%2FUBERON_0000104");

        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
            .thenReturn(pathVariablesMap);

        HttpServletRequestInfo actual = restCallParserService.parse(request);

        assertEquals("/ts4tib/api/ontologies/{onto}/terms/{id}/jstree/children/{nodeid}", actual.getUrl());
    }
}
