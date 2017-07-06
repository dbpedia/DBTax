package org.dbpedia.dbtax.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
import org.yago.javatools.administrative.Elements;
import org.yago.javatools.parsers.PlingStemmer;
*/

public class InterLanguageLinksExtraction {

	public static void findInterlanguageLinks(){
		
		String query = "select node_id from node where is_prominent=1;";

		ResultSet rs = null;
		try(Connection connection = DatabaseConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(query)){
		
			rs = ps.executeQuery();

			//We loop through the entire result set of nodes.
			while ( rs.next() ){

				int id = rs.getInt("node_id");
				int numOfNodes = InterLanguageLinksDB.getLanguageLinksCount(id);
				NodeDB.updateInterlanguageLinks(id, numOfNodes);
			}
			
			ps.close();
			connection.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
