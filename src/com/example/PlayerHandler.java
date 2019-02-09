package com.example;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class for managing the players.
 */
public class PlayerHandler {
    /** Used for adding Random players to the game. */
    private static Random random = new Random();
    /** The players of the game that this class will manage. */
    private static List<Player> players;
    /** The information about the last players turn.*/
    /** The draw pile. */
    private static DrawPile drawPile;
    /** The card at the top of the discard pile. */
    private static Card topOfDiscard;
    /** The current suit that can be played. */
    private static Card.Suit declaredSuit;
    /** The current suit that can be played. */
    private static Card.Rank declaredRank;
    /** The number of points needed to win the game. */
    private static int pointsNeededToWin;
    /** The multiple that the points needed to win increases by. */
    private static final int POINT_MULTIPLIER = 50;
    /** A list the keeps track of each players turn. */
    private List<PlayerTurn> playerTurns;
    /** The Winner of the round. */
    private static List<Player> roundWinners = new ArrayList<>();
    /** The Winner of the game. */
    private static Player gameWinner;
    /** Represents the cheater.*/
    private static Player cheater;

    /**
     * The default constructor.
     * @param setPlayers The players of the game
     */
    PlayerHandler(final List<Player> setPlayers) {
        players = setPlayers;
        playerTurns = new ArrayList<>();

        // Makes sure there is at least for players in the game by randomly adding computer players.
        while (players.size() < Player.MAX_NUM_OF_PLAYERS) {
            ComputerPlayer.type selectedComputerPlayer = ComputerPlayer.type.values()[(int) (Math.random() * ComputerPlayer.type.values().length)];
            switch (selectedComputerPlayer) {
                case RANDOM:
                    players.add(new RandomComputerPlayer());
                case ACCURATE:
                    players.add(new AccurateComputerPlayer());
            }
        }
        Collections.shuffle(players); // The players take turns in a random order.
        pointsNeededToWin = players.size() * POINT_MULTIPLIER;
    }

    /**
     * Initializes the players of the game if there are enough to start.
     */
    public void init() {
        for (Player player : players) {
            List<Integer> opponentIds = players.stream()
                                               .filter(filteredPlayers -> !filteredPlayers.getName().equals(player.getName()))
                                               .map(Player::hashCode)
                                               .collect(Collectors.toList());
            int playerId = (player.getName() + random.nextInt()).hashCode(); // Using the String class' built in hashcode for convenience. Also Computer ids will probably be unique.
            player.init(playerId, opponentIds);
            playerTurns.add(player.getTurn());
        }
        drawPile = new DrawPile(Card.getDeck());
    }

    private void dealInitialCards() {
        Collections.shuffle(drawPile.getPile());
        for (Player player : players) {
            List<Card> hand = new ArrayList<>();
            for (int cardsInHand = 0; cardsInHand < 5; cardsInHand++) {
                hand.add(drawPile.drawFrom());
            }
            player.receiveInitialCards(hand);
        }
    }

    private void processOpponentsAndTakeTurn() {
        for (Player player : players) {
            List<PlayerTurn> opponentsTurns = new ArrayList<>();
            for (Player opponent : players) {
                if (player.getId() != opponent.getId()) {
                    opponentsTurns.add(opponent.getTurn());
                }
            }
            player.processOpponentActions(opponentsTurns);
            player.takeTurn();
            if (cheater != null) {
                return;
            } else if (winner(player) || multipleWinners()) {
                System.out.println("\n==========================================\n"
                        + player.getName() + " won the round."
                        + "\n==========================================\n");
                return;
            }
        }
    }

    private void givePointsToWinner() {
        if (drawPile.pileSize() != 0) {
            for (Player winner : roundWinners) {
                for (Player opponent : players) {
                    int opponentHandValue = getHandValue(opponent.hand);
                    winner.addPoints(opponentHandValue);
                }
            }
        } else if (roundWinners.size() == 1) {
            for (Player winner : roundWinners) {
                int winnerHandValue = getHandValue(winner.hand);
                for (Player opponent : players) {
                    if (opponent.getId() != winner.getId()) {
                        int opponentHandValue = getHandValue(opponent.hand);
                        winner.addPoints(Math.abs(winnerHandValue - opponentHandValue));
                    }
                }
            }
        } else {
            for (Player winner : players) {
                for (Player opponent : players) {
                    int opponentHandValue = getHandValue(opponent.hand);
                    winner.addPoints(opponentHandValue);
                }
            }
        }
        printRunningPoints(players);
    }

