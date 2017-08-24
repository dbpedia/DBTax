package org.dbpedia.dbtax.categories;

import org.apache.jena.rdf.model.StmtIterator;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by shashank on 22/7/17.
 */
public class Instances {
    private static Logger logger = LoggerFactory.getLogger(DBpediaInstances.class);

    private static final String INSTANCES_FILENAME = "instance_types_en.ttl";
    private static final String REDIRECTS_FILENAME = "redirects_en.ttl";
    private static final String LABELS_FILENAME= "labels_en.ttl";


    private static String normalizeName(String original) {
        if(original.length() == 0)
            return original;
        return original.trim().substring(0, 1).toUpperCase() + original.substring(1);
    }

    public static Set<String> generateInstances() throws IOException {

        Set<String> instances= generateInstancesByFile(INSTANCES_FILENAME,true);

        /*instances.addAll(generateInstancesByFile(LABELS_FILENAME, false));
        instances.addAll(generateInstancesByFile(REDIRECTS_FILENAME, true));
        */
//        Set<String> instances = new HashSet<>();
//        save("test.txt",instances);
        return instances;
    }

    private static Set<String> generateInstancesByFile(String filename, boolean isSubject) throws IOException {
        String file = "instance_types_en.ttl";

        // read the file 'example-data-artists.ttl' as an InputStream.
        InputStream input = Instances.class.getResourceAsStream("/home/shashank/instance_types_en.ttl");

        if(input==null){
            logger.info("INPUT IS NULL");
        }
        // Rio also accepts a java.io.Reader as input for the parser.
        Model model = Rio.parse(input, "", RDFFormat.TURTLE);

        // To check that we have correctly read the file, let's print out the model to the screen again
        for (Statement statement: model) {
            System.out.println(statement);
        }
/*
        // read the file 'example-data-artists.ttl' as an InputStream.
        InputStream input = Instances.class.getResourceAsStream("/"+ INSTANCES_FILENAME);

// Rio also accepts a java.io.Reader as input for the parser.
        org.eclipse.rdf4j.model.Model model = Rio.parse(input, "", RDFFormat.TURTLE);
        ValueFactory vf = SimpleValueFactory.getInstance();
        org.eclipse.rdf4j.model.Model aboutVanGogh = model.filter(null, null, null);
*/
        Set<String> instances = new HashSet<>();
/*

// Iterate over the statements that are about Van Gogh
        for (org.eclipse.rdf4j.model.Statement st: aboutVanGogh) {
            // the subject is always `ex:VanGogh`, an IRI, so we can safely cast it
            IRI subject = (IRI) st.getSubject();
            // the property predicate is always an IRI
            IRI predicate = (IRI) st.getPredicate();

            // the property value could be an IRI, a BNode, or a Literal. In RDF4J,
            // Value is is the supertype of all possible kinds of RDF values.
            Value object = (Value) st.getObject();

            // let's print out the statement in a nice way. We ignore the namespaces
            // and only print the local name of each IRI
            System.out.print(subject.getLocalName() + " " + predicate.getLocalName() + " ");
        }
*/

        return instances;
    }

    public static void save(String fileName, Set<String> instances){
        try(FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            PrintWriter pw = new PrintWriter(fileOutputStream)){
            for (String instance: instances)
                pw.println(instance);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

    }
    public static void main(String [] argv) throws IOException {
        generateInstances();
    }

}