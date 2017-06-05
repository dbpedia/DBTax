package org.dbpedia.dbtax.DBTaxProject.database;

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

		// Establish Database Connection	
		Connection connection = DatabaseConnection.getConnection();
				
		PreparedStatement ps = null;
		String query = "INSERT IGNORE INTO edges(parent_id,child_id) VALUES (?, ?)";

		try{
			ps = connection.prepareStatement(query);
			ps.setInt(1, parentId);
			ps.setInt(2, chidId);
			ps.executeUpdate();
			connection.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	/*
	 * This method is used to get all parents of the given leaf Node
	 */
	public static ArrayList<Integer> getTransitiveParents(int leafNode){
		
		//Connect to database
		Connection connection = DatabaseConnection.getConnection();
		
		PreparedStatement ps = null;
		String query =  "SELECT parent_id FROM edges WHERE child_id =?";
		
		ResultSet rs = null;
		
		try{
			ps = connection.prepareStatement(query);
			ps.setInt( 1, leafNode);

			rs = ps.executeQuery();
			ArrayList<Integer> parents= new ArrayList<Integer>();

			while (rs.next()){
				parents.add(rs.getInt(1));
			}
			connection.close();
			
			return parents;
		
		} catch(SQLException e) {
			e.printStackTrace();
			return null;
		}finally{
			if(connection!=null)
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}	
	}
	
	/*
	 * This method returns all the children nodes of the given node
	 * from Edge table
	 */
	public static ArrayList<Integer> getChildren(int parentId){
		
		//Connect to database
		Connection connection = DatabaseConnection.getConnection();
		
		PreparedStatement ps = null;
		String query =  "SELECT child_id FROM edges WHERE parent_id=?";

		ResultSet rs = null;

		try{
			ps = connection.prepareStatement(query);
			ps.setInt( 1, parentId);

			rs = ps.executeQuery();

			ArrayList<Integer> childrenList= new ArrayList<Integer>();

			while (rs.next()){
				childrenList.add(rs.getInt("child_id"));
			}
			connection.close();
			return childrenList;
		} catch(SQLException e)
		{
			e.printStackTrace();
			return null;
		} finally{
			if(connection!=null)
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}	
	}

}