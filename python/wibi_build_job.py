# coding: utf-8

import csv
import random
import sys
from collections import defaultdict

i = open(sys.argv[1])
lines = [l.strip() for l in i.readlines()]
i.close()
diz = defaultdict(list)
for l in lines:
    tokens = l.split('\t')
    resource = 'http://en.wikipedia.org/wiki/' + tokens[0]
    type = tokens[1].replace('_', ' ')
    diz[resource].append(type)
o = open('wibi-input-data.tsv', 'wb')
writer = csv.writer(o, delimiter='\t')
writer.writerow(['resource','type','_golden','question_gold'])
for resource, types in diz.iteritems():
    writer.writerow([resource,random.choice(types)])
o.close()
