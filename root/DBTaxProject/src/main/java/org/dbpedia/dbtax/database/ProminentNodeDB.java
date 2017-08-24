package org.dbpedia.dbtax.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by shashank on 23/8/17.
 */
public class ProminentNodeDB {
    private Logger logger = LoggerFactory.getLogger(ProminentNodeDB.class);
    private Connection connection;
    public ProminentNodeDB(Connection connection){
        this.connection = connection;
    }

    public boolean isProminent(String node){

        String query =  "select * from pnode where head_of_name = ?;";

        try(PreparedStatement ps = connection.prepareStatement(query)){
            ps.setString(1,node);
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                return true;
            }

        } catch(SQLException e){
            logger.error(e.getMessage());
        }
        return false;
    }
}
