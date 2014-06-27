import json
import logging
import os
import sys
import urllib2

logger = logging.getLogger('nba_ingest_logger')

def parse_game_stats_file(game_stats_file):
    path, file_name = os.path.split(game_stats_file)

    with open(game_stats_file) as json_in:
        data = json.load(json_in)

    logger.debug('Processing play-by-play file: ' + file_name)

    records = []
    linescores = []
    for obj in data['resultSets'][0]:
        if obj['name'] == 'GameSummary':
            game_summary = obj
        if obj['name'] == 'LineScore':
            linescores.append(obj)

    general_record = {}
    general_record['game_id'] = game_summary['GAME_ID']
    general_record['game_code'] = game_summary['GAMECODE']
    general_record['home_team_id'] = game_summary['HOME_TEAM_ID']
    general_record['visitor_team_id'] = game_summary['VISITOR_TEAM_ID']
    general_record['season'] = game_summary['SEASON']
    general_record['broadcaster'] = game_summary['NATL_TV_BROADCASTER_ABBREVIATION']

    for team_data in linescores:
        team_record = {}
        team_record['team_id'] = team_data['TEAM_ID']
        team_record['team_abbreviation'] = team_data['TEAM_ABBREVIATION']
        team_record['team_city_name'] = team_data['TEAM_CITY_NAME']
        team_record['team_wins_losses'] = team_data['TEAM_WINS_LOSSES']
        team_record['pts_qtr1'] = team_data['PTS_QTR1']
        team_record['pts_qtr2'] = team_data['PTS_QTR2']
        team_record['pts_qtr3'] = team_data['PTS_QTR3']
        team_record['pts_qtr4'] = team_data['PTS_QTR4']
        team_record['pts_ot1'] = team_data['PTS_OT1']
        team_record['pts_ot2'] = team_data['PTS_OT2']
        team_record['pts_ot3'] = team_data['PTS_OT3']
        team_record['pts_ot4'] = team_data['PTS_OT4']
        team_record['pts_ot5'] = team_data['PTS_OT5']
        team_record['pts_ot6'] = team_data['PTS_OT6']
        team_record['pts_ot7'] = team_data['PTS_OT7']
        team_record['pts_ot8'] = team_data['PTS_OT8']
        team_record['pts_ot9'] = team_data['PTS_OT9']
        team_record['pts_ot10'] = team_data['PTS_OT10']
        team_record['pts'] = team_data['PTS']
        records.append(team_record)

        team_record.update(general_record)
        records.append(team_record)

    return records

if __name__ == '__main__':
    parse_game_stats_file(sys.argv[1])
