#!/usr/bin/python
# -*- coding: utf-8 -*-

import argparse
from ast import literal_eval
from itertools import izip, chain
import requests

SOLR_URL = 'http://localhost:8983/solr/'
GAME_PLAY_BY_PLAY_CORE = 'game-play-by-play/'
QUERY_STRING = '?q=score%3A*%0Agame_id%3{game_id}&wt=json&indent=true&group=true&group.field=score&rows=2147483647'

def detect_streaks(game_id):
    ''''''
    query = SOLR_URL + GAME_PLAY_BY_PLAY_CORE + 'select/' + QUERY_STRING
    query = query.format(game_id=game_id)
    
    results = run_query(query)
    doc_list = strip_doc_list_from_results(results)
    streaks = find_score_streaks(doc_list)
    filtered_streaks = [s for s in streaks if len(s) > 1]
    good_streaks = [s for s in filtered_streaks if 5 <= len(s) < 10]
    great_streaks = [s for s in filtered_streaks if len(s) >= 10]

    print len(good_streaks)
    print len(great_streaks)

def run_query(query):
    ''''''
    r = requests.get(query)
    return r.json()

def strip_doc_list_from_results(query_results):
    ''''''
    if query_results['grouped']['score']['matches'] == 0:
        print "No documents were returned"

    docs = [group['doclist']['docs'][0]
            for group in query_results['grouped']['score']['groups']]
    return sorted(docs, key=lambda i: i['event_num'])

def find_score_streaks(doc_list):
    ''''''
    score_delta_is_pos = True if int(doc_list[0]['score_margin']) > 0 else False
    streak_splits = []
    for i, doc in enumerate(doc_list):
        try:
            score_margin = int(doc['score_margin'])
        except ValueError:
            continue

        delta_flag = True if score_margin > 0 else False

        if delta_flag != score_delta_is_pos:
            streak_splits.append(i)
            score_delta_is_pos = delta_flag

    pairs = izip(chain([0], streak_splits), chain(streak_splits, [None]))
    return (doc_list[i:j] for i, j in pairs)

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='XDATA NBA Query 13')
    parser.add_argument('game_id', help='NBA data game id')
    args = vars(parser.parse_args())

    detect_streaks(**args)
