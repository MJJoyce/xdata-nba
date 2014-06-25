#!/usr/bin/env bash

sudo apt-get update
sudo apt-get install -y vim
sudo apt-get install -y python-software-properties
sudo add-apt-repository -y ppa:webupd8team/java
sudo apt-get update
#sudo apt-get install -y python
#sudo apt-get install -y python-dev
#sudo apt-get install -y python-pip
sudo apt-get install -y maven
sudo apt-get install -y git
sudo apt-get install -y oracle-java7-set-default

# Grab the latest GORA code
cd /home/vagrant
git clone http://git.apache.org/gora.git/
cd gora
mvn clean install

# Grab and configure Solr
cd /home/vagrant
wget http://mirror.reverse.net/pub/apache/lucene/solr/4.8.1/solr-4.8.1.tgz
tar xzf solr-4.8.1.tgz
cd solr-4.8.1/example
rm -rf solr
ln -s /vagrant/solr ./solr

#pip install -r /vagrant/requirements.txt
#python -m nltk.downloader book

echo "JAVA_HOME=/usr" >> /home/vagrant/.bashrc
