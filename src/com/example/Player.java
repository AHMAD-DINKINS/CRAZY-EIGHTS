package com.example;

import java.util.List;
import java.util.Random;

/**
 * Represents a player within the game.
 */
public abstract class Player implements PlayerStrategy {
    /** The minimum number of Players. */
    final static int MIN_NUM_OF_PLAYERS = 4;
    /** The minimum number of Players. */
    final static int MAX_NUM_OF_PLAYERS = 7;
    /** The id of the player. */
    private int id;
    /** The name of the player. */
    private String name;
    /** The players hand of cards. */
    protected List<Card> hand;
    /** The number of points the player has. */
    private int points = 0;
    /** The ids of the opponents of the player. */
    private List<Integer> opponentsIds;
    /** The actions the player. */
    private PlayerTurn turn;

    /**
     * Default constructor used for computer players.
     */
    Player() {
        // Random Object used for naming computer players.
        Random random = new Random();
        name = "PLAYER" + random.nextInt();
    }

    /**
     * The constructor used for real players.
     * @param setName The name of the player that the user passed in.
     */
    Player(final String setName) {
        name = setName;
    }

    /**
     * Getter method for the player's hand.
     * @return The players hand.
     */
    public List<Card> getHand() {
        return hand;
    }
    /**
     * Getter method for the player's id.
     * @return The id of the player.
     */
    public int getId() {
        return id;
    }

    /**
     * Getter method for the player's id.
     * @return The name of the player.
     */
    public String getName () {
        return name;
    }

    /**
     * Getter method for the points the player has.
     * @return The total points the player has.
     */
    public int getPoints() {
        return points;
    }

    /**
     * Method called when the player wins a round.
     * @param pointsToAdd The number of points to add.
     */
    public void addPoints(int pointsToAdd) {
        points += pointsToAdd;
    }

    /**
     * Getter method for the player's turn.
     * @return The previous turn of the player.
     */
    public PlayerTurn getTurn() {
        return turn;
    }

    /**
     * Getter method for the opponent ids.
     * @return The ids of the opponents.
     */
    public List<Integer> getOpponentsIds() {
        return opponentsIds;
    }

    /**
     * Called when this player is ready to take their turn.
     */
    public abstract void takeTurn();

    /**
     * Initializes the state of the player.
     * @param playerId The id for this player
     * @param opponentIds A list of ids for this player's opponents
     */
    @Override
    public void init(int playerId, List<Integer> opponentIds) {
        id = playerId;
        this.opponentsIds = opponentIds;
        turn = new PlayerTurn();
        turn.playerId = id;
    }

    /**
     * Method for initializing the players hand.
     * @param cards The initial list of cards dealt to this player
     */
    @Override
    public void receiveInitialCards(List<Card> cards) {
        hand = cards;
    }

    /**
     * Method called when the players decides to draw a card.
     * @param drawnCard The card that this player has drawn
     */
    @Override
    public void receiveCard(Card drawnCard) {
        hand.add(drawnCard);
    }

    /**
     * Resets the player for a new round of play.
     */
    @Override
    public void reset() {
        turn.playedCard = null;
        turn.drewACard = false;
        turn.declaredSuit = null;
        hand.clear();
    }

    /**
     * Called so the player can decide what to do on their turn.
     * @param opponentActions A list of what the opponents did on each of their turns.
     */
    @Override
    public void processOpponentActions(List<PlayerTurn> opponentActions) {
        if (!(this instanceof RealPlayer)) {
            return;
        }
        System.out.println("\n\n==========================================");
        System.out.println("It is " + getName() + "'s turn.");
        for(PlayerTurn turn: opponentActions) {
            if (!turn.drewACard && turn.playedCard == null) {
                System.out.println("\n" + PlayerHandler.getPlayer(turn.playerId) + " has not gone yet...\n");
                continue;
            }

            System.out.println("\nOn " + PlayerHandler.getPlayer(turn.playerId) + "'s turn they...");
            if (turn.drewACard) {
                System.out.println("\t-Drew a card.");
                continue;
            }

            System.out.print("\t-Played the " + turn.playedCard.getRank() + " of  " + turn.playedCard.getSuit());
            if (turn.playedCard.getRank() == Card.Rank.EIGHT) {
                System.out.println(" and declared the suit to be " + PlayerHandler.getDeclaredSuit() + ".");
            } else {
                System.out.println();
            }
        }
    }

    /**
     * Called whenever the player is to be printed.
     * @return The name of the player.
     */
    @Override
    public String toString() {
        return name;
    }
}
