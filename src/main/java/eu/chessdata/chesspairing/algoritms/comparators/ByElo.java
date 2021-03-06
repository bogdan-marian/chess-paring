package eu.chessdata.chesspairing.algoritms.comparators;

import java.util.Comparator;

import eu.chessdata.chesspairing.model.ChesspairingPlayer;

public class ByElo implements Comparator<ChesspairingPlayer>{

	@Override
	public int compare(ChesspairingPlayer o1, ChesspairingPlayer o2) {
		if (o1 == null){
			throw new IllegalStateException("Player o1 is null");
		}
		if (o2 == null){
			throw new IllegalStateException("Player o2 is null");
		}
		
		int elo1 = o1.getElo();
		int elo2 = o2.getElo();
		//just use the default Integer compare
		return  Integer.compare(elo1, elo2);
	}
}
