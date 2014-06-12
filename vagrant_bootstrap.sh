#!/usr/bin/env bash

# Stop if there is an error
set -e

apt-get update
apt-get install -y unzip
apt-get install -y python python-dev python-pip maven curl git
apt-get install -y openjdk-7-jre-headless

pip install -r /vagrant/requirements.txt
python -m nltk.downloader book

# Grab Solr
wget http://mirror.reverse.net/pub/apache/lucene/solr/4.8.1/solr-4.8.1.zip
unzip solr-4.8.1.zip
