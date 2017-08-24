package org.dbpedia.dbtax.categories;

/**
 * Created by shashank on 1/7/17.
 */

import org.apache.jena.rdf.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class DBpediaInstances {
    private static Logger logger = LoggerFactory.getLogger(DBpediaInstances.class);

    private static final String INSTANCES_FILENAME = "instance_types_en.ttl";
    private static final String REDIRECTS_FILENAME = "redirects_en.ttl";
    private static final String LABELS_FILENAME= "labels_en.ttl";

    private DBpediaInstances(){}

    private static String normalizeName(String original) {
        if(original.length() == 0)
            return original;
        return original.trim().substring(0, 1).toUpperCase() + original.substring(1);
    }

    public static void generateInstances(){
//        generateInstancesByFile(INSTANCES_FILENAME,true);
//        generateInstancesByFile(LABELS_FILENAME, false);
//        generateInstancesByFile(REDIRECTS_FILENAME, true);
    }

    private static void generateInstancesByFile(String filename, boolean isSubject) {

        Model model = ModelFactory.createDefaultModel();
        model.read("/home/shashank/Downloads/" + filename);
        String fileName = "redirects.txt";
        StmtIterator iter = model.listStatements();
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileName, true);
             PrintWriter pw = new PrintWriter(fileOutputStream)) {
            while (iter.hasNext()) {
                Statement stmt = iter.nextStatement();  // get next statement
                Resource subject = stmt.getSubject();     // get the subject
                RDFNode object = stmt.getObject();      // get the object
                if (isSubject){
                    String s =normalizeName(subject.getLocalName().trim());
                    if(s.indexOf("_")==-1)
                        pw.println(s);
                }
                else
                    pw.println(normalizeName(object.asResource().getLocalName()));
            }

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public static void main(String [] argv){
        generateInstances();
    }
}