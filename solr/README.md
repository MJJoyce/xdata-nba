
Example Solr Home Directory
=============================

This directory is provided as an example of what a "Solr Home" directory
should look like.

It's not strictly necessary that you copy all of the files in this
directory when setting up a new instance of Solr, but it is recommended.


Basic Directory Structure
-------------------------

The Solr Home directory typically contains the following...

* solr.xml *

This is the primary configuration file Solr looks for when starting.
This file specifies the list of "SolrCores" it should load, and high 
level configuration options that should be used for all SolrCores.

Please see the comments in ./solr.xml for more details.

If no solr.xml file is found, then Solr assumes that there should be
a single SolrCore named "collection1" and that the "Instance Directory" 
for collection1 should be the same as the Solr Home Directory.

* Individual SolrCore Instance Directories *

nba-comments: Comments about a game from users

nba-gamestats: Game stats

nba-notebook: Notebook of the game after it occurs

nba-playbyplay: Play by play

nba-gameplayers: Players that participated in a game

nba-preview: Preview of game before it occurs

nba-recap: Recap of the game after it occurs

* A Shared 'lib' Directory *

Although solr.xml can be configured with an optional "sharedLib" attribute 
that can point to any path, it is common to use a "./lib" sub-directory of the 
Solr Home Directory.

* ZooKeeper Files *

When using SolrCloud using the embedded ZooKeeper option for Solr, it is 
common to have a "zoo.cfg" file and "zoo_data" directories in the Solr Home 
Directory.  Please see the SolrCloud wiki page for more details...

https://wiki.apache.org/solr/SolrCloud
