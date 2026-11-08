package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DevelopmentDeck {
    private static final int KNIGHTS = 14;
    private static final int VICTORY_POINTS = 5;
    private static final int ROAD_BUILDINGS = 2;
    private static final int YEARS_OF_PLENTY = 2;
    private static final int MONOPOLIES = 2;

    private final List<DevCardType> cards = new ArrayList<>();

    public DevelopmentDeck(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Random is required to shuffle the development deck");
        }
        addCopies(DevCardType.KNIGHT, KNIGHTS);
        addCopies(DevCardType.VICTORY_POINT, VICTORY_POINTS);
        addCopies(DevCardType.ROAD_BUILDING, ROAD_BUILDINGS);
        addCopies(DevCardType.YEAR_OF_PLENTY, YEARS_OF_PLENTY);
        addCopies(DevCardType.MONOPOLY, MONOPOLIES);
        Collections.shuffle(cards, random);
    }

    public DevCardType draw() {
        return cards.remove(cards.size() - 1);
    }

    public int getRemainingCount() {
        return cards.size();
    }

    private void addCopies(DevCardType type, int copies) {
        for (int added = 0; added < copies; added++) {
            cards.add(type);
        }
    }
}
