# -*- coding: utf-8 -*-

import argparse
import collections
import numpy
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

PLAYER_STATS_CORE = 'game-player-stats/'

def analyse_commenter_data(commenter):
    '''Print out a sentiment analysis based overview of a commenter.

    Given a commenter id, we look up a list of of their comments and see if
    we can find interesting connections to team/player performance.
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

    # Generate a query for grabbing player data for a set of game ids.
    #
    # Player information is grouped my player_name and sorted by the number
    # of occurrences of each player.
    PLAYER_QUERY = SOLR_URL + 'game-players/select/?q='
    for g in game_ids:
        PLAYER_QUERY += 'game_id:{}%0A'.format(g)
    PLAYER_QUERY += '&group=true&group.field=player_name&group.limit=-1&wt=json&indent=true&rows=45687988'
    player_data = requests.get(PLAYER_QUERY).json()['grouped']['player_name']['groups']
    player_data = sorted(player_data, key=lambda x: x['doclist']['numFound'])

    # Determine which players we should grab more information on.
    simplified_player_data = [x['doclist']['numFound'] for x in player_data]
    player_mention_mean = numpy.mean(simplified_player_data)
    player_mention_std = numpy.std(simplified_player_data)
    potential_noteworthy = [player_data[i]
                            for i, x in enumerate(simplified_player_data)
                            if x > player_mention_mean + player_mention_std]
    noteworthy_players = [player_data[i]
                          for i, x in enumerate(simplified_player_data)
                          if x > player_mention_mean + 2 * player_mention_std]

    if len(noteworthy_players) > 0:
        players_to_inspect = noteworthy_players
    elif len(potential_noteworthy) > 0:
        players_to_inspect = potential_noteworthy
    else:
        print 'There does not seem to be any players worth investigating.'
        sys.exit(1)

    game_ids = set([player_game_record['game_id']
                    for player in players_to_inspect
                    for player_game_record in player['doclist']['docs']])
    game_ids = sorted(list(game_ids))

    # Create a query for pulling relevant player stat information now that we
    # know which players we are interested in.
    player_ids_query = '&fq='
    for player in players_to_inspect:
        player_ids_query += 'player_id:{} OR '.format(player['doclist']['docs'][0]['player_id'])
    else:
        player_ids_query = player_ids_query[:-4]

    game_ids_query = 'q='
    for game_id in game_ids:
        game_ids_query += 'game_id:{}%0A'.format(game_id)
    else:
        game_ids_query = game_ids_query[:-3]

    PLAYER_STATS_QUERY = SOLR_URL + PLAYER_STATS_CORE + 'select/?' + game_ids_query + player_ids_query + '&wt=json&indent=true&group=true&group.field=game_id&group.limit=-1'
    all_game_player_stats = requests.get(PLAYER_STATS_QUERY).json()['grouped']['game_id']['groups']
    all_game_player_stats = {g['groupValue']:g['doclist']['docs']
                             for g in all_game_player_stats}

    # Collect player performance information per game
    per_game_performance = collections.defaultdict(dict)
    player_avgs = {}
    for player in players_to_inspect:
        # Grab player stats over the entire dataset.
        player_id = player['doclist']['docs'][0]['player_id']
        DATASET_STAT_AVG = (SOLR_URL +
                           PLAYER_STATS_CORE +
                           'select/?q=player_id:{} AND min:[* TO *]&stats=true' +
                           '&stats.field=pts&stats.mean=true' +
                           '&stats.field=blk&stats.mean=true' +
                           '&stats.field=reb&stats.mean=true' +
                           '&stats.field=ftm&stats.mean=true' +
                           '&stats.field=fgm&stats.mean=true' +
                           '&stats.field=fg3m&stats.mean=true' +
                           '&wt=json&indent=true').format(player_id)

        player_avgs[player_id] = requests.get(DATASET_STAT_AVG).json()

        # For each game appearance by a player, determine if that player had a
        # below/above average game and save the results.
        for game_stats in player['doclist']['docs']:
            game_id = game_stats['game_id']
            game_perf = get_game_perf(all_game_player_stats[game_id], player_avgs[player_id], player_id)
            game_overall = sum(game_perf.values()) / float(len(game_perf.keys()))
            per_game_performance[game_id][player_id] = {
                'game_perf': game_perf,
                'game_overall': game_overall
            }

    # Group commenter sentiment values by game_id to make out lives easier.
    gamewise_sentiments = {g['groupValue']:g['doclist']['docs'][0]
                           for g in sentiment_groupings}

    # For each player, determine percent of games where pos perf == pos
    # comments and neg perf == neg comments.
    player_perf_to_sentiment = {}
    for game_id, game in per_game_performance.iteritems():
        for player_id, player in game.iteritems():
            if player_id not in player_perf_to_sentiment.keys():
                player_perf_to_sentiment[player_id] = collections.defaultdict(int)
                
            player_perf_to_sentiment[player_id]['total_play'] += 1

            if player['game_overall'] < 0 and gamewise_sentiments[game_id]['prcnt_neg'] > 0.5:
                player_perf_to_sentiment[player_id]['neg_match'] += 1
                player_perf_to_sentiment[player_id]['tot_match'] += 1
            elif player['game_overall'] > 0 and gamewise_sentiments[game_id]['prcnt_pos'] > 0.5:
                player_perf_to_sentiment[player_id]['pos_match'] += 1
                player_perf_to_sentiment[player_id]['tot_match'] += 1
            else:
                player_perf_to_sentiment[player_id]['missed_match'] += 1

    print
    print 'Potentially Interesting Player Sentiment Matches:'
    print 'player_id\tpos match %\tneg match %\tplayer name'
    print '-'*70
    for player_id, sentiment_match in player_perf_to_sentiment.iteritems():
        player_name = [p['groupValue']
                       for p in players_to_inspect
                       if player_id == p['doclist']['docs'][0]['player_id']][0]

        total = float(sentiment_match['total_play'])
        prc_neg_match = sentiment_match['neg_match'] / total
        prc_pos_match = sentiment_match['pos_match'] / total

        prc_neg_match = '{0:.3f}'.format(prc_neg_match)
        prc_pos_match = '{0:.3f}'.format(prc_pos_match)
        print '{}\t\t{}\t\t{}\t\t{}'.format(player_id, prc_pos_match, prc_neg_match, player_name)

    # For each game, look at all players and determine if their performance was
    # very poor (> 2 std off), semi-poor (> 1 std off), average, 
    # semi-good (> 1 std), very good

def get_game_perf(game_stats, player_avg, player_id):
    game_stats = [g for g in game_stats if player_id == g['player_id']][0]
    stats = player_avg['stats']['stats_fields']
    game_perf = {}

    for interest in ['pts', 'blk', 'reb', 'ftm', 'fgm', 'fg3m']:
        if interest not in game_stats.keys():
            game_stats[interest] = 0

        # Performance == average performance
        if game_stats[interest] == stats[interest]['mean']:
            game_perf[interest] = 0
        # Performance was better than average performance
        elif game_stats[interest] > stats[interest]['mean']:
            game_perf[interest] = 1
        # Performance was worse than average performance
        elif game_stats[interest] < stats[interest]['mean']:
            game_perf[interest] = -1
        else:
            print "This is totally not possible (I think)"
            sys.exit(1)

    return game_perf

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='XDATA NBA Question 8')
    parser.add_argument('commenter', help='Commenter name')
    args = vars(parser.parse_args())

    analyse_commenter_data(**args)
