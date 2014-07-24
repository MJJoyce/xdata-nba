# -*- coding: utf-8 -*-

import argparse
import collections
import requests

''' Pull interesting comment/game data for a given commenter.

Given a commenter name, we will pull comment sentiment data and game
stats for all game_ids on which the commenter posted. We will then do
some minor grouping to see if the user seems to favor commenting when
a particular team wins/loses, etc.
'''

SOLR_URL = 'http://localhost:8983/solr/'
SENTIMENT_CORE = 'comment-sentiment/'
SENTIMENT_Q = (
    'select/?q=cmntr%3A{cmntr}&wt=json&indent=true'
    '&group=true&group.field=game_id&sort=game_id+asc'
)
SENTIMENT_QUERY = SOLR_URL + SENTIMENT_CORE + SENTIMENT_Q

STATS_CORE = 'game-results/'
STATS_Q = 'select/?q={stats_q}&sort=game_id+asc&wt=json&indent=true'
STATS_QUERY = SOLR_URL + STATS_CORE + STATS_Q

TEAM_NAME_CORE = 'league-team-stats/'
TEAM_NAME_Q = (
    'select/?q={team_ids}&wt=json&indent=true'
    '&group=true&group.field=team_id')
TEAM_NAME_QUERY = SOLR_URL + TEAM_NAME_CORE + TEAM_NAME_Q



def analyse_commenter_data(commenter):
    '''Print out a sentiment analysis based overview of a commenter.

    Given a commenter id, we look up a list of of their comments and see if
    we can find an interesting connection between team performance.
    '''
    # Query for commenter sentiment data per game. This gives us information
    # grouped by game_id about the number of comments and the pos/neg
    # sentiment for the commenter of interest.
    r = requests.get(SENTIMENT_QUERY.format(cmntr=commenter))

    if r.json()["grouped"]["game_id"]["matches"] == 0:
        print "Invalid commenter id"
        return 

    sentiment_groupings = r.json()['grouped']['game_id']['groups']
    
    # For each game_id where the commenter posted, we will grab game
    # result information. This tells us which team won/loss the game
    # and helps us identify if the commenter focuses around the
    # performance of a particular team.
    game_ids = set([s['groupValue'] for s in sentiment_groupings])
    stats_q = 'game_id:' + '\ngame_id:'.join(game_ids)
    r = requests.get(STATS_QUERY.format(stats_q=stats_q))
    game_stats = {g['game_id']:g for g in r.json()['response']['docs']}

    # Calculate the number of times a team played when the commenter
    # posted somewhere.
    team_id_count = collections.defaultdict(int)
    for game in r.json()['response']['docs']:
        team_id_count[game['winner_id']] += 1
        team_id_count[game['loser_id']] += 1

    relevant_teams = [(k, v)
                      for k,v in team_id_count.iteritems()
                      if v > 1]

    if len(relevant_teams) == 0:
        print 'No team-based grouping of comments could be found ...'
        return

    # Retrieve the team names for each game if we determine that the
    # commenter seemed to focus on one or more teams.
    team_name_ids = ''
    for k,g in game_stats.iteritems():
        team_name_ids += 'team_id:' + g['winner_id'] + '\n'
        team_name_ids += 'team_id:' + g['loser_id'] + '\n'
    else:
        # Drop the trailing \n so we don't break our query
        team_name_ids = team_name_ids[:-1]

    r = requests.get(TEAM_NAME_QUERY.format(team_ids=team_name_ids))

    # Nicely format team data so we can print things easily later.
    team_groups = {str(g['groupValue']):g['doclist']['docs'][0]
                   for g in r.json()['grouped']['team_id']['groups']}

    print '-'*30
    print commenter
    print '-'*30
    print 'Commenter focused around:'
    print '# of games\tteam'
    print '-'*30
    for k,v in relevant_teams:
        print v, '\t\t', team_groups[k]['team_name']

    print
    print 'Comment information per game:'
    print 'game_id\t\t# cmnts\t# pos\t# neg\twinner\tloser'
    print '-'*60
    out = '{id}\t{tot}\t{pos}\t{neg}\t{win}\t{lose}'
    for g in sentiment_groupings:
        data = g['doclist']['docs'][0]
        game = game_stats[g['groupValue']]

        cmnt_cnt = data['neg'] + data['pos']
        win = team_groups[game['winner_id']]['team_name'].split()[-1]
        lose = team_groups[game['loser_id']]['team_name'].split()[-1]

        print out.format(id=g['groupValue'],
                         tot=cmnt_cnt,
                         pos=data['pos'],
                         neg=data['neg'],
                         win=win,
                         lose=lose)

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='XDATA NBA Question 8')
    parser.add_argument('commenter', help='Commenter name')
    args = vars(parser.parse_args())

    analyse_commenter_data(**args)
