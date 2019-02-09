package com.example;

import java.util.List;

/**
 * Represents the pile of cards from which players will draw from.
 */
public class DrawPile {
    /** The pile of cards the players will draw from. */
    private List<Card> drawPile;

    /** Constructor that initializes the draw pile. */
    DrawPile(final List<Card> initialDrawPile) {
        drawPile = initialDrawPile;
    }

    /**
     * Called when a player decides to draw.
     * @return A card from the draw pile.
     */
    public Card drawFrom () {
        return drawPile.remove(drawPile.size() - 1); // A pile is a stack of cards so list is treated as a stack.
    }

    /**
     * Called if the first card of a new round is an eight.
     * @param card The card to put back.
     */
    public void putBack(final Card card) {
        drawPile.add(card);
    }

    /**
     * Returns the length of the draw pile.
     * @return The size of drawPile.
     */
    public int pileSize() {
        return drawPile.size();
    }

    /**
     * Getter method for the draw pile.
     * @return The draw pile.
     */
    public List<Card> getPile() {
        return drawPile;
    }
}
