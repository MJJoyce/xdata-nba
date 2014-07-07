# -*- mode: ruby -*-
# vi: set ft=ruby :

# Vagrantfile API/syntax version. Don't touch unless you know what you're doing!
VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
  config.vm.provision :shell, :path => "vagrant_bootstrap.sh"
  
  config.vm.define :vagrant_nba do |vagrant_nba|
    vagrant_nba.vm.box = "precise64"
    vagrant_nba.vm.box_url = "http://files.vagrantup.com/precise64.box"
    vagrant_nba.vm.network "forwarded_port", guest: 8983, host: 8983
    vagrant_nba.vm.hostname = "xdata.nba"
  end

  config.vm.provider "virtualbox" do |v|
      v.memory = 4096
      v.cpus = 4
  end
end
