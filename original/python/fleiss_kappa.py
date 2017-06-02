#!/usr/bin/env python
# -*- coding: utf-8 -*-

""" Computes the Fleiss' Kappa value as described in (Fleiss, 1971) """

import argparse
import random
import sys
from majority_vote import read_full_results, compute_majority_vote

DEBUG = False


def build_judgment_matrix(results):
    matrix = []
    for r in results:
        # Skip units with < 5 judgments
        if not r['judgments'] < 5:
            yes = r['answers']['Yes']
            no = r['answers']['No']
            # Ensure that judgments = 5, otherwise no kappa can be computed
            if r['judgments'] > 5:
                if DEBUG:  print "%s got %d judgments, %d YES and %d NO" % (r['resource'], r['judgments'], yes, no)
                while yes + no != 5:
                    # Randomly remove an answer
                    if yes and no: chosen = random.choice([yes, no])
                    # When one answer is not there, choose the other
                    elif not yes: chosen = no
                    else: chosen = yes
                    if chosen == yes:
                        yes -= 1
                        if DEBUG: print "-1 to YES, now at", yes 
                    else:
                        no -= 1
                        if DEBUG: print "-1 to NO, now at", no
                matrix.append([yes, no])
            else: matrix.append([yes, no]) 
    if DEBUG:
        print "Matrix =", matrix
    return matrix

 
def computeKappa(mat):
    """ Computes the Kappa value
        @param n Number of rating per subjects (number of human raters)
        @param mat Matrix[subjects][categories]
        @return The Kappa value """
    n = checkEachLineCount(mat)   # PRE : every line count must be equal to n
    N = len(mat)
    k = len(mat[0])
 
    if DEBUG:
        print n, "raters."
        print N, "subjects."
        print k, "categories."
 
    # Computing p[]
    p = [0.0] * k
    for j in xrange(k):
        p[j] = 0.0
        for i in xrange(N):
            p[j] += mat[i][j]
        p[j] /= N*n
    if DEBUG: print "p =", p
 
    # Computing P[]    
    P = [0.0] * N
    for i in xrange(N):
        P[i] = 0.0
        for j in xrange(k):
            P[i] += mat[i][j] * mat[i][j]
        P[i] = (P[i] - n) / (n * (n - 1))
    if DEBUG: print "P =", P
 
    # Computing Pbar
    Pbar = sum(P) / N
    if DEBUG: print "Pbar =", Pbar
 
    # Computing PbarE
    PbarE = 0.0
    for pj in p:
        PbarE += pj * pj
    if DEBUG: print "PbarE =", PbarE
 
    kappa = (Pbar - PbarE) / (1 - PbarE)
    if DEBUG: print "kappa =", kappa
 
    return kappa


def checkEachLineCount(mat):
    """ Assert that each line has a constant number of ratings
        @param mat The matrix checked
        @return The number of ratings
        @throws AssertionError If lines contain different number of ratings """
    n = sum(mat[0])
 
    assert all(sum(line) == n for line in mat[1:]), "Line count != %d (n value)." % n
    return n


def create_cli_parser():
    parser = argparse.ArgumentParser(description="Compute Fleiss' Kappa agreement from a full results spreadsheet of a CrowdFlower job")
    parser.add_argument('filein', help='CrowdFlower full results input file')
    parser.add_argument('-g', '--gold', action='store_true', help='include test questions')
    parser.add_argument('--debug', action='store_true', help='run with debug messages')
    return parser


if __name__ == "__main__":
    cli = create_cli_parser()
    args = cli.parse_args()
    if args.debug: DEBUG = True
    if args.gold:
        print "[INFO] Test questions will be included"
        results = read_full_results(args.filein, False)
    else: results = read_full_results(args.filein)
    matrix = build_judgment_matrix(compute_majority_vote(results))
    kappa = computeKappa(matrix)
    print "Fleiss' Kappa =", kappa
