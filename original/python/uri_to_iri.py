#!/usr/bin/env python
# encoding: utf-8

import codecs
import rfc3987
import sys
from urllib2 import unquote

with codecs.open(sys.argv[1], 'rb', 'utf-8') as i:
    with codecs.open(sys.argv[2] + '.out', 'wb', 'utf-8') as o:
        for l in i:
            if l.startswith('<'):
                uri = l.replace('<', '').strip()
                uri = uri.replace('>', '')
                iri = unquote(uri)
                try:
                    rfc3987.parse(iri)
                    o.write(u'<%s>\n' % iri)
                except ValueError as e:
                    print e
                    o.write(l)
            else:
                o.write(l)
