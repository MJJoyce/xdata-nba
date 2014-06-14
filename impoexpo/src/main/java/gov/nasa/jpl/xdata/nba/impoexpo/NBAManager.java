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
package gov.nasa.jpl.xdata.nba.impoexpo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.apache.gora.store.DataStore;
import org.apache.gora.store.DataStoreFactory;
import org.apache.hadoop.conf.Configuration;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

/**
 * NBAManager provides a CLI for us to map NBA data to 
 * and from Apache Solr via Apache Gora.
 * Essentially, we parse JSON and populate Gora
 * objects in-memory before flushing them out to 
 * a SolrCloud cluster.
 * @param <GamePlayers>
 *
 */
public class NBAManager<GamePlayers> implements Manager{

  private final static Logger LOG = Logger.getLogger(NBAManager.class.getName()); 

  private DataStore<CharSequence, GamePlayer> dataStore;

  private static final String USAGE = 
          "NBAManager -parse <input_player_json_file>\n" +
          "           -get <game_id:player_id>\n" +
          "           -query <game_id:player_id>\n" +
          "           -query <startGame_id:player_id> <endGame_id:player_id>\n" +
          "           -delete <game_id:player_id>\n" +
          "           -deleteByQuery <startGame_id:player_id> <endGame_id:player_id>\n";

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
      manager.aquire(args[1]);
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
  public void aquire(Object input) {
    LOG.info("Parsing file:" + input);
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader((String) input));
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    long players = 0;
    try {
      JsonParser parser = new JsonFactory().createParser(reader);
      ArrayList<GamePlayer> gamePlayers = read(parser);

      if(gamePlayers != null) {
        for (GamePlayer gamePlayer : gamePlayers) {
          //store the gamePlayers with key id == game_id:player_id
          storeGamePlayers(gamePlayer.getId(), gamePlayer);
          ++players;
        }
      }
    } catch (JsonParseException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    } finally {
      try {
        reader.close();
      } catch (IOException e) {
        e.printStackTrace();
      }  
    }
    LOG.info("finished parsing file. Total number of players:" + players);
  }

  /** 
   * Using the provided {@link org.codehaus.jackson.JsonParser}, which has
   * been constructed using input JSON, we parse out individual GamePlayer data.
   * @throws IOException 
   * @throws JsonParseException 
   */
  private ArrayList<GamePlayer> read(JsonParser jp) throws ParseException, JsonParseException, IOException {
    // Sanity check: verify that we got "Json Object":
    if (jp.nextToken() != JsonToken.START_OBJECT) {
      throw new IOException("Expected data to start with an Object");
    }
    ArrayList<GamePlayer> playerList = new ArrayList<GamePlayer>();
    // Iterate over object fields:
    while (jp.nextToken() != JsonToken.END_OBJECT) {
      if (jp.getCurrentToken().isNumeric()) {
        GamePlayer player = readGamePlayer(jp);
        playerList.add(player);
      }
    }
    jp.close(); // important to close both parser and underlying File reader
    return playerList;
  }

  private GamePlayer readGamePlayer(JsonParser jp) throws IOException {
    GamePlayer gamePlayer = GamePlayer.newBuilder().build();
    //set variables for composite primary key which is game_id_player_id
    String gameId;
    String playerId;

    gamePlayer.setPlayerId(jp.getIntValue());
    playerId = Integer.toString(jp.getIntValue());
    jp.nextToken();
    gamePlayer.setPlayerName(jp.getText());
    jp.nextToken();
    gamePlayer.setTeamId(jp.getIntValue());
    jp.nextToken();
    gamePlayer.setTeamCity(jp.getText());
    jp.nextToken();
    gamePlayer.setId("gameId" + "_" + playerId);
    jp.nextToken();
    return gamePlayer;

  }

  private void storeGamePlayers(CharSequence id, GamePlayer gamePlayer) {
    LOG.info("Storing player with id: " + id + "in: " + dataStore.toString());
    dataStore.put(id, gamePlayer);

  }

  @Override
  public void deleteByQuery(Object key, Object value) {
    // TODO Auto-generated method stub

  }

  @Override
  public void delete(Object key) {
    // TODO Auto-generated method stub

  }

  @Override
  public void query(Object key, Object value) {
    // TODO Auto-generated method stub

  }

  @Override
  public void query(Object key) {
    // TODO Auto-generated method stub

  }

  @Override
  public void get(Object key) {
    // TODO Auto-generated method stub

  }

}
