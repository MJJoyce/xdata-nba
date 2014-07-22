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
from GamePlayerStats import parse_game_players_stats_file
import LeagueTeamStats

import SentimentAnalyser

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
GAME_PLAYER_STATS_CORE = 'game-player-stats/'
GAME_PLAYER_STATS_FILES_PER_THREAD = 10
LEAGUE_TEAM_STATS_CORE = 'league-team-stats/'
GAME_RESULTS_CORE = 'game-results/'

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
    league_team_stats_path = os.path.join(records_directory, 'teamdata.json')

    load_game_play_by_plays(playbyplay_dir)
    load_game_players(game_players_dir)
    load_commentary(commentary_dirs)
    load_game_comments(comments_dir)
    load_game_stats(stats_dir)
    load_game_player_stats(stats_dir)
    load_league_team_stats(league_team_stats_path)

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
    '''Load commentary data into a solr instance.

    :param commentary_dirs: A dict containing commentary directory
        information for loading into solr. Assuming the standard
        NBA data directory, commentary_dirs should contain keys
        listing the matching folder names for 'notebook', 'preview',
        and 'recaps'.
    '''
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

    # Join result set
    results = list(itertools.chain.from_iterable(results))

    # Send single hit to Solr
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

    # Train the sentiment analyser that we'll use when processing
    # all the game comments.
    logger.info('Training sentiment analyser for comment ingestion')
    SentimentAnalyser.train()
    logger.info('Sentiment analyser training complete')

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
    logger.info('Processing GameStats results for GameResults core update')
    sorted_results = sorted(results, key=lambda x: x['game_id'])

    game_results = []
    for team1, team2 in zip(sorted_results[::2], sorted_results[1::2]):
        if team1['game_id'] != team2['game_id']:
            err = (
                'Invalid records. Game_ids don\'t match. '
                'Indices: {} and {}'
            ).format(i, i+1)
            logger.critical(err)
            continue

        if team1['pts'] > team2['pts']:
            winner, loser = team1, team2
        elif team2['pts'] > team1['pts']:
            winner, loser = team2, team1
        else:
            err = (
                'Game resulted in a tie: game_id_1 {} game_id_2 {} '
                'team_1_id {} team_2_id {}'
            ).format(team1['game_id'], team2['game_id'],
                     team1['team_id'], team2['team_id'])
            logger.critical(err)
            continue

        game_results.append({
            'id': winner['game_id'],
            'game_id': winner['game_id'],
            'winner_id': winner['team_id'],
            'loser_id': loser['team_id'],
        })

    solr_url = SOLR_URL + GAME_RESULTS_CORE + 'update?commit=true'
    data = json.dumps(game_results, encoding='latin-1')
    req = urllib2.Request(solr_url, data, {'Content-Type': 'application/json'})
    urllib2.urlopen(req)

    logger.info('GameResults ingestion complete')

def load_game_player_stats(game_stats_dir):
    '''Load GameStats data into a solr instance.

    :param game_stats_dir: Directory containing XDATA NBA GameStats JSON
        files to load into a Solr instance.
    '''
    logger.info('Starting GamePlayerStats ingestion: ' + game_stats_dir)

    # Get a list of all the files we need to load
    data_files = [os.path.join(game_stats_dir, f)
                  for f in os.listdir(game_stats_dir)
                  if os.path.isfile(os.path.join(game_stats_dir, f))]

    # Determine the number of threads it will take to process that many files
    total_threads = len(data_files) / GAME_PLAYER_STATS_FILES_PER_THREAD
    # If the number of files isn't evenly divisible by the number of files
    # per thread that we want to use we need to compensate for the remainder.
    total_threads += 1 if len(data_files) % GAME_PLAYER_STATS_FILES_PER_THREAD else 0

    # Split the data files into chunks to pass to each thread.
    fpt = GAME_PLAYER_STATS_FILES_PER_THREAD
    split_data_files = [data_files[(fpt * index):(fpt * index) + fpt]
                        for index in range(total_threads)]

    # Process all the files!
    thread_pool = multiprocessing.Pool(total_threads)
    results = thread_pool.map(load_game_player_stats_files, split_data_files)
    thread_pool.close()
    thread_pool.join()

    # Join result set here
    results = list(itertools.chain.from_iterable(results))

    solr_url = SOLR_URL + GAME_PLAYER_STATS_CORE + 'update?commit=true'
    data = json.dumps(results, encoding='latin-1')
    req = urllib2.Request(solr_url, data, {'Content-Type': 'application/json'})
    urllib2.urlopen(req)

def load_league_team_stats(league_game_stats_file):
    logger.info('Starting LeagueTeamStats ingestion: ' + league_game_stats_file)

    results = LeagueTeamStats.parse_game_team_data_file(league_game_stats_file)

    solr_url = SOLR_URL + LEAGUE_TEAM_STATS_CORE + 'update?commit=true'
    data = json.dumps(results, encoding='latin-1')
    req = urllib2.Request(solr_url, data, {'Content-Type': 'application/json'})
    urllib2.urlopen(req)

    logger.info('Finished LeagueTeamStats ingestion: ' + league_game_stats_file)

def load_game_players_files(game_players_files):
    '''Load XDATA NBA GamePlayers files into Solr

    :param game_players_files: List of GamePlayers files to load into Solr
    '''
    results = [parse_game_players_file(f) for f in game_players_files]
    return list(itertools.chain.from_iterable(results))

def load_commentary_files(commentary_data_files):
    '''Load XDATA NBA commentary files into Solr

    :param files: Tuple of lists of files to load into Solr. Data should 
        be of the form:

        (
            [preview_files],
            [recap_files],
            [notebook_files],
        )
    '''
    preview_files = commentary_data_files[0]
    recap_files = commentary_data_files[1]
    notebook_files = commentary_data_files[2]

    results = [parse_commentary_files(preview_files[i],
                                       recap_files[i],
                                       notebook_files[i])
               for i in range(len(preview_files))]

    return list(itertools.chain.from_iterable(results))

def load_game_comments_files(game_comment_files):
    '''Load XDATA NBA GameComments files into Solr

    :param game_comment_files: List of GameComments files to load into Solr
    '''
    results = [parse_comment_files(f) for f in game_comment_files]
    return list(itertools.chain.from_iterable(results))

def load_game_play_by_play_files(game_play_by_play_files):
    '''Load XDATA NBA GamePlayByPlay files into Solr

    :param game_players_files: List of GamePlayByPlay files to load into Solr
    '''
    results = [parse_game_play_by_play_file(f) for f in game_play_by_play_files]
    return list(itertools.chain.from_iterable(results))

def load_game_stats_files(game_stats_files):
    '''Load XDATA NBA GameStats files into Solr

    :param game_stats_files: List of GameStats files to load into Solr
    '''
    results = [parse_game_stats_file(f) for f in game_stats_files]
    return list(itertools.chain.from_iterable(results))

def load_game_player_stats_files(game_stats_files):
    '''Load XDATA NBA Player stats from GameStats files into Solr

    :param game_stats_files: List of GameStats files to load into Solr
    '''
    results = [parse_game_players_stats_file(f) for f in game_stats_files]
    return list(itertools.chain.from_iterable(results))

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="XDATA NBA Ingester")
    parser.add_argument("records_directory", help="NBA data records directory")
    args = vars(parser.parse_args())

    load_records(**args)
