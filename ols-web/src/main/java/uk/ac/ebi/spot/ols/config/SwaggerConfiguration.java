package uk.ac.ebi.spot.ols.config;

import io.swagger.annotations.ApiParam;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfiguration {
    @Bean
    public Docket userApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .groupName("Terminology Service Statistics API")
            .select()
            .paths(PathSelectors.any())
            .apis(RequestHandlerSelectors.basePackage("eu.tib.ts.controller"))
            .build()
            .directModelSubstitute(Pageable.class, SwaggerPageable.class);
    }

    @Getter
    private static class SwaggerPageable {

        @ApiParam(value = "Number of records per page", example = "0")
        private Integer size;

        @ApiParam(value = "Results page you want to retrieve (0..N)", example = "0")
        private Integer page;

        @ApiParam(value = "Sorting criteria in the format: property(,asc|desc). " +
            "Default sort order is ascending. Multiple sort criteria are supported." +
            "Example: sort=name,asc&sort=age,desc")
        private String sort;

    }
}


