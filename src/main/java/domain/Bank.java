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
        ResourceArguments.requireType(type);
        ResourceArguments.requirePositiveAmount(amount);
        int count = resources.get(type);
        if (count < amount) {
            throw new IllegalStateException("Bank has " + count + " " + type + " but " + amount + " were requested");
        }
        resources.put(type, count - amount);
    }

    public void depositResource(ResourceCardType type, int amount) {
        ResourceArguments.requireType(type);
        ResourceArguments.requirePositiveAmount(amount);
        int count = resources.get(type);
        if (count + amount > CARDS_PER_RESOURCE) {
            throw new IllegalStateException("Bank cannot hold more than " + CARDS_PER_RESOURCE + " " + type
                    + " (has " + count + ", tried to deposit " + amount + ")");
        }
        resources.put(type, count + amount);
    }

    public Map<PlayerColor, Integer> payProduction(ResourceCardType type, Map<PlayerColor, Integer> owed) {
        if (owed.isEmpty()) {
            return Map.of();
        }
        int totalOwed = owed.values().stream().mapToInt(Integer::intValue).sum();
        int available = getResourceCount(type);
        if (totalOwed > available) {
            if (owed.size() > 1 || available == 0) {
                return Map.of();
            }
            PlayerColor onlyColor = owed.keySet().iterator().next();
            withdrawResource(type, available);
            return Map.of(onlyColor, available);
        }
        withdrawResource(type, totalOwed);
        return Map.copyOf(owed);
    }

    public int getResourceCount(ResourceCardType type) {
        ResourceArguments.requireType(type);
        return resources.get(type);
    }

    public DevCardType withdrawDevelopmentCard() {
        return deck.draw();
    }

    public int getDevelopmentCardsRemaining() {
        return deck.getRemainingCount();
    }
}
