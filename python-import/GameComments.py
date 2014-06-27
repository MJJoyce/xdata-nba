from collections import defaultdict
import logging
import os
import sys

logger = logging.getLogger('nba_ingest_logger')

def parse_comment_files(comment_file):
    logger.debug(comment_file)
    path, file_name = os.path.split(comment_file)
    file_name_split = file_name.split('_')
    game_id = file_name_split[0]
    source = file_name_split[1]

    logger.debug('Processing comment files for game: ' + str(game_id))

    records = defaultdict(list)

    with open(comment_file, 'r') as comment_in:
        for line in comment_in:
            split_line = line.split('::')
            commenter_id = split_line[0]
            comment = split_line[1]
            records[commenter_id].append(comment)

    record = [{'id': str(game_id) + '_' + str(commenter_id),
               'game_id': game_id,
               'commenter': commenter_id,
               'comments': ' '.join(records[commenter_id]),
               'source': source}
              for commenter_id in records]

    return record

if __name__ == '__main__':
    print parse_comment_files(sys.argv[1])
