package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class StartingRollOff {
    private static final int MIN_SEATS = 2;

    private final Dice dice;
    private final List<Map<Integer, DiceRoll>> rounds = new ArrayList<>();

    public StartingRollOff(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Random is required to roll for starting order");
        }
        dice = new Dice(random);
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

    public List<Map<Integer, DiceRoll>> getRounds() {
        return List.copyOf(rounds);
    }

    // Seats that tie roll again among themselves; each tied group keeps its place in the overall order.
    private List<Integer> order(List<Integer> seats) {
        Map<Integer, List<Integer>> seatsByRoll = new TreeMap<>(Comparator.reverseOrder());
        Map<Integer, DiceRoll> round = new LinkedHashMap<>();
        for (int seat : seats) {
            DiceRoll roll = dice.roll();
            round.put(seat, roll);
            seatsByRoll.computeIfAbsent(roll.getTotal(), total -> new ArrayList<>()).add(seat);
        }
        rounds.add(Collections.unmodifiableMap(round));
        List<Integer> ordered = new ArrayList<>();
        for (List<Integer> sameRoll : seatsByRoll.values()) {
            ordered.addAll(sameRoll.size() == 1 ? sameRoll : order(sameRoll));
        }
        return ordered;
    }
}
