#!/usr/bin/python
# -*- coding: utf-8 -*-

import argparse
import itertools
import json
import logging
import multiprocessing
import os
import urllib2

from GamePlayers import parse_game_players_file
from GameCommentary import parse_commentary_files
from GameComments import parse_comment_files
from GamePlayByPlay import parse_game_play_by_play_file
from GameStats import parse_game_stats_file

logger = logging.getLogger('nba_ingest_logger')
logger.setLevel(logging.DEBUG)

fh = logging.FileHandler('nba_ingest.log')
fh.setLevel(logging.INFO)
dh = logging.FileHandler('nba_ingest_debug.log')
dh.setLevel(logging.DEBUG)

formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
fh.setFormatter(formatter)
dh.setFormatter(formatter)

logger.addHandler(fh)
logger.addHandler(dh)

SOLR_URL = 'http://localhost:8983/solr/'
GAME_PLAYERS_CORE = 'game-players/'
GAME_PLAYER_FILES_PER_THREAD = 10
GAME_COMMENTARY_CORE = 'game-commentary/'
GAME_COMMENTARY_FILES_PER_THREAD = 10
GAME_COMMENTS_CORE = 'game-comments/'
GAME_COMMENTS_FILES_PER_THREAD = 10
GAME_PLAY_BY_PLAY_CORE = 'game-play-by-play/'
GAME_PLAY_BY_PLAY_FILES_PER_THREAD = 10
GAME_STATS_CORE = 'game-stats/'
GAME_STATS_FILES_PER_THREAD = 10


def load_records(records_directory):
    '''Load XDATA NBA Records.

    :param records_directory: The directory containing the NBA data records
        to ingest into solr. The passed directory should have the following
        form:

        nba
        ├── Game.ID
        ├── comments
        ├── espn_search_urls.txt
        ├── gameplayers
        ├── gamestats
        ├── notebook
        ├── playbyplay
        ├── preview
        ├── recaps
        ├── teamdata.json
        └── yahoo_search_urls.txt
    '''
    logger.info('Starting data ingest at: ' + records_directory)

    game_players_dir = os.path.join(records_directory, 'gameplayers')
    commentary_dirs = {
        'preview': os.path.join(records_directory, 'preview'),
        'recap': os.path.join(records_directory, 'recaps'),
        'notebook': os.path.join(records_directory, 'notebook')
    }
    comments_dir = os.path.join(records_directory, 'comments')
    playbyplay_dir = os.path.join(records_directory, 'playbyplay')
    stats_dir = os.path.join(records_directory, 'gamestats')

    load_game_play_by_plays(playbyplay_dir)
    load_game_players(game_players_dir)
    load_commentary(commentary_dirs)
    load_game_comments(comments_dir)
    load_game_stats(stats_dir)

    logger.info('Data ingest complete for: ' + records_directory)

def load_game_players(game_players_dir):
    '''Load GamePlayers data into a solr instance.

    :param game_players_dir: Directory containing XDATA NBA GamePlayers JSON
        JSOn files to load into a Solr instance.
    '''
    logger.info('Starting GamePlayers ingestion: ' + game_players_dir)

    # Get a list of all the files we need to load
    data_files = [os.path.join(game_players_dir, f)
                  for f in os.listdir(game_players_dir)
                  if os.path.isfile(os.path.join(game_players_dir, f))]

    # Determine the number of threads it will take to process that many files
    total_threads = len(data_files) / GAME_PLAYER_FILES_PER_THREAD
    # If the number of files isn't evenly divisible by the number of files
    # per thread that we want to use we need to compensate for the remainder.
    total_threads += 1 if len(data_files) % GAME_PLAYER_FILES_PER_THREAD else 0

    # Split the data files into chunks to pass to each thread.
    fpt = GAME_PLAYER_FILES_PER_THREAD
    split_data_files = [data_files[(fpt * index):(fpt * index) + fpt]
                        for index in range(total_threads)]

    # Process all the files!
    thread_pool = multiprocessing.Pool(total_threads)
    results = thread_pool.map(load_game_players_files, split_data_files)
    thread_pool.close()
    thread_pool.join()

    # Join result set here
    results = list(itertools.chain.from_iterable(results))

    # Send single hit to Solr here
    solr_url = SOLR_URL + GAME_PLAYERS_CORE + 'update?commit=true'
    data = json.dumps(results)
    req = urllib2.Request(solr_url, data, {'Content-Type': 'application/json'})
    urllib2.urlopen(req)

    logger.info('GamePlayers ingestions complete')

