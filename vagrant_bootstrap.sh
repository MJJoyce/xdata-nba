#! /bin/bash
##### Provision check ######

# The provision check is intended to not run the full provision script when a box has already been provisioned.
# At the end of this script, a file is created on the vagrant box, we'll check if it exists now.
echo "[vagrant provisioning] Checking if the box was already provisioned..."

if [ -e "/home/vagrant/.vagrant_provision" ]
then
  # Skipping provisioning if the box is already provisioned
  echo "[vagrant provisioning] The box is already provisioned..."
  exit
fi

##### Java installation #####
echo "[vagrant provisioning] Installing Java..."]
apt-get update
apt-get -y install curl
apt-get -y install python-software-properties # adds add-apt-repository
add-apt-repository -y ppa:webupd8team/java
apt-get update

# automatic install of the Oracle JDK 7
echo oracle-java7-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections

sudo apt-get -y install oracle-java7-set-default

export JAVA_HOME="/usr/lib/jvm/java-7-oracle/jre"

echo "export JAVA_HOME=\$(readlink -f /usr/bin/java | sed \"s:bin/java::\")" >> /home/vagrant/.bashrc

##### Simple necessities #####
echo "[vagrant provisioning] Installing simple necessities..."
apt-get install -y vim
apt-get install -y git
apt-get install -y maven

##### Gora installation #####
echo "[vagrant provisioning] Installing and building Gora..."]
cd /home/vagrant
git clone http://git.apache.org/gora.git/
cd gora
mvn clean install

##### Solr installation #####
echo "[vagrant provisioning] Installing Apache Solr..."
curl -O http://mirror.reverse.net/pub/apache/lucene/solr/4.8.1/solr-4.8.1.tgz

tar xzf solr-4.8.1.tgz
cd solr-4.8.1/example

##### Solr startup #####
java -Dsolr.solr.home=/vagrant/solr -jar start.jar > /tmp/solr-server-log.txt &

echo "[vagrant provisioning] Bootstrapping Apache Solr..."

sleep 1
while ! grep -m1 'Registered new searcher' < /tmp/solr-server-log.txt; do
  sleep 1
done

##### Provision check #####

# Create .vagrant_provision for the script to check on during a next vargant up.
echo "[vagrant provisioning] Creating .vagrant_provision file..."
touch .vagrant_provision
