package org.dbpedia.dbtax.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * This class has most of the methods that deal with CategoryLinks table in Database. 
*/
public class CategoryDB {
	
	private CategoryDB(){}
	
	//This function returns the number of categories below the threshold. 
	public static int getCategoryPageCount( int threshold ){
		ResultSet rs = null;
		String query = "SELECT COUNT(*) FROM `category` WHERE `cat_subcats`=0  AND `cat_pages`< ? ";

		try(Connection connection = DatabaseConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(query)) {
			ps.setInt( 1, threshold );
			rs = ps.executeQuery();

			int nodeId = 0;
			while ( rs.next() )
			{
				nodeId = rs.getInt( 1 );
			}
			return nodeId;
		}catch(SQLException e) {
			e.printStackTrace();
			return 0;
		}		
	}

}