# XDATA NBA

### Python Dependency Installation

If you're on OS X Mavericks you need to run the following before <code>pip install</code>-ing the dependencies.

<code>export ARCHFLAGS=-Wno-error=unused-command-line-argument-hard-error-in-future</code>

After cloning the code base, run <code>pip install -r requirements.txt</code> to install the necessary dependencies.

Open Python and run <code>import nltk; nltk.download()</code>. Pick the book identifier and download those packages.

### Running Vagrant

Install Vagrant from [here](http://www.vagrantup.com/).

Install VirtualBox from [here](https://www.virtualbox.org/).

Run `vagrant up` in the root directory of this repo. This will take a while.

This will install a 32 bit Ubuntu virtual machine and run vagrant_bootstrap.sh within it. Note that the `/vagrant` directory in the vm
is shared with the host machine (the same directory with the Vagrantfile, the root of this repo). vagrant_bootstrap.sh will only run the
first time you do `vagrant up`, unless you run `vagrant reload --provision`.

SSH into the VM by running `vagrant ssh`. 

Shut down the VM by running one of `vagrant [suspend | halt | destroy]`. Note that `destroy` deletes the VM, so you'll have to re-download
all of the dependencies in vagrant_bootstrap next time you call `vagrant up`.