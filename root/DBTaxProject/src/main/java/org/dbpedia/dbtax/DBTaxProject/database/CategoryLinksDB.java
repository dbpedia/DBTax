package org.dbpedia.dbtax.DBTaxProject.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * This class has most of the methods that deal with CategoryLinks table in Database. 
*/
public class CategoryLinksDB {

	/*
	 * This function gets all the immediate parents of the given leaf Category
	 * Adds the parent to Node table and also establishes relationship in Edge Table
	 * Input Parameters: PageID of leaf category
	 * This method need to be extended to support multiple-level hierarchy.
	 */
	public static void getCategoryParentsByPageID(int categoryPageID){
		
		// Establish Database Connection	
		Connection connection = DatabaseConnection.getConnection();
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		//We find all the parents for the given leaf category
		String query = "SELECT `cl_to` FROM  `categorylinks` WHERE  `cl_from` =  " + categoryPageID;	
		try {
			ps = connection.prepareStatement( query );
			rs = ps.executeQuery();
			while ( rs.next() ){
				String parentCategory = rs.getString( "cl_to" ).trim();
				
				//We obtain the parent Page ID to add it into the Node and Edge Tables
				int parentID = PageDB.getPageId( parentCategory);
				if ( parentID > 0 ){
					NodeDB.insertNode( parentID, parentCategory);
					EdgeDB.insertEdge( parentID, categoryPageID );
				}
			
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
		
}