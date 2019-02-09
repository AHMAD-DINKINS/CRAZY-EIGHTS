package com.example;


public class RandomComputerPlayer extends ComputerPlayer {
    @Override
    public boolean shouldDrawCard(Card topPileCard, Card.Suit pileSuit) {
        return ((int)(Math.random() * 100) < 80);
    }

}
