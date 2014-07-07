#!/usr/bin/python
# -*- coding: utf-8 -*-

import logging
import os
import sys
import urllib2

logger = logging.getLogger('nba_ingest_logger')

def parse_game_players_stats_file(game_stats_file):
    ''''''
    path, file_name = os.path.split(game_players_file)
    game_id = file_name.split('_')[0]

    with open(game_players_file) as json_in:
        data = json.load(json_in)

    logger.debug('Stripping player stats from: ' + file_name)


    for obj in data['resultSets']:
        if obj['name'] == 'PlayerStats':
            player_stats = obj
            break

    records = []
    for record in player_stats:
        records.append({
            # id is GAME_ID + '_' + PLAYER_ID
            'id': str(record[0]) + '_' + str(record[4]),
            'game_id': record[0],
            'team_id': record[1],
            'team_abbrev': record[2],
            'team_city': record[3],
            'player_id': record[4],
            'player_name': record[5],
            'start_position': record[6],
            'comment': record[7],
            'min': record[8],
            'fgm': record[9],
            'fga': record[10],
            'fg_pct': record[11],
            'fg3m': record[12],
            'fg3a': record[13],
            'fg3_pct': record[14],
            'ftm': record[15],
            'fta': record[16],
            'ft_pct': record[17],
            'oreb': record[18],
            'dreb': record[19],
            'reb': record[20],
            'ast': record[21],
            'stl': record[22],
            'blk': record[23],
            'to': record[24],
            'pf': record[25],
            'pts': record[26],
            'PLUS_MINUS': record[27],
        })

    return records

if __name__ == '__main__':
    parse_game_players_stats_file(sys.argv[1])

