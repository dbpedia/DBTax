# coding: utf-8

import csv
import random
import sys

o = open('input-data.tsv', 'wb')
writer = csv.writer(o, delimiter='\t')
writer.writerow(['resource','type','_golden','question_gold'])
i = open(sys.argv[1])
lines = i.readlines()
random.shuffle(lines)
i.close()
for l in lines[666:1066]:
    tokens = l.split()
    resource = tokens[0].replace('dbpedia.org/resource','en.wikipedia.org/wiki')
    type = tokens[2].replace('http://dbpedia.org/ontology/','')
    resource = resource.replace('<','')
    resource = resource.replace('>','')
    type = type.replace('<','')
    type = type.replace('>','')
    writer.writerow([resource,type])
    
o.close()
