package org.dbpedia.dbtax.categories;

/**
 * Created by shashank on 1/7/17.
 */

import org.apache.jena.rdf.model.*;

import java.util.HashSet;
import java.util.Set;

public class DBpediaInstances {

    private static final String INSTANCES_FILENAME = "instance_types_en.ttl";
    private static final String REDIRECTS_FILENAME = "redirects_en.ttl";
    private static final String LABELS_FILENAME= "labels_en.ttl";

    private DBpediaInstances(){}

    private static String normalizeName(String original) {
        if(original.length() == 0)
            return original;
        return original.trim().substring(0, 1).toUpperCase() + original.substring(1);
    }

    public static Set<String> generateInstances(){

        Set<String> instances= generateInstancesByFile(INSTANCES_FILENAME,true);
        instances.addAll(generateInstancesByFile(LABELS_FILENAME, false));
        instances.addAll(generateInstancesByFile(REDIRECTS_FILENAME, true));
        return instances;
    }

    private static Set<String> generateInstancesByFile(String filename, boolean isSubject){

        Model model = ModelFactory.createDefaultModel() ;
        model.read(filename,"TURTLE");
        StmtIterator iter = model.listStatements();
        Set<String> instances = new HashSet<>();

        while (iter.hasNext()) {
            Statement stmt      = iter.nextStatement();  // get next statement
            Resource  subject   = stmt.getSubject();     // get the subject
            RDFNode   object    = stmt.getObject();      // get the object
            if(isSubject)
                instances.add(normalizeName(subject.getLocalName().trim()));
            else
                instances.add(normalizeName(object.asResource().getLocalName()));
        }

        return instances;
    }
}