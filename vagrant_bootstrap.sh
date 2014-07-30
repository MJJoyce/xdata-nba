#! /bin/bash
##### Java installation #####
echo "[vagrant provisioning] Installing Java..."]
apt-get update
apt-get -y install curl
apt-get -y install python-software-properties # adds add-apt-repository
add-apt-repository -y ppa:webupd8team/java
apt-get update

# automatic install of the Oracle JDK 7
echo oracle-java7-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections

apt-get -y install oracle-java7-set-default

export JAVA_HOME="/usr/lib/jvm/java-7-oracle/jre"

echo "export JAVA_HOME=\$(readlink -f /usr/bin/java | sed \"s:bin/java::\")" >> /home/vagrant/.bashrc

##### Simple necessities #####
echo "[vagrant provisioning] Installing simple necessities..."
apt-get install -y vim
apt-get install -y git
apt-get install -y maven

##### Python install #####
echo "[vagrant provisioning] Installing Python and relevant dependencies ..."
apt-get install -y python-dev
apt-get install -y python-pip
pip install -r /vagrant/requirements.txt
python -m nltk.downloader all

##### Gora installation #####
echo "[vagrant provisioning] Installing and building Gora..."]
cd /home/vagrant
git clone http://git.apache.org/gora.git/
chown -R vagrant gora
chgrp -R vagrant gora
cd gora
mvn clean install

##### Solr installation #####
echo "[vagrant provisioning] Installing Apache Solr..."
cd /home/vagrant
curl -O http://mirror.reverse.net/pub/apache/lucene/solr/4.8.1/solr-4.8.1.tgz

tar xzf solr-4.8.1.tgz
chown -R vagrant solr-4.8.1
chgrp -R vagrant solr-4.8.1
cd solr-4.8.1/example

##### Solr startup #####
java -Dsolr.solr.home=/vagrant/solr -jar start.jar > /tmp/solr-server-log.txt &

echo "[vagrant provisioning] Bootstrapping Apache Solr..."

sleep 1
while ! grep -m1 'Registered new searcher' < /tmp/solr-server-log.txt; do
  sleep 1
done

##### Run Python ingestion #####
if [ -d "../nba" ]; then
    cd /vagrant/python-import
    python NBAIngest.py ../nba
fi
