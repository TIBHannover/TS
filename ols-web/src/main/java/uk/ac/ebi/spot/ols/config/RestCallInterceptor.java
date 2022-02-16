package uk.ac.ebi.spot.ols.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import uk.ac.ebi.spot.ols.service.RestCallHandlerService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RestCallInterceptor extends HandlerInterceptorAdapter {
    private final RestCallHandlerService restCallHandlerService;

    @Autowired
    public RestCallInterceptor(RestCallHandlerService restCallHandlerService) {
        this.restCallHandlerService = restCallHandlerService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!request.getRequestURL().toString().contains("/api")
            || request.getRequestURL().toString().contains("/api/rest/statistics")) {
            return true;
        }


        restCallHandlerService.handle(request);

        return true;
    }
}
