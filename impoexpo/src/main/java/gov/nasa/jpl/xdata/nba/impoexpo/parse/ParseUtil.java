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
package gov.nasa.jpl.xdata.nba.impoexpo.parse;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import org.apache.avro.util.Utf8;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import gov.nasa.jpl.xdata.nba.impoexpo.structs.AvailableVideo;
import gov.nasa.jpl.xdata.nba.impoexpo.structs.GameInfo;
import gov.nasa.jpl.xdata.nba.impoexpo.structs.GameStats;
import gov.nasa.jpl.xdata.nba.impoexpo.structs.GameSummary;
import gov.nasa.jpl.xdata.nba.impoexpo.structs.InactivePlayers;
import gov.nasa.jpl.xdata.nba.impoexpo.structs.LastMeeting;
import gov.nasa.jpl.xdata.nba.impoexpo.structs.LineScore;
import gov.nasa.jpl.xdata.nba.impoexpo.structs.Notebook;
import gov.nasa.jpl.xdata.nba.impoexpo.structs.Officials;
import gov.nasa.jpl.xdata.nba.impoexpo.structs.OtherStats;
import gov.nasa.jpl.xdata.nba.impoexpo.structs.PlayerStats;
import gov.nasa.jpl.xdata.nba.impoexpo.structs.Preview;
import gov.nasa.jpl.xdata.nba.impoexpo.structs.Recap;
import gov.nasa.jpl.xdata.nba.impoexpo.structs.SeasonSeries;
import gov.nasa.jpl.xdata.nba.impoexpo.structs.TeamStats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * ParseUtil, amongst other things, contains static functions for
 * implementing dataset specific parse functions involved within
 * ETL.
 */
public class ParseUtil {
  
  private static Logger LOG = LoggerFactory.getLogger(ParseUtil.class);

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

  /**
   * Simply reads in a String path to a Preview file, parses the File, populates
   * a Preview object and returns that object.
   * @param preview 
   * @param string
   * @return
   */
  public static Preview parsePreview(Preview preview, String string) {
    preview.setPreviewText(parseTextFile(string));
    return preview;
    
  }

  /**
   * Simply reads in a String path to a Recap file, parses the File, populates
   * a Recap object and returns that object.
   * @param recap 
   * @param string
   * @return
   */
  public static Recap parseRecap(Recap recap, String string) {
    recap.setRecapText(parseTextFile(string));
    return recap;
  }

  /**
   * Simply reads in a String path to a Notebook file, parses the File, populates
   * a Notebook object and returns that object.
   * TODO the {@link }
   * @param string
   * @return
   * @throws IOException 
   */
  public static Notebook parseNotebook(String string) throws IOException {
    LOG.info("Parsing Notebook: {}", string);
    BufferedReader reader = new BufferedReader(new FileReader(string));
    Notebook notebook = Notebook.newBuilder().build();
    try {
      String line = reader.readLine();
      do {
        if (line.contains("Notebook:")) {
          notebook.setTeamNotebook(line);
        } else if (line.contains("Posted")) {
          notebook.setPostedDate(line);
        } else if (line.contains("THE FACT:")) {
          notebook.setTheFacts(line);
        } else if (line.contains("THE LEAD:")) {
          notebook.setTheLead(line);
        } else if (line.contains("QUOTABLE:") || line.contains("QUOTABLE II:") || line.contains("QUOTABLE III:")) {
          notebook.getTheQuotes().add(line);
        } else if (line.contains("THE STAT")) {
          notebook.setTheStat(line);
        }  else if (line.contains("TURNING POINT:")) {
          notebook.setTheTurningPoint(line);
        } else if (line.contains("HOT:")) {
          notebook.setHot(line);
        } else if (line.contains("NOT:")) {
          notebook.setNot(line);
        } else if (line.contains("GOOD MOVE:")) {
          notebook.setGoodMove(line);
        } else if (line.contains("BAD MOVE:")) {
          notebook.setBadMove(line);
        } else if (line.contains("NEXT:")) {
          notebook.setUpNext(line);
        }
        
        line = reader.readLine();
      } while(line != null);
      
    } finally {
      reader.close();  
    }
    return notebook;
  }

  public static GameStats parseGameStats(String string) throws IOException, ParseException {
    LOG.info("Parsing GameStats: {}" + string);
    FileReader reader = new FileReader(string);

    // Pare with JSON simple parser
    JSONParser jsonParser = new JSONParser();
    JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

    JSONArray result = (JSONArray) jsonObject.get("resultSets");
    JSONObject jObj = (JSONObject) result.get(0);
    JSONArray rows = (JSONArray) jObj.get("rowset");
    
    GameSummary summary = parseGameSummary();
    LineScore score = null;
    SeasonSeries series = null;
    LastMeeting meeting = null;
    PlayerStats playerStats = null;
    TeamStats teamStats = null;
    
    //Tyler
    OtherStats otherStats = null;
    Officials officials = null;
    GameInfo info = null;
    InactivePlayers inactivePlayers = null;
    AvailableVideo video = null;
    
    GameStats stats = GameStats.newBuilder().setGameSummary(summary).setLineScore(score)
        .setSeasonSeries(series).setLastMeeting(meeting).setPlayerStats(playerStats)
        .setTeamStats(teamStats).setOtherStats(otherStats).setOfficials(officials)
        .setGameInfo(info).setInactivePlayers(inactivePlayers).setAvailableVideo(video).build();
    
    return stats;
    
  }
  

  private static GameSummary parseGameSummary() {
    // TODO Auto-generated method stub
    return null;
  }

  private static String parseTextFile(String textString) {
    LOG.info("Parsing Text: {}", textString);
    InputStream is = null;
    try {
      is = new FileInputStream(textString);
    } catch (Exception e) {
      // TODO: handle exception
    }
    Tika tika = new Tika();
    String text = null;
    try {
      text = tika.parseToString(is);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (TikaException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    finally {
      try {
        is.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return text;
  }

}
