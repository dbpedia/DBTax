package org.dbpedia.dbtax.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.dbpedia.dbtax.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/*
 * Extract Leaves Function adds the leaf nodes and their corresponding Edges 
 */

public class LeafExtractionDB {

    private static final Logger logger = LoggerFactory.getLogger(LeafExtractionDB.class);

	private LeafExtractionDB(){ }
	public static void extractLeaves(){

		// To find threshold: Can use: ThresholdCalculations.findThreshold()
		int threshold = 56;

		String query = "SELECT cat_title "
				+ "FROM category "
				+ "WHERE cat_title in "
				+ "(select cl_to from "
				+ "categorylinks "
				+ "WHERE cl_from in "
				+ "(select page_id "
				+ "from page "
				+ "where page_namespace =0)) "
				+ "AND cat_subcats=0 "
				+ "AND cat_pages>0 "
				+ "AND cat_pages< "+threshold;
		/*
		 * Threshold Value is calculated.
		 * The above query gets all categories names of the actual article pages 
		 * With no sub categories
		 */
		Set<Node> nodeMap = new HashSet<>();

		try(Connection connection = DatabaseConnection.getConnection();
			PreparedStatement ps = connection.prepareStatement(query)) {

            CategoryLinksDB categoryLinksDB = new CategoryLinksDB(connection);
            PageDB pageDB = new PageDB(connection);
            NodeDB nodeDB = new NodeDB(connection);

            ResultSet rs = ps.executeQuery();
			logger.info("Executed the query to retrieve the leaves");

			//We loop through the entire result set of leaves.
			while ( rs.next() ){

				String catName = rs.getString("cat_title");

				//Get the page id of category 
				int pageid = pageDB.getPageId(catName);

				if(pageid!=-1){
					//Add the leaf node to the database Node table
					nodeMap.add(new Node(pageid,catName));

                    //The below function is to find the immediate parents of the categories
					//And add them to corresponding node and edge databases
					categoryLinksDB.getCategoryParentsByPageID(pageid, nodeDB, pageDB);
				}
			}

			nodeDB.insertNode(nodeMap);

		} catch ( SQLException e ){
			logger.error(e.getMessage());
		}
	}
}