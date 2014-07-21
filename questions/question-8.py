# -*- coding: utf-8 -*-

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
    #cmnts = requests.get(COMMENTS_QUERY.format(cmntr=cmntr.encode('utf-8'))).json()
    if '"' in cmntr:
        cmntr = cmntr.replace('"', '\\\"')

    q = COMMENTS_QUERY.format(cmntr=urllib.quote(cmntr.encode('utf-8')))
    cmnts = requests.get(q).json()
    cmnts = cmnts['response']['docs']

    # Calculate percentage of positive vs negative comments
    # Group comments by game_id to categorize
    comments_per_game = {'pos': 0, 'neg': 0}
    for c in cmnts:
        if c['sentiment'] == 'pos':
            comments_per_game['pos'] += 1
        else:
            comments_per_game['neg'] += 1

    total = comments_per_game['pos'] + comments_per_game['neg']
    return {
        'id': cmntr,
        'total': total,
        'pos': comments_per_game['pos'],
        'neg': comments_per_game['neg'],
        'prcnt_pos': comments_per_game['pos'] / float(total),
        'prcnt_neg': comments_per_game['neg'] / float(total),
    }

r = requests.get(GAME_ID_QUERY).json()
game_ids = [group['groupValue'] for group in r['grouped']['game_id']['groups']]

commenter_json = requests.get(COMMENTER_QUERY).json()
commenter_list = [c['groupValue'] for c in commenter_json['grouped']['commenter']['groups']]

processed = {}

thread_pool = multiprocessing.Pool(TOTAL_THREADS)
results = thread_pool.map(process_cmntr_data, commenter_list)
thread_pool.close()
thread_pool.join()

solr_url = 'http://localhost:8983/solr/comment-sentiment/update?commit=true'
data = json.dumps(results)
req = urllib2.Request(solr_url, data, {'Content-Type': 'application/json'})
urllib2.urlopen(req)
