package eu.chessdata.chesspairing.model;

import com.google.gson.Gson;
import eu.chessdata.chesspairing.Api;
import eu.chessdata.chesspairing.tools.Tools;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;


public class ChesspairingTournamentTest {
    @BeforeClass
    public static void checGeneratedFilesFolder() {
        TestUtils.createIfNotPresentGeneratedFilesFolder();
    }

    /**
     * basic serialize and deserialize of a tournament
     */
    @Test
    public void simpetBuildTournament() {
        ChesspairingTournament tournament = TestUtils.buildTournament("Simple chess tournament");
        String stringTournament = Api.serializeTournament(tournament);
        ChesspairingTournament secondTournament = Api.deserializeTournament(stringTournament);
        Assert.assertTrue("Tournament names should be the same",
                tournament.getName().equals(secondTournament.getName()));
        String secondString = Api.serializeTournament(secondTournament);
        Assert.assertTrue("The hole serialized tournament should be the same", stringTournament.equals(secondString));
    }

    /**
     * Just as the name implies this is just a simple snippet that shows a simple
     * way to create a {@link ChesspairingTournament} object from a json file.
     *
     * @throws UnsupportedEncodingException
     */
    @Test
    public void loadFromFileSnipet() throws UnsupportedEncodingException {
        InputStream inputStream = ChesspairingTournamentTest.class
                .getResourceAsStream("/chesspairingTournamentTest/tournament1.json");
        Reader reader = new InputStreamReader(inputStream, "UTF-8");
        Gson gson = Tools.getGson();

        // line that loads from file
        ChesspairingTournament tournament = gson.fromJson(reader, ChesspairingTournament.class);

        // simple line that tests that wee have the correct data
        Assert.assertTrue("Not the expected data", tournament.getName().equals("Tournament 1"));
    }

    /**
     * Simple test meant to be used to build specific tournaments and then save them
     * in files. The java.io package will only be used in tests. No java.io.File
     * objects will be used the bye chessparing package.
     *
     * @throws IOException
     */
    @Test
    public void constumizeTournament() throws IOException {

        String tournament1FilePath = Tools.GENERATED_FILES + "/tournament1.json";
        Writer writer = new FileWriter(tournament1FilePath);
        Gson gson = Tools.getGson();

        ChesspairingTournament tournament = TestUtils.buildTournament("Tournament 1");

        // write tournament to file
        gson.toJson(tournament, writer);
        writer.close();
    }

    @Test
    public void test1ComputeStandings() throws IOException {
        ChesspairingTournament tournament = TestUtils.loadFile("/model/test1ComputeStandings.json");
        assertNotNull("Tournament object is null", tournament);

        for (ChesspairingRound round : tournament.getRounds()) {
            int roundNumber = round.getRoundNumber();
            List<ChesspairingPlayer> standings = tournament.computeStandings(roundNumber);
            assert (standings.size() > 0);
        }

    }

