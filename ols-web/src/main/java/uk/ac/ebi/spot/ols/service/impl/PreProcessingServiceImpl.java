package uk.ac.ebi.spot.ols.service.impl;

import uk.ac.ebi.spot.ols.model.ontology.ProcessedOntology;
import uk.ac.ebi.spot.ols.model.ontology.TsOntology;
import uk.ac.ebi.spot.ols.repositories.TsRepository;
import uk.ac.ebi.spot.ols.service.PreProcessingOntologyService;
import uk.ac.ebi.spot.ols.service.PreProcessingService;
import uk.ac.ebi.spot.ols.service.ProcessedOntologyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PreProcessingServiceImpl implements PreProcessingService {
    private final TsRepository tsRepository;
    private final ProcessedOntologyService processedOntologyService;
    private final PreProcessingOntologyService preProcessingOntologyService;

    private final List<String> skipList;

    @Autowired
    public PreProcessingServiceImpl(TsRepository tsRepository,
                                    ProcessedOntologyService processedOntologyService,
                                    PreProcessingOntologyService preProcessingOntologyService,
                                    @Value("#{'${skip.ontologies.processing}'.split(',')}")
                                        List<String> skipList) {
        this.tsRepository = tsRepository;
        this.preProcessingOntologyService = preProcessingOntologyService;
         this.processedOntologyService = processedOntologyService;
        this.skipList = skipList;
        System.out.println("PreProcessingServiceImpl constructor : ");

    }

    @Override
    public void doPreProcessing() {
        List<TsOntology> tsOntologies = tsRepository.getOntologies();
        System.out.println("Titled doPreProcessing:");

         List<ProcessedOntology> processedOntologies = processedOntologyService.findAll();

        System.out.println("Titled : third line "+tsOntologies);

         List<TsOntology> unprocessedOntologies = tsOntologies.stream()
             .filter(tsOntology -> !ontologyExists(tsOntology, processedOntologies))
             .filter(tsOntology -> !skipList.contains(tsOntology.getOntologyId().toLowerCase()))
             .collect(Collectors.toList());

        int count = 0;
        log.info("Pre-processing starts");
        long startTime = System.currentTimeMillis();

        System.out.println("Titled : " + tsOntologies.get(0).getTitle());


       for (TsOntology tsOntology : unprocessedOntologies) {
            String fileLocation = tsOntology.getConfig().getFileLocation();
            String title = tsOntology.getConfig().getTitle();
            if(title.equals("")||title.equals("null")){
                title = "no title found";
            }
            long startRead = System.currentTimeMillis();

            log.info("Titled : " + title);
            System.out.println("Titled : inner loop " + title);


            ProcessedOntology processedOntology =
                preProcessingOntologyService.preProcess(Optional.of(tsOntology), fileLocation, title);

            long endRead = System.currentTimeMillis();
            log.debug("{} {} {} ms", tsOntology.getOntologyId(), fileLocation, endRead - startRead);

             processedOntologyService.save(processedOntology);
            count++;
        }
        System.out.println("Titled : after loop");
        log.info("Pre-processing done in {} ms", System.currentTimeMillis() - startTime);
        log.info("Saved {} ontologies", count);
    }

    private boolean ontologyExists(TsOntology tsOntology, List<ProcessedOntology> processedOntologies) {
        return processedOntologies.stream()
            .anyMatch(ont -> ont.equalsTsOntology(tsOntology));
    }
}
