# coding: utf-8

import csv
import random
import sys
from collections import defaultdict

i = open(sys.argv[1])
lines = i.readlines()
i.close()
diz = defaultdict(list)
for l in lines:
    tokens = l.split()
    resource = tokens[0].replace('dbpedia.org/resource','en.wikipedia.org/wiki')
    type = tokens[2].replace('<http://dbpedia.org/class/yago/','')
    resource = resource.replace('<','')
    resource = resource.replace('>','')
    type = type.replace('>','')
    diz[resource].append(type)

o = open('yago-input-data.tsv', 'wb')
writer = csv.writer(o, delimiter='\t')
writer.writerow(['resource','type','_golden','question_gold'])
for resource, types in diz.iteritems():
    writer.writerow([resource,random.choice(types)])
o.close()
