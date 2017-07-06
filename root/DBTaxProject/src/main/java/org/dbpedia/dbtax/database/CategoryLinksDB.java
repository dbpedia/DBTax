package org.dbpedia.dbtax.database;

import org.dbpedia.dbtax.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * This class has most of the methods that deal with CategoryLinks table in Database. 
*/
public class CategoryLinksDB {

	private static final Logger logger = LoggerFactory.getLogger(CategoryLinksDB.class);
	/*
	 * This function gets all the immediate parents of the given leaf Category
	 * Adds the parent to Node table and also establishes relationship in Edge Table
	 * Input Parameters: PageID of leaf category
	 * This method need to be extended to support multiple-level hierarchy.
	 */
	public static void getCategoryParentsByPageID(int categoryPageID) {
		Random rand = new Random();

		ResultSet rs = null;

		//We find all the parents for the given leaf category
		String query = "SELECT `cl_to` FROM  `categorylinks` WHERE  `cl_from` =  " + categoryPageID;

		try (Connection connection = DatabaseConnection.getConnection();
			 PreparedStatement ps = connection.prepareStatement(query)) {

			rs = ps.executeQuery();
			while (rs.next()) {
				String parentCategory = rs.getString("cl_to").trim();

				//We obtain the parent Page ID to add it into the Node and Edge Tables
				int parentID = PageDB.getPageId(parentCategory);
				if (parentID > 0) {
					if (rand.nextInt(50) % 5 == 0) {
						try {
							Thread.sleep(2000);
							logger.debug("Sleeeping");
						} catch (InterruptedException e) {
							logger.error(e.getMessage());
						}
					}
					NodeDB.insertNode(parentID, parentCategory);
					EdgeDB.insertEdge(parentID, categoryPageID);
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static List<Integer> getPageIdOfCategory(String category){

		String query = "select cl_from from categorylinks where cl_to =? ";

		ResultSet rs = null;

		List<Integer> pageIds= new ArrayList<Integer>();

		try(Connection connection = DatabaseConnection.getConnection();
			PreparedStatement ps = connection.prepareStatement(query)){
			ps.setString(1, category);
			//Execute the query
			rs = ps.executeQuery();


			while (rs.next()){
				pageIds.add(rs.getInt("cl_from"));
			}

		} catch(SQLException e){
			e.printStackTrace();
		}
		return pageIds;
	}
}