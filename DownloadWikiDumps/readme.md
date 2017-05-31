Download Wikipedia Category Dumps

This folder contains the scripts to download the latest Wikipedia dumps. 
For our project DBTax, we require dumps of the following tables: category, categorylinks, langlinks and page.
Link to download latest Wikipedia dumps: https://dumps.wikimedia.org/metawiki/latest/


To run: 
1. In config.sh file, enter your MySQL credentials like username, password and also database name in which you wanted to add tables. 
2. Open console and run "./download.sh"


Note:
Few tables like page table SQL file is 1GB in size. 
Python and Java are also tried to develop the scipts, but MySQL connectors cause time outs for such huge dumps. MySQL Workbench also causes same problem.
So, its recommended to use MySQL from console to import dumps.