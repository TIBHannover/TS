package uk.ac.ebi.spot.ols.controller;

import uk.ac.ebi.spot.ols.controller.dto.RatioDto;
import uk.ac.ebi.spot.ols.model.ontology.CharacteristicsType;
import uk.ac.ebi.spot.ols.model.ontology.SimpleOntology;
import uk.ac.ebi.spot.ols.service.RatioService;
import uk.ac.ebi.spot.ols.utils.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ontology/ratio")
public class RatioController {
    private final RatioService ratioService;

    @Autowired
    public RatioController(RatioService ratioService) {
        this.ratioService = ratioService;
    }

    @PostMapping(value = "/{characteristics}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RatioDto> getRatio(
        @PathVariable("characteristics") CharacteristicsType characteristicsType,
        @RequestBody List<SimpleOntology> ontologies
    ) {
        RatioDto ratioDto = ratioService.getRatio(ontologies, characteristicsType);

        return HttpUtils.ok(ratioDto);
    }
}
