import json
import logging
import os
import sys
import urllib2

logger = logging.getLogger('nba_ingest_logger')

def parse_game_players_file(game_players_file):
    path, file_name = os.path.split(game_players_file)
    game_id = file_name.split('_')[0]

    with open(game_players_file) as json_in:
        data = json.load(json_in)

    logger.debug('Processing file: ' + file_name)

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

    return records

if __name__ == '__main__':
    parse_game_players_file(sys.argv[1])
