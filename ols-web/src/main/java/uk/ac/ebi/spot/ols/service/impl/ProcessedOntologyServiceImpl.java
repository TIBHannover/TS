package uk.ac.ebi.spot.ols.service.impl;

import com.github.jsonldjava.shaded.com.google.common.collect.Lists;
import uk.ac.ebi.spot.ols.controller.dto.OntologyDto;
import uk.ac.ebi.spot.ols.model.ontology.ProcessedOntology;
import uk.ac.ebi.spot.ols.repositories.ProcessedMongoOntologyRepository;
import uk.ac.ebi.spot.ols.service.ProcessedOntologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProcessedOntologyServiceImpl implements ProcessedOntologyService {
    private final ProcessedMongoOntologyRepository repository;

    @Autowired
    public ProcessedOntologyServiceImpl(ProcessedMongoOntologyRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ProcessedOntology> findAll() {
        return Lists.newArrayList(repository.findAll());
    }

    @Override
    public List<OntologyDto> getOntologies() {
        return Lists.newArrayList(repository.findAll()).stream()
            .map(OntologyDto::of)
            .sorted(Comparator.comparing(OntologyDto::getOntologyId))
            .collect(Collectors.toList());
    }

    @Override
    public List<String> getOntologyIds() {
        return Lists.newArrayList(repository.findAll()).stream()
            .map(ProcessedOntology::getOntologyId)
            .sorted()
            .collect(Collectors.toList());
    }

    @Override
    public ProcessedOntology save(ProcessedOntology processedOntology) {
        return repository.save(processedOntology);
    }
}
