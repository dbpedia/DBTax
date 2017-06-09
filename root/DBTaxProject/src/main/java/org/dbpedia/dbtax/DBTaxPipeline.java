package org.dbpedia.dbtax;

import org.dbpedia.dbtax.database.ClassesIdenification;
import org.dbpedia.dbtax.database.LeafExtractionDB;

public class DBTaxPipeline {

	public static void main(String[] args){
		System.out.println("DBTax:: hello world");
		//Stage 1: Extract Leaf Nodes
		LeafExtractionDB.extractLeaves();
		System.out.println("DBTax:: Stage 1 is completed.");
		
		//Stage 2: Find Prominent Nodes
		NodeUtils.findProminentNodes();
		
		// Stage 2 B: NLP for is a relations
		ClassesIdenification.findPlural();
		
		System.out.println("End of World !!");
	}
	
}
