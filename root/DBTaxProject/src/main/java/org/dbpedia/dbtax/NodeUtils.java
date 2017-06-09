package org.dbpedia.dbtax;

import java.util.ArrayList;
import java.util.HashSet;

import org.dbpedia.dbtax.database.EdgeDB;
import org.dbpedia.dbtax.database.NodeDB;


public class NodeUtils {


	public static void findProminentNodes(){
		
		//Input L: get all leaf nodes
		ArrayList<Integer> leafNodes=NodeDB.getDisinctleafNodes();
		
		//PN =empty array
		HashSet<Integer> prominentNodes= new HashSet<Integer>();

		// for all l in leaves L
		for(int l=0; l<leafNodes.size();l++){
			
			int leaf = leafNodes.get(l);
			
			boolean isProminent = true;
			//P -> getTransitiveParents(leaf)
			ArrayList<Integer> parents = EdgeDB.getTransitiveParents(leaf);
			
			for(int p=0;p<parents.size();p++){
				int parent = parents.get(p);
				
				//C <- getChildren(p); areAllLeaves=true
				boolean areAllLeaves = true;
				ArrayList<Integer> children = EdgeDB.getChildren(parent);
				
				//for all c belongs to Children do
				int c=0;
				while(c<children.size() && areAllLeaves){
					//if c 62 L then areAllLeaves false;
					if (!leafNodes.contains(children.get( c ))){
						areAllLeaves = false;
					}
					c++;
				}
				
				if(areAllLeaves){
					prominentNodes.add(parent);
					isProminent = false;
				}
				
			}
			if(isProminent)
				prominentNodes.add(leaf);
			NodeDB.updateProminentNode(prominentNodes);
		}
	}
	
}