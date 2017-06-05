package org.dbpedia.dbtax.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;		

/*
 * This class establishes the Database Connection. 
*/

public class DatabaseConnection {
	
	private static Connection con = null;
	
	public static Connection getConnection() {	
		if(con!=null){
			return con;
		}
		
		Connection con = null;
		Properties props= new Properties();
		FileInputStream file = null;  
		
		try {
			file = new FileInputStream("db.properties");
			props.load(file);

			// load the Driver Class
			Class.forName("com.mysql.jdbc.Driver").newInstance();

			// create the connection now	
			con = DriverManager.getConnection(props.getProperty("DB_URL"),
					props.getProperty("DB_USERNAME"),
					props.getProperty("DB_PASSWORD"));

		} catch (IOException | ClassNotFoundException | SQLException | InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return con;
	}
}