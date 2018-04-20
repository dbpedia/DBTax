# DBTax

## Unsupervised Learning for DBpedia Taxonomy
In the digital era, Wikipedia represents a comprehensive cross-domain source of knowledge with millions of contributors. The DBpedia project tries to extract structured information from Wikipedia and transform it into RDF. This helps anyone to ask sophisticated queries against Wikipedia.

The main classification system of DBpedia depends on human curation, which causes it to lack coverage, resulting in a large amount of untyped resources. DBTax provides an unsupervised approach that automatically learns a taxonomy from the Wikipedia category system and extensively assigns types to DBpedia entities, through the combination of several NLP and interdisciplinary techniques. 
It provides a robust backbone for DBpedia knowledge and has the benefit of being easy to understand for end users.

## Requirements
* Java 1.8
* MySQL 5.5.39 or above

## Installation
Follow these instructions step by step to set up the project locally on your machine.

### Database setup
1. Create a database, say "dbtax" for purpose of this project. 
2. Then in the created database, download the latest wikipedia dumps of the following tables : Category, LangLinks, CategoryLinks, Page.
You may use the scripts in root/setup/downloadWikidumps to download the data. Please make sure you have atleast 80 GB of free space if you want to import the dumps.
3. Run the /root/setup/setup.sql script to create required tables.
4. Update the database credentials in /root/DBTaxProject/db.properties file.
5. You would need fill in instances dataset using latest DBpedia Datasets (Labels, Redirects, Instances). Either run /root/DBTaxProject/src/main/java/org/dbpedia/dbtax/utils/InstancesGeneration.java file or load the dump.

### Running the program
Run the /root/DBTaxProject/src/main/java/org/dbpedia/dbtax/DBTaxPipeline.java file. 

## License
This project is This project is licensed under the Apache License 2.0- see the [LICENSE](https://github.com/dbpedia/DBTax/blob/master/LICENSE) file for details.
