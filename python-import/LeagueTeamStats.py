import json
import logging
import os
import sys
import urllib2

logger = logging.getLogger('nba_ingest_logger')

def parse_game_team_data_file(team_data_file):
    path, file_name = os.path.split(team_data_file)

    with open(team_data_file) as json_in:
        data = json.load(json_in)

    logger.debug('Processing Team Data file: ' + file_name)

    records = []
    for record in data['resultSets'][0]['rowSet']:
        records.append({
            "id": record[0],
            "team_id": record[0],
            "team_name": record[1],
            "gp": record[2],
            "w": record[3],
            "l": record[4],
            "w_pct": record[5],
            "min": record[6],
            "fgm": record[7],
            "fga": record[8],
            "fg_pct": record[9],
            "fg3m": record[10],
            "fg3a": record[11],
            "fg3_pct": record[12],
            "ftm": record[13],
            "fta": record[14],
            "ft_pct": record[15],
            "oreb": record[16],
            "dreb": record[17],
            "reb": record[18],
            "ast": record[19],
            "tov": record[20],
            "stl": record[21],
            "blk": record[22],
            "blka": record[23],
            "pf": record[24],
            "pfd": record[25],
            "pts": record[26],
            "plus_minus": record[27],
            "cfid": record[28],
            "cfparams": record[29]
        })

    return records

if __name__ == '__main__':
    parse_game_team_data_file(sys.argv[1])
