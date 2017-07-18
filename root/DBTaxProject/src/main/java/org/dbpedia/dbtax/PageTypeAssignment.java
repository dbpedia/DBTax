package org.dbpedia.dbtax;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.dbpedia.dbtax.database.NodeDB;
import org.dbpedia.dbtax.database.PageDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageTypeAssignment {

    private static final Logger logger = LoggerFactory.getLogger(PageTypeAssignment.class);
    private OntModel model;
    private String headNamespace = "http://dbpedia.org/dbtax/";
    private String resourceNamespace ="http://dbpedia.org/resource/";

    public PageTypeAssignment() {
        model= ModelFactory.createOntologyModel();
    }

    public void assignPageTypes() {

        Set<String> heads = NodeDB.getDisinctheads();

        for (String head : heads) {
            Set<String> categories = NodeDB.getCategoriesByHead(head);

            for (String category : categories) {
                List<String> currentPath = new ArrayList<>();
                assignPagesForCategory(category,head, currentPath);
            }
        }

        //Output the generated model to a file.
        try {
            model.write(new FileOutputStream("test.ttl"), "N-TRIPLES");
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        }

    }


    private void assignPagesForCategory(String category, String head, List<String> currentPath){

        List<Page> pages = PageDB.getPagesByCategory(category);
        currentPath.add(category);
        for (Page page : pages) {
            String pageName =page.getName();

            //namespace==0 means it's a article page
            if (page.getNamespace() == 0) {
                Resource resource = model.createResource( resourceNamespace+ page.getName());
                resource.addProperty(RDF.type, model.createResource(headNamespace+head));
            }

            //namespace==14 means it's a category and have some pages associated with it.
            if (page.getNamespace() == 14)
                logger.info("entered loop: "+category+" "+pageName);
                //cycle detection
                if(!currentPath.contains(pageName))
                    assignPagesForCategory(pageName,head,currentPath);
                else
                    logger.info("cycle found!"+ currentPath.toString());
        }
    }

}