package integration;

import domain.Dice;
import java.util.Random;

final class SeededDice {
    private static final int SIDES = 6;
    private static final Random SEED_PROBE = new Random();

    private SeededDice() {
    }

    // A real Random with a seed whose first dice are the ones asked for, so no class is mocked.
    static Dice rolling(int... dice) {
        for (long seed = 0; ; seed++) {
            if (startsWith(seed, dice)) {
                return new Dice(new Random(seed));
            }
        }
    }

    private static boolean startsWith(long seed, int... dice) {
        SEED_PROBE.setSeed(seed);
        for (int die : dice) {
            if (SEED_PROBE.nextInt(SIDES) + 1 != die) {
                return false;
            }
        }
        return true;
    }
}
