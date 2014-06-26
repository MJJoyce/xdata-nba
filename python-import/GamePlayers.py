import requests
import json
import sys
import os
import re
import solr
import subprocess
import string
import urllib2

def parse_game_players_file(game_players_file):
    path, file_name = os.path.split(game_players_file)
    game_id = file_name.split('_')[0]

    with open(game_players_file) as json_in:
        data = json.load(json_in)

    print "Processing file: ", file_name

    records = []
    for record in data['resultSets'][0]['rowset']:
        player_id = record[0]
        player_name = record[1]
        team_id = record[2]
        team_city = record[3]

        records.append({
            'id': str(game_id) + '_' + str(player_id),
            'game_id': game_id,
            'player_id': player_id,
            'player_name': player_name,
            'team_id': team_id,
            'team_city': team_city
        })

    url = 'http://localhost:8983/solr/game-players/update?commit=true'
    data = json.dumps(records)
    req = urllib2.Request(url, data, {'Content-Type': 'application/json'})
    urllib2.urlopen(req)

    print "\tLoaded ", len(records), " records"

if __name__ == '__main__':
    parse_game_players_file(sys.argv[1])
