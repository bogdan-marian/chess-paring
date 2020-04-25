package eu.chessdata.chesspairing.model;

import eu.chessdata.chesspairing.algoritms.comparators.ByInitialOrderIdReverce;
import eu.chessdata.chesspairing.algoritms.comparators.ChainedComparator;

import java.util.*;

public class ChesspairingTournament {
    private String name;
    private String description;
    private String city;
    private String federation;
    private Date dateOfStart;
    private Date dateOfEnd;
    private String typeOfTournament;
    private String ChiefArbiter;
    private String deputyChiefArbiters;
    /**
     * this is the maximum allowed number of rounds in a tournament. If you try to
     * pare over this number some algorithms will just crash.
     */
    private int totalRounds;
    private ChesspairingByeValue chesspairingByeValue;
    private List<ChesspairingPlayer> players = new ArrayList<ChesspairingPlayer>();
    private List<ChesspairingRound> rounds = new ArrayList<ChesspairingRound>();
    private PairingSummary parringSummary;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFederation() {
        return federation;
    }

    public void setFederation(String federation) {
        this.federation = federation;
    }

    public Date getDateOfStart() {
        return dateOfStart;
    }

    public void setDateOfStart(Date dateOfStart) {
        this.dateOfStart = dateOfStart;
    }

    public Date getDateOfEnd() {
        return dateOfEnd;
    }

    public void setDateOfEnd(Date dateOfEnd) {
        this.dateOfEnd = dateOfEnd;
    }

    public String getTypeOfTournament() {
        return typeOfTournament;
    }

    public void setTypeOfTournament(String typeOfTournament) {
        this.typeOfTournament = typeOfTournament;
    }

    public String getChiefArbiter() {
        return ChiefArbiter;
    }

    public void setChiefArbiter(String chiefArbiter) {
        ChiefArbiter = chiefArbiter;
    }

    public String getDeputyChiefArbiters() {
        return deputyChiefArbiters;
    }

    public void setDeputyChiefArbiters(String deputyChiefArbiters) {
        this.deputyChiefArbiters = deputyChiefArbiters;
    }


    public ChesspairingByeValue getChesspairingByeValue() {
        if (null == this.chesspairingByeValue) {
            this.chesspairingByeValue = ChesspairingByeValue.ONE_POINT;
        }
        return chesspairingByeValue;
    }

    public void setChesspairingByeValue(ChesspairingByeValue chesspairingByeValue) {
        this.chesspairingByeValue = chesspairingByeValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTotalRounds() {
        return totalRounds;
    }

    public void setTotalRounds(int totalRounds) {
        this.totalRounds = totalRounds;
    }

    public List<ChesspairingRound> getRounds() {
        return rounds;
    }

    public void setRounds(List<ChesspairingRound> rounds) {
        this.rounds = rounds;
    }

    public List<ChesspairingPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<ChesspairingPlayer> players) {
        this.players = players;
    }

    public PairingSummary getParringSummary() {
        return parringSummary;
    }

    public void setParringSummary(PairingSummary parringSummary) {
        this.parringSummary = parringSummary;
    }

    /**
     * It returns the player in {@link #players} by player key
     *
     * @param playerKey is the key of a specific player
     * @return a player and throws exception if player does not exist
     */
    public ChesspairingPlayer getPlayer(String playerKey) {
        for (ChesspairingPlayer player : players) {
            String key = player.getPlayerKey();
            if (key.equals(playerKey)) {
                return player;
            }
        }
        throw new IllegalStateException("Player does not exist in the players list");
    }

    /**
     * It returns the player by initial index If index is 0 or does not exist in the
     * tournament ten it throws exception
     *
     * @param indexPlayer is the index of the player
     * @return the playerf located in the players list
     */
    public ChesspairingPlayer getPlayerByInitialRank(int indexPlayer) {
        if (indexPlayer <= 0) {
            throw new IllegalStateException("Index is <= then zero");
        }
        if (indexPlayer > players.size()) {
            throw new IllegalStateException("Index is >= players.size()");
        }

        for (ChesspairingPlayer player : this.players) {
            if (player.getInitialOrderId() == indexPlayer) {
                return player;
            }
        }

        throw new IllegalStateException("Index nod found. indexPlayer = " + indexPlayer);
    }

    /**
     * It adds a round to this tournament;
     *
     * @param round
     */
    public void addRound(ChesspairingRound round) {
        this.rounds.add(round);
    }

