package org.dbpedia.dbtax.database;

import edu.stanford.nlp.ie.machinereading.structure.Span;
import edu.stanford.nlp.simple.Sentence;
import org.dbpedia.dbtax.categories.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PluralIdenification {

	private static final Logger logger = LoggerFactory.getLogger(PluralIdenification.class);

	private PluralIdenification(){
	}

	public static void findPlural(){

		String query = "select category_name, node_id from node;";

		try(Connection connection = DatabaseConnection.getConnection();
			PreparedStatement ps = connection.prepareStatement( query )){

		    NodeDB nodeDB = new NodeDB(connection);
            ResultSet rs = ps.executeQuery();

			//We loop through the entire result set of nodes.
			while ( rs.next() ) {


				String catName = rs.getString("category_name");
				catName = catName.replace("_", " ");

				//Get the head from the Node's category
                Sentence sentence = new Sentence(catName);

                int headIndex = sentence.algorithms().headOfSpan(new Span(0,sentence.length()));
                String head = sentence.originalText(headIndex);
                String lemmatizedHead = sentence.lemma(headIndex);
				String normalizedHead = Utils.normalizeName(lemmatizedHead);

				if(!lemmatizedHead.equals(head))
                    nodeDB.updatePluralNode(rs.getInt("node_id"), normalizedHead, true);
                else
                	nodeDB.updatePluralNode(rs.getInt("node_id"), normalizedHead, false);

			}
			
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
	}
}