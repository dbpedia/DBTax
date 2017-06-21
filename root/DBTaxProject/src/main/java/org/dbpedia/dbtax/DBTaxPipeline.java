package org.dbpedia.dbtax;


import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import org.dbpedia.dbtax.database.ClassesIdenification;
import org.dbpedia.dbtax.database.InterLanguageLinksExtraction;
import org.dbpedia.dbtax.database.LeafExtractionDB;

public class DBTaxPipeline {

    private static final Logger logger = Logger.getLogger(DBTaxPipeline.class);

	public static void main(String[] args){
        PropertyConfigurator.configure("src/main/Resources/Log4j.properties");

        logger.info("Hello World!");

		//Stage 1: Extract Leaf Nodes
		LeafExtractionDB.extractLeaves();
		logger.info("Leaf Extraction is completed.");
		
		//Stage 2: Find Prominent Nodes
		NodeUtils.findProminentNodes();
		logger.info("Prominent Node discovery algorithm is ran.");
		
		// Stage 2 B: NLP for is a relations
		ClassesIdenification.findPlural();
		logger.info("Finding plural is completed.");
		
		// Stage 2 C: Interlanguage links as weights
		InterLanguageLinksExtraction.findInterlanguageLinks();
		logger.info("Calculated inter language links score");
		
		logger.info("End of World !!");
	}
	
}