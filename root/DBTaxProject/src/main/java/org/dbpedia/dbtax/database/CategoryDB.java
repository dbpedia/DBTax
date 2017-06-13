package org.dbpedia.dbtax.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoryDB {
	
	private CategoryDB(){
		
	}
	
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