def load_commentary(commentary_dirs):
    ''''''
    logger.info('Starting Commentary ingestion: ' + str(commentary_dirs))

    preview_dir = commentary_dirs['preview']
    recap_dir = commentary_dirs['recap']
    notebook_dir = commentary_dirs['notebook']

    # Get lists of all the files we need to load
    preview_files = [os.path.join(preview_dir, f)
                  for f in os.listdir(preview_dir)
                  if os.path.isfile(os.path.join(preview_dir, f))]
    preview_files.sort()

    recap_files = [os.path.join(recap_dir, f)
                  for f in os.listdir(recap_dir)
                  if os.path.isfile(os.path.join(recap_dir, f))]
    recap_files.sort()

    notebook_files = [os.path.join(notebook_dir, f)
                  for f in os.listdir(notebook_dir)
                  if os.path.isfile(os.path.join(notebook_dir, f))]
    notebook_files.sort()

    # Determine the number of threads it will take to process that many files
    total_threads = len(preview_files) / GAME_COMMENTARY_FILES_PER_THREAD
    # If the number of files isn't evenly divisible by the number of files
    # per thread that we want to use we need to compensate for the remainder.
    total_threads += 1 if len(preview_files) % GAME_COMMENTARY_FILES_PER_THREAD else 0

    # Split the data files into chunks to pass to each thread.
    fpt = GAME_COMMENTARY_FILES_PER_THREAD
    split_preview_files = [preview_files[(fpt * index):(fpt * index) + fpt]
                           for index in range(total_threads)]
    split_recap_files = [recap_files[(fpt * index):(fpt * index) + fpt]
                        for index in range(total_threads)]
    split_notebook_files = [notebook_files[(fpt * index):(fpt * index) + fpt]
                            for index in range(total_threads)]

    split_data_files = [(split_preview_files[i], split_recap_files[i], split_notebook_files[i])
                       for i in range(len(split_preview_files))]

    # Process all the files!
    thread_pool = multiprocessing.Pool(total_threads)
    results = thread_pool.map(load_commentary_files, split_data_files)
    thread_pool.close()
    thread_pool.join()

    # Join result set here
    results = list(itertools.chain.from_iterable(results))

    # Send single hit to Solr here
    solr_url = SOLR_URL + GAME_COMMENTARY_CORE + 'update?commit=true'
    data = json.dumps(results, encoding='latin-1')
    req = urllib2.Request(solr_url, data, {'Content-Type': 'application/json'})
    urllib2.urlopen(req)

    logger.info('Commentary ingestions complete')

def load_game_comments(game_comments_dir):
    '''Load GameComments data into a solr instance.

    :param game_comments_dir: Directory containing XDATA NBA GameComments JSON
        files to load into a Solr instance.
    '''
    logger.info('Starting GameComments ingestion: ' + game_comments_dir)

    # Get a list of all the files we need to load
    data_files = [os.path.join(game_comments_dir, f)
                  for f in os.listdir(game_comments_dir)
                  if os.path.isfile(os.path.join(game_comments_dir, f))]

    # Determine the number of threads it will take to process that many files
    total_threads = len(data_files) / GAME_COMMENTS_FILES_PER_THREAD
    # If the number of files isn't evenly divisible by the number of files
    # per thread that we want to use we need to compensate for the remainder.
    total_threads += 1 if len(data_files) % GAME_COMMENTS_FILES_PER_THREAD else 0

    # Split the data files into chunks to pass to each thread.
    fpt = GAME_COMMENTS_FILES_PER_THREAD
    split_data_files = [data_files[(fpt * index):(fpt * index) + fpt]
                        for index in range(total_threads)]

    # Process all the files!
    thread_pool = multiprocessing.Pool(total_threads)
    results = thread_pool.map(load_game_comments_files, split_data_files)
    thread_pool.close()
    thread_pool.join()

    # Join result set here
    results = list(itertools.chain.from_iterable(results))

    # Send single hit to Solr here
    solr_url = SOLR_URL + GAME_COMMENTS_CORE + 'update?commit=true'
    data = json.dumps(results, encoding='latin-1')
    req = urllib2.Request(solr_url, data, {'Content-Type': 'application/json'})
    urllib2.urlopen(req)

    logger.info('GameComments ingestions complete')

