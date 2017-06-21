package org.dbpedia.dbtax.categories;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Gets a list of leaf nodes and generates the hierarchy up to owl:Thing
 *
 * @author Dimitris Kontokostas
 * @since 7/11/14 9:25 AM
 */
public class LeafToRoot {

    public static void main(String[] args) {
    	
        Set<String> leafs = HierarchyGenerator.generateInstances();
        
        List<Relation> hierarchy =  HierarchyGenerator.mainFunc();

        List<List<String>> path_list = new ArrayList<>();

        for (String leaf : leafs) {
            List<String> current_path = new ArrayList<>();
            current_path.add(leaf);

            String current_resource = leaf;
            String current_parent = getParentResource(hierarchy, current_resource);
            boolean cycle = false;
            while (!current_parent.isEmpty()) {
                current_path.add(current_parent);
                current_resource = current_parent;
                current_parent = getParentResource(hierarchy, current_resource);
                if (current_path.contains(current_parent)) {
                    cycle = true;
                    hierarchy = deleteResource(hierarchy, current_parent, current_resource);
                    System.out.println("Cycle when adding:" + current_path.toString());
                    break;
                }

            }
            if (!cycle)
                path_list.add(current_path);
        }

        try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("test.paths"), "UTF8"))){
            for(Relation r: hierarchy) {
                writer.write(r.getParent() + "***" + r.getChild() + " \n ");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    static List<Relation> deleteResource(List<Relation> hierarchy, String parent, String child){
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