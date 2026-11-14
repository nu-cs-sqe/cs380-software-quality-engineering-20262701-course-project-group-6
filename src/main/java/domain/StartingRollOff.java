package domain;

import java.util.List;
import java.util.Random;

public class StartingRollOff {
    private static final int MIN_SEATS = 2;

    private final Random random;

    public StartingRollOff(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Random is required to roll for starting order");
        }
        this.random = random;
    }

    public List<Integer> rollOrder(int seatCount) {
        if (seatCount < MIN_SEATS) {
            throw new IllegalArgumentException(
                    "At least " + MIN_SEATS + " seats are needed to roll for starting order (was " + seatCount + ")");
        }
        return List.of();
    }
}
