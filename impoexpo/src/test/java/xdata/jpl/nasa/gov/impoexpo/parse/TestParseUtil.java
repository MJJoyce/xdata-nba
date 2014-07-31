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
import java.util.List;

public class TestParseUtil {

    @Test
    public void testParseGameSummary() throws Exception {
        JSONObject gameJson = getGameJSON();
        GameSummary gameSummary = ParseUtil.parseGameSummary(gameJson);
    }

    @Test
    public void testParseLineScore() throws Exception {
        JSONObject gameJson = getGameJSON();
        List<LineScore> lineScore = ParseUtil.parseLineScore(gameJson);
    }

    @Test
    public void testParseSeasonSeries() throws Exception {
        JSONObject gameJson = getGameJSON();
        SeasonSeries seasonSeries = ParseUtil.parseSeasonSeries(gameJson);
    }

    @Test
    public void testParseLastMeeting() throws Exception {
        JSONObject gameJson = getGameJSON();
        LastMeeting lastMeeting = ParseUtil.parseLastMeeting(gameJson);
    }

    @Test
    public void testParsePlayerStats() throws Exception {
        JSONObject gameJson = getGameJSON();
        List<PlayerStats> playerStats = ParseUtil.parsePlayerStats(gameJson);
    }

    @Test
    public void testParseTeamStats() throws Exception {
        JSONObject gameJson = getGameJSON();
        List<TeamStats> teamStats = ParseUtil.parseTeamStats(gameJson);
    }

    @Test
    public void testParseOtherStats() throws Exception {
        JSONObject gameJson = getGameJSON();
        List<OtherStats> otherStats = ParseUtil.parseOtherStats(gameJson);
    }

    @Test
    public void testParseOfficials() throws Exception {
        JSONObject gameJson = getGameJSON();
        List<Officials> officials = ParseUtil.parseOfficials(gameJson);
    }

    @Test
    public void testParseInfo() throws Exception {
        JSONObject gameJson = getGameJSON();
        GameInfo gameInfo = ParseUtil.parseInfo(gameJson);
    }

    @Test
    public void testParseInactivePlayers() throws Exception {
        JSONObject gameJson = getGameJSON();
        List<InactivePlayers> inactivePlayers = ParseUtil.parseInactivePlayers(gameJson);
    }

    @Test
    public void testParseAvailableVideo() throws Exception {
        JSONObject gameJson = getGameJSON();
        AvailableVideo availableVideo = ParseUtil.parseAvailableVideo(gameJson);
    }

    private static JSONObject getGameJSON() throws ParseException, IOException {
        InputStream stream = TestParseUtil.class.getResourceAsStream("/test-documents/0021300632_gamestats_pp.json");
        JSONParser jsonParser = new JSONParser();
        return (JSONObject) jsonParser.parse(new InputStreamReader(stream));
    }

}
