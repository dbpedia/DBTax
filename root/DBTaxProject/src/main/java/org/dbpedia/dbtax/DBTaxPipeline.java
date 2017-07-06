package org.dbpedia.dbtax;

import org.dbpedia.dbtax.database.PluralIdenification;
import org.dbpedia.dbtax.database.InterLanguageLinksExtraction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBTaxPipeline {

    private static final Logger logger = LoggerFactory.getLogger(DBTaxPipeline.class);

    public static void main(String[] args) {
/*        try(Connection con = DatabaseConnection.getConnection()){
            ScriptRunner runner = new ScriptRunner(con,true, true);
            runner.runScript(new BufferedReader(new FileReader("sample.sql")));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        logger.info("Hello World");


        //Stage 1: Extract Leaf Nodes
//        LeafExtractionDB.extractLeaves();
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
		
		logger.info("End of World !!");
    }
}