package org.dbpedia.dbtax.categories;

import org.dbpedia.dbtax.database.NodeDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Gets a list of leaf nodes and generates the hierarchy up to owl:Thing
 *
 * @author Dimitris Kontokostas
 * @since 7/11/14 9:25 AM
 */
public class LeafToRoot{

    private static final Logger logger = LoggerFactory.getLogger(LeafToRoot.class);
    private LeafToRoot() {}

    public static void cycleRemoval(){
    	
        Set<String> leafs = NodeDB.getDisinctleafNodeNames();
        
        List<Relation> hierarchy =  HierarchyGenerator.mainFunc();

        List<List<String>> pathList = new ArrayList<>();

        for (String leaf : leafs) {
            List<String> currentPath = new ArrayList<>();
            currentPath.add(leaf);

            String currentResource = leaf;
            String currentParent = getParentResource(hierarchy, currentResource);
            boolean cycle = false;
            while (!currentParent.isEmpty()) {
                currentPath.add(currentParent);
                currentResource = currentParent;
                currentParent = getParentResource(hierarchy, currentResource);
                if (currentPath.contains(currentParent)) {
                    cycle = true;
                    hierarchy = deleteResource(hierarchy,  currentResource);
                    String pathToCycle = currentPath.toString();
                    logger.info("Cycle when adding: %s" , pathToCycle);
                    break;
                }

            }
            if (!cycle)
                pathList.add(currentPath);
        }

        try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("test.paths"), "UTF8"))){
            for(Relation r: hierarchy) {
                writer.write(r.getParent() + "***" + r.getChild() + " \n ");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }


    }

    static List<Relation> deleteResource(List<Relation> hierarchy, String child){
    	for(int i=0;i<hierarchy.size();i++){
    		Relation r = hierarchy.get(i);
    		if(r.getChild() != null && r.getChild().contains(child))
	        	hierarchy.remove(i);
    	}
    	return hierarchy;
    }

    /* returns the resource or empty String */
    static String getParentResource(List<Relation> hierarchy, String search) {
    	String result ="";
    	for(Relation r : hierarchy){
    	        if(r.getChild() != null && r.getChild().contains(search))
    	        	result = r.getParent();
    	    }
    	return result;
    }

}