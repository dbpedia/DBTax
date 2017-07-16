package org.dbpedia.dbtax;

import org.dbpedia.dbtax.categories.LeafToRoot;
import org.dbpedia.dbtax.categories.OWLGenerator;
import org.dbpedia.dbtax.database.LeafExtractionDB;
import org.dbpedia.dbtax.database.PluralIdenification;
import org.dbpedia.dbtax.database.InterLanguageLinksExtraction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBTaxPipeline {

    private static final Logger logger = LoggerFactory.getLogger(DBTaxPipeline.class);

    public static void main(String[] args) {

        logger.info("Hello World");

        //Stage 1: Extract Leaf Nodes
        LeafExtractionDB.extractLeaves();
        logger.info("Leaf Extraction is completed.");

		//Stage 2: Find Prominent Nodes
		NodeUtils.findProminentNodes();
		logger.info("Prominent Node discovery algorithm is ran.");
		
		// Stage 2 B: NLP for is a relations
		PluralIdenification.findPlural();
		logger.info("Finding plural is completed.");
		
		// Stage 2 C: Interlanguage links as weights
		InterLanguageLinksExtraction.findInterlanguageLinks();
		logger.info("Calculated inter language links score");

		//Stage 3: Hierarchy generator, Cycle Removal and pruning instances
        LeafToRoot.cycleRemoval();
        logger.info("Hierarchy generation, Cycle removal and pruning instances step done");

        OWLGenerator.generateTBox();
        logger.info("Generation of T-Box labels");

        //Stage 4: PageTypeAssignment
        PageTypeAssignment aBox = new PageTypeAssignment();
        aBox.assignPageTypes();
		logger.info("Page types are assigned and in file named pageTypeAssignment.ttl");

		logger.info("End of World !!");
    }
}