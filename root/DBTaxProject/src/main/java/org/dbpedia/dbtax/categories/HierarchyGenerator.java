package org.dbpedia.dbtax.categories;

import org.dbpedia.dbtax.database.DatabaseConnection;
import org.dbpedia.dbtax.database.InstancesDB;
import org.dbpedia.dbtax.database.PNodeDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static org.dbpedia.dbtax.categories.Utils.normalizeName;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 6/17/14 4:33 PM
 */
public class HierarchyGenerator {

    private static Logger logger = LoggerFactory.getLogger(HierarchyGenerator.class);

    private HierarchyGenerator() {

    }

    public static void generateHierarchy() {

        //To Do: Change to uniquegraph.paths to improve performance
        Map<String, Node> nodeMap = generateNodeMap("graph.paths");

        Node contentNode = new Node("Content");
        Set<Node> latestLevelCategories = new HashSet<>();
        List<Relation> hierarchy = new LinkedList<>();

        nodeMap = addContentNode(nodeMap, contentNode);

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

        //Count the number of nodes which are unassigned.
        checkUnassigned(nodeMap);

        //Store the output hierarchy in TSV file
        writeOutput(hierarchy);
    }

    private static void checkUnassigned(Map<String, Node> nodeMap) {
        long unassignedNodesCount = 0;
        for (Node node : nodeMap.values()) {
            if (node.getLevel() < 0) {
                unassignedNodesCount++;
            }
        }
        logger.info("Total unassigned nodes: " + unassignedNodesCount);

    }

    private static void writeOutput(List<Relation> hierarchy) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("distinct.tsv"), "UTF8"))) {

            Collections.sort(hierarchy);
            for (Relation rel : hierarchy) {
                writer.write(rel.getParent() + "\t" + rel.getChild() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        logger.info("Output: distinct.tsv");
    }

    private static Map<String,Node> addContentNode(Map<String, Node> nodeMap, Node contentNode) {

        /* put all parentless nodes under content */
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

        return nodeMap;
    }

    public static Map<String, Node> generateNodeMap(String filename) {
        Map<String, Node> nodeMap = new HashMap<>();

        String line;

        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
             Connection connection = DatabaseConnection.getConnection()) {

            InstancesDB instancesDB = new InstancesDB(connection);
            PNodeDB prominentNodeDB = new PNodeDB(connection);

            /* Fill the existing tree */
            while ((line = in.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length != 2) {
                    logger.error("Invalid row with parts != 2");
                }
                String parentName = normalizeName(parts[0].trim());
                String childName = normalizeName(parts[1].trim());

                if(prilimChecksFailed(parentName,childName, prominentNodeDB, instancesDB)){
                    continue;
                }

                Node parentNode = nodeMap.get(parentName);
                Node childNode = nodeMap.get(childName);
                if (childNode == null) {
                    childNode = new Node(childName);
                    nodeMap.put(childName, childNode);
                }

                if(parentChecksFailed(parentName,childName))
                    continue;

                if (parentNode == null) {
                    parentNode = new Node(parentName);
                    nodeMap.put(parentName, parentNode);
                }
                parentNode.addChildren(childNode);
            }
        } catch (SQLException | IOException e) {
            logger.error(e.getMessage());
        }


        return nodeMap;
    }

    private static boolean parentChecksFailed(String parentName, String childName) {
        return ( parentName.equals(childName) || "Category".equals(parentName) || "Categories".equals(parentName));
    }

    private static boolean prilimChecksFailed(String parentName, String childName, PNodeDB prominentNodeDB, InstancesDB instancesDB) {
        if ("Null".equals(parentName) || "Null".equals(childName)) {
            return true;
        }

        if (!prominentNodeDB.isProminent(parentName) || !prominentNodeDB.isProminent(childName)) {
            return true;
        }

        if (parentName.toLowerCase().contains("wikidata") || childName.toLowerCase().contains("wikidata")) {
            return true;
        }

        if (Character.isDigit(parentName.charAt(0)) || Character.isDigit(childName.charAt(0))) {
            return true;
        }
        if (instancesDB.isAnInstance(parentName.toLowerCase(), childName.toLowerCase())) {
            return true;
        }

        return false;
    }

    private static void storeHeads(Map<String, Node> nodeMap) {

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("heads"), "UTF8"))) {
            for (String key : nodeMap.keySet()) {
                writer.write(key + "\n");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

}