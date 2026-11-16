package domain;

import java.util.Random;

public class Dice {
    private static final int SIDES = 6;

    private final Random random;

    public Dice(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Random is required to roll dice");
        }
        this.random = random;
    }

    public int roll() {
        return rollOne() + rollOne();
    }

    private int rollOne() {
        return random.nextInt(SIDES) + 1;
    }
}
