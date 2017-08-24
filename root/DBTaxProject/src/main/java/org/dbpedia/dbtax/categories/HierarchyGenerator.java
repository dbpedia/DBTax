package org.dbpedia.dbtax.categories;

import org.dbpedia.dbtax.database.DatabaseConnection;
import org.dbpedia.dbtax.database.InstancesDB;
import org.dbpedia.dbtax.database.ProminentNodeDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 6/17/14 4:33 PM
 */
public class HierarchyGenerator {

    private HierarchyGenerator() {

    }

    private static Logger logger = LoggerFactory.getLogger(HierarchyGenerator.class);

    private static String normalizeName(String original) {
        if (original == null)
            return original;
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    public static void generateHierarchy() {

        //To Do: Change to uniquegraph.paths to improve performance
        Map<String, Node> nodeMap = generateNodeMap("graph.paths");


        Node contentNode = new Node("Content");


        ///* put all parentless nodes under content *//*
        long nodeCount = 0;
        long parentlessNodes = 0;
        long childlessNodes = 0;
        for (Node node : nodeMap.values()) {
            if (node.getParent() == null) {
                contentNode.addChildren(node);
                parentlessNodes++;
            }

            long childCount = node.getChildren().size();

            nodeCount += childCount;
            if (childCount == 0) {
                childlessNodes++;
            }
        }
        storeHeads(nodeMap);

        logger.info("Total distinct nodes: " + nodeMap.size());
        logger.info("Total parentless nodes: " + parentlessNodes);
        logger.info("Total childless nodes: " + childlessNodes);
        logger.info("Total child count: " + nodeCount);


        Set<Node> latestLevelCategories = new HashSet<>();
        List<Relation> hierarchy = new LinkedList<>();


        latestLevelCategories.add(contentNode);
        int level = 0;

        while (true) {
            logger.info("Level: " + level + " contains: " + latestLevelCategories.size() + " nodes");
            Set<Node> currentLevel = new HashSet<>();

            int levelSkippedNodes = 0;
            int levelAddedRelations = 0;
            for (Node parentNode : latestLevelCategories) {
                parentNode.setLevel(level);

                int skippedNodes = 0;
                int addedRelations = 0;
                for (Node childNode : parentNode.getChildren()) {
                    if (childNode.hasAssignedLevel()) { // means already assigned level so skip
                        skippedNodes++;

                    } else {

                        currentLevel.add(childNode);
                        Relation relation = new Relation(parentNode.getName(), childNode.getName());
                        hierarchy.add(relation);
                        addedRelations++;
                    }

                }
                //
                levelAddedRelations += addedRelations;
                levelSkippedNodes += skippedNodes;
            }
            logger.info("\tAdded " + levelAddedRelations + " Skipped " + levelSkippedNodes);

            if (currentLevel.isEmpty()) {
                break;
            }

            latestLevelCategories = currentLevel;
            level++;


        }

        logger.info("New hierarchy contains : " + hierarchy.size());

        long unussignedNodesCount = 0;
        for (Node node : nodeMap.values()) {
            if (node.getLevel() < 0) {
                unussignedNodesCount++;
            }
        }

        logger.info("Total unussigned nodes: " + unussignedNodesCount);

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("ss.distinct.tsv"), "UTF8"))) {

            Collections.sort(hierarchy);
            for (Relation rel : hierarchy) {
                writer.write(rel.getParent() + "\t" + rel.getChild() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

    }

    public static Map<String, Node> generateNodeMap(String filename) {
        Map<String, Node> nodeMap = new HashMap<>();

        String line = null;
        long instancesSkipped = 0;

        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
             Connection connection = DatabaseConnection.getConnection()) {

            InstancesDB instancesDB = new InstancesDB(connection);
            ProminentNodeDB prominentNodeDB = new ProminentNodeDB(connection);

            int count = 0;
            /* Fill the existing tree */
            while ((line = in.readLine()) != null) {
                count++;
                String[] parts = line.split("\t");
                if (parts.length != 2) {
                    logger.info("Invalid row with parts != 2");
                }
                String parentName = normalizeName(parts[0].trim());
                String childName = normalizeName(parts[1].trim());

                if ("Null".equals(parentName) || "Null".equals(childName)) {
                    continue;
                }

                if (!prominentNodeDB.isProminent(parentName) || !prominentNodeDB.isProminent(childName)) {
                    continue;
                }

                if (parentName.toLowerCase().contains("wikidata") || childName.toLowerCase().contains("wikidata")) {
                    continue;
                }

                if (Character.isDigit(parentName.charAt(0)) || Character.isDigit(childName.charAt(0))) {
                    continue;
                }
                if (instancesDB.isAnInstance(parentName.toLowerCase(), childName.toLowerCase())) {
                    instancesSkipped++;
                    continue;
                }

                Node parentNode = nodeMap.get(parentName);
                Node childNode = nodeMap.get(childName);
                if (childNode == null) {
                    childNode = new Node(childName);
                    nodeMap.put(childName, childNode);
                }

                if (parentName.equals(childName)) {
                    continue;
                }
                if (parentName.equals("Category") || parentName.equals("Categories")) {
                    continue;
                }

                if (parentNode == null) {
                    parentNode = new Node(parentName);
                    nodeMap.put(parentName, parentNode);
                }

                parentNode.addChildren(childNode);
            }
        } catch (SQLException | IOException e) {
            logger.error(e.getMessage());
        }

        logger.info("Skipped a total of " + instancesSkipped + " relations containing instances");
        return nodeMap;
    }

    private static void storeHeads(Map<String, Node> nodeMap) {
        Set<String> heads = new HashSet<>();

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("heads"), "UTF8"))) {
            for (String key : nodeMap.keySet()) {
                writer.write(key + "\n");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

}