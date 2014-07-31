# -*- coding: utf-8 -*-

import itertools
import json
import multiprocessing
import requests
import urllib
import urllib2

TOTAL_THREADS = 10


TEAM_ID_QUERY = (
    'http://localhost:8983/solr/game-results/select?'
    'q=*:*&wt=json&indent=true&rows=0&facet=true'
    '&facet.limit=-1&facet.field=winner_id&facet.field=loser_id'
    )

TEAM_NAME_QUERY = (
    'http://localhost:8983/solr/league-team-stats/select?'
    'q=team_id%3A{team_id}&fl=team_name&wt=json&indent=true'
    )

TEAM_LOSS_QUERY = (
    'http://localhost:8983/solr/game-results/select?'
    'q=loser_id%3A{team_id}&sort=game_id+asc&rows=10000'
    '&fl=game_id&wt=json&indent=true'
    )

TEAM_WIN_QUERY = (
    'http://localhost:8983/solr/game-results/select?'
    'q=winner_id%3A{team_id}&sort=game_id+asc&rows=10000'
    '&fl=game_id&wt=json&indent=true'
    )

TEAM_PLAYER_QUERY = ('http://localhost:8983/solr/game-players/select?'
    'q=team_id%3A{team_id}%20AND%20game_id:{game_id}&rows=100'
    '&fl=player_name&wt=json&indent=true'
    )

COMMENTS_QUERY = (
    'http://localhost:8983/solr/game-comments/select?'
    'q={query}&wt=json&indent=true'
    )



def process_team_cmts_per_game(team_id):

    # Get list of game IDs that team has lost
    q_loss = TEAM_LOSS_QUERY.format(team_id=urllib.quote(team_id.encode('utf-8')))
    team_loss_list = requests.get(q_loss).json()['response']['docs']
    
    # Get list of game IDs that team has won
    q_win = TEAM_WIN_QUERY.format(team_id=urllib.quote(team_id.encode('utf-8')))
    team_win_list = requests.get(q_win).json()['response']['docs']

    # Process cmts for team loss/win games
    win_results = process_team_cmts(team_id, team_win_list, 'winner')
    loss_results = process_team_cmts(team_id, team_loss_list, 'loser')
    results = win_results + loss_results
    return results

def process_team_cmts(team_id, team_game_list, status):

    # Get team name to help query mentions in comments data
    q_name = TEAM_NAME_QUERY.format(team_id=urllib.quote(team_id.encode('utf-8')))
    team_name = requests.get(q_name).json()['response']['docs'][0]['team_name']

    # Create dict to hold count of pos/neg comments for every game a team has played
    team_cmts_per_game = []
    for game in team_game_list:
        game_id = game['game_id']
        comment_query = "game_id:%s AND (comment:'%s' OR comment:" % (game_id, team_name)

        # Get team players for each game - 
        #  Assumption: If team player is mentioned then relevant to team mentions
        q_players = TEAM_PLAYER_QUERY.format(team_id=urllib.quote(team_id.encode('utf-8')),
            game_id=urllib.quote(game_id.encode('utf-8')))
        team_players = requests.get(q_players).json()['response']['docs']
        qstr = 'OR comment:'
        comment_query += qstr.join(["'%s' " % (v['player_name']) for (v) in team_players])
        comment_query += ')'

        # Query cmts for each game - using team name, city and players
        q_comments = COMMENTS_QUERY.format(query=urllib.quote(comment_query.encode('utf-8')))
        team_comments = requests.get(q_comments).json()['response']['docs']

        # Set all pos/neg fields
        team_cmts_per_game.append({
            'id': game_id,
            status+'_pos_cmt_count': {"add" : 0},
            status+'_neg_cmt_count': {"add" : 0},
            status+'_pos_pct': {"add" : 0},
            status+'_neg_pct': {"add" : 0}
            })

        # Calculate totals for pos/neg comments
        for tgc in team_comments:
            if tgc['sentiment'] == 'pos':
                team_cmts_per_game[-1][status+'_pos_cmt_count'] += 1
            else:
                team_cmts_per_game[-1][status+'_neg_cmt_count'] += 1

        pos = team_cmts_per_game[-1][status+'_pos_cmt_count']
        neg = team_cmts_per_game[-1][status+'_neg_cmt_count']
        total = pos + neg
        if total != 0:
            team_cmts_per_game[-1][status+'_pos_pct'] = pos / float(total)
            team_cmts_per_game[-1][status+'_neg_pct'] = neg / float(total)
    
    return team_cmts_per_game


# Get a list of all TEAM IDs that have win/lose data in the game-result data.
team_result = requests.get(TEAM_ID_QUERY).json()

winning_team_list = team_result['facet_counts']['facet_fields']['winner_id']
lossing_team_list = team_result['facet_counts']['facet_fields']['loser_id']
wlist = winning_team_list[::2]
llist = lossing_team_list[::2]
team_list = list(set(wlist + llist))
team_list = [u'1610612766', u'1610612737']#, u'1610612762']
print team_list

thread_pool = multiprocessing.Pool(TOTAL_THREADS)
results = thread_pool.map(process_team_cmts_per_game, team_list)
thread_pool.close()
thread_pool.join()
# print len(results[0]),len(results[1])
# print type(results), len(results)
print results

results = list(itertools.chain.from_iterable(results))
print len(results)
solr_url = 'http://localhost:8983/solr/game-comments-results/update?commit=true'
data = json.dumps(results)
# print data
# print results[1], len(data)
# req = urllib2.Request(solr_url, data, {'Content-Type': 'application/json'})
# print req
# urllib2.urlopen(req)

