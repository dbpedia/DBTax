package org.dbpedia.dbtax.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.yago.javatools.administrative.Elements;
import org.yago.javatools.parsers.PlingStemmer;

public class ClassesIdenification {

	private ClassesIdenification(){
		
	}
	
	public static void findPlural(){
		
		// Establish Database Connection
		Connection connection = DatabaseConnection.getConnection();

		PreparedStatement ps = null;
		String query = "select category_name, node_id from node;";

		ResultSet rs = null;

		try {
			ps = connection.prepareStatement( query );
			rs = ps.executeQuery();
			

			//We loop through the entire result set of nodes.
			while ( rs.next() ){

				String cat_name = rs.getString("category_name");
				
				//Get the head from the Node's category
				String head = Elements.getHead(cat_name);
				
				if(PlingStemmer.isPlural(head))
					NodeDB.updatePluralNode(rs.getInt("node_id"));
			}
			
			ps.close();
			connection.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
