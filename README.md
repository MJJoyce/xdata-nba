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

This will install a 64 bit Ubuntu virtual machine and run vagrant_bootstrap.sh within it. Note that the `/vagrant` directory in the VM
is shared with the host machine (the same directory with the Vagrantfile, the root of this repo). vagrant_bootstrap.sh will only run the
first time you do `vagrant up`. You can run it again with `vagrant reload --provision`.

SSH into the VM by running `vagrant ssh`. 

Shut down the VM by running one of `vagrant [suspend | halt | destroy]`. Note that `destroy` deletes the VM, so you'll have to re-download
all of the dependencies in vagrant_bootstrap next time you call `vagrant up`.