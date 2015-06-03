package uk.ac.ebi.spot.model;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class TermDocumentBuilder {
    private String id;
    private String uri;
    private int uri_key;
    private String label;
    private List<String> synonyms = new ArrayList<>();
    private List<String> description = new ArrayList<>();
    private List<String> shortForm = new ArrayList<>();
    private String ontologyName;
    private String ontologyUri;
    private String type;
    private boolean isDefiningOntology;
    private List<String> subsets = new ArrayList<>();
    private boolean isObsolete = false;
    private boolean hasChildren = false;
    private boolean isRoot = false;
    private List<String> equivalentUris = new ArrayList<>();
    private Map<String, List<String>> annotation = new HashMap<>();
    private List<String> logicalDescription = new ArrayList<>();
    private List<String> parents = new ArrayList<>();
    private List<String> ancestors = new ArrayList<>();
    private List<String> children = new ArrayList<>();
    private List<String> descendants = new ArrayList<>();
    private Map<String, List<String>> relatedTerms = new HashMap<>();
    private String bbopSiblingGraph = new String();

    public TermDocumentBuilder setBbopSibblingGraph(String bbopSiblingGraph) throws IOException {

        //Set string directly
        this.bbopSiblingGraph = bbopSiblingGraph;


//        //Read string from file
//        String termId = this.uri.toString().substring(this.uri.toString().lastIndexOf('/') + 1, this.uri.toString().length());
//
//        String filePath = "/Users/catherineleroy/Documents/json-graphs/" + termId + ".json";
//        System.out.println("filePath = " + filePath);
//        BufferedReader br = new BufferedReader(new FileReader(filePath));
//        StringBuilder sb = new StringBuilder();
//        String line = br.readLine();
//        while (line != null) {
//            sb.append(line);
//            sb.append("\n");
//            line = br.readLine();
//        }
//        System.out.println("this.bbopSiblingGraph = " + this.bbopSiblingGraph);
//        this.bbopSiblingGraph = sb.toString().intern();
//        br.close();


        return this;
    }


    public TermDocumentBuilder setId(String id) {
        this.id = id;
        return this;
    }


    public TermDocumentBuilder setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public TermDocumentBuilder setUri_key(int uri_key) {
        this.uri_key = uri_key;
        return this;
    }

    public TermDocumentBuilder setLabel(String label) {
        this.label = label;
        return this;
    }

    public TermDocumentBuilder setSynonyms(Collection<String> synonyms) {
        this.synonyms = new ArrayList<>(synonyms);
        return this;
    }

    public TermDocumentBuilder setDescription(Collection<String> description) {
        this.description = new ArrayList<>(description);
        return this;
    }

    public TermDocumentBuilder setShortForm(Collection<String> shortForm) {
        this.shortForm = new ArrayList<>(shortForm);
        return this;
    }

    public TermDocumentBuilder setOntologyName(String ontologyName) {
        this.ontologyName = ontologyName;
        return this;
    }

    public TermDocumentBuilder setOntologyUri(String ontologyUri) {
        this.ontologyUri = ontologyUri;
        return this;
    }

    public TermDocumentBuilder setType(String type) {
        this.type = type;
        return this;
    }

    public TermDocumentBuilder setIsDefiningOntology(boolean isDefiningOntology) {
        this.isDefiningOntology = isDefiningOntology;
        return this;
    }

    public TermDocumentBuilder setSubsets(List<String> subsets) {
        this.subsets = subsets;
        return this;
    }

    public TermDocumentBuilder setIsObsolete(boolean isObsolete) {
        this.isObsolete = isObsolete;
        return this;
    }

    public TermDocumentBuilder setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
        return this;
    }

    public TermDocumentBuilder setIsRoot(boolean isRoot) {
        this.isRoot = isRoot;
        return this;
    }

    public TermDocumentBuilder setEquivalentUris(Collection<String> equivalentUris) {
        this.equivalentUris = new ArrayList<>(equivalentUris);
        return this;
    }


    public TermDocumentBuilder setLogicalDescription(Collection<String> logicalDescription) {
        this.logicalDescription = new ArrayList<>(logicalDescription);
        return this;
    }

    public TermDocumentBuilder setParentUris(Collection<String> parentUris) {
        this.parents = new ArrayList<>(parentUris);
        return this;
    }

    public TermDocumentBuilder setAncestorUris(Collection<String> ancestorUris) {
        this.ancestors = new ArrayList<>(ancestorUris);
        return this;
    }

    public TermDocumentBuilder setChildUris(Collection<String> childUris) {
        this.children = new ArrayList<>(childUris);
        return this;
    }

    public TermDocumentBuilder setDescendantUris(Collection<String> descendantUris) {
        this.descendants = new ArrayList<>(descendantUris);
        return this;
    }

    public TermDocumentBuilder setAnnotation(Map<String, Collection<String>> annotations) {
        for (String key : annotations.keySet()) {
            this.annotation.put(key, new ArrayList<>(annotations.get(key)));
        }
        return this;
    }

    public TermDocumentBuilder setRelatedTerms(Map<String, Collection<String>> relatedTerms) {
        for (String key : relatedTerms.keySet()) {
            this.relatedTerms.put(key, new ArrayList<>(relatedTerms.get(key)));
        }
        return this;
    }

    public TermDocument createTermDocument() {
        return new TermDocument(
                id,
                uri,
                uri_key,
                label,
                synonyms,
                description,
                shortForm,
                ontologyName,
                ontologyUri,
                type,
                isDefiningOntology,
                subsets,
                isObsolete,
                hasChildren,
                isRoot,
                equivalentUris,
                logicalDescription,
                annotation,
                parents,
                ancestors,
                children,
                descendants,
                relatedTerms,
                bbopSiblingGraph);
    }
}