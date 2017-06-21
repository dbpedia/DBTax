package org.dbpedia.dbtax.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dbpedia.dbtax.Page;

/*
 * This class has most of the methods that deal with Page table in Database. 
*/

public class PageDB {

	public static int getPageId(String catPageTitle){
		
		int resultId = -1;

		String query = "SELECT page_id FROM `page` WHERE `page_title` = ? AND page_namespace=14";
		
		ResultSet rs = null;

		try(Connection connection = DatabaseConnection.getConnection();
				PreparedStatement ps =	connection.prepareStatement( query )){
		
			ps.setString(1, catPageTitle);
			
			rs = ps.executeQuery();

			while( rs.next() ){
				resultId= rs.getInt("page_id");
			}
			
		} catch ( SQLException e ){
			e.printStackTrace();
		}
		return resultId;
	}
	
	public static List<Page>  getPagesByCategory(String category){

		String query = "select page_id, page_namespace, page_title from page where page_id in"
				+ "(select cl_from from categorylinks where cl_to =? )";

		ResultSet rs = null;

		List<Page> pages= new ArrayList<Page>();

		try(Connection connection = DatabaseConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(query)){
			ps.setString(1, category);
			//Execute the query
			rs = ps.executeQuery();


			while (rs.next()){
				Page page = new Page(rs.getString("page_title"), rs.getInt("page_namespace"));
				pages.add(page);
			}

		} catch(SQLException e){
			e.printStackTrace();
		}	
		return pages;
	}
}