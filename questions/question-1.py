from collections import defaultdict
import itertools
import json
import multiprocessing
import random
import requests
import time
import urllib2

import commentary_ne_extractor as com_ne_extract

SOLR_URL = 'http://localhost:8983/solr/game-players/update/'

GAME_ID_QUERY = (
    'http://localhost:8983/solr/game-stats/select?q=*%3A*&'
    'sort=game_id+asc&fl=game_id&wt=json&indent=true&'
    'rows=10000&group=true&group.field=game_id'
)

GAME_PLAYER_QUERY = (
    'http://localhost:8983/solr/game-players/select?'
    'q=*%3A*&wt=json&indent=true&group=true'
    '&group.field=game_id&rows=123456789&group.limit=10000'
    '&rows=13456789'
) 

PLAYER_NAME_QUERY = (
    'http://localhost:8983/solr/game-players/select?'
    'q=*%3A*&wt=json&indent=true&group=true'
    '&group.field=player_name&rows=123456789'
)

TEAM_STATS_QUERY = (
    'http://localhost:8983/solr/league-team-stats/select?'
    'q=*%3A*&rows=23456789&wt=json'
)

# Get a complete game_id list and determine how many threads we're going
# to use for processing. Not the prettiest way of handling this but it
# does a decent job :/
fpt = 30 # How many Files Per Thread should be use during processing.
r = requests.get(GAME_ID_QUERY).json()
game_ids = [group['groupValue'] for group in r['grouped']['game_id']['groups']]
total_threads = len(game_ids) / fpt
total_threads += 1 if len(game_ids) % fpt else 0

split_game_ids = [game_ids[(fpt * index):(fpt * index) + fpt]
                    for index in range(total_threads)]

# Grab all player data in one go and group it by game_id so we don't
# have to hit Solr 4 billion times.
r = requests.get(GAME_PLAYER_QUERY)
all_player_records = {}
for game_data in r.json()['grouped']['game_id']['groups']:
    all_player_records[game_data['groupValue']] = game_data['doclist']['docs']

# Grab all player info ordered by name that we will use later for inserting
# records into a new Solr core.
r = requests.get(PLAYER_NAME_QUERY)
all_player_names = {}
for p in r.json()['grouped']['player_name']['groups']:
    all_player_names[p['groupValue']] = p['doclist']['docs'][0]

# Grab all team stats information and group by team_id. We'll need this later
# so we can strip out team name information.
r = requests.get(TEAM_STATS_QUERY)
team_stats = {}
for team_record in r.json()['response']['docs']:
    team_stats[team_record['team_id']] = team_record

# Do named entity extraction for each game record that we have
def ne_stuff(game_ids):
    '''Do NE extraction for a list of game_id's commentary data

    Process a list of game_id's commentary data and performs NE extraction over
    them. There are two ways to handle player data updates with the NEs that are
    extracted per game_id. Player records per game can be updated with NE date
    from that game's commentary data or a single player record can be created and
    have NE data added to the record for all game's commentary data. Currently
    the second one is the approach being taken.

    Per game_id, commentary data has NEs extracted from. A player list is updated
    with new NE data from that game_id. After all game_ids have been processed,
    the global player data is returned upstream.

    :param game_ids: List of game_ids specifying which commentary data should
        have NE extraction done.

    :returns: Dictionary containing player:ne_data pairs for all processed
        game_ids. Each player record contains NE data for all processed games.
    '''
    player_ne_collection = defaultdict(set)
    for game_id in game_ids:
        player_records = all_player_records[game_id]

        # Make a list of player names that we should ignore in NE extraction
        player_names = [r['player_name'] for r in player_records]
        split_player_names = []
        for name in player_names:
            split_player_names += name.split(' ')

        skip_entities = set(player_names + split_player_names)

        # Grab team name information to hopefully avoid unneeded NE matches
        team_ids = set([r['team_id'] for r in player_records])
        team_names = [team_stats[team_id]['team_name']
                      for team_id in team_ids]

        # Create a list of skip words for the names of the teams involved in the
        # game including upper case and split versions.
        team_names += map(lambda x: x.upper(), team_names)
        team_names += list(itertools.chain(*map(lambda x: x.split(' '), team_names)))
        skip_entities = skip_entities.union(set(team_names))

        # Do NE extraction
        named_entities = com_ne_extract.detect_named_entities_in_commentary(game_id)

        # Strip out skip entities
        cleaned_entities = named_entities.difference(skip_entities)

        # Update player records with references. This assumes that a single player
        # record will hold all references to named entities from every game. These
        # records will not be stored in the game-players core like Approach #2.
        #
        # Approach #1. Be sure to comment out the other relevant section below!
        for player in player_records:
            player_ne_collection[player['player_name']] |= cleaned_entities

        # Update player records with references. This assumes that we're keeping
        # each player record separate per game. The named entities found for this
        # game's commentary data is only associated with player data from the
        # same game_id.
        #
        # Note, this is going to hit Solr quite a bit and hasn't been thoroughly
        # tested. Stick with other approach for now (namely, Updating a separate
        # Solr core).
        #
        # Approach #2
        #cleaned_entities = list(named_entities.difference(skip_entities))
        #update_json = [{'id':p['id'], 'ne':cleaned_entities} for p in player_records]
        #data = json.dumps(update_json, encoding='latin-1')
        #req = urllib2.Request(solr_url, data, {'Content-Type': 'application/json'})
        #urllib2.urlopen(req)

    return player_ne_collection

thread_pool = multiprocessing.Pool(total_threads)
results = thread_pool.map(ne_stuff, split_game_ids)
thread_pool.close()
thread_pool.join()

player_entity_collection = results[0]
for d in results[0:]:
    for k,v in d.iteritems():
        player_entity_collection[k] |= v

# Assuming the new table is of the form:
# id == player_id
# player_name - Same as a player record
# ne - multivalued field containing all the NE's that were extracted from comments
#
# TODO: Consider making the relationship between the players and these entities
# more explicit. For instance, each NE could be a (game_id, NE) pair or perhaps
# the ID for the commentary that was used should be included as well in the
# relation
#
# Approach #1. Be sure to comment the other relevant section above!
solr_url = 'http://localhost:8983/solr/player-commentary-ne-data/update/?commit=true'
player_er_records = []
for _, player in all_player_names.iteritems():
    player_er_records.append({
        'id': player['player_id'],
        'player_name': player['player_name'],
        'ne': list(player_entity_collection[player['player_name']])
    })
data = json.dumps(player_er_records)
req = urllib2.Request(solr_url, data, {'Content-Type': 'application/json'})
urllib2.urlopen(req)
