package domain;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

public class Bank {
    private static final int CARDS_PER_RESOURCE = 19;

    private final Map<ResourceCardType, Integer> resources = new EnumMap<>(ResourceCardType.class);
    private final DevelopmentDeck deck;

    public Bank(Random random) {
        deck = new DevelopmentDeck(random);
        for (ResourceCardType type : ResourceCardType.values()) {
            resources.put(type, CARDS_PER_RESOURCE);
        }
    }

    public void withdrawResource(ResourceCardType type, int amount) {
        requireResourceType(type);
        requirePositiveAmount(amount);
        int count = resources.get(type);
        if (count < amount) {
            throw new IllegalStateException("Bank has " + count + " " + type + " but " + amount + " were requested");
        }
        resources.put(type, count - amount);
    }

    public void depositResource(ResourceCardType type, int amount) {
        requireResourceType(type);
        requirePositiveAmount(amount);
        int count = resources.get(type);
        if (count + amount > CARDS_PER_RESOURCE) {
            throw new IllegalStateException("Bank cannot hold more than " + CARDS_PER_RESOURCE + " " + type
                    + " (has " + count + ", tried to deposit " + amount + ")");
        }
        resources.put(type, count + amount);
    }

    public int getResourceCount(ResourceCardType type) {
        return resources.get(type);
    }

    public int getDevelopmentCardsRemaining() {
        return deck.getRemainingCount();
    }

    private void requireResourceType(ResourceCardType type) {
        if (type == null) {
            throw new IllegalArgumentException("Resource type is required");
        }
    }

    private void requirePositiveAmount(int amount) {
        if (amount < 1) {
            throw new IllegalArgumentException("Resource amount must be at least 1 (was " + amount + ")");
        }
    }
}
