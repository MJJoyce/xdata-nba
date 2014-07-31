# -*- coding: utf-8 -*-

import itertools
import json
import multiprocessing
import requests
import urllib
import urllib2

TOTAL_THREADS = 10

GAME_ID_QUERY = (
    'http://localhost:8983/solr/game-stats/select?q=*%3A*&'
    'sort=game_id+asc&fl=game_id&wt=json&indent=true&'
    'rows=10000&group=true&group.field=game_id'
)

COMMENTS_QUERY = (
    'http://localhost:8983/solr/game-comments/select?'
    'q=commenter%3A%22{cmntr}%22&sort=game_id+asc'
    '&wt=json&indent=true&rows=23456789'
)

# Get list of all commenter names
COMMENTER_QUERY = (
    'http://localhost:8983/solr/game-comments/select?'
    'q=*%3A*&wt=json&indent=true&group=true'
    '&group.field=commenter&rows=3548675'
)

def process_cmntr_data(cmntr):
    # Get all comments for commenter sorted by game_id
    if '"' in cmntr:
        cmntr = cmntr.replace('"', '\\\"')

    q = COMMENTS_QUERY.format(cmntr=urllib.quote(cmntr.encode('utf-8')))
    cmnts = requests.get(q).json()
    cmnts = cmnts['response']['docs']

    # Retrieve a list of game_ids where the current commenter posted. For each
    # game_id we will count the number of positive and negative sentiment
    # posts so we initialize a dictionary for use later.
    game_ids = set([c['game_id'] for c in cmnts])
    comments_per_game = {}
    for g in game_ids:
        comments_per_game[g] = {'pos': 0, 'neg': 0}

    # Check every comment the user made and increment the pos/neg count
    # for the proper game_ids. This lets us look at a commenters' overall
    # sentiment on a per game basis.
    for c in cmnts:
        if c['sentiment'] == 'pos':
            comments_per_game[c['game_id']]['pos'] += 1
        else:
            comments_per_game[c['game_id']]['neg'] += 1

    # Commenter sentiment is stored per game_id. Format the JSON and
    # return the values so they can be added to Solr in batch.
    returns = []
    for game_id in game_ids:
        pos = comments_per_game[game_id]['pos']
        neg = comments_per_game[game_id]['neg']
        total = pos + neg

        returns.append({
            'id': cmntr + '_' + game_id,
            'cmntr': cmntr,
            'game_id': game_id,
            'pos': pos,
            'neg': neg,
            'prcnt_pos': pos / float(total),
            'prcnt_neg': neg / float(total),
        })

    return returns

# Get a list of all commenter names in the comment data.
commenter_json = requests.get(COMMENTER_QUERY).json()
commenter_list = [c['groupValue'] for c in commenter_json['grouped']['commenter']['groups']]

thread_pool = multiprocessing.Pool(TOTAL_THREADS)
results = thread_pool.map(process_cmntr_data, commenter_list)
thread_pool.close()
thread_pool.join()

results = list(itertools.chain.from_iterable(results))
solr_url = 'http://localhost:8983/solr/comment-sentiment/update?commit=true'
data = json.dumps(results)
req = urllib2.Request(solr_url, data, {'Content-Type': 'application/json'})
urllib2.urlopen(req)
