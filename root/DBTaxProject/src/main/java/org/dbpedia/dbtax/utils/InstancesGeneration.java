package org.dbpedia.dbtax.utils;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.*;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.TDBLoader;

import org.dbpedia.dbtax.categories.Utils;
import org.dbpedia.dbtax.database.DatabaseConnection;
import org.dbpedia.dbtax.database.InstancesDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by shashank on 22/7/17.
 *
 * This file is used to generate instances.
 */
public class InstancesGeneration {
    private static Logger logger = LoggerFactory.getLogger(InstancesGeneration.class);

    private static final String INSTANCES_FILENAME = "instance_types_en.ttl";
    private static final String REDIRECTS_FILENAME = "redirects_en.ttl";
    private static final String LABELS_FILENAME= "labels_en.ttl";


    public static void generateInstances() throws IOException {
        generateInstancesByFile(INSTANCES_FILENAME);
        generateInstancesByFile(REDIRECTS_FILENAME);
        generateInstancesByFile(LABELS_FILENAME);
    }


    private static void generateInstancesByFile(String filename) throws IOException {

        String file ="/home/shashank/Downloads/" + filename;
        Dataset dataset = TDBFactory.createDataset("instancesDataset");
        Model model = dataset.getNamedModel("http://nameFile");
        TDBLoader.loadModel(model, file );

        StmtIterator iter = model.listStatements();

        Set<String> instances = new HashSet<>();

        try (Connection connection = DatabaseConnection.getConnection()){
            InstancesDB instancesDB = new InstancesDB(connection);
            while (iter.hasNext()) {
                Statement stmt = iter.nextStatement();  // get next statement
                Resource subject = stmt.getSubject();     // get the subject
                String temp = Utils.normalizeName(subject.getLocalName().trim());
                if(temp!=null && temp.length()!=0 && temp.indexOf('_')==-1)
                    instances.add(temp.toLowerCase());
                if(instances.size()>3000){
                    instancesDB.insertInstances(instances);
                    instances = new HashSet<>();
                }
            }
            model.close();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        logger.info("Loaded instances from "+ filename);
    }

    public static void main(String [] argv) throws IOException {
        generateInstances();
    }
}