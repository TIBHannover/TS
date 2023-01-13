package uk.ac.ebi.spot.ols.controller;

import uk.ac.ebi.spot.ols.controller.assember.PairwiseSimilarityModelAssembler;
import uk.ac.ebi.spot.ols.controller.assember.SimilarityModelAssembler;
import uk.ac.ebi.spot.ols.controller.dto.PairwiseSimilarityModel;
import uk.ac.ebi.spot.ols.model.ontology.*;
import uk.ac.ebi.spot.ols.service.PreProcessingOntologyService;
import uk.ac.ebi.spot.ols.service.SimilarityService;
import uk.ac.ebi.spot.ols.utils.HttpUtils;
import uk.ac.ebi.spot.ols.utils.PageUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ontology/similarity")
public class SimilarityController {
    private final SimilarityService similarityService;
    private final PreProcessingOntologyService preProcessingOntologyService;
    private final SimilarityModelAssembler modelAssembler;
    private final PairwiseSimilarityModelAssembler pairwiseSimilarityModelAssembler;
    private final PagedResourcesAssembler<Similarity> pagedResourcesAssembler;
    private final PagedResourcesAssembler<PairwiseSimilarity> pairwiseSimilarityPagedResourcesAssembler;

    @Autowired
    public SimilarityController(
        SimilarityService similarityService,
        PreProcessingOntologyService preProcessingOntologyService,
        SimilarityModelAssembler modelAssembler,
        PairwiseSimilarityModelAssembler pairwiseSimilarityModelAssembler,
        PagedResourcesAssembler<Similarity> pagedResourcesAssembler,
        PagedResourcesAssembler<PairwiseSimilarity> pairwiseSimilarityPagedResourcesAssembler
    ) {
        this.similarityService = similarityService;
        this.preProcessingOntologyService = preProcessingOntologyService;
        this.modelAssembler = modelAssembler;
        this.pairwiseSimilarityModelAssembler = pairwiseSimilarityModelAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.pairwiseSimilarityPagedResourcesAssembler = pairwiseSimilarityPagedResourcesAssembler;
    }

    @ApiOperation(value = "Similarity measure between TS internal ontologies " +
        "by calculating shared Properties | Classes | Imports | Namespaces")
    @RequestMapping(method = RequestMethod.GET, value = "/{characteristics}/internal/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedResources<SimilarityModel>> getSimilarityForInternalOntologyList(
        @ApiParam(value = "Characteristics to be compared by", example = "PROPERTY")
        @PathVariable("characteristics") CharacteristicsType characteristicsType,
        @ApiParam(value = "A set of Ontology IDs managed in the TS", example = "dicl,dicob")
        @RequestParam List<String> ids,
        @ApiParam(value = "Collection to filter set of ontologies", example = "NFDI4ING")
        @RequestParam(required = false) Optional<String> collection,
        Pageable pageable
    ) {
        Page<Similarity> page = similarityService.getSimilarities(ids, characteristicsType, collection, pageable);
        PagedResources<SimilarityModel> PagedResources =
            PageUtils.toPagedResources(page, SimilarityModel.class, pagedResourcesAssembler, modelAssembler);

        return HttpUtils.ok(PagedResources);
    }

