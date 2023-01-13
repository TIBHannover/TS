package uk.ac.ebi.spot.ols.service.impl;

import uk.ac.ebi.spot.ols.service.OntologyTraverseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.semanticweb.owlapi.model.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OntologyTraverseServiceImpl implements OntologyTraverseService {

    @Override
    public Set<String> getImports(OWLOntology owlOntology) {
        log.debug("Getting imports");
        if (owlOntology == null) {
            return Collections.singleton("");
        }

        return owlOntology.importsDeclarations()
            .map(OWLImportsDeclaration::getIRI)
            .map(IRI::toString)
            .map(String::toLowerCase)
            .collect(Collectors.toSet());
    }

    @Override
    public Set<String> getNamespaces(OWLOntology owlOntology) {
        log.debug("Getting namespaces");
        if (owlOntology == null) {
            return Collections.singleton("");
        }

        OWLDocumentFormat format = owlOntology.getOWLOntologyManager().getOntologyFormat(owlOntology);
        Set<String> set = new HashSet<>();
        if (format != null && format.isPrefixOWLDocumentFormat()) {
            set = format.asPrefixOWLDocumentFormat()
                .getPrefixName2PrefixMap()
                .values().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        }

        return set;
    }

    @Override
    public Set<String> getProperties(OntModel model) {
        log.debug("Getting properties");
        if (model == null) {
            return Collections.singleton("");
        }

        return model.listOntProperties().toList().stream()
            .filter(OntProperty::isObjectProperty)
            .map(OntProperty::getURI)
            .map(String::toLowerCase)
            .collect(Collectors.toSet());
    }

    @Override
    public Set<String> getIndividuals(OWLOntology owlOntology) {
        log.debug("Getting individuals");
        if (owlOntology == null) {
            return Collections.singleton("");
        }

        Set<OWLNamedIndividual> set = new HashSet<>();
        owlOntology.individualsInSignature().forEach(set::add);

        return set.stream()
            .map(OWLNamedIndividual::getIRI)
            .map(IRI::toString)
            .map(String::toLowerCase)
            .collect(Collectors.toSet());
    }

    @Override
    public Set<String> getClasses(OWLOntology owlOntology) {
        log.debug("Getting classes");
        if (owlOntology == null) {
            return Collections.singleton("");
        }

        Set<OWLClass> set = new HashSet<>();
        owlOntology.classesInSignature().forEach(set::add);

        return set.stream()
            .map(OWLClass::toString)
            .map(String::toLowerCase)
            .collect(Collectors.toSet());
    }

    @Override
    public Set<String> getClasses(OntModel model) {
        log.debug("Getting classes");
        if (model == null) {
            return Collections.singleton("");
        }

        Set<String> classes = new HashSet<>();
        // create an iterator over the root classes
        Iterator<OntClass> iterator = model.listHierarchyRootClasses();

        // traverse through all roots
        while (iterator.hasNext()) {
            OntClass tmp = iterator.next();
            traverse(tmp, new ArrayList<>(), classes);
        }

        return classes;
    }

    private void traverse(OntClass oc, List<OntClass> occurs, Set<String> classes) {
        if (oc == null) {
            return;
        }
        // if end reached abort (Thing == root, Nothing == deadlock)
        if (oc.getLocalName() == null || oc.getLocalName().equals("Nothing")) {
            return;
        }

        classes.add(oc.toString().toLowerCase());

        // check if we already visited this OntClass (avoid loops in graphs)
        if (oc.canAs(OntClass.class) && !occurs.contains(oc)) {
            // for every subClass, traverse down
            for (Iterator<OntClass> i = oc.listSubClasses(true); i.hasNext(); ) {
                OntClass subClass = i.next();
                // push this expression on the occurs list before we recurse to avoid loops
                occurs.add(oc);
                // traverse down and increase depth (used for logging tabs)
                traverse(subClass, occurs, classes);
                // after traversing the path, remove from occurs list
                occurs.remove(oc);
            }
        }
    }
}
