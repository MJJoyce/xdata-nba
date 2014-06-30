/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gov.nasa.jpl.xdata.nba.impoexpo.manager;

import gov.nasa.jpl.xdata.nba.impoexpo.GamePlayer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

import org.apache.gora.query.Query;
import org.apache.gora.query.Result;
import org.apache.gora.store.DataStore;
import org.apache.gora.store.DataStoreFactory;
import org.apache.hadoop.conf.Configuration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 * NBAManager provides a CLI for us to map NBA data to 
 * and from Apache Solr via Apache Gora.
 * Essentially, we parse JSON and populate Gora
 * objects in-memory before flushing them out to 
 * a SolrCloud cluster.
 * @param <GamePlayers>
 * @param <K>
 *
 */
public class NBAManager<GamePlayers, K> implements Manager{

  private final static Logger LOG = Logger.getLogger(NBAManager.class.getName()); 

  private DataStore<CharSequence, GamePlayer> dataStore;

  private static final String USAGE = 
      "NBAManager -parse <parserType> <inputFile>\n" +
          "             parserTypes    \n" +
          "               <parseComments>    - Parse structured .txt comments data\n" +
          "               <parseGamePlayers>  - Parse structured .json game player data\n" +
          "               <parseGamestats>   - Parse structured .json gamestats data\n" +
          "               <parseNotebook>    - Parse unstructured .txt notebook data\n" +
          "               <parsePlayByPlay>  - Parse structured .json play by play data\n" +
          "               <parsePreview>     - Parse unstructured .txt preview data\n" +
          "               <parseRecap>       - Parse unstructured .txt recap data\n" +
          "             inputFiles    \n" +
          "               <comments.txt>     - e.g. 0021000001_comments.txt\n" +
          "               <gamePlayers.json> - e.g. 0021000001_gamePlayers.json\n" +
          "               <gamestats.json>   - e.g. 0021000001_gamestats.json\n" +
          "               <notebook.txt>     - e.g. 0021000001_notebook.txt\n" +
          "               <playbyplay.json>  - e.g. 0021000001_playbyplay.json\n" +
          "               <preview.txt>      - e.g. 0021000001_preview.txt\n" +
          "               <recap.txt>        - e.g. 0021000001_recap.txt\n" +
          "           -get <id>\n" +
          "           -query <id>\n" +
          "           -query <id> <id>\n" +
          "           -delete <id>\n" +
          "           -deleteByQuery <id> <id>\n";

