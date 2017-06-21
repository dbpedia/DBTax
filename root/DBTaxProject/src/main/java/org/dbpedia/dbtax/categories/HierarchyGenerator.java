package org.dbpedia.dbtax.categories;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.dbpedia.dbtax.database.DatabaseConnection;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 6/17/14 4:33 PM
 * This code corresponds to Class Removal Algorithm 2 in paper.
 * 
 */

public class HierarchyGenerator {

	private static String normalizeName(String original) {
		if(original.length() == 0)
			return original;
		return original.substring(0, 1).toUpperCase() + original.substring(1);
	}

	public static List<Relation> mainFunc() {

		Set<String> instances = generateInstances();
		Map<String, Node> nodeMap = generateNodeMap(instances);


		Node contentNode = new Node("Content");


		/* put all parentless nodes under content */
		long nodeCount = 0;
		long parentlessNodes = 0;
		long childlessNodes = 0;
		for (Node node: nodeMap.values()) {
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
		System.out.println("Total distinct nodes: " + nodeMap.size());
		System.out.println("Total parentless nodes: " + parentlessNodes);
		System.out.println("Total childless nodes: " + childlessNodes);
		System.out.println("Total child count: " + nodeCount);


		Set<Node> latestLevelCategories = new HashSet<Node>();
		List<Relation> hierarchy = new LinkedList<Relation>();


		latestLevelCategories.add(contentNode);
		int level = 0;

		while (true) {
			System.out.println("Level: " + level + " contains: " + latestLevelCategories.size() + " nodes");
			Set<Node> currentLevel = new HashSet<Node>();

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
			System.out.println("\tAdded " + levelAddedRelations + " Skipped " + levelSkippedNodes );

			if (currentLevel.isEmpty()) {
				break;
			}

			latestLevelCategories = currentLevel;
			level++;


		}

		System.out.println("New hierarchy contains : " + hierarchy.size());

		long unussignedNodesCount = 0;
		for (Node node: nodeMap.values()) {
			if (node.getLevel() <0) {
				unussignedNodesCount++;
			}
		}

		System.out.println("Total unussigned nodes: " + unussignedNodesCount);

		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("test.distinct.tsv"), "UTF8"));
			Collections.sort(hierarchy);
			for (Relation rel: hierarchy) {
				writer.write(rel.getParent() + "\t" + rel.getChild() + "\n");
			}
			writer.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return hierarchy;
	}

	public static Map<String, Node> generateNodeMap( Set<String> instances) {
		Map<String, Node> nodeMap = new HashMap<String, Node>();

		String query = "SELECT pnode.category_name parent_name, cnode.category_name child_name "
				+" FROM edges "
				+" JOIN node pnode ON edges.parent_id = pnode.node_id "
				+" JOIN node cnode ON edges.child_id = cnode.node_id"
				+" WHERE cnode.category_name is NOT NULL"
				+ " AND pnode.category_name is NOT NULL;";
		long instancesSkipped = 0;
		ResultSet rs = null;
		try(Connection connection = DatabaseConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(query)) {   

			rs = ps.executeQuery();

			while (rs.next()) {
				String parentName  = normalizeName(rs.getString("parent_name").trim());
				String childName   = normalizeName(rs.getString("child_name").trim());

				if (parentName.toLowerCase().contains("wikidata") || childName.toLowerCase().contains("wikidata")) {
					continue;
				}

				if (Character.isDigit(parentName.charAt(0)) || Character.isDigit(childName.charAt(0))) {
					continue;
				}

				if (instances.contains(parentName.toLowerCase()) || instances.contains(childName.toLowerCase())) {
					instancesSkipped++;
					continue;
				}

				Node parentNode = nodeMap.get(parentName);
				Node childNode  = nodeMap.get(childName);
				if (childNode == null) {
					childNode = new Node(childName);
					nodeMap.put(childName, childNode);
				}

				if (parentName.equals(childName)) {
					continue;
				}

				if ("Category".equals(parentName) || "Categories".equals(parentName)) {
					continue;
				}

				if (parentNode == null ) {
					parentNode = new Node(parentName);
					nodeMap.put(parentName, parentNode);
				}

				parentNode.addChildren(childNode);
			}
		} catch (SQLException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}

		System.out.println("Skipped a total of " + instancesSkipped + " relations containing instances");
		return nodeMap;
	}

	public static Set<String> generateInstances() {

		Set<String> instances = new HashSet();
		String query = "select distinct category_name from node;";
		ResultSet rs = null;

		try(Connection connection = DatabaseConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(query)) {

			rs = ps.executeQuery();
			while ( rs.next() ){
				instances.add(rs.getString("category_name"));
			}

		}catch(SQLException e) {
			e.printStackTrace();
		}		

		return instances;
	}
}