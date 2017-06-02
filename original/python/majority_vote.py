#!/usr/bin/env python
# -*- coding: utf-8 -*-

import csv
import json
import sys
from collections import Counter, defaultdict


def read_full_results(results_file, skip_gold=True):
    processed = defaultdict(list)
    with open(results_file, 'rb') as i:
        results = csv.DictReader(i)
        fields = results.fieldnames
        if skip_gold:
            results = [row for row in results if row['_golden'] == 'false']
        for row in results:
            answer = row['question']
            processed[row['resource']].append(answer)
    return processed


def compute_majority_vote(processed_results):
    to_return = []
    for res, answers in processed_results.iteritems():
        judgments = len(answers)
        count = Counter(answers)
        majority = float(judgments) / 2.0
        clean = {'resource': res, 'judgments': judgments, 'answers': count, 'needed_majority': majority}
        for answer, freq in count.iteritems():
            if freq > majority: clean['majority_vote'] = answer
        if not clean.get('majority_vote'): print "[HEADS UP!] No majority answer for resource [%s]. Judgments = %d" % (res, judgments)
        to_return.append(clean)
    return to_return


def compute_majority_precision(results):
    answers_count = Counter([result['majority_vote'] for result in results if result.get('majority_vote')])
    true_positives = answers_count['Yes']
    false_positives = answers_count['No']
    precision = float(true_positives) / float(true_positives + false_positives)
    return precision


if __name__ == '__main__':
    if len(sys.argv) == 2:
        processed = read_full_results(sys.argv[1])
        majority = compute_majority_vote(processed)
        precision = compute_majority_precision(majority)
        print json.dumps(majority, indent=2)
        print "Majority vote precision = %f" % precision
    elif len(sys.argv) == 3:
        processed = read_full_results(sys.argv[1], sys.argv[2])
        majority = compute_majority_vote(processed)
        print json.dumps(majority, indent=2)
    else:
        print "Usage: python %s <CROWDFLOWER_FULL_RESULTS> [SKIP_GOLD]" % __file__
        sys.exit(1)

