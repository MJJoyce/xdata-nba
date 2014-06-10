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

/**
 * NBAManager provides a CLI for us to map NBA data to 
 * and from Apache Solr via Apache Gora.
 * Essentially, we parse JSON and populate Gora
 * objects in-memory before flushing them out to 
 * a SolrCloud cluster.
 *
 */
public class NBAManager {
  
  
  private static final String USAGE = "NBAManager -parse <input_player_json_file>\n" +
      "           -get <game_id:player_id>\n" +
      "           -query <game_id:player_id>\n" +
      "           -query <startGame_id:player_id> <endGame_id:player_id>\n" +
      "           -delete <game_id:player_id>\n" +
      "           -deleteByQuery <startGame_id:player_id> <endGame_id:player_id>\n";

  /**
   * @param args
   */
  public static void main(String[] args) throws Exception {
    if(args.length < 2) {
      System.err.println(USAGE);
      System.exit(1);
    }
    
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

  private void parse(String string) {
    // TODO Auto-generated method stub
    
  }

}
