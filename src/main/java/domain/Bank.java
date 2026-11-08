package domain;

import java.util.Random;

public class Bank {
    private static final int CARDS_PER_RESOURCE = 19;

    private final DevelopmentDeck deck;

    public Bank(Random random) {
        deck = new DevelopmentDeck(random);
    }

    public int getResourceCount(ResourceCardType type) {
        return CARDS_PER_RESOURCE;
    }

    public int getDevelopmentCardsRemaining() {
        return deck.getRemainingCount();
    }
}
