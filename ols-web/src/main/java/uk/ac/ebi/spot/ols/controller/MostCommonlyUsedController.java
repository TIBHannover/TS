package uk.ac.ebi.spot.ols.controller;

import uk.ac.ebi.spot.ols.controller.assember.KeyValueModelAssembler;
import uk.ac.ebi.spot.ols.controller.dto.KeyValueResultDto;
import uk.ac.ebi.spot.ols.model.ontology.CharacteristicsType;
import uk.ac.ebi.spot.ols.model.ontology.KeyValueModel;
import uk.ac.ebi.spot.ols.service.MostCommonlyUsedService;
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
@RequestMapping("/api/ontology/mostCommonlyUsed")
public class MostCommonlyUsedController {
    private final MostCommonlyUsedService mostCommonlyUsedService;
    private final PagedResourcesAssembler<KeyValueResultDto> pagedResourcesAssembler;
    private final KeyValueModelAssembler modelAssembler;

    @Autowired
    public MostCommonlyUsedController(MostCommonlyUsedService mostCommonlyUsedService,
                                      PagedResourcesAssembler<KeyValueResultDto> pagedResourcesAssembler,
                                      KeyValueModelAssembler modelAssembler) {
        this.mostCommonlyUsedService = mostCommonlyUsedService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.modelAssembler = modelAssembler;
    }

    @ApiOperation(value = "Most commonly used Properties | Classes | Imports | Namespaces")
    @RequestMapping(method = RequestMethod.GET, value = "/{characteristics}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedResources<KeyValueModel>> getMostCommonlyUsed(
        @ApiParam(value = "Characteristics to be compared by", example = "PROPERTY")
        @PathVariable("characteristics") CharacteristicsType characteristicsType,
        @ApiParam(value = "A set of Ontology IDs managed in the TS", example = "dicl,dicob")
        @RequestParam(required = false) Optional<List<String>> ids,
        @ApiParam(value = "Collection to filter set of ontologies", example = "NFDI4ING")
        @RequestParam(required = false) Optional<String> collection,
        Pageable pageable
    ) {
        Page<KeyValueResultDto> page =
            mostCommonlyUsedService.getMostCommonlyUsedCharacteristics(ids, characteristicsType, collection, pageable);

        PagedResources<KeyValueModel> PagedResources =
            PageUtils.toPagedResources(page, KeyValueModel.class, pagedResourcesAssembler, modelAssembler);

        return HttpUtils.ok(PagedResources);
    }
}
