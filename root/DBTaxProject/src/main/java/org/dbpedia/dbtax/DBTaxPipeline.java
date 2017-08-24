package org.dbpedia.dbtax;

import org.dbpedia.dbtax.categories.HierarchyGenerator;
import org.dbpedia.dbtax.categories.LeafToRootGraphGeneneration;
import org.dbpedia.dbtax.categories.OWLGenerator;
import org.dbpedia.dbtax.database.InterLanguageLinksExtraction;
import org.dbpedia.dbtax.database.LeafExtractionDB;

import org.dbpedia.dbtax.database.PluralIdenification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBTaxPipeline {

    private static final Logger logger = LoggerFactory.getLogger(DBTaxPipeline.class);

    public static void main(String[] args) {

        logger.info("Hello World");

        //Stage 1: Extract Leaf Nodes
        LeafExtractionDB.extractLeaves();
        logger.info("Stage 1: Leaf Extraction step is completed.");

		//Stage 2: Find Prominent Nodes
		NodeUtils.findProminentNodes();
		logger.info("Stage 2A: Prominent Node discovery algorithm is ran.");

		// Stage 2 B: NLP for is a relations
		PluralIdenification.findPlural();
		logger.info("Stage 2B: Finding plural is completed.");

		// Stage 2 C: Interlanguage links as weights
		InterLanguageLinksExtraction.findInterlanguageLinks();
		logger.info("Stage 2C: Calculated inter language links score");

		//Stage 3: Hierarchy generator, Cycle Removal and pruning instances
        LeafToRootGraphGeneneration.generateGraph();
        logger.info("Stage 3: Graph is generated.");

        HierarchyGenerator.generateHierarchy();
        logger.info("Stage 3: Hierarchy generation, Cycle removal and pruning instances step done");

        OWLGenerator.generateTBox();
        logger.info("Stage 3: Generation of T-Box labels. File named: T-Box.ttl");

        //Stage 4: PageTypeAssignment
        PageTypeAssignment aBox = new PageTypeAssignment();
        aBox.assignPageTypes();
		logger.info("Stage4: Page types are assigned. File named: A-BoxHeads.ttl");

		logger.info("End of World !!");
    }
}