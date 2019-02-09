package com.example;

import java.util.stream.Collectors;

public class AccurateComputerPlayer extends ComputerPlayer {

    @Override
    public boolean shouldDrawCard(Card topPileCard, Card.Suit pileSuit) {
        return !(hand.stream()
                .filter(card -> card.getRank().equals(PlayerHandler.getDeclaredRank())
                        || card.getSuit().equals(PlayerHandler.getDeclaredSuit())
                        || card.getRank().equals(Card.Rank.EIGHT))
                .collect(Collectors.toList()).size() > 0);
    }
}