    /**
     * The actual games implemented by this test can be located in this document
     * https://docs.google.com/spreadsheets/d/1ikVLzkDVAEpbsk9cJaXeC8l3uL3QY8LUOEvcn1rwZ7A/edit?usp=sharing
     */
    @Test
    public void testGetBuchholzPoints() {

        ChesspairingTournament tournament = new ChesspairingTournament();
        tournament.setTotalRounds(4);
        ChesspairingPlayer p1G = TestUtils.buildPlayer("1G", 1000);
        ChesspairingPlayer p2A = TestUtils.buildPlayer("2A", 1000);
        ChesspairingPlayer p3B = TestUtils.buildPlayer("3B", 1000);
        ChesspairingPlayer p4F = TestUtils.buildPlayer("4F", 1000);
        ChesspairingPlayer p5E = TestUtils.buildPlayer("5E", 1000);
        ChesspairingPlayer p6D = TestUtils.buildPlayer("6D", 1000);
        ChesspairingPlayer p7C = TestUtils.buildPlayer("7C", 1000);
        tournament.setPlayers(Arrays.asList(p1G, p2A, p3B, p4F, p5E, p6D, p7C));
        assertEquals(7, tournament.getPlayers().size());

        //round 1
        ChesspairingRound round1 = new ChesspairingRound();
        round1.setRoundNumber(1);
        round1.setGames(
                Arrays.asList(
                        TestUtils.buildGame(1, p1G, null, ChesspairingResult.BYE),
                        TestUtils.buildGame(2, p6D, p2A, ChesspairingResult.BLACK_WINS),
                        TestUtils.buildGame(3, p3B, p5E, ChesspairingResult.WHITE_WINS_BY_FORFEIT),
                        TestUtils.buildGame(4, p4F, p7C, ChesspairingResult.WHITE_WINS)
                )
        );
        round1.setAbsentPlayers(new ArrayList<>());
        round1.setPresentPlayers(Arrays.asList(p1G, p2A, p3B, p4F, p5E, p6D, p7C));

        // round 2
        ChesspairingRound round2 = new ChesspairingRound();
        round2.setRoundNumber(2);
        round2.setGames(
                Arrays.asList(
                        TestUtils.buildGame(1, p1G, p4F, ChesspairingResult.WHITE_WINS),
                        TestUtils.buildGame(2, p2A, p5E, ChesspairingResult.DRAW_GAME),
                        TestUtils.buildGame(3, p6D, p7C, ChesspairingResult.DOUBLE_FORFEIT)
                )
        );
        round2.setAbsentPlayers(Arrays.asList(p3B));
        round2.setPresentPlayers(Arrays.asList(p1G, p2A, p4F, p5E, p6D, p7C));


        // round 3
        ChesspairingRound round3 = new ChesspairingRound();
        round3.setRoundNumber(3);
        round3.setAbsentPlayers(new ArrayList<>());
        round3.setPresentPlayers(Arrays.asList(p1G, p2A, p3B, p4F, p5E, p6D, p7C));
        round3.setGames(
                Arrays.asList(
                        TestUtils.buildGame(1, p2A, p1G, ChesspairingResult.WHITE_WINS),
                        TestUtils.buildGame(2, p4F, p3B, ChesspairingResult.BLACK_WINS),
                        TestUtils.buildGame(3, p6D, p5E, ChesspairingResult.BLACK_WINS_BY_FORFEIT),
                        TestUtils.buildGame(3, p7C, null, ChesspairingResult.BYE)
                )
        );


        // round 4
        ChesspairingRound round4 = new ChesspairingRound();
        round4.setRoundNumber(4);
        round4.setAbsentPlayers(new ArrayList<>());
        round4.setPresentPlayers(Arrays.asList(p1G, p2A, p4F, p6D, p7C));
        round4.setGames(
                Arrays.asList(
                        TestUtils.buildGame(1, p7C, p1G, ChesspairingResult.BLACK_WINS_BY_FORFEIT),
                        TestUtils.buildGame(2, p2A, p4F, ChesspairingResult.DRAW_GAME),
                        TestUtils.buildGame(3, p6D, null, ChesspairingResult.BYE)
                )
        );

        tournament.setRounds(
                Arrays.asList(round1, round2, round3, round4)
        );
        //tests round 1
        assertEquals(0.0f, tournament.computeBuchholzPoints(1, p1G.getPlayerKey()));
        assertEquals(0.0f, tournament.computeBuchholzPoints(1, p2A.getPlayerKey()));
        assertEquals(0.0f, tournament.computeBuchholzPoints(1, p3B.getPlayerKey()));
        assertEquals(0.0f, tournament.computeBuchholzPoints(1, p4F.getPlayerKey()));
        assertEquals(0.0f, tournament.computeBuchholzPoints(1, p5E.getPlayerKey()));
        assertEquals(1.0f, tournament.computeBuchholzPoints(1, p6D.getPlayerKey()));
        assertEquals(1.0f, tournament.computeBuchholzPoints(1, p7C.getPlayerKey()));

        //tests round2
        assertEquals(1.5f, tournament.computeBuchholzPoints(2, p1G.getPlayerKey()));
        assertEquals(0.5f, tournament.computeBuchholzPoints(2, p2A.getPlayerKey()));
        assertEquals(1.5f, tournament.computeBuchholzPoints(2, p3B.getPlayerKey()));
        assertEquals(2.0f, tournament.computeBuchholzPoints(2, p4F.getPlayerKey()));
        assertEquals(2.0f, tournament.computeBuchholzPoints(2, p5E.getPlayerKey()));
        assertEquals(1.5f, tournament.computeBuchholzPoints(2, p6D.getPlayerKey()));
        assertEquals(1.0f, tournament.computeBuchholzPoints(2, p7C.getPlayerKey()));

        //tests round3
        assertEquals(4.5f, tournament.computeBuchholzPoints(3, p1G.getPlayerKey()));
        assertEquals(3.5f, tournament.computeBuchholzPoints(3, p2A.getPlayerKey()));
        assertEquals(3.5f, tournament.computeBuchholzPoints(3, p3B.getPlayerKey()));
        assertEquals(5.0f, tournament.computeBuchholzPoints(3, p4F.getPlayerKey()));
        assertEquals(4.0f, tournament.computeBuchholzPoints(3, p5E.getPlayerKey()));
        assertEquals(3.0f, tournament.computeBuchholzPoints(3, p6D.getPlayerKey()));
        assertEquals(1.5f, tournament.computeBuchholzPoints(3, p7C.getPlayerKey()));

        // test round4
        assertEquals(8.0f, tournament.computeBuchholzPoints(4, p1G.getPlayerKey()));
        assertEquals(7.0f, tournament.computeBuchholzPoints(4, p2A.getPlayerKey()));
        assertEquals(7.0f, tournament.computeBuchholzPoints(4, p3B.getPlayerKey()));
        assertEquals(9.0f, tournament.computeBuchholzPoints(4, p4F.getPlayerKey()));
        assertEquals(7.0f, tournament.computeBuchholzPoints(4, p5E.getPlayerKey()));
        assertEquals(4.5f, tournament.computeBuchholzPoints(4, p6D.getPlayerKey()));
        assertEquals(4.0f, tournament.computeBuchholzPoints(4, p7C.getPlayerKey()));

    }
}
