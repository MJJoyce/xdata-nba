# XDATA NBA

### Python Dependency Installation

If you're on OS X Mavericks you need to run the following before `pip install`-ing the dependencies.

`export ARCHFLAGS=-Wno-error=unused-command-line-argument-hard-error-in-future`

After cloning the code base, run `pip install -r requirements.txt` to install the necessary dependencies.

Then run `python -m nltk.downloader book` to download the corpus.

### Running Vagrant

Install Vagrant from [here](http://www.vagrantup.com/).

Install VirtualBox from [here](https://www.virtualbox.org/).

Run `vagrant up` in the root directory of this repo. This will take a while.

This will install a 64 bit Ubuntu virtual machine. Note that the `/vagrant` directory in the VM
is shared with the host machine (the same directory with the Vagrantfile, the root of this repo).

You will need to manually execute the provisioning script `/vagrant/vagrant_bootstrap.sh` to initialize the VM. Unfortunately some of the dependencies require user input during install so this step cannot be fully automated at this time.

SSH into the VM by running `vagrant ssh`.

Shut down the VM by running one of `vagrant [suspend | halt | destroy]`. Note that `destroy` deletes the VM, so you'll have to re-download
all of the dependencies in vagrant_bootstrap next time you call `vagrant up`.

### Starting Solr

If you need to manually start Solr, you can do so by running the following commands after SSH-ing into the VM.

```
cd solr-4.8.1/example/
java -Dsolr.solr.home=/vagrant/solr -jar start.jar > /tmp/solr-server-log.txt &

```

To test that solr has started successfully open up your browser and navigate to http://localhost:8983/solr where you will see the Solr dashboard.



### Loading the NBA dataset

There are scripts for ingesting the NBA data into Solr in the `/python-import`. You can run the scripts with the following to ingest all the NBA data.

```
cd python-import
python NBAIngest /path/to/nba/data
```