def load_game_play_by_plays(game_play_by_play_dir):
    '''Load GamePlayByPlay data into a solr instance.

    :param game_play_by_play_dir: Directory containing XDATA NBA
        GamePlayByPlay JSON files to load into a Solr instance.
    '''
    logger.info('Starting GamePlayByPlay ingestion: ' + game_play_by_play_dir)

    # Get a list of all the files we need to load
    data_files = [os.path.join(game_play_by_play_dir, f)
                  for f in os.listdir(game_play_by_play_dir)
                  if os.path.isfile(os.path.join(game_play_by_play_dir, f))]

    # Determine the number of threads it will take to process that many files
    total_threads = len(data_files) / GAME_PLAY_BY_PLAY_FILES_PER_THREAD
    # If the number of files isn't evenly divisible by the number of files
    # per thread that we want to use we need to compensate for the remainder.
    total_threads += 1 if len(data_files) % GAME_PLAY_BY_PLAY_FILES_PER_THREAD else 0

    # Split the data files into chunks to pass to each thread.
    fpt = GAME_PLAY_BY_PLAY_FILES_PER_THREAD
    split_data_files = [data_files[(fpt * index):(fpt * index) + fpt]
                        for index in range(total_threads)]

    # Process all the files!
    thread_pool = multiprocessing.Pool(total_threads)
    results = thread_pool.map(load_game_play_by_play_files, split_data_files)
    thread_pool.close()
    thread_pool.join()

    # Join result set here
    results = list(itertools.chain.from_iterable(results))

    solr_url = SOLR_URL + GAME_PLAY_BY_PLAY_CORE + 'update?commit=true'
    num_splits = 10
    files_per_split = int(len(results)) / num_splits
    for i in range(num_splits + 1):
        logger.info("Sending PlayByPlay split #" + str(i))
        if i == num_splits:
            data = json.dumps([results[-1]])
        else:
            data = json.dumps(results[i*files_per_split:(i+1)*files_per_split])

        req = urllib2.Request(solr_url, data, {'Content-Type': 'application/json'})
        urllib2.urlopen(req)

    logger.info('GamePlayByPlay ingestions complete')

def load_game_stats(game_stats_dir):
    '''Load GameStats data into a solr instance.

    :param game_stats_dir: Directory containing XDATA NBA
        GameStats JSON files to load into a Solr instance.
    '''
    logger.info('Starting GameStats ingestion: ' + game_stats_dir)

    # Get a list of all the files we need to load
    data_files = [os.path.join(game_stats_dir, f)
                  for f in os.listdir(game_stats_dir)
                  if os.path.isfile(os.path.join(game_stats_dir, f))]

    # Determine the number of threads it will take to process that many files
    total_threads = len(data_files) / GAME_STATS_FILES_PER_THREAD
    # If the number of files isn't evenly divisible by the number of files
    # per thread that we want to use we need to compensate for the remainder.
    total_threads += 1 if len(data_files) % GAME_STATS_FILES_PER_THREAD else 0

    # Split the data files into chunks to pass to each thread.
    fpt = GAME_STATS_FILES_PER_THREAD
    split_data_files = [data_files[(fpt * index):(fpt * index) + fpt]
                        for index in range(total_threads)]

    # Process all the files!
    thread_pool = multiprocessing.Pool(total_threads)
    results = thread_pool.map(load_game_stats_files, split_data_files)
    thread_pool.close()
    thread_pool.join()

    # Join result set here
    results = list(itertools.chain.from_iterable(results))

    solr_url = SOLR_URL + GAME_STATS_CORE + 'update?commit=true'
    data = json.dumps(results, encoding='latin-1')
    req = urllib2.Request(solr_url, data, {'Content-Type': 'application/json'})
    urllib2.urlopen(req)

    logger.info('GameStats ingestions complete')

def load_game_players_files(game_players_files):
    '''Load XDATA NBA GamePlayers files into Solr

    :param game_players_files: List of GamePlayers files to load into Solr
    '''
    results = [parse_game_players_file(f) for f in game_players_files]
    return list(itertools.chain.from_iterable(results))

def load_commentary_files(commentary_data_files):
    ''''''
    preview_files = commentary_data_files[0]
    recap_files = commentary_data_files[1]
    notebook_files = commentary_data_files[2]

    results = [parse_commentary_files(preview_files[i],
                                       recap_files[i],
                                       notebook_files[i])

               for i in range(len(preview_files))]
    return list(itertools.chain.from_iterable(results))

def load_game_comments_files(game_comment_files):
    results = [parse_comment_files(f) for f in game_comment_files]
    return list(itertools.chain.from_iterable(results))

def load_game_play_by_play_files(game_play_by_play_files):
    ''''''
    results = [parse_game_play_by_play_file(f) for f in game_play_by_play_files]
    return list(itertools.chain.from_iterable(results))

def load_game_stats_files(game_stats_files):
    ''''''
    results = [parse_game_stats_file(f) for f in game_stats_files]
    return list(itertools.chain.from_iterable(results))

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="XDATA NBA Ingester")
    parser.add_argument("records_directory", help="NBA data records directory")
    args = vars(parser.parse_args())

    load_records(**args)
