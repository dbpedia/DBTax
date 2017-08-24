package org.dbpedia.dbtax.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.dbpedia.dbtax.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * This class has most of the methods that deal with Page table in Database. 
*/

public class PageDB {

	private static final Logger logger= LoggerFactory.getLogger(PageDB.class);
    private Connection connection;

    public PageDB(Connection connection) {
        if(this.connection==null)
            this.connection = connection;
    }

    public int getPageId(String catPageTitle){
		
		int resultId = -1;

		String query = "SELECT page_id FROM `page` WHERE `page_title` = ? AND page_namespace=14";

		try(PreparedStatement ps =	connection.prepareStatement( query )){
		
			ps.setString(1, catPageTitle);
			
			ResultSet rs = ps.executeQuery();

			while( rs.next() ){
				resultId= rs.getInt("page_id");
			}
			
		} catch ( SQLException e ){
			logger.error(e.getMessage());
		}
		return resultId;
	}
	
	public static List<Page>  getPagesByCategory(String category){

		String query = "select page_id, page_namespace, page_title from page where page_id =? ;";

		ResultSet rs;
		List<Page> pages= new ArrayList<>();

		try(Connection connection = DatabaseConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(query)){

			CategoryLinksDB categoryLinksDB = new CategoryLinksDB(connection);
			List<Integer> pageIds = categoryLinksDB.getPageIdOfCategory(category);

			for(Integer pageId: pageIds){
				ps.setInt(1, pageId);
				//Execute the query
				rs = ps.executeQuery();
				while (rs.next()){
					Page page = new Page(rs.getString("page_title"), rs.getInt("page_namespace"));
					pages.add(page);
				}
			}
		} catch(SQLException e){
			logger.error(e.getMessage());
		}	
		return pages;
	}
}