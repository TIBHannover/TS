package uk.ac.ebi.spot.ols.neo4j.service;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ClassJsTreeBuilder extends AbstractJsTreeBuilder {

    private static Logger logger = LoggerFactory.getLogger(ClassJsTreeBuilder.class);

    @Override
<<<<<<< HEAD
    String getJsTreeParentQuery() {
=======
    String getJsTreeParentQuery(String lang) {
>>>>>>> 6b26b5e43ada0ebc714898f7a81a1620b94f0802

        StringBuilder query = new StringBuilder();

        query.append("MATCH path = (n:Class)-[r:SUBCLASSOF|RelatedTree*]->(parent)\n");
        query.append("USING INDEX n:Class(iri)\n");
        query.append("WHERE n.ontology_name = {0} AND n.iri = {1}\n");
        query.append("UNWIND rels(path) as r1\n");
        query.append("RETURN distinct id(startNode(r1)) as startId, startNode(r1).iri as startIri,");
<<<<<<< HEAD
        query.append("startNode(r1).label as startLabel, startNode(r1).has_children as hasChildren, ");
=======
        query.append("startNode(r1).label as startLabel, startNode(r1).`localizedLabels-" + lang + "`[0] as startLabelLocalized, startNode(r1).has_children as hasChildren, ");
>>>>>>> 6b26b5e43ada0ebc714898f7a81a1620b94f0802
        query.append("r1.label as relation, collect( distinct id(endNode(r1)) ) as parents");

        return query.toString();
    }

    private static String getNodeQueryString(String alias, ViewMode viewMode) {
        switch (viewMode) {
            case PREFERRED_ROOTS: {
                StringBuilder query = new StringBuilder(alias);
                query.append(":");
                query.append(viewMode.getNeo4jLabel());
                return query.toString();
            }
            default: {
                logger.warn("Unexpected view mode detected. Defaulting to preferred roots.");
                StringBuilder query = new StringBuilder(alias);
                query.append(":");
                query.append(ViewMode.PREFERRED_ROOTS.getNeo4jLabel());
                return query.toString();
            }
        }
    }


//    @Override
//    String getJsTreeParentQuery(ViewMode viewMode) {
//
//        switch (viewMode) {
//            case ALL:
//                return getJsTreeParentQuery();
//            default: {
//                StringBuilder query = new StringBuilder();
//
//                query.append("MATCH path = (n:Class)-[r:SUBCLASSOF|RelatedTree*]->(");
//                query.append(getNodeQueryString("pr", viewMode));
//                query.append(")-[:SUBCLASSOF|RelatedTree]->(root)\n");
//                query.append("USING INDEX n:Class(iri)\n");
//                query.append("WHERE n.ontology_name = {0} AND n.iri = {1}\n");
//                query.append("UNWIND rels(path) as r1\n");
//                query.append("RETURN distinct id(startNode(r1)) as startId, startNode(r1).iri as startIri,\n");
//                query.append("startNode(r1).label as startLabel, startNode(r1).has_children as hasChildren,\n");
//                query.append("r1.label as relation, collect( distinct id(endNode(r1)) ) as parents\n");
//                return query.toString();
//            }
//        }
//    }

    @Override
<<<<<<< HEAD
    String getJsTreeParentQuery(ViewMode viewMode) {

        switch (viewMode) {
            case ALL:
                return getJsTreeParentQuery();
=======
    String getJsTreeParentQuery(String lang, ViewMode viewMode) {

        switch (viewMode) {
            case ALL:
                return getJsTreeParentQuery(lang);
>>>>>>> 6b26b5e43ada0ebc714898f7a81a1620b94f0802
            default: {
                StringBuilder query = new StringBuilder();

                query.append("MATCH path = (n:Class)-[r:SUBCLASSOF|RelatedTree*]->(");
                query.append(getNodeQueryString("pr", viewMode));
                query.append(")-[:SUBCLASSOF|RelatedTree]->(root)\n");
                query.append("USING INDEX n:Class(iri)\n");
                query.append("WHERE n.ontology_name = {0} AND n.iri = {1}\n");
                query.append("UNWIND rels(path) as r1\n");
                query.append("WITH r1, startNode(r1) as startNode\n");
<<<<<<< HEAD
                query.append("RETURN distinct id(startNode) as startId, startNode(r1).iri as startIri,\n");
=======
                query.append("RETURN distinct id(startNode) as startId, startNode(r1).`localizedLabels-" + lang + "`[0] as startLabelLocalized,  startNode(r1).iri as startIri,\n");
>>>>>>> 6b26b5e43ada0ebc714898f7a81a1620b94f0802
                query.append("startNode(r1).label as startLabel, startNode(r1).has_children as hasChildren,\n");
                query.append("r1.label as relation, CASE WHEN \n");
                query.append(getNodeQueryString("startNode", viewMode));
                query.append(" THEN []  ELSE collect( distinct id(endNode(r1)) )END as parents\n");
                return query.toString();
            }
        }
    }

    @Override
<<<<<<< HEAD
    String getJsTreeParentSiblingQuery() {
=======
    String getJsTreeParentSiblingQuery(String lang) {
>>>>>>> 6b26b5e43ada0ebc714898f7a81a1620b94f0802
        StringBuilder query = new StringBuilder();

        query.append("MATCH path = (n:Class)-[r:SUBCLASSOF|RelatedTree*]");
        query.append("->(parent)<-[r2:SUBCLASSOF|RelatedTree]-(n1:Class)\n");
        query.append("USING INDEX n:Class(iri)\n");
        query.append("WHERE n.ontology_name = {0} AND n.iri = {1}\n");
        query.append("UNWIND rels(path) as r1\n");
        query.append("WITH r1\n");
        query.append("WHERE startNode(r1).is_obsolete=false\n");
<<<<<<< HEAD
        query.append("RETURN distinct id(startNode(r1)) as startId, startNode(r1).iri as startIri, ");
=======
        query.append("RETURN distinct id(startNode(r1)) as startId, startNode(r1).`localizedLabels-" + lang + "`[0] as startLabelLocalized,  startNode(r1).iri as startIri, ");
>>>>>>> 6b26b5e43ada0ebc714898f7a81a1620b94f0802
        query.append("startNode(r1).label as startLabel, startNode(r1).has_children as hasChildren, ");
        query.append("r1.label as relation, collect( distinct id(endNode(r1)) ) as parents");

        return query.toString();
    }


    @Override
<<<<<<< HEAD
    String getJsTreeParentSiblingQuery(ViewMode viewMode) {
        switch (viewMode) {
            case ALL:
                return getJsTreeParentSiblingQuery();
=======
    String getJsTreeParentSiblingQuery(String lang, ViewMode viewMode) {
        switch (viewMode) {
            case ALL:
                return getJsTreeParentSiblingQuery(lang);
>>>>>>> 6b26b5e43ada0ebc714898f7a81a1620b94f0802
            default: {
                StringBuilder query = new StringBuilder();

                query.append("MATCH path = (n:Class)-[r:SUBCLASSOF|RelatedTree*]->(");
                query.append(getNodeQueryString("pr", viewMode));
                query.append(")\n");
                query.append("USING INDEX n:Class(iri)\n");
                query.append("WHERE n.ontology_name = {0} AND n.iri = {1}\n");
                query.append("WITH nodes(path) as all_classes\n");
                query.append("UNWIND all_classes as class\n");
                query.append("MATCH path2 = (class)-[:SUBCLASSOF|RelatedTree]->(");
                query.append(getNodeQueryString("parent", viewMode));
                query.append(")<-[:SUBCLASSOF|RelatedTree]-(sibling)");
                query.append("UNWIND rels(path2) as r1\n");
                query.append("RETURN distinct id(startNode(r1)) as startId, startNode(r1).iri as startIri, ");
<<<<<<< HEAD
                query.append("startNode(r1).label as startLabel, startNode(r1).has_children as hasChildren, ");
=======
                query.append("startNode(r1).label as startLabel,  startNode(r1).`localizedLabels-" + lang + "`[0] as startLabelLocalized, startNode(r1).has_children as hasChildren, ");
>>>>>>> 6b26b5e43ada0ebc714898f7a81a1620b94f0802
                query.append("r1.label as relation, collect( distinct id(endNode(r1)) ) as parents \n");
                query.append("UNION\n");
                query.append("MATCH (");
                query.append(getNodeQueryString("n1", viewMode));
                query.append(")");
                query.append("WHERE n1.ontology_name = {0}");
<<<<<<< HEAD
                query.append("RETURN id(n1) as startId, n1.iri as startIri,n1.label as startLabel, ");
=======
                query.append("RETURN id(n1) as startId, n1.iri as startIri,n1.label as startLabel, n1.`localizedLabels-" + lang + "`[0] as startLabelLocalized,  ");
>>>>>>> 6b26b5e43ada0ebc714898f7a81a1620b94f0802
                query.append("n1.has_children as hasChildren, 'is-a' as relation, [] as parents");

                return query.toString();
            }
        }
    }
    @Override
<<<<<<< HEAD
    String getJsTreeChildrenQuery() {
=======
    String getJsTreeChildrenQuery(String lang) {
>>>>>>> 6b26b5e43ada0ebc714898f7a81a1620b94f0802
        StringBuilder query = new StringBuilder();

        query.append("MATCH path = (child)-[r:SUBCLASSOF|RelatedTree]->(n:Class)\n");
        query.append("USING INDEX n:Class(iri)\n");
        query.append("WHERE n.ontology_name = {0} AND n.iri = {1}\n");
        query.append("UNWIND rels(path) as r1\n");
        query.append("RETURN distinct id(startNode(r1)) as startId, startNode(r1).iri as startIri, ");
<<<<<<< HEAD
        query.append("startNode(r1).label as startLabel, startNode(r1).has_children as hasChildren, r1.label as relation");
=======
        query.append("startNode(r1).label as startLabel, startNode(r1).`localizedLabels-" + lang + "`[0] as startLabelLocalized, startNode(r1).has_children as hasChildren, r1.label as relation");
>>>>>>> 6b26b5e43ada0ebc714898f7a81a1620b94f0802

        return query.toString();
    }

    @Override
    String getRootName() {
        return "Thing";
    }

    @Override
<<<<<<< HEAD
    String getJsTreeRoots(ViewMode viewMode) {
=======
    String getJsTreeRoots(String lang, ViewMode viewMode) {
>>>>>>> 6b26b5e43ada0ebc714898f7a81a1620b94f0802
        StringBuilder query = new StringBuilder();

        query.append("MATCH (");
        query.append(getNodeQueryString("n1", viewMode));
        query.append(")\n");
//        query.append("WHERE n1.ontology_name = {0} AND n1.iri = {1}\n");
        query.append("WHERE n1.ontology_name = {0}\n");
<<<<<<< HEAD
        query.append("RETURN id(n1) as startId, n1.iri as startIri,n1.label as startLabel, n1.has_children as hasChildren, ");
=======
        query.append("RETURN id(n1) as startId, n1.iri as startIri,n1.label as startLabel, n1.`localizedLabels-" + lang + "`[0] as startLabelLocalized, n1.has_children as hasChildren, ");
>>>>>>> 6b26b5e43ada0ebc714898f7a81a1620b94f0802
        query.append("'is-a' as relation, [] as parents");

        return query.toString();
    }
}
