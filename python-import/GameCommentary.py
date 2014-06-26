import logging
import os
import sys

logger = logging.getLogger('nba_ingest_logger')

def parse_commentary_files(preview_file, recap_file, notebook_file):
    game_id = preview_file.split('_')[0]
    logger.debug('Processing commentary files for game: ' + str(game_id))

    try:
        with open(preview_file, 'r') as preview_in:
            preview_data = preview_in.read()
    except UnicodeDecodeError:
        logger.warning("Unable to decode file: " + preview_file)
        preview_data = ""

    try:
        with open(recap_file, 'r') as recap_in:
            recap_data = recap_in.read()
    except UnicodeDecodeError:
        logger.warning("Unable to decode file: " + recap_file)
        recap_data = ""

    try:
        with open(notebook_file, 'r') as notebook_in:
            notebook_data = notebook_in.read()
    except UnicodeDecodeError:
        logger.warning("Unable to decode file: " + notebook_file)
        notebook_data = ""

    record = [{
        'game_id': game_id,
        'preview': preview_data,
        'recap': recap_data,
        'notebook': notebook_data
    }]

    return record

if __name__ == '__main__':
    parse_commentary_files(sys.argv[1], sys.argv[2], sys.argv[3])

