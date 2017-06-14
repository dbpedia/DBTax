#!/bin/bash  
source config.sh
echo "DBTax Project: Hello, this program is going to download required dumps from wikipedia."

prefix="metawiki-latest-"
declare -a arr=("category" "categorylinks" "langlinks" "page")
# declare -a arr=("page" "category" "categorylinks" "langlinks" "change_tag" "externallinks" "flaggedpages" "flaggedrevs" "geo_tags" "image" "imagelinks" "iwlinks" "langlinks" "page_props" "-page_restrictions" "redirect" "site_stats" "sites" "templatelinks" "user_groups" "wbc_entity_usage")

for f in "${arr[@]}"
	do
	   	echo "DBTax Project: Processing "$f" tables"
   		if [ -f $prefix"$f".sql ]; then
	   		rm $prefix"$f".sql
    		echo "DBTax Project: File already Existing. Removed "$prefix"$f".sql
		fi
		
		echo "DBTax Project: Downloading "$prefix$f".sql.gz"
		wget https://dumps.wikimedia.org/metawiki/latest/$prefix"$f".sql.gz
		
		echo "DBTax Project: Extracting "$prefix$f".sql"
		gunzip $prefix$f.sql.gz

		echo "DBTax Project: Dumping " $f " to MySql"
		mysql -u$MYSQL_ROOT -p$MYSQL_PASSWORD $MYSQL_DATABASE <$prefix"$f".sql

		rm $prefix"$f".sql
	done	

mysql -u$MYSQL_ROOT -p$MYSQL_PASSWORD $MYSQL_DATABASE << SQL >> session.log
CREATE TABLE IF NOT EXISTS node ( node_id int(10) NOT NULL AUTO_INCREMENT, category_name varchar(40) NOT NULL, is_leaf tinyint(1) NOT NULL, is_prominent tinyint(1) NOT NULL, is_head_plural tinyint(1) NOT NULL, head_of_name varchar(40) DEFAULT NULL, score_interlang double DEFAULT NULL, score_edit_histo double NOT NULL, PRIMARY KEY (node_id), UNIQUE KEY category_name (category_name) ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;
CREATE TABLE IF NOT EXISTS edges ( parent_id int(10) NOT NULL, child_id int(10) NOT NULL, PRIMARY KEY (parent_id,child_id) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
quit
SQL
echo "DBTax Project: Completed setting up the database"
echo "Bye"
