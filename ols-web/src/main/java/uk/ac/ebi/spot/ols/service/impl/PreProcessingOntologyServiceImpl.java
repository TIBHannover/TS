package uk.ac.ebi.spot.ols.service.impl;

import uk.ac.ebi.spot.ols.model.ontology.ProcessedOntology;
import uk.ac.ebi.spot.ols.model.ontology.TsOntology;
import uk.ac.ebi.spot.ols.service.OntologyReadService;
import uk.ac.ebi.spot.ols.service.OntologyTraverseService;
import uk.ac.ebi.spot.ols.service.PreProcessingOntologyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.jena.ontology.OntModel;
import org.semanticweb.owlapi.model.OWLOntology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class PreProcessingOntologyServiceImpl implements PreProcessingOntologyService {
    public static final String EXTERNAL = "external";
    private final OntologyReadService ontologyReadService;
    private final OntologyTraverseService ontologyTraverseService;

    long idd = 0;


    @Autowired
    public PreProcessingOntologyServiceImpl(OntologyReadService ontologyReadService,
                                            OntologyTraverseService ontologyTraverseService) {
        this.ontologyReadService = ontologyReadService;
        this.ontologyTraverseService = ontologyTraverseService;

    }

    @Override
    public ProcessedOntology preProcess(Optional<TsOntology> tsOntology, String fileLocation, String title) {
        log.debug("Start pre-processing {} {}", tsOntology.map(TsOntology::getOntologyId).orElse(EXTERNAL), fileLocation);

        OntModel ontModel = null;
        OWLOntology owlOntology = null;
        try {
            ontModel = ontologyReadService.readOntologyWithJenaApi(fileLocation);
        } catch (Exception e) {
            log.error("Could not read with Jena API {} {}", fileLocation, e.getLocalizedMessage());
        }

        try {
            owlOntology = ontologyReadService.readOntologyWithOwlApi(fileLocation);
        } catch (Exception e) {
            log.error("Could not read with OWL API {} {}", fileLocation, e.getLocalizedMessage());
        }
        return buildOntology(tsOntology, owlOntology, ontModel, fileLocation, title);
    }

    private ProcessedOntology buildOntology(Optional<TsOntology> tsOntology,
                                            OWLOntology owlOntology,
                                            OntModel ontModel,
                                            String uri,
                                            String titled) {
        Set<String> classes = ontologyTraverseService.getClasses(ontModel);
        if (CollectionUtils.isEmpty(classes)) {
            classes = ontologyTraverseService.getClasses(owlOntology);
        }

        return ProcessedOntology.builder()
                .ontologyId(tsOntology.map(TsOntology::getOntologyId).orElse(EXTERNAL))
                .classes(classes)
                .imports(CollectionUtils.isEmpty(ontologyTraverseService.getImports(owlOntology)) ? Collections.emptySet():ontologyTraverseService.getImports(owlOntology))
                .properties(CollectionUtils.isEmpty(ontologyTraverseService.getProperties(ontModel)) ? Collections.emptySet():ontologyTraverseService.getProperties(ontModel))
                .namespaces(CollectionUtils.isEmpty(ontologyTraverseService.getNamespaces(owlOntology)) ? Collections.emptySet():ontologyTraverseService.getNamespaces(owlOntology))
                .individuals(CollectionUtils.isEmpty(ontologyTraverseService.getIndividuals(owlOntology)) ? Collections.emptySet():ontologyTraverseService.getIndividuals(owlOntology))
                .title(titled)
                .collection(tsOntology.map(TsOntology::getCollection).orElse(Collections.emptySet()))
                .uri(uri)
                .build();
    }
}
