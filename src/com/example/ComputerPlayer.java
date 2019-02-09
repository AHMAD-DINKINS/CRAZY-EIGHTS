package com.example;

import java.util.stream.Collectors;

public abstract class ComputerPlayer extends Player {
    /** The possible number of different computer players that can exist in the game. */
    final static int NUM_OF_DIFF_COMPUTER_PLAYERS = 2;

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

    @Override
    public Card playCard() {
        getTurn().playedCard = hand.stream()
                .filter(card -> card.getRank().equals(PlayerHandler.getDeclaredRank())
                        || card.getSuit().equals(PlayerHandler.getDeclaredSuit())
                        || card.getRank().equals(Card.Rank.EIGHT))
                .collect(Collectors.toList()).size() > 0 ? hand.stream()
                .filter(card -> card.getRank().equals(PlayerHandler.getDeclaredRank())
                        || card.getSuit().equals(PlayerHandler.getDeclaredSuit())
                        || card.getRank().equals(Card.Rank.EIGHT))
                .collect(Collectors.toList()).get(0) : null;

        if (getTurn().playedCard == null) {
            PlayerHandler.setCheater(this);
        } else {
            PlayerHandler.setDeclaredRank(getTurn().playedCard.getRank());
            PlayerHandler.setDeclaredSuit(getTurn().playedCard.getSuit());
        }

        getHand().remove(getTurn().playedCard);
        return getTurn().playedCard;
    }

    @Override
    public Card.Suit declareSuit() {
        getTurn().declaredSuit = Card.Suit.values()[(int) (Math.random() * Card.Suit.values().length)];
        return getTurn().declaredSuit;
    }
}
