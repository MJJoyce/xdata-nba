#!/usr/bin/env bash

# Stop if there is an error
#set -e

apt-get update
apt-get install -y curl
apt-get install -y python python-dev python-pip maven git
apt-get install -y openjdk-7-jre-headless

## Grab the latest GORA code
cd /home/vagrant
git clone http://git.apache.org/gora.git/
cd gora
mvn clean compile

# Grab and configure Solr
cd /home/vagrant
curl -o solr-4.8.1.tgz http://mirror.reverse.net/pub/apache/lucene/solr/4.8.1/solr-4.8.1.tgz
#unzip solr-4.8.1.zip
tar xzf solr-4.8.1.tgz
cd solr-4.8.1/example
rm -rf solr
ln -s /vagrant/solr ./solr

pip install -r /vagrant/requirements.txt
python -m nltk.downloader book
