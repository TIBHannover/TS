package uk.ac.ebi.spot.ols.controller.api;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.ols.controller.dto.RestCallDto;

@Component
public class RestCallAssembler implements ResourceAssembler<RestCallDto, Resource<RestCallDto>> {

    @Override
    public Resource<RestCallDto> toResource(RestCallDto document) {
        Resource<RestCallDto> resource = new Resource<>(document);

        return resource;
    }
}