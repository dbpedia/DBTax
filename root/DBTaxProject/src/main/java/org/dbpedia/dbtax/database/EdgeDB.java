package org.dbpedia.dbtax.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class EdgeDB {

	private static final Logger logger = LoggerFactory.getLogger(EdgeDB.class);
	private Connection connection;
	/*
	 * This function is to insert an Edge which captures the 
	 * Parent-Child Relationship among categories
	 */

	public EdgeDB(Connection connection) {
		if(this.connection==null)
		    this.connection= connection;
	}

	public EdgeDB() {
	}

	public static void insertEdge(int parentId, int chidId){

		String query = "INSERT IGNORE INTO edges(parent_id,child_id) VALUES (?, ?)";

		try(Connection connection = DatabaseConnection.getConnection();
				PreparedStatement ps=connection.prepareStatement(query)){

			ps.setInt(1, parentId);
			ps.setInt(2, chidId);
			ps.executeUpdate();
		}catch(SQLException e){
			logger.error(e.getMessage());
		}
	}
	
	/*
	 * This method is used to get all parents of the given leaf Node
	 */
	public ArrayList<Integer> getTransitiveParents(int leafNode){
		
		String query =  "SELECT parent_id FROM edges WHERE child_id =?";
		ArrayList<Integer> parents= new ArrayList<>();

		try(PreparedStatement ps = connection.prepareStatement(query)) {
			
			ps.setInt( 1, leafNode);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()){
				parents.add(rs.getInt(1));
			}
	    }catch(SQLException e) {
			logger.error(e.getMessage());
		}
		return parents;
	}
	
	/*
	 * This method returns all the children nodes of the given node
	 * from Edge table
	 */
	public ArrayList<Integer> getChildren(int parentId){
		
		String query =  "SELECT child_id FROM edges WHERE parent_id=?";

        ArrayList<Integer> childrenList= new ArrayList<>();

        try(PreparedStatement ps = connection.prepareStatement(query)) {
			
			ps.setInt( 1, parentId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()){
				childrenList.add(rs.getInt("child_id"));
			}
	    } catch (SQLException e) {
			logger.error(e.getMessage());
		}

        return childrenList;
	}	

}