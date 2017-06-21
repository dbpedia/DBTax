package org.dbpedia.dbtax;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.dbpedia.dbtax.database.CategoryLinksDB;
import org.dbpedia.dbtax.database.NodeDB;
import org.dbpedia.dbtax.database.PageDB;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class PageTypeAssignment {

    public static void main(String[] argv) {
        Set<String> heads = NodeDB.getDisinctheads();
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("test.ttl"), "UTF8"))) {

            for (String head : heads) {
                Set<String> categories = NodeDB.getCategoriesByHead(head);
                for (String category : categories) {
                    List<Page> pages = PageDB.getPagesByCategory(category);
                    for (Page page : pages) {
                        System.out.println(page.getName() + " " + category + " " + head);
                        if (page.getNamespace() == 0) {
                            //namespace==0 means it's a article page
                            writer.write("<http://dbpedia.org/resource/" + page.getName() + "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/dbtax/" + category + "> . \n");
                        }
                        if (page.getNamespace() == 14) {
                            //namespace==14 means it's a categorypage recurcive the categorypage
                            //recursion causes segmentation error go for only fist child
                            // getPagesForCategory( page.getPageName() );
//                            getPagesForCategoryFirstChild(page.getName());
                        }
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getPagesForCategoryFirstChild(String catName) {
        List<Page> pages = PageDB.getPagesByCategory(catName);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("test.ttl"), "UTF8"))) {
            for (Page page : pages) {
                if (page.getNamespace() == 0) {
                    //namespace==0 means it's a article page
                    writer.write("<http://dbpedia.org/resource/" + page.getName() + "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/dbtax/" + catName + "> . \n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}