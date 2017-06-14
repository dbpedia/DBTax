package org.dbpedia.dbtax.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class EdgeDB {

	/*
	 * This function is to insert an Edge which captures the 
	 * Parent-Child Relationship among categories
	 */
	public static void insertEdge(int parentId, int chidId){

		String query = "INSERT IGNORE INTO edges(parent_id,child_id) VALUES (?, ?)";

		try(Connection connection = DatabaseConnection.getConnection();
				PreparedStatement ps=connection.prepareStatement(query)){

			ps.setInt(1, parentId);
			ps.setInt(2, chidId);
			ps.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	/*
	 * This method is used to get all parents of the given leaf Node
	 */
	public static ArrayList<Integer> getTransitiveParents(int leafNode){
		
		String query =  "SELECT parent_id FROM edges WHERE child_id =?";
		
		ResultSet rs = null;

		try(Connection connection = DatabaseConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(query)) {
			
			ps.setInt( 1, leafNode);
			rs = ps.executeQuery();
			
			ArrayList<Integer> parents= new ArrayList<Integer>();
			while (rs.next()){
				parents.add(rs.getInt(1));
			}
			return parents;
	    }catch(SQLException e) {
			e.printStackTrace();
			return null;
		}		
	}	
	
	/*
	 * This method returns all the children nodes of the given node
	 * from Edge table
	 */
	public static ArrayList<Integer> getChildren(int parentId){
		
		String query =  "SELECT child_id FROM edges WHERE parent_id=?";

		ResultSet rs = null;
		
		try(Connection connection = DatabaseConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(query)) {
			
			ps.setInt( 1, parentId);
			rs = ps.executeQuery();
			ArrayList<Integer> childrenList= new ArrayList<Integer>();

			while (rs.next()){
				childrenList.add(rs.getInt("child_id"));
			}
			return childrenList;
	    } catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}	

}