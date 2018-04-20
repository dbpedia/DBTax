package org.dbpedia.dbtax.categories;

import org.dbpedia.dbtax.database.DatabaseConnection;
import org.dbpedia.dbtax.database.NodeDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shashank on 16/8/17.
 */
public class LeafToRootGraphGeneneration {

    private static Logger logger = LoggerFactory.getLogger(LeafToRootGraphGeneneration.class);
    private LeafToRootGraphGeneneration(){

    }

    public static void generateGraph(){
        try(Connection connection = DatabaseConnection.getConnection();
            FileOutputStream fout = new FileOutputStream( "graph.paths");
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fout, "UTF8"))){

            NodeDB nodeDB = new NodeDB(connection);

            List<Integer> leafNodes = nodeDB.getDisinctleafNodes();

            for (int leaf : leafNodes) {
                List<Integer> currentPath = new ArrayList<>();
                currentPath.add(leaf);
                int currentResource = leaf;
                int currentParent = nodeDB.getSingleParentNode(leaf);
                String child = nodeDB.getHeadOfNode(currentResource);
                String parent = nodeDB.getHeadOfNode(currentParent);
                writer.write(parent + "\t"+child+"\n");
                while (currentParent!=-1) {
                    currentPath.add(currentParent);
                    currentResource = currentParent;
                    currentParent = nodeDB.getSingleParentNode(currentResource);
                    if (currentPath.contains(currentParent)) {
                        String cyclePath = currentPath.toString();
                        logger.debug("Cycle when adding: %s", cyclePath);
                        break;
                    }
                    parent = nodeDB.getHeadOfNode(currentParent);
                    child = nodeDB.getHeadOfNode(currentResource);
                    if(parent!=null && child!=null)
                        writer.write(parent + "\t"+child+"\n");
                }
            }
            logger.info("Graph is generated and stored in graph.paths file.");
        } catch (IOException | SQLException e) {
            logger.error(e.getMessage());
        }
    }
}