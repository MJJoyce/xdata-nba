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
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import org.apache.avro.util.Utf8;
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
@SuppressWarnings("hiding")
public class NBAManager<GamePlayers> {
  
  private final static Logger LOG = Logger.getLogger(NBAManager.class.getName()); 
  
  private DataStore<CharSequence, GamePlayer> dataStore;
  
  private static final String USAGE = "NBAManager -parse <input_player_json_file>\n" +
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
      manager.parse(args[1]);
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
    // TODO Auto-generated method stub
    
  }

  private void deleteByQuery(long parseLong, long parseLong2) {
    // TODO Auto-generated method stub
    
  }

  private void delete(long parseLong) {
    // TODO Auto-generated method stub
    
  }

  private void query(long parseLong, long parseLong2) {
    // TODO Auto-generated method stub
    
  }

  private void query(long parseLong) {
    // TODO Auto-generated method stub
    
  }

  private void get(long parseLong) {
    // TODO Auto-generated method stub
    
  }

  private void parse(String input) throws IOException, ParseException, Exception {
    LOG.info("Parsing file:" + input);
    BufferedReader reader = new BufferedReader(new FileReader(input));
    long players = 0;
    try {
      JsonParser parser = new JsonFactory().createParser(reader);
      do {
        GamePlayer gamePlayer = read(parser);
        
        if(gamePlayer != null) {
          //store the gamePlayers with key id == game_id:player_id
          storeGamePlayers(gamePlayer.getId(), gamePlayer);
          ++players;
        }
        
        //players = reader.readLine();
      } while(players != 0L);
      
    } finally {
      reader.close();  
    }
    LOG.info("finished parsing file. Total number of players:" + players);
  }
  
  private void storeGamePlayers(CharSequence id, GamePlayer gamePlayer) {
    LOG.info("Storing player with id: " + id + "in: " + dataStore.toString());
    dataStore.put(id, gamePlayer);
    
  }

  /** 
   * Using the provided {@link org.codehaus.jackson.JsonParser}, which has
   * been constructed using input JSON, we parse out individual GamePlayer data.
   * @throws IOException 
   * @throws JsonParseException 
   */
  private GamePlayer read(JsonParser jp) throws ParseException, JsonParseException, IOException {
    // Sanity check: verify that we got "Json Object":
    if (jp.nextToken() != JsonToken.START_OBJECT) {
      throw new IOException("Expected data to start with an Object");
    }
    GamePlayer gamePlayer = GamePlayer.newBuilder().build();
    // Iterate over object fields:
    while (jp.nextToken() != JsonToken.END_OBJECT) {
     //set variables for composite primary key which is game_id:player_id
     String gameId = null;
     String playerId = null;
     
     String fieldName = jp.getCurrentName();
     // Let's move to value
     jp.nextToken();
     if (fieldName.equals("player_id")) {
       gamePlayer.setPlayerId(jp.getIntValue());
     } else if (fieldName.equals("player_name")) {
       gamePlayer.setPlayerName(jp.getText());
     } else if (fieldName.equals("team_id")) {
       gamePlayer.setTeamId(jp.getIntValue());
     } else if (fieldName.equals("team_city")) {
       gamePlayer.setTeamCity(jp.getText());
     } else { // ignore, or signal error?
      throw new IOException("Unrecognized field '"+fieldName+"'");
     }
     gamePlayer.setId(gameId + ":" + playerId);
    }
    jp.close(); // important to close both parser and underlying File reader
    
    return gamePlayer;
  }

}
