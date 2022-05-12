package uk.ac.ebi.spot.ols.service.impl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UrlCyclicDecoderTest {
    private final UrlCyclicDecoder decoder = new UrlCyclicDecoder();

    @Test
    public void givenSimpleEncodedUrl_shouldReturnDecodedUrl() {
        String url = "http%3A%2F%2Fwww.orpha.net%2FORDO%2FOrphanet_122043";

        assertEquals(
            "http://www.orpha.net/ORDO/Orphanet_122043",
            decoder.decode(url)
        );
    }

    @Test
    public void givenDoubleEncodedUrl_shouldReturnDecodedUrl() {
        String url = "/ts4tib/api/ontologies/{onto}/terms/http%253A%252F%252Fwww.orpha.net%252FORDO%252FOrphanet_122043/jstree";

        assertEquals(
            "/ts4tib/api/ontologies/{onto}/terms/http://www.orpha.net/ORDO/Orphanet_122043/jstree",
            decoder.decode(url)
        );
    }

    @Test
    public void givenSimpleString_shouldReturnSameString() {
        String str = "simple_string";

        assertEquals(str, decoder.decode(str));
    }
}
