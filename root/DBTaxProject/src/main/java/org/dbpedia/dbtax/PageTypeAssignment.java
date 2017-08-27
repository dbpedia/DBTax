package org.dbpedia.dbtax;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.dbpedia.dbtax.categories.Utils;
import org.dbpedia.dbtax.database.NodeDB;
import org.dbpedia.dbtax.database.PageDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageTypeAssignment {

    private static final Logger logger = LoggerFactory.getLogger(PageTypeAssignment.class);
    private OntModel model;
    private String headNamespace = "http://dbpedia.org/dbtax/";
    private String resourceNamespace = "http://dbpedia.org/resource/";

    public PageTypeAssignment() {
        model = ModelFactory.createOntologyModel();
    }

    public void assignPageTypes() {

        Set<String> heads = generateHeads("heads");
        for (String head : heads) {
            Set<String> categories = NodeDB.getCategoriesByHead(head);

            for (String category : categories) {
                List<String> currentPath = new ArrayList<>();
                assignPagesForCategory(category, head, currentPath);
            }
        }
        //Output the generated model to a file.
        try {
            model.write(new FileOutputStream("A-Box.ttl"), "N-TRIPLES");
            logger.info("Output : A-Box.ttl");
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        }

    }


    private void assignPagesForCategory(String category, String head, List<String> currentPath) {

        List<Page> pages = PageDB.getPagesByCategory(category);
        currentPath.add(category);

        for (Page page : pages) {
            String pageName = page.getName();

            //namespace==0 means it's a article page
            if (page.getNamespace() == 0) {
                try {
                    String result = java.net.URLEncoder.encode(pageName, "UTF-8");
                    Resource resource = model.createResource(resourceNamespace + result);
                    resource.addProperty(RDF.type, model.createResource(headNamespace + Utils.normalizeName(head)));
                } catch (UnsupportedEncodingException e) {
                    logger.error(e.getMessage());
                }
            }
            
            //namespace==14 means it's a category and have some pages associated with it.
            if (page.getNamespace() == 14) {
                //cycle detection
                if (!currentPath.contains(pageName))
                    assignPagesForCategory(pageName, head, currentPath);
                else
                    logger.debug("cycle found!" + currentPath.toString());
            }
        }
    }
    public static Set<String> generateHeads(String filename) {
        Set<String> heads = new HashSet();

        String line;

        try(BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"))) {
            /* Get all the heads */
            while ((line = in.readLine()) != null) {
                heads.add(line.toLowerCase().trim());
            }

        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        return heads;
    }

    public static void main(String[] argv) {
        PageTypeAssignment pageTypeAssignment = new PageTypeAssignment();
        pageTypeAssignment.assignPageTypes();
    }
}