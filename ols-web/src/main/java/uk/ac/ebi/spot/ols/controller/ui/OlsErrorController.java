
package uk.ac.ebi.spot.ols.controller.ui;

<<<<<<< HEAD
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

=======
>>>>>>> 6b26b5e43ada0ebc714898f7a81a1620b94f0802
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class OlsErrorController implements ErrorController  {

    @Autowired
    private CustomisationProperties customisationProperties;

    @RequestMapping("/error")
<<<<<<< HEAD
    public String handleError(HttpServletRequest request,Model model) {
    	
    	Date now = new Date();
    	TimeZone.setDefault( TimeZone.getTimeZone("GMT"));
    	model.addAttribute("timestamp",now); 
    	model.addAttribute("message",request.getAttribute(RequestDispatcher.ERROR_MESSAGE));
    	model.addAttribute("status",request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE));
=======
    public String handleError(Model model) {
>>>>>>> 6b26b5e43ada0ebc714898f7a81a1620b94f0802

        customisationProperties.setCustomisationModelAttributes(model);

        return "error";
    }

    @Override
    public String getErrorPath() {
<<<<<<< HEAD
        return "/error";
=======
        return null;
>>>>>>> 6b26b5e43ada0ebc714898f7a81a1620b94f0802
    }
}

