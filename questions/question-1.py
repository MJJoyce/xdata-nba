from collections import defaultdict
import itertools
import json
import multiprocessing
import requests
import urllib2

import commentary_ne_extractor as com_ne_extract

SOLR_URL = 'http://localhost:8983/solr/game-players/update/'

GAME_PLAYER_QUERY = (
    'http://localhost:8983/solr/game-players/select'
    '?q=game_id%3A{game_id}%0A&wt=json&indent=true'
    '&rows=2000'
)

GAME_ID_QUERY = (
    'http://localhost:8983/solr/game-stats/select?q=*%3A*&'
    'sort=game_id+asc&fl=game_id&wt=json&indent=true&'
    'rows=10000&group=true&group.field=game_id'
)

TEAM_STATS_QUERY = (
    'http://localhost:8983/solr/league-team-stats/select'
    '?q=team_id%3A{team_id}&wt=json&indent=true'
)

player_entity_collection = defaultdict(set)

# Get complete game id list
r = requests.get(GAME_ID_QUERY).json()
game_ids = [group['groupValue'] for group in r['grouped']['game_id']['groups']]

# Do named entity extraction for each game record that we have
for game_id in game_ids:
    # Get a list of player records present at the game
    r = requests.get(GAME_PLAYER_QUERY.format(game_id=game_id))
    player_records = r.json()['response']['docs']

    # Make a list of player names that we should ignore in NE extraction
    player_names = [r['player_name'] for r in player_records]
    split_player_names = []
    for name in player_names:
        split_player_names += name.split(' ')

    skip_entities = set(player_names + split_player_names)

    # Grab team name information to hopefully avoid unneeded NE matches
    team_ids = set([r['team_id'] for r in player_records])
    team_queries = [TEAM_STATS_QUERY.format(team_id=t) for t in team_ids]
    team_names = [r['team_name']
                  for team_query in team_queries
                  for r in requests.get(team_query).json()['response']['docs']]

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
    # record will hold all references to named entities from every game. This
    # records will not be stored in the game-players core like the following
    # update.
    #
    # Approach #1. Be sure to comment out the other relevant section below!
    #for player in player_records:
        #player_entity_collection[player['player_name']] |= cleaned_entities

    # Update player records with references. This assumes that we're keeping
    # each player record separate per game. The named entities found for this
    # games commentary data is only associated with player data from the
    # same game_id
    #
    # Approach #2
    #cleaned_entities = list(named_entities.difference(skip_entities))
    #update_json = [{'id':p['id'], 'ne':cleaned_entities} for p in player_records]
    #data = json.dumps(update_json, encoding='latin-1')
    #req = urllib2.Request(solr_url, data, {'Content-Type': 'application/json'})
    #urllib2.urlopen(req)

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
#solr_url = 'http://localhost:8983/solr/player-commentary-ne-data/update/'
#player_er_records = []
#for player in player_records:
    #player_er_records.append({
        #'id': player['player_id'],
        #'player_name': player['player_name'],
        #'ne': list(player_entity_collection[player['player_name']])
    #})
#data = json.dumps(player_er_records)
#req = urllib2.Request(solr_url, data, {'Content-Type': 'application/json'})
#urllib2.urlopen(req)
