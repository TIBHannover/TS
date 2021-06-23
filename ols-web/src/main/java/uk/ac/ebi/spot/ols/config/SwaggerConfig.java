package uk.ac.ebi.spot.ols.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.builders.RequestHandlerSelectors;


@Configuration
@EnableSwagger2
public class SwaggerConfig {

	@Bean
	public Docket postsApi() {
		return new Docket(DocumentationType.SWAGGER_2).select()
		         .apis(RequestHandlerSelectors.basePackage("uk.ac.ebi.spot.ols.controller.api")).build().apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("TIB Terminology Service Documentation")
				.description("TIB Terminology Service API Reference for Developers")
				.termsOfServiceUrl("https://www.tib.eu/en/service/terms-of-use")
				.contact("TIB Terminology Service Development Team").license("imprint")
				.licenseUrl("imprint").version("1.0").build();
	}

}