/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



/** 
 * 
 *      Date             Author          Changes 
 *      Aug 31, 2013     Kasun Perera    Created   
 * 
 */ 

package org.dbpedia.dbtax.database;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



/**
 * Communications with the Language Links table
 * 
 */
public class InterLanguageLinksDB {

	public static int getLanguageLinksCount(int pageId){
		
		Connection connection = DatabaseConnection.getConnection();
		
		PreparedStatement ps = null;
		ResultSet rs = null;

		String query =  "select count(*) from langlinks where ll_from=?";

		try{
			ps = connection.prepareStatement(query);
			ps.setInt( 1, pageId);

			rs = ps.executeQuery();
			int nodeId=0;
			while (rs.next())
			{
				nodeId=rs.getInt(1);
			}
			
			ps.close();
			connection.close();
			return nodeId;
		} catch(SQLException e){
			e.printStackTrace();
			return 0;
		}
	}
}