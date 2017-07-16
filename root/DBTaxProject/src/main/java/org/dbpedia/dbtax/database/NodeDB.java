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
			logger.error(e.getMessage());
		}
	}

	public static void insertNode(Set<Node> nodeMap){

		String query = "INSERT IGNORE INTO node(node_id,category_name,is_leaf,is_prominent,score_edit_histo) VALUES (?,?,1,0,0);";

		try(Connection connection = DatabaseConnection.getConnection();
			PreparedStatement ps =connection.prepareStatement(query)){
			for(Node node: nodeMap){
				ps.setInt( 1, node.getNodeId());
				ps.setString( 2, node.getCategoryName());
				ps.addBatch();
			}
			connection.setAutoCommit(false);
			ps.executeBatch();
			connection.setAutoCommit(true);
			connection.close();
		} catch(SQLException e){
			logger.error(e.getMessage());
		}
	}

	/*
	 * This method gets all the distinct leaf nodes from Edges Table
	 * which are not parent Nodes
	 */
	public static ArrayList<Integer>  getDisinctleafNodes(){

		String query =  "SELECT  distinct `child_id` FROM edges WHERE `child_id` NOT IN (SELECT `parent_id` FROM edges );";

        ArrayList<Integer> leafId= new  ArrayList<>();
		try(Connection connection = DatabaseConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(query)){
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

	public static void updateProminentNode(HashSet<Integer> prominentNodes){


		String query = "UPDATE node SET is_prominent=true WHERE node_id= ?";

		try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)){
		
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

	public static void updatePluralNode(int id, String head, boolean isPlural){
		String query;
		if(isPlural)
			query = "UPDATE node SET is_head_plural=true, head_of_name=? WHERE node_id= ? ;";
		else 
			query = "UPDATE node SET head_of_name= ? WHERE node_id= ?;";

		try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)){
			preparedStatement.setString(1,head);
			preparedStatement.setInt(2, id);
			preparedStatement.execute();
		} catch(SQLException e){
            logger.error(e.getMessage());
		} 
	}

    public static void updateInterlanguageLinks(ArrayList<Node> nodes){

        String query = "UPDATE node SET score_interlang= ? WHERE node_id= ?;";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)){

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

		String query =  "SELECT  category_name FROM node where `head_of_name` = ? ;";

		Set<String> categories= new HashSet<String>();

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