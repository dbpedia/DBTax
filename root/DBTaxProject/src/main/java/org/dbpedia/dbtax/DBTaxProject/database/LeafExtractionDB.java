package org.dbpedia.dbtax.DBTaxProject.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * Extract Leaves Function adds the leaf nodes and their corresponding Edges 
 */

public class LeafExtractionDB {

	public static void extractLeaves(){	

		// Establish Database Connection
		Connection connection = DatabaseConnection.getConnection();

		PreparedStatement ps = null;
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
				+ "AND cat_pages<50;";
		/*
		 * Threshold Value is chosen as 50
		 * The above query gets all categories names of the actual article pages 
		 * With no sub categories
		 */

		ResultSet rs = null;

		try{

			ps = connection.prepareStatement( query );
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

			connection.close();

		} catch ( SQLException e ){
			e.printStackTrace();
		}finally{
			if(connection!=null)
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
}