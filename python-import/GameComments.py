from collections import defaultdict
import logging
import os
import sys

import SentimentAnalyser

logger = logging.getLogger('nba_ingest_logger')

def parse_comment_files(comment_file):
    logger.debug(comment_file)
    path, file_name = os.path.split(comment_file)
    file_name_split = file_name.split('_')
    game_id = file_name_split[0]
    source = file_name_split[1]

    logger.debug('Processing comment files for game: ' + str(game_id))

    records = []

    with open(comment_file, 'r') as comment_in:
        for index, line in enumerate(comment_in):
            split_line = line.split('::')

            records.append({
               'id': str(game_id) + '_' + str(index),
               'game_id': game_id,
               'comment_order': index,
               'commenter': split_line[0],
               'comment': split_line[1],
               'source': source,
               #'sentiment': SentimentAnalyser.classify(split_line[1])
            })
    return records

if __name__ == '__main__':
    print parse_comment_files(sys.argv[1])