    /**
     * Compute players ranking after all games are played for a specific round
     *
     * @param roundNumber is the round number for witch wee have to compute the standings
     * @return and ordered list of players. The best player is ranked number one
     */
    public List<ChesspairingPlayer> computeStandings(final int roundNumber) {
        List<ChesspairingPlayer> standings = new ArrayList<>();
        standings.addAll(this.players);


        // collect all games. for each player create a list with all the games that he
        // played
        final Map<ChesspairingPlayer, List<ChesspairingGame>> playerGames = new HashMap<>();
        // map with players this player won against
        final Map<ChesspairingPlayer, List<ChesspairingPlayer>> woneAgainst = new HashMap<>();
        for (ChesspairingPlayer player : this.players) {
            List<ChesspairingGame> games = new ArrayList<>();
            playerGames.put(player, games);
            List<ChesspairingPlayer> players = new ArrayList<>();
            woneAgainst.put(player, players);
        }
        for (int i = 1; i <= roundNumber; i++) {
            // for each
            ChesspairingRound round = getRoundByRoundNumber(i);
            for (ChesspairingPlayer player : this.getPlayers()) {
                if (!round.playerAbsent(player)) {
                    ChesspairingGame game = round.getGame(player);
                    List<ChesspairingGame> games = playerGames.get(player);
                    games.add(game);

                    if (game.playerWins(player)) {
                        ChesspairingPlayer adversery = game.getAdversary(player);
                        List<ChesspairingPlayer> trofeyList = woneAgainst.get(player);
                        trofeyList.add(adversery);
                    }
                }
            }
        }

        Comparator<ChesspairingPlayer> byPoints = new Comparator<ChesspairingPlayer>() {
            Map<ChesspairingPlayer, Float> pointsMap = computePointsUntilRound(roundNumber);

            @Override
            public int compare(ChesspairingPlayer o1, ChesspairingPlayer o2) {
                // TODO Auto-generated method stub
                Float points1 = pointsMap.get(o1);
                Float points2 = pointsMap.get(o2);
                // compare in reverce order

                return points2.compareTo(points1);
            }
        };

        Comparator<ChesspairingPlayer> byDirectMatches = new Comparator<ChesspairingPlayer>() {

            @Override
            public int compare(ChesspairingPlayer o1, ChesspairingPlayer o2) {
                List<ChesspairingPlayer> trofeyList = woneAgainst.get(o1);
                if (trofeyList.contains(o2)) {
                    return 1;
                }
                return 0;
            }
        };

        Comparator<ChesspairingPlayer> byRevercedInitialOrder = new ByInitialOrderIdReverce();

        ChainedComparator chainedComparator = new ChainedComparator(byPoints, byDirectMatches, byRevercedInitialOrder);

        Collections.sort(standings, chainedComparator);

        return standings;
    }

    /**
     * @param roundNumber
     * @return
     */
    private Map<ChesspairingPlayer, Float> computePointsUntilRound(int roundNumber) {
        final Map<ChesspairingPlayer, Float> pointsMap = new HashMap<>();
        //final Map<ChesspairingPlayer, Float> pointsMap =  new HashMap<>();
        // set the points to 0
        for (ChesspairingPlayer player : this.players) {
            pointsMap.put(player, 0f);
        }

        for (int i = 1; i <= roundNumber; i++) {
            ChesspairingRound round = getRoundByRoundNumber(i);
            if (!round.allGamesHaveBeanPlayed()) {
                throw new IllegalStateException(
                        "Attempt to compute standings when there are still games with no result");
            }

            // cycle all games and collect the points
            for (ChesspairingPlayer player : this.players) {
                for (ChesspairingGame game : round.getGames()) {
                    Float initialPoints = pointsMap.get(player);
                    if (game.hasPlayer(player)) {
                        Float points = game.getPointsForPlayer(player);
                        Float result = points + initialPoints;
                        pointsMap.put(player, result);
                    }
                }
                // Float points = round.getPointsFor(player, this.getChesspairingByeValue());
                // Float initialPoints = pointsMap.get(player);
                // Float result = points + initialPoints;
                // pointsMap.put(player, result);
            }
        }
        return pointsMap;
    }

    /**
     * It finds the round by a specific round number. If the round requested does
     * not exist then the request it will just throw exception
     *
     * @param roundNumber of the round requested
     * @return the round identified by round number
     */
    public ChesspairingRound getRoundByRoundNumber(int roundNumber) {
        for (ChesspairingRound round : getRounds()) {
            if (roundNumber == round.getRoundNumber()) {
                return round;
            }
        }

        // no round located
        throw new IllegalStateException("Not able to locate round nr " + roundNumber);
    }

