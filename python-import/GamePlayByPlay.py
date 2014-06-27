import json
import logging
import os
import sys
import urllib2

logger = logging.getLogger('nba_ingest_logger')

def parse_game_play_by_play_file(game_play_by_play_file):
    path, file_name = os.path.split(game_play_by_play_file)

    with open(game_play_by_play_file) as json_in:
        data = json.load(json_in)

    logger.debug('Processing play-by-play file: ' + file_name)

    records = []
    for record in data['resultSets'][0]['rowSet']:
        records.append({
            'id': str(record[0]) + '_' + str(record[1]),
            'game_id': record[0],
            'event_num': record[1],
            'event_msg_type': record[2],
            'event_msg_action_type': record[3],
            'period': record[4],
            'sc_time_string': record[5],
            'pc_time_string': record[6],
            'home_description': record[7],
            'neutral_description': record[8],
            'visit_description': record[9],
            'score': record[10],
            'score_margin': record[11]
        })

    return records

if __name__ == '__main__':
    parse_game_play_by_play_file(sys.argv[1])
