package org.dbpedia.dbtax.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.dbpedia.dbtax.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/*
 * This class has most of the methods that deal with Page table in Database. 
 */

public class NodeDB{

	private static final Logger logger = LoggerFactory.getLogger(NodeDB.class);
	private Connection connection;

	public NodeDB(Connection connection) {
		if(this.connection==null)
			this.connection = connection;
	}
	public NodeDB(){

	}

	/*
         * This function is responsible for adding Categories/Node to Node table.
         * Input Parameters: PageID(from page Table) and CategoryName.
         */
	public void insertNode( int nodeID, String categoryName){

		String query = "INSERT IGNORE INTO node(node_id,category_name,is_leaf,is_prominent, is_head_plural, score_interlang) VALUES (?,?,0,0,0,0)";

		try(PreparedStatement ps =connection.prepareStatement(query)){

			ps.setInt( 1, nodeID);
			ps.setString( 2, categoryName);
			ps.executeUpdate();

		} catch(SQLException e){
			logger.error(e.getMessage());
		}
	}

	public void insertNode(Set<Node> nodeMap){

		String query = "INSERT IGNORE INTO node(node_id,category_name,is_leaf,is_prominent, is_head_plural, score_interlang) VALUES (?,?,1,0,0,0);";
		try (PreparedStatement ps = connection.prepareStatement(query)) {
			for (Node node : nodeMap) {
				ps.setInt(1, node.getNodeId());
				ps.setString(2, node.getCategoryName());
				ps.addBatch();
			}
			connection.setAutoCommit(false);
			ps.executeBatch();
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
	}

	/*
	 * This method gets all the distinct leaf nodes from Edges Table
	 * which are not parent Nodes
	 */
	public ArrayList<Integer>  getDisinctleafNodes(){

		String query =  "SELECT  distinct `child_id` FROM edges WHERE `child_id` NOT IN (SELECT `parent_id` FROM edges );";

		ArrayList<Integer> leafId= new  ArrayList<>();
		try(PreparedStatement ps = connection.prepareStatement(query)){

			//Execute the query
			ResultSet rs = ps.executeQuery();

			//All the nodes which are not parents and distinct are stored in LeafID array.
			while (rs.next()){
				leafId.add(rs.getInt("child_id") );
			}
		} catch(SQLException e){
			logger.error(e.getMessage());
		}
		return leafId;
	}

	public ArrayList<String>  getDisinctLeafHeads(){

		String query =  "SELECT  distinct `head_of_name` FROM node WHERE `is_leaf`=1;";

		ArrayList<String> leafHeads= new  ArrayList<>();
		try(PreparedStatement ps = connection.prepareStatement(query)){

			//Execute the query
			ResultSet rs = ps.executeQuery();

			//All the nodes which are not parents and distinct are stored in LeafID array.
			while (rs.next()){
				leafHeads.add(rs.getString("head_of_name") );
			}
		} catch(SQLException e){
			logger.error(e.getMessage());
		}
		return leafHeads;
	}

	public ArrayList<Integer>  getParentNodes(int childId){

		String query =  "SELECT `parent_id` FROM edges WHERE `child_id` = ?;";

		ArrayList<Integer> parentId= new  ArrayList<>();
		try(PreparedStatement ps = connection.prepareStatement(query)){
			ps.setInt(1,childId);
			//Execute the query
			ResultSet rs = ps.executeQuery();

			//All the nodes which are not parents and distinct are stored in LeafID array.
			while (rs.next()){
				parentId.add(rs.getInt("parent_id") );
			}
		} catch(SQLException e){
			logger.error(e.getMessage());
		}
		return parentId;
	}


	public int getSingleParentNode(int childId){

		String query =  "SELECT `parent_id` FROM edges WHERE `child_id` = ?;";

		int parentId=-1;
		try(PreparedStatement ps = connection.prepareStatement(query)){
			ps.setInt(1,childId);
			//Execute the query
			ResultSet rs = ps.executeQuery();
			//All the nodes which are not parents and distinct are stored in LeafID array.
			while (rs.next()){
				parentId= rs.getInt("parent_id");
			}
		} catch(SQLException e){
			logger.error(e.getMessage());
		}

		return parentId;
	}

	public String  getHeadOfNode(int nodeId){
		String query =  "SELECT head_of_name FROM node WHERE `node_id` = ?;";

		String headName = null;
		try(PreparedStatement ps = connection.prepareStatement(query)){
			ps.setInt(1,nodeId);
			//Execute the query
			ResultSet rs = ps.executeQuery();

			//All the nodes which are not parents and distinct are stored in LeafID array.
			while (rs.next()){
				headName = rs.getString("head_of_name");
			}
		} catch(SQLException e){
			logger.error(e.getMessage());
		}
		return headName;
	}
	/*
	 * This method gets all the distinct leaf nodes from Edges Table
	 * which are not parent Nodes
	 */
	public static Set<String>  getDisinctleafNodeNames(){

		String query = "SELECT category_name from node JOIN\n" +
				"  (SELECT  distinct `child_id`\n" +
				"FROM edges\n" +
				"WHERE `child_id` NOT IN (SELECT `parent_id` FROM edges )) AS leaf\n" +
				"ON leaf.child_id= node.node_id;";

		Set<String> leafNames= new HashSet<>();

		try(Connection connection = DatabaseConnection.getConnection();
			PreparedStatement ps = connection.prepareStatement(query)){
			//Execute the query
			ResultSet rs = ps.executeQuery();

			//All the nodes which are not parents and distinct are stored in LeafID array.
			while (rs.next()){
				leafNames.add(rs.getString("category_name") );
			}

		} catch(SQLException e){
			logger.error(e.getMessage());
		}

		return leafNames;
	}

	public void updateProminentNode(HashSet<Integer> prominentNodes){

		String query = "UPDATE node SET is_prominent=true WHERE node_id= ?";

		try(PreparedStatement preparedStatement = connection.prepareStatement(query)){

			for (Integer i : prominentNodes) {
				preparedStatement.setInt(1,i);
				preparedStatement.addBatch();
			}

			connection.setAutoCommit(false);
			preparedStatement.executeBatch();
			connection.setAutoCommit(true);

		} catch(SQLException e){
			logger.error(e.getMessage());
		}
	}

	public void updatePluralNode(int id, String head, boolean isPlural){
		String query;
		if(isPlural)
			query = "UPDATE node SET is_head_plural=true, head_of_name=? WHERE node_id= ? ;";
		else
			query = "UPDATE node SET head_of_name= ? WHERE node_id= ?;";

		try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
			preparedStatement.setString(1,head);
			preparedStatement.setInt(2, id);
			preparedStatement.execute();
		} catch(SQLException e){
			logger.error(e.getMessage());
		}
	}

