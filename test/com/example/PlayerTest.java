package com.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import java.util.*;

public class PlayerTest {

    DrawPile drawPile = new DrawPile(Card.getDeck());
    Random random = new Random();

    @Test
    public void playersHaveUniqueIds() {
        assertNotEquals(new AccurateComputerPlayer().getId(), new AccurateComputerPlayer().getId());
    }
    @Test
    public void nameProperlySet() {
        final String NAME = "PLAYER";
        assertEquals(NAME, new RealPlayer(NAME).getName());
    }
    @Test
    public void correctInitialCards() {
        List<Card> initialHand = new ArrayList<>();
        Collections.shuffle(drawPile.getPile());
        for (int cardsInHand = 0; cardsInHand < 5; cardsInHand++) {
            initialHand.add(drawPile.drawFrom());
        }
        Player testPlayer = new AccurateComputerPlayer();
        testPlayer.receiveInitialCards(initialHand);
        assertEquals(initialHand, testPlayer.getHand());
    }
}
