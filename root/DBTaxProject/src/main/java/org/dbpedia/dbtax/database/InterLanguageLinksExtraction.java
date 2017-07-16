package org.dbpedia.dbtax.database;

import org.dbpedia.dbtax.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class InterLanguageLinksExtraction {

    private static final Logger logger = LoggerFactory.getLogger(InterLanguageLinksExtraction.class);

	public static void findInterlanguageLinks(){

		ArrayList<Node> nodes = new ArrayList<>();
		String query = "select node_id from node where is_prominent=1;";

		try(Connection connection = DatabaseConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(query)){

			ResultSet rs = ps.executeQuery();

			//We loop through the entire result set of nodes.
			while ( rs.next() ){
				int id = rs.getInt("node_id");
				int numOfNodes = InterLanguageLinksDB.getLanguageLinksCount(id);
                Node node = new Node(id,numOfNodes);
                nodes.add(node);
			}

			NodeDB.updateInterlanguageLinks(nodes);
		} catch (SQLException e) {
	        logger.error(e.getMessage());
		}
	}

}