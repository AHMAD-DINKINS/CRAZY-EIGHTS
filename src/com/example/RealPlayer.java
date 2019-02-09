package com.example;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class RealPlayer extends Player {
    /** Utility used for getting input from real players. */
    private Scanner scanner = new Scanner(System.in);
    /** The decision of the real player. */
    private String decision = "";

    /**
     * Then constructor for real players.
     * @param name The name of the real player.
     */
    RealPlayer(final String name) {
        super(name);
    }

    @Override
    public void takeTurn() {
        if (!shouldDrawCard(PlayerHandler.getTopOfDiscard(), PlayerHandler.getDeclaredSuit())) {
            PlayerHandler.setTopOfDiscard(playCard());
            if (PlayerHandler.getCheater() == null && getTurn().playedCard.getRank() == Card.Rank.EIGHT) {
                PlayerHandler.setDeclaredSuit(declareSuit());
            }
            getTurn().drewACard = false;
            return;
        }
        receiveCard(PlayerHandler.getDrawPile().drawFrom());
        getTurn().drewACard = true;
    }

    /**
     * Called when so the player can decide if they want to take a card or not.
     * @param topPileCard The card currently at the top of the pile
     * @param pileSuit The suit that the pile was changed to as the result of an "8" being played.
     * Will be null if no "8" was played.
     * @return True if the player decides to draw a card.
     */
    @Override
    public boolean shouldDrawCard(Card topPileCard, Card.Suit pileSuit) {
        System.out.println("\n==========================================");
        System.out.println("Card at top of discard pile: " + topPileCard.getRank() + " of " + topPileCard.getSuit());
        if (pileSuit != null) {
            System.out.println("The current declared suit: " + pileSuit);
        }
        System.out.print("\nHAND:\t");
        this.hand.forEach(card -> System.out.print(card.getRank() + " of " + card.getSuit() + "\t\t"));
        System.out.print("\nWould you like to draw a card?(yes/no): ");

        decision = scanner.nextLine().toUpperCase();
        while(!(decision.equals("YES") || decision.equals("NO"))) {
            System.out.print("\n================\nINVALID INPUT!!!\n================\n\nPlease Enter yes or no: ");
            decision = scanner.nextLine().toUpperCase();
        }
        return decision.equals("YES");
    }

    /**
     * Called when the player decides to play a card.
     * @return The card the player wants to play.
     */
    @Override
    public Card playCard() {
        String rank;
        System.out.print("\nEnter the rank of the card you would like to play(Ex eight): ");

        decision = scanner.nextLine().toUpperCase();
        while(!(VALID_RANKS.contains(decision))) {
            System.out.print("\n================\nINVALID INPUT!!!\n================\n\nPlease Enter a valid Rank: ");
            decision = scanner.nextLine().toUpperCase();
        }
        rank = decision;

        String suit;
        System.out.print("\nEnter the suit of the card you would like to play(Ex spades): ");

        decision = scanner.nextLine().toUpperCase();
        while(!(VALID_SUITS.contains(decision) && decision.endsWith("S"))) {
            System.out.print("\n================\nINVALID INPUT!!!\n================\n\nPlease Enter a valid Suit: ");
            decision = scanner.nextLine().toUpperCase();
        }
        suit = decision;

        int amountOfPlayableCards = hand.stream()
                .filter(card -> card.getRank().equals(PlayerHandler.getDeclaredRank())
                        || card.getSuit().equals(PlayerHandler.getDeclaredSuit())
                        || rank.equals(Card.Rank.EIGHT.name()))
                .filter(card -> card.getRank().toString().equals(rank))
                .filter(card -> card.getSuit().toString().equals(suit))
                .collect(Collectors.toList()).size();
        List<Card> playableCards = hand.stream()
                .filter(card -> card.getRank().equals(PlayerHandler.getDeclaredRank())
                        || card.getSuit().equals(PlayerHandler.getDeclaredSuit())
                        || rank.equals(Card.Rank.EIGHT.name()))
                .filter(card -> card.getRank().toString().equals(rank))
                .filter(card -> card.getSuit().toString().equals(suit))
                .collect(Collectors.toList());
        getTurn().playedCard = amountOfPlayableCards > 0 ? playableCards.get(0) : null;

        if (getTurn().playedCard == null) {
            PlayerHandler.setCheater(this);
        } else {
            PlayerHandler.setDeclaredRank(getTurn().playedCard.getRank());
            PlayerHandler.setDeclaredSuit(getTurn().playedCard.getSuit());
        }

        getHand().remove(getTurn().playedCard);
        return getTurn().playedCard;
    }

    /**
     * The suit that the players must play.
     * @return The declared suit.
     */
    @Override
    public Card.Suit declareSuit() {
        System.out.print("\nEnter the new suit(Diamonds/Hearts/Clubs/Spades): ");

        decision = scanner.nextLine().toUpperCase();
        while(!(VALID_SUITS.contains(decision) && decision.endsWith("S"))) {
            System.out.print("\n================\nINVALID INPUT!!!\n================\n\nPlease Enter an valid Suit: ");
            decision = scanner.nextLine().toUpperCase();
        }

        getTurn().declaredSuit = Arrays.stream(Card.Suit.values())
                .filter(suit -> suit.toString().equals(decision))
                .collect(Collectors.toList()).get(0);

        return getTurn().declaredSuit;
    }
}
