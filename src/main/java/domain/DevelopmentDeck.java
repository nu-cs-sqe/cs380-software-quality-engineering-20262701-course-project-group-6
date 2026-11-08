package domain;

import java.util.Random;

public class DevelopmentDeck {
    private static final int DECK_SIZE = 25;

    public DevelopmentDeck(Random random) {
    }

    public int getRemainingCount() {
        return DECK_SIZE;
    }
}
