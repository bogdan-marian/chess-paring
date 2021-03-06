package eu.chessdata.chesspairing.algoritms.fideswissduch.v2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;


import eu.chessdata.chesspairing.tools.Tools;

public class PairingResult {
	private boolean ok;
	private List<Game> games;
	
	private PairingResult(){
		
	}

	/**
	 * it attempts to create the games and if it does not succeed it invalidates
	 * the result.
	 * 
	 * @param players
	 * @param indexA
	 * @param indexB
	 */
	public PairingResult(final List<Player> players, Integer[] indexA, Integer[] indexB) {
		this.ok = true;
		this.games = new ArrayList<>();

		for (int i = 0; i < indexA.length; i++) {
			Player a = players.get(indexA[i]);
			Player b = players.get(indexB[i]);
			Game game = Game.createGame(a, b);
			if (null == game){
				resultIsNoGood();
				break;
			}
			if (!game.isValid()) {
				// invalidate the result and break the loop
				resultIsNoGood();
				break;
			}
			this.games.add(game);
		}
		
		for (Game game: games){
			if (null == game){
				throw new IllegalStateException("Wee have null games");
			}
		}
		/**
		 * if pairing is OK then check that there are no null games
		 */
		if (this.ok){
			validateResult();
		}
	}

	/**
	 * it makes sure that there are no null games ore games with null players
	 */
	protected void validateResult() {
		for (Game game:this.games){
			if (null == game){
				throw new IllegalStateException("And the games list in paringResult contains null items");
			}
			if (null == game.getWhite()){
				throw new IllegalStateException("White player is null");
			}
			if (null == game.getBlack()){
				throw new IllegalStateException("Black player is null");
			}
		}
		
	}

	public boolean isOk() {
		return ok;
	}

	public List<Game> getGames() {
		if (null == this.games){
			this.games = new ArrayList<>();
		}
		//just remove null games no matter what
		this.games.removeAll(Collections.singleton(null));
		
		for (Game game: this.games){
			if (game == null){
				throw new IllegalStateException("Null game in games");
			}
		}
		return games;
	}

	public void addGame(Game game) {
		this.games.add(game);
	}

	/**
	 * it makes sure too release the resources and to set the inner boolean
	 * value to false;
	 */
	private void resultIsNoGood() {
		//this.games = null;
		this.ok = false;
	}

	protected static final Comparator<PairingResult> byB3Factor = new Comparator<PairingResult>() {

		@Override
		public int compare(PairingResult o1, PairingResult o2) {
			Double o1Diff = o1.getPointsDiffFactor();
			Double o2Diff = o2.getPointsDiffFactor();
			return o1Diff.compareTo(o2Diff);
		}

	};
	private static final PairingResult notValidResult = PairingResult.notValid();

	/**
	 * it computes the B3 factor and it returns the result
	 * 
	 * @return
	 */
	protected Double getPointsDiffFactor() {
		Double result = 0.0;
		for (Game game : this.games) {
			Double factor = game.getPointsDiff();
			result += factor;
		}
		return result;
	}

	public static PairingResult notValid() {
		if (PairingResult.notValidResult == null){
			PairingResult result = new PairingResult();
			result.ok = false;
			return result;
		}
		return PairingResult.notValidResult;
	}

	/**
	 * it builds a valid result with an empty list;
	 * @return
	 */
	public static PairingResult buildEmtyValidResult() {
		PairingResult result = new PairingResult();
		result.ok = true;
		result.games = new ArrayList<>();
		// TODO Auto-generated method stub
		return result;
	}
	
	/**
	 * it will pare the players 2 by 2 in all the possible combinations.
	 * If no good combination found than it will return notValidResut;
	 * @param players
	 * @return
	 */
	public static PairingResult pareInOrder(List<Player> players){
		if (players.size()<2){
			throw new IllegalStateException("size smaller than 2");
		}
		
		if (players.size()%2 != 0){
			throw new IllegalStateException("not even size");
		}
		
		
		//get all the ids combinations
		Integer[] seead = Tools.getAllListIds(players.size());
		Generator<Integer> generator = Tools.getPermutations(seead);
		for (ICombinatoricsVector<Integer> vector : generator) {
			//List<Integer> list = vector.getVector();
			int i=0;
			boolean vResultOk=true;
			List<Game> vGames = new ArrayList<>();
			
			while (i<players.size()){
				int a = vector.getValue(i++);
				int b = vector.getValue(i++);
				Player A = players.get(a);
				Player B = players.get(b);
				Game game = Game.createGame(A, B);
				if (!game.isValid()){
					vResultOk=false;
					break;
				}
				vGames.add(game);
			}
			if (vResultOk){
				PairingResult pairingResult = new PairingResult();
				pairingResult.ok = true;
				pairingResult.games = vGames;
				return pairingResult;
			}
		}
		
		return PairingResult.notValidResult;
	}

}
