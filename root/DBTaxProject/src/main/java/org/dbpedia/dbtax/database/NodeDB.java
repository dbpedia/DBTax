package org.dbpedia.dbtax.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;

import com.mysql.jdbc.Statement;

/*
 * This class has most of the methods that deal with Page table in Database. 
 */

public class NodeDB{

	/*
	 * This function is responsible for adding Categories/Node to Node table.
	 * Input Parameters: PageID(from page Table) and CategoryName.
	 */
	public static void insertNode( int nodeID, String categoryName){

		String query = "INSERT IGNORE INTO node(node_id,category_name,is_leaf,is_prominent,score_edit_histo) VALUES (?,?,0,0,0)";

		try(Connection connection = DatabaseConnection.getConnection();
				PreparedStatement ps =connection.prepareStatement(query)){
			
			ps.setInt( 1, nodeID);
			ps.setString( 2, categoryName);
			ps.executeUpdate();

			connection.close();
		} catch(SQLException e){
			e.printStackTrace();
		}
	}

	/*
	 * This method gets all the distinct leaf nodes from Edges Table
	 * which are not parent Nodes
	 */
	public static ArrayList<Integer>  getDisinctleafNodes(){

		String query =  "SELECT  distinct `child_id` FROM edges WHERE  `child_id` NOT IN (SELECT `parent_id` FROM edges )";

		ResultSet rs = null;

		try(Connection connection = DatabaseConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(query)){
			//Execute the query
			rs = ps.executeQuery();

			//All the nodes which are not parents and distinct are stored in LeafID array.
			ArrayList<Integer> leafId= new  ArrayList<Integer>();

			while (rs.next()){
				leafId.add(rs.getInt("child_id") );
			}

			return leafId;

		} catch(SQLException e){
			e.printStackTrace();
			return null;
		}	
	}

	public static void updateProminentNode(HashSet<Integer> prominentNodes){


		String query = "UPDATE node SET is_prominent=true WHERE node_id= ";

		try(Connection connection = DatabaseConnection.getConnection()){
		
			for (Integer i : prominentNodes) {
				String updatedQuery = query+i;
				Statement stmt = (Statement) connection.createStatement();
				stmt.executeUpdate(updatedQuery);
			}
		} catch(SQLException e){
			e.printStackTrace();
		}	
	}

	public static void updatePluralNode(int id){

		String query = "UPDATE node SET is_head_plural=true WHERE node_id= ";

		try(Connection connection = DatabaseConnection.getConnection()){
			String updatedQuery = query+id;
			Statement stmt = (Statement) connection.createStatement();
			stmt.executeUpdate(updatedQuery);
		} catch(SQLException e){
			e.printStackTrace();
		} 
	}
	
	public static void updateInterlanguageLinks(int id, int number){

		String query = "UPDATE node SET score_interlang= "+number+" WHERE node_id= ";

		try(Connection connection = DatabaseConnection.getConnection()){
			
			String updatedQuery = query+id;
			Statement stmt = (Statement) connection.createStatement();
			stmt.executeUpdate(updatedQuery);
		
		} catch(SQLException e){
			e.printStackTrace();
		} 
	}
}