package org.dbpedia.dbtax.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * This class has most of the methods that deal with Page table in Database. 
*/

public class PageDB {

	public static int getPageId(String catPageTitle){
		
		int resultId = -1;

		String query = "SELECT page_id FROM `page` WHERE `page_title` = ? AND page_namespace=14";
		
		ResultSet rs = null;

		try(Connection connection = DatabaseConnection.getConnection();
				PreparedStatement ps =	connection.prepareStatement( query )){
		
			ps.setString(1, catPageTitle);
			
			rs = ps.executeQuery();

			while( rs.next() ){
				resultId= rs.getInt("page_id");
			}
			
		} catch ( SQLException e ){
			e.printStackTrace();
		}
		return resultId;
	}
}