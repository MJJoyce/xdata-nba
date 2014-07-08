import json
import logging
import os
import sys
import urllib2
import datetime

logger = logging.getLogger('nba_ingest_logger')

def parse_game_stats_file(game_stats_file):
    path, file_name = os.path.split(game_stats_file)

    with open(game_stats_file) as json_in:
        data = json.load(json_in)

    logger.debug('Processing play-by-play file: ' + file_name)

    records = []

    #for obj in data['resultSets'][0]:
    for obj in data['resultSets']:
        if obj['name'] == 'GameSummary':
            game_summary = obj['rowSet'][0]
        if obj['name'] == 'LineScore':
            linescores = obj['rowSet']

    general_record = {}
    general_record['game_id'] = game_summary[2]
    general_record['game_code'] = game_summary[5]
    general_record['game_date_est'] = game_summary[0] + 'Z'
    general_record['home_team_id'] = game_summary[6]
    general_record['visitor_team_id'] = game_summary[7]
    general_record['season'] = game_summary[8]
    general_record['broadcaster'] = game_summary[11]

    for team_data in linescores:
        team_record = {}
        team_record['id'] = str(general_record['game_id']) + '_' + str(team_data[3])
        team_record['team_id'] = team_data[3]
        team_record['team_abbreviation'] = team_data[4]
        team_record['team_city_name'] = team_data[5]
        team_record['team_wins_losses'] = team_data[6]
        team_record['pts_qtr1'] = team_data[7]
        team_record['pts_qtr2'] = team_data[8]
        team_record['pts_qtr3'] = team_data[9]
        team_record['pts_qtr4'] = team_data[10]
        team_record['pts_ot1'] = team_data[11]
        team_record['pts_ot2'] = team_data[12]
        team_record['pts_ot3'] = team_data[13]
        team_record['pts_ot4'] = team_data[14]
        team_record['pts_ot5'] = team_data[15]
        team_record['pts_ot6'] = team_data[16]
        team_record['pts_ot7'] = team_data[17]
        team_record['pts_ot8'] = team_data[18]
        team_record['pts_ot9'] = team_data[19]
        team_record['pts_ot10'] = team_data[20]
        team_record['pts'] = team_data[21]
        records.append(team_record)

        team_record.update(general_record)
        records.append(team_record)

    return records

if __name__ == '__main__':
    parse_game_stats_file(sys.argv[1])
