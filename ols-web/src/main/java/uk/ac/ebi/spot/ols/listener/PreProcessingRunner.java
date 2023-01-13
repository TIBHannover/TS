package uk.ac.ebi.spot.ols.listener;

import uk.ac.ebi.spot.ols.service.PreProcessingService;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "preprocessing", name = "run")
public class PreProcessingRunner implements ApplicationListener<ApplicationReadyEvent> {
    private final PreProcessingService preProcessingService;

    @Autowired
    public PreProcessingRunner(PreProcessingService preProcessingService) {
        Log.info("starting PreProcessingRunner");
        this.preProcessingService = preProcessingService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Log.info("Event Executed");
        preProcessingService.doPreProcessing();
    }
}