    private void printRunningPoints(List<Player> players) {
        System.out.println("\n==========================================");
        System.out.println("Points: \n---------------\n");
        for (Player player : players) {
            System.out.println("\t-" + player.getName() + ": " + player.getPoints() + "\n");
        }
    }

    private boolean winner(final Player player) {
        if (player.getHand().size() == 0) {
            roundWinners.add(player);
            return true;
        }
        return false;
    }

    private boolean multipleWinners() {
        if (drawPile.pileSize() == 0) {
            evaluateHands(players);
            return true;
        }
        return false;
    }

    public void evaluatePlayersTurns() {
        dealInitialCards();
        topOfDiscard = drawPile.drawFrom();
        while (topOfDiscard.getRank().equals(Card.Rank.EIGHT)) {
            drawPile.putBack(topOfDiscard);
            Collections.shuffle(drawPile.getPile());
            topOfDiscard = drawPile.drawFrom();
        }

        declaredSuit = topOfDiscard.getSuit();
        declaredRank = topOfDiscard.getRank();

        while (roundWinners.size() == 0 && cheater == null) {
            processOpponentsAndTakeTurn();
        }
        if (cheater != null) {
            System.out.println("\n" + cheater.getName() + " CHEATED!!!");
            System.out.println("Thanks for ruining the game...\n");
            return;
        }
        givePointsToWinner();
        checkForGameWinner();
        if (gameWinner != null) {
            System.out.println("\n" + gameWinner.getName() + " WON!!!");
            System.out.println("Thanks for playing!!!\n");
            return;
        }
        reset();
    }

    /**
     * Initializes the players of the game if there are enough to start.
     */
    public void reset() {
        drawPile = new DrawPile(Card.getDeck());
        topOfDiscard = null;
        declaredSuit = null;
        roundWinners.clear();
        players.forEach(Player::reset);
    }

    private void evaluateHands(final List<Player> players) {
        int min = getHandValue(players.get(0).hand);
        for (Player player : players) {
            int handValue = getHandValue(player.hand);
            if (min > handValue) {
                min = handValue;
            }
        }

        for (Player player : players) {
            if (getHandValue(player.hand) == min) {
                roundWinners.add(player);
            }
        }
    }

    private int getHandValue(final List<Card> hand) {
        int sum = 0;
        for (Card card : hand) {
            sum += card.getPointValue();
        }
        return sum;
    }

    public void checkForGameWinner() {
        for (Player player : players) {
            if (player.getPoints() >= pointsNeededToWin) {
                gameWinner = player;
            }
        }
    }

    public Player getGameWinner() {
        return gameWinner;
    }

    /**
     * Getter method for the top card of the discard pile.
     * @return The top card of the discard pile.
     */
    public static Card getTopOfDiscard() {
        return topOfDiscard;
    }

    /**
     * Setter method for the top card of the discard pile. Called whenever a player decides to play a card.
     * @param newTopOfDiscard The new card to be placed at the top of the discard pile.
     */
    public static void setTopOfDiscard(final Card newTopOfDiscard) {
        topOfDiscard = newTopOfDiscard;
    }

    public static Player getCheater() {
        return cheater;
    }

    public static void setCheater(Player theCheater) {
        cheater = theCheater;
    }

    /**
     * Getter method for a player.
     * @param playerId The id of the requested player.
     * @return A player with the specified id.
     */
    public static Player getPlayer(final int playerId) {
        return players.stream()
                      .filter(player -> player.getId() == playerId)
                      .collect(Collectors.toList()).get(0);
    }

    /**
     * Getter method for the declared suit.
     * @return The declared suit.
     */
    public static Card.Suit getDeclaredSuit() {
        return declaredSuit;
    }

    /**
     * Setter method for the declared suit.
     * @param newSuit The new suit.
     */
    public static void setDeclaredSuit(Card.Suit newSuit) {
        declaredSuit = newSuit;
    }

    public static Card.Rank getDeclaredRank() {
        return declaredRank;
    }

    public static void setDeclaredRank(Card.Rank newRank) {
        declaredRank = newRank;
    }

    /**
     * Getter method for the draw pile.
     * @return The draw pile.
     */
    public static DrawPile getDrawPile() {
        return drawPile;
    }
}