	public void updateInterlanguageLinks(ArrayList<Node> nodes){

		String query = "UPDATE node SET score_interlang= ? WHERE node_id= ?;";

		try(PreparedStatement preparedStatement = connection.prepareStatement(query)){

			for(Node node:nodes){
				preparedStatement.setInt(1,node.getInterLangScore());
				preparedStatement.setInt(2,node.getNodeId());
				preparedStatement.addBatch();
			}
			connection.setAutoCommit(false);
			preparedStatement.executeBatch();
			connection.setAutoCommit(true);

		} catch(SQLException e){
			logger.error(e.getMessage());
		}
	}

	public static Set<String>  getDisinctheads(){

		String query =  "SELECT  distinct `head_of_name` FROM node WHERE  `is_prominent` = true AND `is_head_plural`=true AND `score_interlang` > 2";

		Set<String> heads= new HashSet<>();

		try(Connection connection = DatabaseConnection.getConnection();
			PreparedStatement ps = connection.prepareStatement(query)){
			//Execute the query
			ResultSet rs = ps.executeQuery();

			while (rs.next()){
				heads.add(rs.getString("head_of_name") );
			}

		} catch(SQLException e){
			logger.error(e.getMessage());
		}
		return heads;

	}

	public static Set<String>  getCategoriesByHead(String head){

		String query =  "SELECT  category_name FROM node where `head_of_name` = ? and is_prominent=1;";

		Set<String> categories= new HashSet<>();

		try(Connection connection = DatabaseConnection.getConnection();
			PreparedStatement ps = connection.prepareStatement(query)){
			ps.setString(1,head);
			ResultSet rs = ps.executeQuery();

			while (rs.next()){
				categories.add(rs.getString("category_name") );
			}

		} catch(SQLException e){
			logger.error(e.getMessage());
		}
		return categories;
	}

}