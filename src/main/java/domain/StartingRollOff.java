package domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

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
        List<Integer> seats = new ArrayList<>();
        for (int seat = 1; seat <= seatCount; seat++) {
            seats.add(seat);
        }
        return order(seats);
    }

    // Seats that tie roll again among themselves; each tied group keeps its place in the overall order.
    private List<Integer> order(List<Integer> seats) {
        Map<Integer, List<Integer>> seatsByRoll = new TreeMap<>(Comparator.reverseOrder());
        for (int seat : seats) {
            seatsByRoll.computeIfAbsent(rollTwoDice(), roll -> new ArrayList<>()).add(seat);
        }
        List<Integer> ordered = new ArrayList<>();
        for (List<Integer> sameRoll : seatsByRoll.values()) {
            ordered.addAll(sameRoll.size() == 1 ? sameRoll : order(sameRoll));
        }
        return ordered;
    }

    private int rollTwoDice() {
        return rollDie() + rollDie();
    }

    private int rollDie() {
        return random.nextInt(DIE_SIDES) + 1;
    }
}
