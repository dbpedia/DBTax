package org.dbpedia.dbtax;

import java.util.ArrayList;
import java.util.HashSet;

import org.dbpedia.dbtax.database.EdgeDB;
import org.dbpedia.dbtax.database.NodeDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NodeUtils {

	private static final Logger logger = LoggerFactory.getLogger(NodeUtils.class);

	private NodeUtils(){}

	public static void findProminentNodes(){
		
		//Input L: get all leaf nodes
		ArrayList<Integer> leafNodes=NodeDB.getDisinctleafNodes();
		
		//PN =empty array
		HashSet<Integer> prominentNodes= new HashSet<>();

		// for all l in leaves L
		for(int l=0; l<leafNodes.size();l++){
			
			int leaf = leafNodes.get(l);
			
			boolean isProminent = true;

			//P -> getTransitiveParents(leaf)
			ArrayList<Integer> parents = EdgeDB.getTransitiveParents(leaf);
			
			for (int parent: parents){

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
		}
		logger.info("Added the parent-child relations");
		NodeDB.updateProminentNode(prominentNodes);
	}
}