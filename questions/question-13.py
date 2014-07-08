#!/usr/bin/python
# -*- coding: utf-8 -*-

import argparse
from ast import literal_eval
from itertools import izip, chain
import requests

SOLR_URL = 'http://localhost:8983/solr/'
GAME_PLAY_BY_PLAY_CORE = 'game-play-by-play/'
QUERY_STRING = '?q=game_id%3A{game_id}&fq=score_margin%3A*&sort=event_num+asc&rows=214749364&wt=json&indent=true&group=true&group.field=score&'

def detect_streaks(game_id):
    ''''''
    query = SOLR_URL + GAME_PLAY_BY_PLAY_CORE + 'select/' + QUERY_STRING
    query = query.format(game_id=game_id)
    
    results = run_query(query)
    doc_list = strip_doc_list_from_results(results)
    streaks = find_score_streaks(doc_list)
    filtered_streaks = [s for s in streaks if len(s) > 1]
    ok_streaks = [s for s in filtered_streaks if 2 <= len(s) < 5]
    good_streaks = [s for s in filtered_streaks if 5 <= len(s) < 10]
    great_streaks = [s for s in filtered_streaks if len(s) >= 10]

    print len(filtered_streaks)
    print len(ok_streaks)
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
    return docs

def find_score_streaks(doc_list):
    ''''''
    home_team_streak = True
    prev_score_margin = None
    streak_splits = []
    for i, doc in enumerate(doc_list):
        try:
            score_margin = int(doc['score_margin'])
        except ValueError:
            continue

        if prev_score_margin == None:
            prev_score_margin = score_margin
            home_team_streak = True if prev_score_margin > 0 else False
            continue

        if home_team_streak:
            if score_margin < prev_score_margin:
                streak_splits.append(i)
                home_team_streak = False
        else:
            if score_margin > prev_score_margin:
                streak_splits.append(i)
                home_team_streak = True

        prev_score_margin = score_margin

    pairs = izip(chain([0], streak_splits), chain(streak_splits, [None]))
    return (doc_list[i:j] for i, j in pairs)

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='XDATA NBA Query 13')
    parser.add_argument('game_id', help='NBA data game id')
    args = vars(parser.parse_args())

    detect_streaks(**args)
