package com.example;

import java.util.List;
import java.util.stream.Collectors;

public abstract class ComputerPlayer extends Player {
    enum type {ACCURATE, RANDOM}

    @Override
    public void takeTurn() {
        if (!shouldDrawCard(PlayerHandler.getTopOfDiscard(), PlayerHandler.getDeclaredSuit())) {
            PlayerHandler.setTopOfDiscard(playCard());
            if (PlayerHandler.getCheater() == null && getTurn().playedCard.getRank() == Card.Rank.EIGHT) {
                PlayerHandler.setDeclaredSuit(declareSuit());
            }
            getTurn().drewACard = false;
            if (PlayerHandler.getCheater() == null) {
                System.out.println("\n==========================================\n"
                        + getName() + " played the " + getTurn().playedCard.getRank() + " of " + getTurn().playedCard.getSuit()
                        + ".\n==========================================\n");
            }
            return;
        }
        receiveCard(PlayerHandler.getDrawPile().drawFrom());
        getTurn().drewACard = true;
        System.out.println("\n==========================================\n"
                         + getName() + " drew a card."
                         + "\n==========================================\n");
    }

    @Override
    public Card playCard() {
        int amountOfPlayableCards = hand.stream()
                .filter(card -> card.getRank().equals(PlayerHandler.getDeclaredRank())
                        || card.getSuit().equals(PlayerHandler.getDeclaredSuit())
                        || card.getRank().equals(Card.Rank.EIGHT))
                .collect(Collectors.toList()).size();
        List<Card> playableCards = hand.stream()
                .filter(card -> card.getRank().equals(PlayerHandler.getDeclaredRank())
                        || card.getSuit().equals(PlayerHandler.getDeclaredSuit())
                        || card.getRank().equals(Card.Rank.EIGHT))
                .collect(Collectors.toList());
        getTurn().playedCard = amountOfPlayableCards > 0 ? playableCards.get((int)(Math.random() * playableCards.size())) : null;

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
