package xdata.jpl.nasa.gov.impoexpo.parse;

import gov.nasa.jpl.xdata.nba.impoexpo.parse.ParseUtil;
import gov.nasa.jpl.xdata.nba.impoexpo.structs.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TestParseUtil {

    @Test
    void testParseGameSummary() throws Exception {
        JSONObject gameJson = getGameJSON();
        GameSummary gameSummary = ParseUtil.parseGameSummary(gameJson);
    }

    @Test
    void testParseLineScore() throws Exception {
        JSONObject gameJson = getGameJSON();
        LineScore lineScore = ParseUtil.parseLineScore(gameJson);
    }

    @Test
    void testParseSeasonSeries() throws Exception {
        JSONObject gameJson = getGameJSON();
        SeasonSeries seasonSeries = ParseUtil.parseSeasonSeries(gameJson);
    }

    @Test
    void testParseLastMeeting() throws Exception {
        JSONObject gameJson = getGameJSON();
        LastMeeting lastMeeting = ParseUtil.parseLastMeeting(gameJson);
    }

    @Test
    void testParsePlayerStats() throws Exception {
        JSONObject gameJson = getGameJSON();
        PlayerStats playerStats = ParseUtil.parsePlayerStats(gameJson);
    }

    @Test
    void testParseTeamStats() throws Exception {
        JSONObject gameJson = getGameJSON();
        TeamStats teamStats = ParseUtil.parseTeamStats(gameJson);
    }

    @Test void testParseOtherStats() throws Exception {
        JSONObject gameJson = getGameJSON();
        OtherStats otherStats = ParseUtil.parseOtherStats(gameJson);
    }

    @Test void testParseOfficials() throws Exception {
        JSONObject gameJson = getGameJSON();
        Officials officials = ParseUtil.parseOfficials(gameJson);
    }

    @Test void testParseInfo() throws Exception {
        JSONObject gameJson = getGameJSON();
        GameInfo gameInfo = ParseUtil.parseInfo(gameJson);
    }

    @Test void testParseInactivePlayers() throws Exception {
        JSONObject gameJson = getGameJSON();
        InactivePlayers inactivePlayers = ParseUtil.parseInactivePlayers(gameJson);
    }

    @Test void testParseAvailableVideo() throws Exception {
        JSONObject gameJson = getGameJSON();
        AvailableVideo availableVideo = ParseUtil.parseAvailableVideo(gameJson);
    }

    private static JSONObject getGameJSON() throws ParseException, IOException {
        InputStream stream = TestParseUtil.class.getResourceAsStream("/test-documents/0021300632_gamestats_pp.json");
        JSONParser jsonParser = new JSONParser();
        return (JSONObject) jsonParser.parse(new InputStreamReader(stream));
    }

}
