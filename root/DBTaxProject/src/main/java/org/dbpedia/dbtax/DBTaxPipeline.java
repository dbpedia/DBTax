package org.dbpedia.dbtax;

import org.dbpedia.dbtax.database.ClassesIdenification;
import org.dbpedia.dbtax.database.InterLanguageLinksExtraction;
import org.dbpedia.dbtax.database.LeafExtractionDB;

public class DBTaxPipeline {

	public static void main(String[] args){

		System.out.println("DBTax:: hello world");
		//Stage 1: Extract Leaf Nodes
		LeafExtractionDB.extractLeaves();
		System.out.println("DBTax:: Stage 1 is completed.");
		
		//Stage 2: Find Prominent Nodes
		NodeUtils.findProminentNodes();
		System.out.println("DBTax:: Stage 2A is completed.");
		
		// Stage 2 B: NLP for is a relations
		ClassesIdenification.findPlural();
		System.out.println("DBTax:: Stage 2B is completed.");
		
		// Stage 2 C: Interlanguage links as weights
		InterLanguageLinksExtraction.findInterlanguageLinks();
		System.out.println("DBTax:: Stage 2C is completed.");
		
		System.out.println("End of World !!");
	}
	
}