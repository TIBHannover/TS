package uk.ac.ebi.spot.ols.controller.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

@RestController
public class HelloController {
	
	@ApiOperation(value = "Checks if API is accesible.")
	@RequestMapping(method = RequestMethod.GET, value = "/api/accessibility")
	public String sayHello() {
		return "API is Accessible!";
	}
}
