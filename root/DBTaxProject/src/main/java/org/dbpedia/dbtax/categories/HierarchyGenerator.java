package org.dbpedia.dbtax.categories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.dbpedia.dbtax.database.DatabaseConnection;
import org.dbpedia.dbtax.utils.DBpediaInstances;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 6/17/14 4:33 PM
 * This code corresponds to Class Removal Algorithm 2 in paper.
 * 
 */

public class HierarchyGenerator {

	private static final Logger logger = LoggerFactory.getLogger(HierarchyGenerator.class);
    private static final Node contentNode = new Node("Content");
    private static long instancesSkipped = 0;

    private HierarchyGenerator(){ }

	public static List<Relation> mainFunc() {

        //Instances from the files
		Set<String> instances = DBpediaInstances.generateInstances();

		//Pruning of Instances happens here
		Map<String, Node> nodeMap = generateNodeMap(instances);

		Set<Node> latestLevelCategories = new HashSet<>();
		List<Relation> hierarchy = new LinkedList<>();

        addParentToParentlessNodes(nodeMap);

        //Starting with content nodes
		latestLevelCategories.add(contentNode);
		int level = 0;

		while (true) {
			logger.info("Level: %d contains: %d nodes", level,latestLevelCategories.size());

			Set<Node> currentLevel = new HashSet<>();

			int levelSkippedNodes = 0;
			int levelAddedRelations = 0;

			for (Node parentNode: latestLevelCategories) {
				parentNode.setLevel(level);

				int skippedNodes = 0;
				int addedRelations = 0;
				for (Node childNode: parentNode.getChildren()) {
					if (childNode.hasAssignedLevel()) { // means already assigned level so skip
						skippedNodes++;
					}
					else {

						currentLevel.add(childNode);
						Relation relation = new Relation(parentNode.getName(), childNode.getName());
						hierarchy.add(relation);
						addedRelations++;
					}

				}
				levelAddedRelations += addedRelations;
				levelSkippedNodes += skippedNodes;
			}
			logger.info("\tAdded %d Skipped %d",levelAddedRelations ,levelSkippedNodes );

			if (currentLevel.isEmpty()) {
				break;
			}

			latestLevelCategories = currentLevel;
			level++;
		}

		logger.info("New hierarchy contains : %d" , hierarchy.size());

		countUnassignedNodes(nodeMap);

		return hierarchy;
	}

	private static void countUnassignedNodes(Map<String, Node> nodeMap){

        long unussignedNodesCount = 0;
        for (Node node: nodeMap.values()) {
            if (node.getLevel() <0) {
                unussignedNodesCount++;
            }
        }

        logger.info("Total unussigned nodes: %d" , unussignedNodesCount);

    }
	private static void addParentToParentlessNodes(Map<String, Node> nodeMap){

        long nodeCount = 0;
        long parentlessNodes = 0;
        long childlessNodes = 0;

        for (Node node: nodeMap.values()) {

		/* put all parentless nodes under content */
            if (node.getParent() == null ) {
                contentNode.addChildren(node);
                parentlessNodes++;
            }

            long childCount = node.getChildren().size();

            nodeCount += childCount;
            if (childCount == 0) {
                childlessNodes++;
            }
        }
        logger.info("Total distinct nodes: %d", nodeMap.size());
        logger.info("Total parentless nodes: %d", parentlessNodes);
        logger.info("Total childless nodes: %d", childlessNodes);
        logger.info("Total child count: %d ", nodeCount);

    }

    private static String normalizeName(String original) {
        if(original.length() == 0)
            return original;
        return original.trim().substring(0, 1).toUpperCase() + original.substring(1);
    }

	public static Map<String, Node> generateNodeMap( Set<String> instances) {

		Map<String, Node> nodeMap = new HashMap<>();

		String query = "SELECT pnode.head_of_name parent_name, cnode.head_of_name child_name "
				+" FROM edges "
				+" JOIN node pnode ON edges.parent_id = pnode.node_id "
				+" JOIN node cnode ON edges.child_id = cnode.node_id"
				+" WHERE cnode.category_name is NOT NULL"
				+ " AND pnode.category_name is NOT NULL;";


		try(Connection connection = DatabaseConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(query)) {

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				String parentName  = normalizeName(rs.getString("parent_name"));
				String childName   = normalizeName(rs.getString("child_name"));

				if(!isInstance(parentName, childName, instances))
				    continue;

				Node parentNode = nodeMap.get(parentName);
				Node childNode  = nodeMap.get(childName);

				if (childNode == null) {
					childNode = new Node(childName);
					nodeMap.put(childName, childNode);
				}

				if(isChildInvalid(parentName, childName))
				    continue;

				if (parentNode == null ) {
					parentNode = new Node(parentName);
					nodeMap.put(parentName, parentNode);
				}

				parentNode.addChildren(childNode);
			}
		} catch (SQLException e) {
            logger.error(e.getMessage());
		}

		return nodeMap;
	}

	private static boolean isInstance(String parentName, String childName, Set<String> instances){

        if (parentName.toLowerCase().contains("wikidata") || childName.toLowerCase().contains("wikidata")) {
            return false;
        }

        if (Character.isDigit(parentName.charAt(0)) || Character.isDigit(childName.charAt(0))) {
            return false;
        }

        if (instances.contains(parentName.toLowerCase()) || instances.contains(childName.toLowerCase())) {
            instancesSkipped++;
            return false;
        }

        logger.debug("Skipped a total of %d relations containing instances",instancesSkipped);

        return true;
    }

    private static boolean isChildInvalid(String parentName, String childName){
	    return (parentName.equals(childName)||"Category".equals(parentName) || "Categories".equals(parentName));
    }
}