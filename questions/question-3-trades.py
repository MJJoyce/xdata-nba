#!/usr/bin/python
# -*- coding: utf-8 -*-

import argparse
from ast import literal_eval
from itertools import izip, chain
import requests
import sys

SOLR_URL = 'http://localhost:8983/solr/'
GAME_PLAY_BY_PLAY_CORE = 'game-players/'
QUERY_STRING = '?q=player_id%3A{player_id}&wt=json&indent=true&group=true&group.field=team_city'

def detect_trades(player_id):
    ''''''
    query = SOLR_URL + GAME_PLAY_BY_PLAY_CORE + 'select/' + QUERY_STRING
    query = query.format(player_id=player_id)
    
    results = run_query(query)
    doc_list = strip_doc_list_from_results(results)
    player_name = doc_list[0]['player_name']

    if len(doc_list) > 1:
        print player_name, 'has been traded at some point'
        print [doc['team_city'] for doc in doc_list]
    else:
        print 'Only one team found for', player_name
        print doc_list[0]['team_city']

def run_query(query):
    ''''''
    r = requests.get(query)
    return r.json()

def strip_doc_list_from_results(query_results):
    ''''''
    if query_results['grouped']['team_city']['matches'] == 0:
        print "No documents were returned"
        sys.exit(1)

    docs = [group['doclist']['docs'][0]
            for group in query_results['grouped']['team_city']['groups']]
    return docs

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='XDATA NBA Question 3 (trades)')
    parser.add_argument('player_id', help='NBA player id')
    args = vars(parser.parse_args())

    detect_trades(**args)
