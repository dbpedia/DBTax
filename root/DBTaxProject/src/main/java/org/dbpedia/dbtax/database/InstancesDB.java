package org.dbpedia.dbtax.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

/**
 * Created by shashank on 22/8/17.
 */
public class InstancesDB {

    private static Logger logger = LoggerFactory.getLogger(InstancesDB.class);
    private Connection connection;

    public InstancesDB(Connection con){
        connection = con;
    }

    public void insertInstances(Set<String> instances){

        String query = "INSERT IGNORE INTO instances(instance) VALUE (?);";

        try(PreparedStatement ps =connection.prepareStatement(query)){
            for(String instance: instances){
                ps.setString(1, instance);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch(SQLException e){
            logger.error(e.getMessage());
        }
    }


    public boolean isAnInstance(String parent,String child){

        String query =  "select * from instances where instance in (?,?);";

        try(PreparedStatement ps = connection.prepareStatement(query)){

            ps.setString(1,parent);
            ps.setString(2,child);
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                return true;
            }

        } catch(SQLException e){
            logger.error(e.getMessage());
        }
        logger.debug("Instance not found: "+ parent+" "+ child);
        return false;
    }

}