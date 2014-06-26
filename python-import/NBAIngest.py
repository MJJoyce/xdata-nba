#!/usr/bin/python
# -*- coding: utf-8 -*-

import argparse
import logging
#from multiprocessing import Process
import multiprocessing
import os

from GamePlayers import parse_game_players_file

logging.basicConfig(filename='nba_ingest.log', level=logging.INFO)

SOLR_URL = 'http://localhost:8983/solr/'
GAME_PLAYERS_CORE = 'game-players/'
GAME_PLAYER_FILES_PER_THREAD = 50

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
    logging.info('Starting data ingest at: ' + records_directory)

    game_players_dir = os.path.join(records_directory, 'gameplayers')

    load_game_players(game_players_dir)

    logging.info('Data ingest complete for: ' + records_directory)

def load_game_players(game_players_dir):
    '''Load GamePlayers data into a solr instance.

    :param game_players_dir: Directory containing XDATA NBA GamePlayers JSON
        JSOn files to load into a Solr instance.
    '''
    logging.info('Starting GamePlayers ingestion: ' + game_players_dir)

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
    thread_pool.map(load_game_players_files, split_data_files)

    logging.info('GamePlayers ingestions complete')

def load_game_players_files(game_players_files):
    '''Load XDATA NBA GamePlayers files into Solr

    :param game_players_files: List of GamePlayers files to load into Solr
    '''
    solr_url = SOLR_URL + GAME_PLAYERS_CORE + 'update?commit=true'

    for f in game_players_files:
        parse_game_players_file(f, solr_url)

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="XDATA NBA Ingester")
    parser.add_argument("records_directory", help="NBA data records directory")
    args = vars(parser.parse_args())

    load_records(**args)
