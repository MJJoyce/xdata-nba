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
import java.util.ArrayList;
import java.util.List;

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
  public static Notebook parseNotebook(Notebook notebook, String string) throws IOException {
    LOG.info("Parsing Notebook: {}", string);
    BufferedReader reader = new BufferedReader(new FileReader(string));
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

  public static GameStats parseGameStats(GameStats.Builder gameStats, String string) throws IOException, ParseException {
    LOG.info("Parsing GameStats: {}", string);
    FileReader reader = new FileReader(string);

    // Pare with JSON simple parser
    JSONParser jsonParser = new JSONParser();
    JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

    GameSummary summary = parseGameSummary(jsonObject);
    List<LineScore> score = parseLineScore(jsonObject);
    SeasonSeries series = parseSeasonSeries(jsonObject);
    LastMeeting meeting = parseLastMeeting(jsonObject);
    List<PlayerStats> playerStats = parsePlayerStats(jsonObject);
    List<TeamStats> teamStats = parseTeamStats(jsonObject);
    List<OtherStats> otherStats = parseOtherStats(jsonObject);
    List<Officials> officials = parseOfficials(jsonObject);
    GameInfo info = parseInfo(jsonObject);
    List<InactivePlayers> inactivePlayers = parseInactivePlayers(jsonObject);
    AvailableVideo video = parseAvailableVideo(jsonObject);
    
    gameStats.setGameSummary(summary).setLineScore(score)
        .setSeasonSeries(series).setLastMeeting(meeting).setPlayerStats(playerStats)
        .setTeamStats(teamStats).setOtherStats(otherStats).setOfficials(officials)
        .setGameInfo(info).setInactivePlayers(inactivePlayers).setAvailableVideo(video).build();
    
    return gameStats.build();
    
  }

  public static GameSummary parseGameSummary(JSONObject jsonObject) {
    JSONArray resultSets = (JSONArray) jsonObject.get("resultSets");
    JSONObject infoObject = (JSONObject) resultSets.get(0);
    JSONArray rowSet = (JSONArray) infoObject.get("rowSet");
    JSONArray row = (JSONArray) rowSet.get(0);
    String gameSummaryDateEst  = (String) row.get(0);
    long gameSummarySequence   = (Long) row.get(1);
    long gameSummaryId         = Long.parseLong(row.get(2).toString());
    long gameStatusId          = (Long) row.get(3);
    String gameStatusText      = (String) row.get(4);
    String gameCode            = (String) row.get(5);
    long homeTeamId            = Long.parseLong(row.get(6).toString());
    long visitorTeamId         = (Long) row.get(7);
    long season                = Long.parseLong(row.get(8).toString());
    long livePeriod            = (Long) row.get(9);
    String livePcTimeString    = row.get(10).toString();
    long livePcTime            = livePcTimeString.isEmpty() ? 0 : Long.parseLong(livePcTimeString);
    String broadcasterAbbrev   = (String) row.get(11);
    String livePeriodTimeBcast = (String) row.get(12);
    long whStatus              = (Long) row.get(13);

    return GameSummary.newBuilder()
            .setGameSummaryDateEst(gameSummaryDateEst)
            .setGameSummarySequence(gameSummarySequence)
            .setGameGameSummaryId(gameSummaryId)
            .setGameStatusId(gameStatusId)
            .setGameStatusText(gameStatusText)
            .setGameCode(gameCode)
            .setGameSummaryHomeTeamId(homeTeamId)
            .setGameSummaryVisitorTeamId(visitorTeamId)
            .setSeason(season)
            .setLivePeriod(livePeriod)
            .setLivePcTime(livePcTime)
            .setNatlTvBroadcasterAbbreviation(broadcasterAbbrev)
            .setLivePeriodTimeBcast(livePeriodTimeBcast)
            .setWhStatus(whStatus)
            .build();
  }
  public static List<LineScore> parseLineScore(JSONObject jsonObject) {
      JSONArray resultSets = (JSONArray) jsonObject.get("resultSets");
      JSONObject infoObject = (JSONObject) resultSets.get(1);
      JSONArray rowSet = (JSONArray) infoObject.get("rowSet");
      List<LineScore> result = new ArrayList<LineScore>();
      for (int i = 0; i < 2; i++) {
        JSONArray row = (JSONArray) rowSet.get(i);
        result.add(LineScore.newBuilder()
                .setGameLineScoreDateEst((String) row.get(0))
                .setGameLineScoreSequence((Long) row.get(1))
                .setGameLineScoreId(Long.parseLong((String) row.get(2)))
                .setLineScoreTeamId((Long) row.get(3))
                .setLineScoreTeamAbbreviation((String) row.get(4))
                .setTeamCityName((String) row.get(5))
                .setTeamWinsLosses((String) row.get(6))
                .setPtsQtr1((Long) row.get(7))
                .setPtsQtr2((Long) row.get(8))
                .setPtsQtr3((Long) row.get(9))
                .setPtsQtr4((Long) row.get(10))
                .setPtsOt1((Long) row.get(11))
                .setPtsOt2((Long) row.get(12))
                .setPtsOt3((Long) row.get(13))
                .setPtsOt4((Long) row.get(14))
                .setPtsOt5((Long) row.get(15))
                .setPtsOt6((Long) row.get(16))
                .setPtsOt7((Long) row.get(17))
                .setPtsOt8((Long) row.get(18))
                .setPtsOt9((Long) row.get(19))
                .setPtsOt10((Long) row.get(20))
                .setLineScorePts((Long) row.get(21))
                .build()
        );
      }
      return result;
  }
  public static SeasonSeries parseSeasonSeries(JSONObject jsonObject) {
    JSONArray resultSets = (JSONArray) jsonObject.get("resultSets");
    JSONObject infoObject = (JSONObject) resultSets.get(2);
    JSONArray rowSet = (JSONArray) infoObject.get("rowSet");
    JSONArray row = (JSONArray) rowSet.get(0);
    long seriesId = Long.parseLong(row.get(0).toString());
    long homeTeamId = (Long) row.get(1);
    long visitorTeamId = (Long) row.get(2);
    String seriesDateEst = (String) row.get(3);
    long homeTeamWins = (Long) row.get(4);
    long homeTeamLosses = (Long) row.get(5);
    String seriesLeader = (String) row.get(6);
    return SeasonSeries.newBuilder()
            .setGameSeasonSeriesId(seriesId)
            .setSeasonSeriesHomeTeamId(homeTeamId)
            .setSeasonSeriesVisitorTeamId(visitorTeamId)
            .setGameSeasonSeriesDateEst(seriesDateEst)
            .setHomeTeamWins(homeTeamWins)
            .setHomeTeamLosses(homeTeamLosses)
            .setSeriesLeader(seriesLeader)
            .build();
  }
  public static LastMeeting parseLastMeeting(JSONObject jsonObject) {
    JSONArray resultSets = (JSONArray) jsonObject.get("resultSets");
    JSONObject infoObject = (JSONObject) resultSets.get(3);
    JSONArray rowSet = (JSONArray) infoObject.get("rowSet");
    JSONArray row = (JSONArray) rowSet.get(0);
    long gameLastMeetingId = Long.parseLong(row.get(0).toString());
    long lastGameId = Long.parseLong(row.get(1).toString());
    String lastGameDate = (String) row.get(2);
    long lastGameHomeTeamId = (Long) row.get(3);
    String lastGameHomeTeamCity = (String) row.get(4);
    String lastGameHomeTeamName = (String) row.get(5);
    String lastGameHomeTeamAbbreviation = (String) row.get(6);
    long lastGameHomeTeamPoints = (Long) row.get(7);
    long lastGameVisitorId = (Long) row.get(8);
    String lastGameVisitorCity = (String) row.get(9);
    String lastGameVisitorName = (String) row.get(10);
    String lastGameVisitorCity1 = (String) row.get(11);
    long lastGameVisitorPoints = (Long) row.get(12);
    return LastMeeting.newBuilder()
            .setGameLastMeetingId(gameLastMeetingId)
            .setLastGameId(lastGameId)
            .setLastGameDateEst(lastGameDate)
            .setLastGameHomeTeamId(lastGameHomeTeamId)
            .setLastGameHomeTeamCity(lastGameHomeTeamCity)
            .setLastGameHomeTeamName(lastGameHomeTeamName)
            .setLastGameHomeTeamAbbreviation(lastGameHomeTeamAbbreviation)
            .setLastGameHomeTeamPoints(lastGameHomeTeamPoints)
            .setLastGameHomeTeamId(lastGameHomeTeamId)
            .setLastGameVisitorTeamId(lastGameVisitorId)
            .setLastGameVisitorTeamCity(lastGameVisitorCity)
            .setLastGameVisitorTeamName(lastGameVisitorName)
            .setLastGameVisitorTeamCity1(lastGameVisitorCity1)
            .setLastGameVisitorTeamPoints(lastGameVisitorPoints)
            .build();
  }
  public static List<PlayerStats> parsePlayerStats(JSONObject jsonObject) {
    JSONArray resultSets = (JSONArray) jsonObject.get("resultSets");
    JSONObject infoObject = (JSONObject) resultSets.get(4);
    JSONArray rowSet = (JSONArray) infoObject.get("rowSet");
    List<PlayerStats> result = new ArrayList<PlayerStats>();
    for (Object rowObject : rowSet) {
      JSONArray row = (JSONArray) rowObject;
      result.add(PlayerStats.newBuilder()
          .setGamePlayerStatsId     (row.get(0) == null ? -1 : Long.parseLong((String) row.get(0)))
          .setPlayerStatsTeamId     (row.get(1) == null ? -1 : (Long) row.get(1))
          .setPlayerStatsTeamAbbreviation(row.get(2) == null ? "" : (String) row.get(2))
          .setPlayerStatsTeamCity   (row.get(3) == null ? "" : (String) row.get(3))
          .setPlayerStatsPlayerId   (row.get(4) == null ? -1 : (Long) row.get(4))
          .setPlayerName            (row.get(5) == null ? "" : (String) row.get(5))
          .setStartPosition         (row.get(6) == null ? "" : (String) row.get(6))
          .setComment               (row.get(7) == null ? "" : (String) row.get(7))
          .setPlayerStatsMin        (row.get(8) == null ? "" : (String) row.get(8))
          .setPlayerStatsFgm        (row.get(9) == null ? -1 : (Long) row.get(9))
          .setPlayerStatsFga        (row.get(10) == null ? -1 : (Long) row.get(10))
          .setPlayerStatsfgPct      (row.get(11) == null ? -1 : Math.round((Double.parseDouble(row.get(11).toString())) * 100))
          .setPlayerStatsfg3m       (row.get(12) == null ? -1 : (Long) row.get(12))
          .setPlayerStatsfg3a       (row.get(13) == null ? -1 : (Long) row.get(13))
          .setPlayerStatsfg3Pct     (row.get(14) == null ? -1 : Math.round((Double.parseDouble(row.get(14).toString())) * 100))
          .setPlayerStatsftm        (row.get(15) == null ? -1 : (Long) row.get(15))
          .setPlayerStatsfta        (row.get(16) == null ? -1 : (Long) row.get(16))
          .setPlayerStatsftPct      (row.get(17) == null ? -1 : Math.round((Double.parseDouble(row.get(17).toString())) * 100))
          .setPlayerStatsoreb       (row.get(18) == null ? -1 : (Long) row.get(18))
          .setPlayerStatsdreb       (row.get(19) == null ? -1 : (Long) row.get(19))
          .setPlayerStatsreb        (row.get(20) == null ? -1 : (Long) row.get(20))
          .setPlayerStatsast        (row.get(21) == null ? -1 : (Long) row.get(21))
          .setPlayerStatsstl        (row.get(22) == null ? -1 : (Long) row.get(22))
          .setPlayerStatsblk        (row.get(23) == null ? -1 : (Long) row.get(23))
          .setPlayerStatsto         (row.get(24) == null ? -1 : (Long) row.get(24))
          .setPlayerStatspf         (row.get(25) == null ? -1 : (Long) row.get(25))
          .setPlayerStatsPts        (row.get(26) == null ? -1 : (Long) row.get(26))
          .setPlayerStatsPlusMinus  (row.get(27) == null ? -1 : (Long) row.get(27))
          .build()
      );
    }
    return result;
  }
  public static List<TeamStats> parseTeamStats(JSONObject jsonObject) {
    //return TeamStats.newBuilder()
            // TODO: TeamStats has two teams, each with stats.
            //.build();
      return new ArrayList<TeamStats>();
  }

  public static List<OtherStats> parseOtherStats(JSONObject jsonObject) {
/*      JSONArray resultSets = (JSONArray) jsonObject.get("resultSets");
      JSONObject result = (JSONObject) resultSets.get(6);
      assert("OtherStats".equals(result.get("name")));
      long largestLead = ;
      long leadChanges = ;
      long leagueId = ;*/

      //OtherStats otherStats = OtherStats.newBuilder()
              /*.setLeagueId()
              .setSeasonId()
              .setOtherStatsTeamId()
              .setLargestLead()
              .setLeadChanges()
              .setOtherStatsTeamAbbreviation()
              .setOtherStatsTeamCity()
              .setPts2ndChance()
              .setPtsFb()
              .setPtsPaint()
              .setTimesTied()*/
              //.build();
      //return otherStats;
      return new ArrayList<OtherStats>();
  }

  public static List<Officials> parseOfficials(JSONObject jsonObject) {
      //JSONArray resultSets = (JSONArray) jsonObject.get("resultSets");

      /*Officials officials = Officials.newBuilder()
              .build();
      return officials;*/
      return new ArrayList<Officials>();
  }

  public static GameInfo parseInfo(JSONObject jsonObject) {
      JSONArray resultSets = (JSONArray) jsonObject.get("resultSets");
      JSONObject infoObject = (JSONObject) resultSets.get(8);
      JSONArray rowSet = (JSONArray) infoObject.get("rowSet");
      JSONArray row = (JSONArray) rowSet.get(0);
      String gameDate = (String) row.get(0);
      long attendance = (Long) row.get(1);
      String gameTime = (String) row.get(2);
      return GameInfo.newBuilder()
              .setGameDate(gameDate)
              .setAttendance(attendance)
              .setGameTime(gameTime)
              .build();
  }

  public static List<InactivePlayers> parseInactivePlayers(JSONObject jsonObject) {
      JSONArray resultSets = (JSONArray) jsonObject.get("resultSets");
      JSONObject inactivePlayersObject = (JSONObject) resultSets.get(9);
      JSONArray rowSet = (JSONArray) inactivePlayersObject.get("rowSet");
      InactivePlayers.Builder builder = InactivePlayers.newBuilder();
      //return builder.build();
      /*for (Object o : rowSet) {
          JSONArray player = (JSONArray) o;
          builder.set
      }*/
      return new ArrayList<InactivePlayers>();
  }

  public static AvailableVideo parseAvailableVideo(JSONObject jsonObject) {
      JSONArray resultSets = (JSONArray) jsonObject.get("resultSets");
      JSONObject availableVideoObject = (JSONObject) resultSets.get(10);
      JSONArray rowSet = (JSONArray) availableVideoObject.get("rowSet");
      JSONArray row = (JSONArray) rowSet.get(0);
      return AvailableVideo.newBuilder()
              .setVideoAvailableFlag((Long) row.get(0))
              .build();
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
