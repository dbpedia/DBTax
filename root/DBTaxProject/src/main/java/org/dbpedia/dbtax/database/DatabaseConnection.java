package org.dbpedia.dbtax.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;		

/*
 * This class establishes the Database Connection. 
*/

public class DatabaseConnection {

    private static Connection con = null;
    private static Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);

    private DatabaseConnection(){ }

    public static Connection getConnection() {
        if(con!=null){
            return con;
        }

        Connection con = null;
        Properties props= new Properties();

        try(FileInputStream file =new FileInputStream("db.properties")){
            props.load(file);

            // load the Driver Class
            Class.forName(props.getProperty("DB_DRIVER")).newInstance();

            // create the connection now
            con = DriverManager.getConnection(props.getProperty("DB_URL"),
                    props.getProperty("DB_USERNAME"),
                    props.getProperty("DB_PASSWORD"));
        } catch (IOException | ClassNotFoundException | SQLException | InstantiationException | IllegalAccessException e) {
            logger.error(e.getMessage());
        }

        return con;
    }

/*



    private DatabaseConnection(){ }

    public static Connection getConnection() {
        if(con!=null){
            return con;

        }

        Connection con = null;

        try{

            // load the Driver Class
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            // create the connection now
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbtax",
                    "shashank",
                    "");
        } catch ( ClassNotFoundException | SQLException | InstantiationException | IllegalAccessException e) {
            logger.error(e.getMessage());
        }

        return con;
    }

*/

   public static void main(String [] argc){
        DatabaseConnection.getConnection();

        System.out.print("done");
    }
}