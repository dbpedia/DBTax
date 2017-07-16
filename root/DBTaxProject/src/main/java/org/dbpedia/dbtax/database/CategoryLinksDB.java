package org.dbpedia.dbtax.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*
 * This class has most of the methods that deal with CategoryLinks table in Database. 
*/
public class CategoryLinksDB {

	private static final Logger logger = LoggerFactory.getLogger(CategoryLinksDB.class);

	private CategoryLinksDB(){ }

	/*
	 * This function gets all the immediate parents of the given leaf Category
	 * Adds the parent to Node table and also establishes relationship in Edge Table
	 * Input Parameters: PageID of leaf category
	 * This method need to be extended to support multiple-level hierarchy.
	 */
	public static void getCategoryParentsByPageID(int categoryPageID) {

		//We find all the parents for the given leaf category
		String query = "SELECT `cl_to` FROM  `categorylinks` WHERE  `cl_from` = ?;";

		try (Connection connection = DatabaseConnection.getConnection();
			 PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1,categoryPageID);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String parentCategory = rs.getString("cl_to").trim();

				//We obtain the parent Page ID to add it into the Node and Edge Tables
				int parentID = PageDB.getPageId(parentCategory);
				if (parentID > 0) {
					NodeDB.insertNode(parentID, parentCategory);
					EdgeDB.insertEdge(parentID, categoryPageID);
				}
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
	}

	public static List<Integer> getPageIdOfCategory(String category){

		String query = "select cl_from from categorylinks where cl_to =? ;";

		List<Integer> pageIds= new ArrayList<>();

		try(Connection connection = DatabaseConnection.getConnection();
			PreparedStatement ps = connection.prepareStatement(query)){
			ps.setString(1, category);

			//Execute the query
		    ResultSet rs = ps.executeQuery();

			while (rs.next()){
				pageIds.add(rs.getInt("cl_from"));
			}

		} catch(SQLException e){
			logger.error(e.getMessage());
		}
		return pageIds;
	}
}