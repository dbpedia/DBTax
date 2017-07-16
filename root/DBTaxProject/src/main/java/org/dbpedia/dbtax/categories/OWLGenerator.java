package org.dbpedia.dbtax.categories;


import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.*;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 9/15/14 3:34 PM
 */
public class OWLGenerator {
    private static final Logger logger = LoggerFactory.getLogger(OWLGenerator.class);

    public static void generateTBox() {

        String in = "test.paths";
        
        List<String> lines = Utils.getFileLines(in);
        Set<String> classes = new HashSet<>();
        Map<String,String> subClasses = new HashMap<>();
        for (String line: lines) {
            if (line.startsWith("#")) {
                continue;
            }
            System.out.println(line);
            String[] parts = line.split("\\*\\*\\*");
            if (parts.length != 2) {
            	System.out.println(line);
                System.err.println("Invalid row with parts != 2");
            }

            String parent = parts[0];
            String child = parts[1];

            classes.add(parent);
            classes.add(child);
            subClasses.put(child, parent);
        }

        Model model = ModelFactory.createOntologyModel();
        String namespace = "http://dbpedia.org/dbtax/";
        model.setNsPrefix("dbtax", namespace);
        for (String cls: classes) {
            if ("Content".equals(cls)) {
                continue;
            }
            Resource resource = model.createResource(namespace + cls);
            resource.addLiteral(RDFS.label, model.createLiteral(cls, "en"));

            String parent = subClasses.get(cls);
            if (parent != null) {
                if ("Content".equals(parent) ) {
                    resource.addProperty(RDFS.subClassOf, OWL.Thing);
                } else {
                    resource.addProperty(RDFS.subClassOf, model.createResource(namespace + parent));
                }
            }
        }

        try {
            model.write(new FileOutputStream("test.ttl"), "N-TRIPLES");
        } catch (FileNotFoundException e) {

        }
    }
}