    @ApiOperation(value = "Similarity measure between given TS internal ontology " +
        "and set of TS internal ontologies")
    @RequestMapping(method = RequestMethod.GET, value = "/{characteristics}/internal", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedResources<SimilarityModel>> getSimilarityForInternalOntology(
        @ApiParam(value = "Characteristics to be compared by", example = "PROPERTY")
        @PathVariable("characteristics") CharacteristicsType characteristicsType,
        @ApiParam(value = "A given Ontology ID managed in the TS", example = "swo")
        @RequestParam String id,
        @ApiParam(value = "Collection to filter set of ontologies", example = "NFDI4ING")
        @RequestParam(required = false) Optional<String> collection,
        Pageable pageable
    ) {
        Page<Similarity> page = similarityService.getSimilarities(id, characteristicsType, collection, pageable);
        PagedResources<SimilarityModel> PagedResources =
            PageUtils.toPagedResources(page, SimilarityModel.class, pagedResourcesAssembler, modelAssembler);

        return HttpUtils.ok(PagedResources);
    }

    @ApiOperation(value = "Similarity measure for external ontology " +
        "by calculating shared Properties | Classes | Imports | Namespaces")
    @RequestMapping(method = RequestMethod.GET, value = "/{characteristics}/external", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedResources<SimilarityModel>> getSimilarityForExternalOntology(
        @ApiParam(value = "Characteristics to be compared by", example = "PROPERTY")
        @PathVariable("characteristics") CharacteristicsType characteristicsType,
        @ApiParam(value = "External ontology URL")
        @RequestParam String url,
        @ApiParam(value = "Collection to filter set of ontologies", example = "NFDI4ING")
        @RequestParam(required = false) Optional<String> collection,
        Pageable pageable
    ) {
        ProcessedOntology ontology = preProcessingOntologyService.preProcess(Optional.empty(), url,"getSimilarityForExternalOntology");
        Page<Similarity> page = similarityService.getSimilarities(ontology, characteristicsType, collection, pageable);
        PagedResources<SimilarityModel> PagedResources =
            PageUtils.toPagedResources(page, SimilarityModel.class, pagedResourcesAssembler, modelAssembler);


        return HttpUtils.ok(PagedResources);
    }

    @ApiOperation("Pairwise similarity between TS internal ontologies")
    @RequestMapping(method = RequestMethod.GET, value = "/pairwise/internal/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedResources<PairwiseSimilarityModel>> getPairwiseSimilarityForInternalOntologyList(
        @ApiParam(value = "A set of Ontology IDs managed in the TS", example = "dicl,dicob")
        @RequestParam(required = false) Optional<List<String>> ids,
        @ApiParam(value = "Collection to filter set of ontologies", example = "NFDI4ING")
        @RequestParam(required = false) Optional<String> collection,
        Pageable pageable
    ) {
        Page<PairwiseSimilarity> page = similarityService.getPairwiseSimilarity(ids, collection, pageable);

        PagedResources<PairwiseSimilarityModel> PagedResources = PageUtils.toPagedResources(
            page,
            PairwiseSimilarityModel.class,
            pairwiseSimilarityPagedResourcesAssembler,
            pairwiseSimilarityModelAssembler
        );

        return HttpUtils.ok(PagedResources);
    }

    @ApiOperation("Pairwise similarity between given TS internal ontology " +
        "and a set of TS internal ontologies")
    @RequestMapping(method = RequestMethod.GET, value = "/pairwise/internal", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedResources<PairwiseSimilarityModel>> getPairwiseSimilarityForInternalOntology(
        @ApiParam(value = "A given Ontology ID managed in the TS", example = "swo")
        @RequestParam String id,
        @ApiParam(value = "A set of Ontology IDs managed in the TS", example = "dicl,dicob")
        @RequestParam(required = false) Optional<List<String>> ids,
        @ApiParam(value = "Collection to filter set of ontologies", example = "NFDI4ING")
        @RequestParam(required = false) Optional<String> collection,
        Pageable pageable
    ) {
        Page<PairwiseSimilarity> page = similarityService.getPairwiseSimilarity(id, ids, collection, pageable);

        PagedResources<PairwiseSimilarityModel> PagedResources = PageUtils.toPagedResources(
            page,
            PairwiseSimilarityModel.class,
            pairwiseSimilarityPagedResourcesAssembler,
            pairwiseSimilarityModelAssembler
        );

        return HttpUtils.ok(PagedResources);
    }

    @ApiOperation("Pairwise similarity for external ontology")
    @RequestMapping(method = RequestMethod.GET, value = "/pairwise/external", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedResources<PairwiseSimilarityModel>> getPairwiseSimilarityForExternalOntology(
        @ApiParam(value = "External ontology URL")
        @RequestParam String url,
        @ApiParam(value = "Collection to filter set of ontologies", example = "NFDI4ING")
        @RequestParam(required = false) Optional<String> collection,
        Pageable pageable
    ) {
        ProcessedOntology ontology = preProcessingOntologyService.preProcess(Optional.empty(), url,"getPairwiseSimilarityForExternalOntology");
        Page<PairwiseSimilarity> page = similarityService.getPairwiseSimilarity(ontology, collection, pageable);
        PagedResources<PairwiseSimilarityModel> PagedResources = PageUtils.toPagedResources(
            page,
            PairwiseSimilarityModel.class,
            pairwiseSimilarityPagedResourcesAssembler,
            pairwiseSimilarityModelAssembler
        );

        return HttpUtils.ok(PagedResources);
    }

    @ApiOperation("Pairwise similarity for external ontology")
    @RequestMapping(method = RequestMethod.GET, value = "/pairwise/external/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedResources<PairwiseSimilarityModel>> getPairwiseSimilarityForExternalOntologyList(
        @ApiParam(value = "External ontology URL")
        @RequestParam String url,
        @ApiParam(value = "A set of Ontology IDs managed in the TS", example = "dicl,dicob")
        @RequestParam(required = false) Optional<List<String>> ids,
        @ApiParam(value = "Collection to filter set of ontologies", example = "NFDI4ING")
        @RequestParam(required = false) Optional<String> collection,
        Pageable pageable
    ) {
        ProcessedOntology ontology = preProcessingOntologyService.preProcess(Optional.empty(), url,"getPairwiseSimilarityForExternalOntologyList");
        Page<PairwiseSimilarity> page = similarityService.getPairwiseSimilarity(ontology, ids, collection, pageable);
        PagedResources<PairwiseSimilarityModel> PagedResources = PageUtils.toPagedResources(
            page,
            PairwiseSimilarityModel.class,
            pairwiseSimilarityPagedResourcesAssembler,
            pairwiseSimilarityModelAssembler
        );

        return HttpUtils.ok(PagedResources);
    }
}
