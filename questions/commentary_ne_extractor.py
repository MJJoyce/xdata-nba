import argparse
import nltk
import pprint
import re
import requests
import sys

SOLR_URL = 'http://localhost:8983/solr/'
CORE = 'game-commentary/'
QUERY_STRING = '?q=game_id%3A{game_id}&wt=json&indent=true'

# Retrieve all commentary data for future processing
r = requests.get(SOLR_URL + CORE + 'select/' + '?q=*:*&wt=json&rows=1236457890')
commentary_data = {}
for r in r.json()['response']['docs']:
    commentary_data[r['game_id']] = r

def detect_named_entities_in_commentary(game_id):
    '''Retrieve named entities in commentary data for a specific game.

    :param game_id: The game Id for which commentary data should be analyzed.
    :type game_id: string

    :returns: A set of named entities extracted from commentary data.
    '''
    doc = commentary_data[game_id]

    prepared_preview = prep_for_named_entity_extraction(doc['preview'])
    prepared_recap = prep_for_named_entity_extraction(doc['recap'])
    prepared_notebook = prep_for_named_entity_extraction(doc['notebook'])

    preview_ne = set(extract_entity_names(prepared_preview))
    recap_ne = set(extract_entity_names(prepared_recap))
    notebook_ne = set(extract_entity_names(prepared_notebook))

    named_entities = preview_ne.union(recap_ne.union(notebook_ne))
    return named_entities

def prep_for_named_entity_extraction(string):
    '''Prepare a string for NLTK-based named entity extraction.

    :param string: The string to run NE extraction over.
    :type string: string

    :returns: The processed string which has been tokenized, part-of-speech
        tagged, and ne chunked. The string is now in a Tree format that can
        be traversed to extract Named Entities.
    '''
    return nltk.ne_chunk(nltk.pos_tag(nltk.word_tokenize(string)), binary=True)

def extract_entity_names(tree):
    '''Parse an NLTK ne_chunk returned Tree for NE tags.

    Check the NLTK ne_chunk docs out for additional information on the
    expected format.
    http://www.nltk.org/api/nltk.chunk.html
    '''
    entity_names = []
    
    if hasattr(tree, 'node') and tree.node:
        if tree.node == 'NE':
            entity_names.append(' '.join([child[0] for child in tree]))
        else:
            for child in tree:
                entity_names.extend(extract_entity_names(child))
                
    return entity_names

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='XDATA NBA NE Extractor')
    parser.add_argument('game_id', help='NBA data game id')
    args = vars(parser.parse_args())

    print detect_named_entities_in_commentary(**args)
