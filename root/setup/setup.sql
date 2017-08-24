DROP TABLE IF EXISTS node;
CREATE TABLE IF NOT EXISTS node (
node_id int(10) NOT NULL AUTO_INCREMENT,
category_name varchar(255) NOT NULL,
is_leaf tinyint(1) NOT NULL,
is_prominent tinyint(1) NOT NULL,
is_head_plural tinyint(1) NOT NULL,
head_of_name varchar(100) DEFAULT NULL,
score_interlang double DEFAULT NULL,
PRIMARY KEY (node_id),
UNIQUE KEY category_name (category_name) )
ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

DROP TABLE IF EXISTS edges;
CREATE TABLE IF NOT EXISTS edges (
parent_id int(10) NOT NULL,
child_id int(10) NOT NULL,
PRIMARY KEY (parent_id,child_id) )
ENGINE=InnoDB DEFAULT CHARSET=utf8;
