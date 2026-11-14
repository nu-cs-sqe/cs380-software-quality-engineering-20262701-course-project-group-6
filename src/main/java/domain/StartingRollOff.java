package domain;

import java.util.Random;

public class StartingRollOff {
    private final Random random;

    public StartingRollOff(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Random is required to roll for starting order");
        }
        this.random = random;
    }
}
