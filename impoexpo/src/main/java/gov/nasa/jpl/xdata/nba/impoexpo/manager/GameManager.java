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

import gov.nasa.jpl.xdata.nba.impoexpo.structs.Game;
import gov.nasa.jpl.xdata.nba.impoexpo.structs.GameStats;
import gov.nasa.jpl.xdata.nba.impoexpo.structs.Notebook;
import gov.nasa.jpl.xdata.nba.impoexpo.structs.Preview;
import gov.nasa.jpl.xdata.nba.impoexpo.structs.Recap;
import gov.nasa.jpl.xdata.nba.impoexpo.parse.ParseUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.gora.query.Query;
import org.apache.gora.query.Result;
import org.apache.gora.store.DataStore;
import org.apache.gora.store.DataStoreFactory;
import org.apache.hadoop.conf.Configuration;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GameManager is an implementation of {@link Manager} 
 * the executes the ETL process for the Game data model, the
 * Avro JSON schema for which can be found in <code>/src/main/avro</code>.
 *
 */
public class GameManager implements Manager {

  private final static Logger LOG = LoggerFactory.getLogger(GameManager.class.getName()); 

  private DataStore<Integer, Game> dataStore;

  private static final String USAGE = 
      "NBAManager -aquire <previewText> <recapText> <notebookText> <gameStatsJSON>\n" +
          "           -get <gameId>\n" +
          "           -query <gameId>\n" +
          "           -query <gameId> <gameId>\n" +
          "           -delete <gameId>\n" +
          "           -deleteByQuery <gameId> <gameId>\n";


  public GameManager() {
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
    dataStore = DataStoreFactory.getDataStore(Integer.class, Game.class,
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

    GameManager manager = new GameManager();

    if("-aquire".equals(args[0])) {
      manager.aquire(args);
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
    // unused
  }


  @Override
  public void aquire(Object input) throws IOException {
    // unused
  }

  @Override
  public void aquire(String[] inputFiles) {
    try {
      if (inputFiles.length != 5) {
        throw new IOException("Wrong number of arguments: Expected 4, got: " 
            + (inputFiles.length - 1));
      }
      ArrayList<String> list = new ArrayList<String>();
      for (String string : inputFiles) {
        list.add(string);
      }
      Game game = Game.newBuilder().build();
      Preview preview = Preview.newBuilder().build();
      Recap recap = Recap.newBuilder().build();
      Notebook notebook = Notebook.newBuilder().build();
      GameStats gameStats = GameStats.newBuilder().build();
      // we start at arg 1 as arg 0 is '-aquire'
      for (int i = 1; i < list.size(); i++) {
        
        switch (i) {
        case 1: // switch to parsePreview
          preview = ParseUtil.parsePreview(preview, list.get(i));
          game.setPreview(preview);
          break;
        case 2: // switch to parseRecap
          recap = ParseUtil.parseRecap(recap, list.get(i));
          game.setRecap(recap);
          break;
        case 3: // switch to parseNotebook
          notebook = ParseUtil.parseNotebook(list.get(i));
          game.setNotebook(notebook);
          break;
        case 4: // switch to parseGameStats
          try {
            gameStats = ParseUtil.parseGameStats(list.get(i));
          } catch (ParseException e) {
            e.printStackTrace();
            break;
          }
          game.setGameStats(gameStats);
          break;
        default:
          LOG.warn("A parser case cannot be located for the input file at position {} "
              + "in input arguments therefore no processing will occur.", i);
          break;
        }
      }
      storeGame(game.getGameStats().getGameId(), game);
      LOG.info("Finished parsing files. Total number of Game' objects persisted:");// + games);
    } catch (FileNotFoundException ex) {
      ex.printStackTrace();
    } catch (IOException ex) {
      ex.printStackTrace();
    } catch (NullPointerException ex) {
      ex.printStackTrace();
    }

  }



  private void storeGame(Integer integer, Game game) {
    LOG.info("Storing Game with id: " + integer + " in: " + 
        dataStore.getBeanFactory().getClass().getName());
    dataStore.put(integer, game);

  }

  @Override
  public void deleteByQuery(Object key, Object value) {
    //Constructs a query from the dataStore. The matching rows to this query will be deleted
    Query<Integer, Game> query = dataStore.newQuery();
    //set the properties of query
    query.setStartKey((Integer) key);
    query.setEndKey((Integer) value);

    dataStore.deleteByQuery(query);
    LOG.info("Games with keys between " + key.toString() + " and " + value.toString() + " are deleted");

  }

  @Override
  public void delete(Object key) {
    dataStore.delete((Integer) key);
    dataStore.flush(); //write changes may need to be flushed before
    //they are committed 
    LOG.info("Games with key:" + key.toString() + " deleted");

  }

  @Override
  public void query(Object key, Object value) {
    org.apache.gora.query.Query<Integer, Game> query = dataStore.newQuery();
    query.setStartKey((Integer) key);
    query.setEndKey((Integer) value);
    Result<Integer, Game> result = query.execute();
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
    org.apache.gora.query.Query<Integer, Game> query = dataStore.newQuery();
    query.setStartKey((Integer) key);
    Result<Integer, Game> result = query.execute();
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
    Game game = dataStore.get((Integer) key);
    printGame(game);

  }

  /** Pretty prints the game object to stdout */
  private void printGame(Game game) {
    if(game == null) {
      System.out.println("No result to show"); 
    } else {
      System.out.println(game.toString());
    }
  }

  private void printResult(Result<Integer, Game> result) throws IOException, Exception {

    while(result.next()) { //advances the Result object and breaks if at end
      Integer resultKey = result.getKey(); //obtain current key
      Game resultGame = result.get(); //obtain current value object

      //print the results
      System.out.println(resultKey + ":");
      printGame(resultGame);
    }

    System.out.println("Number of games from the query:" + result.getOffset());
  }

}
