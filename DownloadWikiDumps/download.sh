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

echo "DBTax Project: Completed setting up the database"
echo "Bye"
