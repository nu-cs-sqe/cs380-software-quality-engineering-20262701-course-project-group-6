package domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class StartingRollOff {
    private static final int MIN_SEATS = 2;
    private static final int DIE_SIDES = 6;

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
        Map<Integer, Integer> rolls = new HashMap<>();
        List<Integer> seats = new ArrayList<>();
        for (int seat = 1; seat <= seatCount; seat++) {
            rolls.put(seat, rollTwoDice());
            seats.add(seat);
        }
        seats.sort(Comparator.comparing(rolls::get, Comparator.reverseOrder()));
        return seats;
    }

    private int rollTwoDice() {
        return rollDie() + rollDie();
    }

    private int rollDie() {
        return random.nextInt(DIE_SIDES) + 1;
    }
}
