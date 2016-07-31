package eu.chessdata.chessparing.model;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;

import eu.chessdata.chessparing.Api;
import eu.chessdata.chessparing.Tools;

public class ChessparingTournamentTest {
	@BeforeClass
	public static void checGeneratedFilesFolder(){
		TestUtils.createIfNotPresentGeneratedFilesFolder();
	}

	/**
	 * basic serialize and deserialize of a tournament
	 */
	@Test
	public void simpetBuildTournament() {
		ChessparingTournament tournament = TestUtils.buildTournament("Simple chess tournament");
		String stringTournament = Api.serializeTournament(tournament);
		ChessparingTournament secondTournament = Api.deserializeTournament(stringTournament);
		Assert.assertTrue("Tournament names should be the same",
				tournament.getName().equals(secondTournament.getName()));
		String secondString = Api.serializeTournament(secondTournament);
		Assert.assertTrue("The hole serialized tournament should be the same", stringTournament.equals(secondString));
	}


	/**
	 * Just as the name implies this is just a simple snippet that shows a
	 * simple way to create a {@link ChessparingTournament} object from a json
	 * file.
	 * 
	 * @throws UnsupportedEncodingException
	 */
	@Test
	public void loadFromFileSnipet() throws UnsupportedEncodingException {
		InputStream inputStream = ChessparingTournamentTest.class
				.getResourceAsStream("/chessparingTournamentTest/tournament1.json");
		Reader reader = new InputStreamReader(inputStream, "UTF-8");
		Gson gson = Tools.getGson();

		// line that loads from file
		ChessparingTournament tournament = gson.fromJson(reader, ChessparingTournament.class);

		// simple line that tests that wee have the correct data
		Assert.assertTrue("Not the expected data", tournament.getName().equals("Tournament 1"));
	}
	
	/**
	 * Simple test meant to be used to build specific tournaments and then save
	 * them in files. The java.io package will only be used in tests. No
	 * java.io.File objects will be used the bye chessparing package.
	 * 
	 * @throws IOException
	 */
	@Test
	public void constumizeTournament() throws IOException {
		String tournament1FilePath = Tools.GENERATED_FILES+"/tournament1.json";
		Writer writer = new FileWriter(tournament1FilePath);
		Gson gson = Tools.getGson();

		ChessparingTournament tournament = TestUtils.buildTournament("Tournament 1");

		// write tournament to file
		gson.toJson(tournament, writer);
		writer.close();
	}

}