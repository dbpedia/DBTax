package org.dbpedia.dbtax.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.dbpedia.dbtax.ThresholdCalculations;

/*
 * Extract Leaves Function adds the leaf nodes and their corresponding Edges 
 */

public class LeafExtractionDB {

	private LeafExtractionDB(){ }
	
	public static void extractLeaves(){	
		
		int threshold = ThresholdCalculations.findThreshold();
		
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

		ResultSet rs = null;


		try(Connection connection = DatabaseConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(query)) {
			
			rs = ps.executeQuery();
			
			System.out.println("Excueted Query");

			//We loop through the entire result set of leaves.
			while ( rs.next() ){

				String cat_name = rs.getString("cat_title");

				//Get the page id of category 
				int pageid = PageDB.getPageId(cat_name);

				if(pageid!=-1){

					//Add the leaf node to the database Node table
					NodeDB.insertNode(pageid, cat_name);

					//The below function is to find the immediate parents of the categories
					//And add them to corresponding node and edge databases
					CategoryLinksDB.getCategoryParentsByPageID(pageid);
				}
			}
		} catch ( SQLException e ){
			e.printStackTrace();
		}
	}
}