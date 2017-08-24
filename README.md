# DBTax
DBTax project

## Requirements
* Java 1.8
* MySQL 5.5.39 or above

## Installation
Follow these instructions step by step to set up the project locally on your machine.

### Database setup
1. Create a database, say "dbtax" for purpose of this project. 
2. Then in the created database, download the latest wikipedia dumps of the following tables : Category, LangLinks, CategoryLinks, Page.

You may use the scripts in root/setup/downloadWikidumps to download the data. Please make sure you have atleast 80 GB of free space if you want to import the dumps.

3. Run the /root/setup/setup.sql to create required databases.

### Running instruction
Run the /root/DBTaxProject/src/main/java/org/dbpedia/dbtax/DBTaxPipeline.java file. 


## License
This project is This project is licensed under the Apache License 2.0- see the [LICENSE](https://github.com/dbpedia/DBTax/blob/master/LICENSE) file for details.
