package uk.ac.ebi.spot.ols.controller.api;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.ols.controller.dto.KeyValueResultDto;

@Component
public class KeyValueResultAssembler implements ResourceAssembler<KeyValueResultDto, Resource<KeyValueResultDto>> {

    @Override
    public Resource<KeyValueResultDto> toResource(KeyValueResultDto document) {
        Resource<KeyValueResultDto> resource = new Resource<>(document);

        return resource;
    }
}