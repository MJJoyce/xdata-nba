# -*- mode: ruby -*-
# vi: set ft=ruby :

# Vagrantfile API/syntax version. Don't touch unless you know what you're doing!
VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
  config.vm.box = "hashicorp/precise64"

  # Setup port forwarding for solr
  config.vm.network "forwarded_port", guest: 8983, host: 8983

  # Use provision script
  config.vm.provision :shell, path: "vagrant_bootstrap.sh"
end