    /**
     * It computes the BuchholzPoints for a specific player in a specific round
     *
     * @param roundNumber the round number
     * @param playerId    the player id
     * @return float value
     */
    public float computeBuchholzPoints(int roundNumber, String playerId) {
        ChesspairingRound round = this.getRoundByRoundNumber(roundNumber);
        ChesspairingPlayer player = this.getPlayer(playerId);
        if (!round.isPaired(player)) {
            if (roundNumber == 1) {
                return 0.0f;
            }
            return computeBuchholzPoints(roundNumber - 1, playerId);
        }

        Map<ChesspairingPlayer, Float> pointsMap = computePointsUntilRound(roundNumber);
        List<ChesspairingPlayer> opponents = getOpponentsForPlayer(roundNumber, playerId);
        float opponentPoints = 0.0f;
        for (ChesspairingPlayer opponent : opponents) {
            opponentPoints += pointsMap.get(opponent);
        }
        float byePoints = computeByeBuchholzPoints(roundNumber, playerId);
        float forfeitPoints = computeForfeitBuchholzPoints(roundNumber, playerId);

        // we need to ignore the opponent points in case the current player forfeit the game
        boolean isPaired = this.getRoundByRoundNumber(roundNumber).isPaired(player);
        if (isPaired) {
            ChesspairingGame game = this.getRoundByRoundNumber(roundNumber).getGame(player);
            if (game.playerForfeitedTheGame(player)) {
                // in this case we do not add the opponent points because the player actually forfeited the game
                return byePoints + forfeitPoints;
            }
        }
        return opponentPoints + byePoints + forfeitPoints;
    }

    /**
     * It computes the forfeit Buchholz points. The forfeit points are computed in the same manner as the bye points.
     *
     * @param gamesPlayedUntilRound
     * @param playerId
     * @return
     */
    private float computeForfeitBuchholzPoints(int gamesPlayedUntilRound, String playerId) {
        for (int i = 1; i <= gamesPlayedUntilRound; i++) {
            ChesspairingRound round = this.getRoundByRoundNumber(i);
            for (ChesspairingGame game : round.getGames()) {
                if (game.getResult() != ChesspairingResult.BYE && game.getResult() != ChesspairingResult.BYE) {
                    if (playerId.equals(game.getWhitePlayer().getPlayerKey())
                            && game.getResult() == ChesspairingResult.WHITE_WINS_BY_FORFEIT) {
                        int remainingRounds = totalRounds - i;
                        float forfeitPoints = 0.5f * remainingRounds;
                        return forfeitPoints;
                    } else if (playerId.equals(game.getBlackPlayer().getPlayerKey())
                            && game.getResult() == ChesspairingResult.BLACK_WINS_BY_FORFEIT) {
                        int remainingRounds = totalRounds - i;
                        float forfeitPoints = 0.5f * remainingRounds;
                        return forfeitPoints;
                    }
                }
            }
        }
        return 0.0f;
    }

    /**
     * Returns Buchholz points for a player related to the games plaid until a specific round
     *
     * @param gamesPlayedUntilRound
     * @param playerId
     * @return
     */
    private float computeByeBuchholzPoints(int gamesPlayedUntilRound, String playerId) {
        for (int i = 1; i <= gamesPlayedUntilRound; i++) {
            ChesspairingRound round = this.getRoundByRoundNumber(i);
            if (round.playerIsBye(playerId)) {
                int remainingRounds = totalRounds - i;
                float byePoints = 0.5f * remainingRounds;
                return byePoints;
            }
        }
        return 0.0f;
    }


    /**
     * It returns the opponents of a player from first round until a specific round
     *
     * @param gamesPlayedUntilRound
     * @param playerId
     * @return
     */
    private List<ChesspairingPlayer> getOpponentsForPlayer(int gamesPlayedUntilRound, String playerId) {
        List<ChesspairingPlayer> opponents = new ArrayList<>();
        for (int i = 1; i <= gamesPlayedUntilRound; i++) {
            ChesspairingRound round = this.getRoundByRoundNumber(i);
            Optional<ChesspairingPlayer> optionalOpponent = round.getOpponent(playerId);
            optionalOpponent.ifPresent(opponents::add);
        }
        return opponents;
    }
}
