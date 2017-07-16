package org.dbpedia.categories;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;

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

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("file missing from arguments");
            System.exit(0);
        }

        List<String> lines = Utils.getFileLines(args[0]);
        Set<String> classes = new HashSet<>();
        Map<String,String> subClasses = new HashMap<>();
        for (String line: lines) {
            if (line.startsWith("#")) {
                continue;
            }
            String[] parts = line.split("\t");
            if (parts.length != 2) {
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
            if (cls.equals("Content")) {
                continue;
            }
            Resource resource = model.createResource(namespace + cls);
            resource.addLiteral(RDFS.label, model.createLiteral(cls, "en"));

            String parent = subClasses.get(cls);
            if (parent != null) {
                if (parent.equals("Content") ) {
                    resource.addProperty(RDFS.subClassOf, OWL.Thing);
                } else {
                    resource.addProperty(RDFS.subClassOf, model.createResource(namespace + parent));
                }
            }
        }

        try {
            model.write(new FileOutputStream(args[0] + ".ttl"), "TURTLE");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
