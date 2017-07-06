package org.dbpedia.dbtax;

import java.io.*;
import java.util.List;
import java.util.Set;

import org.dbpedia.dbtax.database.NodeDB;
import org.dbpedia.dbtax.database.PageDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageTypeAssignment {
    private static final Logger logger = LoggerFactory.getLogger(PageTypeAssignment.class);
    public static void main(String[] argv) {
        Set<String> heads = NodeDB.getDisinctheads();
        logger.debug("Got the heads");
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("pageTypeAssigment.ttl"), "UTF8"))) {
            for (String head : heads) {
                Set<String> categories = NodeDB.getCategoriesByHead(head);
                logger.debug("got cat for head: "+head);
                for (String category : categories) {
                    logger.debug("cat is "+ category);
                    List<Page> pages = PageDB.getPagesByCategory(category);
                    logger.debug("got pages for category"+ category);
                    for (Page page : pages) {
                        logger.debug(page.getName() + " " + category + " " + head);
                        if (page.getNamespace() == 0) {
                            //namespace==0 means it's a article page
                            writer.write("<http://dbpedia.org/resource/" + page.getName() + "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/dbtax/" + head + "> . \n");
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
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("testp.ttl"), "UTF8"))) {
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