  public NBAManager() {
    try {
      init();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  private void init() throws IOException {
    //Data store objects are created from a factory. It is necessary to 
    //provide the key and value class. The datastore class is optional, 
    //and if not specified it will be read from the properties file
    dataStore = DataStoreFactory.getDataStore(CharSequence.class, GamePlayer.class,
        new Configuration());
  }

  /**
   * @param args
   */
  public static void main(String[] args) throws Exception {
    if(args.length < 2) {
      System.err.println(USAGE);
      System.exit(1);
    }

    @SuppressWarnings("rawtypes")
    NBAManager manager = new NBAManager();

    if("-parse".equals(args[0])) {
      manager.aquire(args[1],args[2]);
    } else if("-get".equals(args[0])) {
      manager.get(Long.parseLong(args[1]));
    } else if("-query".equals(args[0])) {
      if(args.length == 2) 
        manager.query(Long.parseLong(args[1]));
      else 
        manager.query(Long.parseLong(args[1]), Long.parseLong(args[2]));
    } else if("-delete".equals(args[0])) {
      manager.delete(Long.parseLong(args[1]));
    } else if("-deleteByQuery".equalsIgnoreCase(args[0])) {
      manager.deleteByQuery(Long.parseLong(args[1]), Long.parseLong(args[2]));
    } else {
      System.err.println(USAGE);
      System.exit(1);
    }

    manager.close();
  }

  private void close() {
    //It is very important to close the datastore properly, otherwise
    //some data loss might occur.
    if(dataStore != null)
      dataStore.close();
  }

  @Override
  public void aquire(String parseType, Object input) {

    try {
      // Read given JSON file
      LOG.info("Parsing file:" + input);
      FileReader reader = new FileReader((String) input);

      // Pare with JSON simple parser
      JSONParser jsonParser = new JSONParser();
      JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

      JSONArray result = (JSONArray) jsonObject.get("resultSets");
      JSONObject jObj = (JSONObject) result.get(0);
      JSONArray rows = (JSONArray) jObj.get("rowset");

      String[] inputlist = input.toString().split("/");
      String filename = inputlist[inputlist.length -1];
      String gameId = filename.substring(0,filename.indexOf('_'));

      if (parseType.equalsIgnoreCase("parseComments")) {
        long comments = 0;
        LOG.info("finished parsing file. Total number of comments:" + comments);
      } else if (parseType.equalsIgnoreCase("parseGamePlayers")) {
        long players = 0;
        for (int i = 0; i < rows.size(); i++) {
          // Store the gamePlayers with key id == gameId_playerId
          GamePlayer gpGamePlayer = parseGamePlayer(gameId, (JSONArray) rows.get(i));
          storeGamePlayers(gpGamePlayer.getId(), gpGamePlayer);
          ++players;
        }
        reader.close();
        LOG.info("finished parsing file. Total number of players:" + players);
      } else if (parseType.equalsIgnoreCase("parseGamestats")) {
        long stats = 0;
        LOG.info("finished parsing file. Total number of game stats:" + stats);
      } else if (parseType.equalsIgnoreCase("parseNotebook")) {
        long notebook = 0;
        LOG.info("finished parsing file. Total number of game stats:" + notebook);
      } else if (parseType.equalsIgnoreCase("parsePlayByPlay")) {
        long plays = 0;
        LOG.info("finished parsing file. Total number of game stats:" + plays);
      } else if (parseType.equalsIgnoreCase("parsePreview")) {
        long preview = 0;
        LOG.info("finished parsing file. Total number of game stats:" + preview);
      } else if (parseType.equalsIgnoreCase("parseRecap")) {
        long recaps = 0;
        LOG.info("finished parsing file. Total number of game stats:" + recaps);
      }
    } catch (FileNotFoundException ex) {
      ex.printStackTrace();
    } catch (IOException ex) {
      ex.printStackTrace();
    } catch (ParseException ex) {
      ex.printStackTrace();
    } catch (NullPointerException ex) {
      ex.printStackTrace();
    }
  }

  /** Parses a single log line in combined log format using StringTokenizers */
  private GamePlayer parseGamePlayer(String gameId, JSONArray object) throws ParseException {
    // Construct and return GamePlayer object
    GamePlayer gameplayer = GamePlayer.newBuilder().build();
    gameplayer.setId(gameId + "_" + (String) object.get(0).toString());
    gameplayer.setPlayerId(((Long) object.get(0)).intValue());
    gameplayer.setPlayerName((String) object.get(1).toString());
    gameplayer.setTeamId(((Long) object.get(2)).intValue());
    gameplayer.setTeamCity((String) object.get(3).toString());

    return gameplayer;
  }

  private void storeGamePlayers(CharSequence id, GamePlayer gamePlayer) {
    LOG.info("Storing player with id: " + id + " in: " + dataStore.toString());
    dataStore.put(id, gamePlayer);

  }

  @Override
  public void deleteByQuery(Object key, Object value) {
    //Constructs a query from the dataStore. The matching rows to this query will be deleted
    Query<CharSequence, GamePlayer> query = dataStore.newQuery();
    //set the properties of query
    query.setStartKey((CharSequence) key);
    query.setEndKey((CharSequence) value);

    dataStore.deleteByQuery(query);
    LOG.info("GamePlayers with keys between " + key.toString() + " and " + value.toString() + " are deleted");

  }

  @Override
  public void delete(Object key) {
    dataStore.delete((CharSequence) key);
    dataStore.flush(); //write changes may need to be flushed before
    //they are committed 
    LOG.info("GamePlayer with key:" + key.toString() + " deleted");

  }

  @Override
  public void query(Object key, Object value) {
    org.apache.gora.query.Query<CharSequence, GamePlayer> query = dataStore.newQuery();
    query.setStartKey((CharSequence) key);
    query.setEndKey((CharSequence) value);
    Result<CharSequence, GamePlayer> result = query.execute();
    try {
      printResult(result);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Override
  public void query(Object key) {
    org.apache.gora.query.Query<CharSequence, GamePlayer> query = dataStore.newQuery();
    query.setStartKey((CharSequence) key);
    Result<CharSequence, GamePlayer> result = query.execute();
    try {
      printResult(result);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void get(Object key) {
    GamePlayer player = dataStore.get((CharSequence) key);
    printPlayer(player);

  }

  /** Pretty prints the gameplayer object to stdout */
  private void printPlayer(GamePlayer player) {
    if(player == null) {
      System.out.println("No result to show"); 
    } else {
      System.out.println(player.toString());
    }
  }

  private void printResult(Result<CharSequence, GamePlayer> result) throws IOException, Exception {

    while(result.next()) { //advances the Result object and breaks if at end
      CharSequence resultKey = result.getKey(); //obtain current key
      GamePlayer resultGameplayer = result.get(); //obtain current value object

      //print the results
      System.out.println(resultKey + ":");
      printPlayer(resultGameplayer);
    }

    System.out.println("Number of gameplayers from the query:" + result.getOffset());
  }

}
