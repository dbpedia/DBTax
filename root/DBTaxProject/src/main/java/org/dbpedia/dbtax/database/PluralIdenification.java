package org.dbpedia.dbtax.database;


import edu.stanford.nlp.ie.machinereading.structure.Span;
import edu.stanford.nlp.simple.*;

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

            ResultSet rs = ps.executeQuery();

			//We loop through the entire result set of nodes.
			while ( rs.next() ){

				String catName = rs.getString("category_name");
				catName = catName.replace("_", " ");

				//Get the head from the Node's category
                Sentence sentence = new Sentence(catName);

                int headIndex = sentence.algorithms().headOfSpan(new Span(0,sentence.length()));
                String head = sentence.originalText(headIndex);
                String lemmatizedHead = sentence.lemma(headIndex);

				if(lemmatizedHead.equals(head))
					NodeDB.updatePluralNode(rs.getInt("node_id"), lemmatizedHead, false);
				else
					NodeDB.updatePluralNode(rs.getInt("node_id"), lemmatizedHead, true);
			}
			
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
	}
}