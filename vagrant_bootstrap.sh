#!/usr/bin/env bash

# Stop if there is an error
set -e

apt-get update
apt-get install -y python python-dev maven curl git
curl https://bootstrap.pypa.io/get-pip.py > get-pip.py
python get-pip.py
rm get-pip.py
pip install -r /vagrant/requirements.txt
python -m nltk.downloader book
