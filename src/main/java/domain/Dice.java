package domain;

import java.util.Random;

public class Dice {
    private final Random random;

    public Dice(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Random is required to roll dice");
        }
        this.random = random;
    }
}
