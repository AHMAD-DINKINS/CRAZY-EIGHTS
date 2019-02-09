package com.example;

import java.util.ArrayList;
import java.util.List;

public class Main {
    /** Manages the players within the game. */
    private static PlayerHandler playerHandler;

    /**
     * The method that will run a Crazy Eights game.
     * @param args unused
     */
    public static void main(String[] args) {
        List<Player> players = new ArrayList<>();
        for (String possiblePlayer : args) {
            players.add(new RealPlayer(possiblePlayer));
        }

        playerHandler = new PlayerHandler(players);
        playerHandler.init();

        System.out.println("Let's play a game of Crazy Eights!!!");

        while (playerHandler.getGameWinner() == null && PlayerHandler.getCheater() == null) {
            playerHandler.evaluatePlayersTurns();
        }
        System.out.println("Game Over");
    }
}
