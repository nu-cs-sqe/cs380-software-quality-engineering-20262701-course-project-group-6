package domain;

import java.util.EnumMap;
import java.util.Map;

public class Player {
    private static final int STARTING_SETTLEMENTS = 2;
    private static final int STARTING_ROADS = 2;
    private static final int MAX_SETTLEMENTS = 5;
    private static final int MAX_CITIES = 4;
    private static final int MAX_ROADS = 15;
    private static final int CITY_VICTORY_POINTS = 2;

    private final PlayerColor color;
    private int settlementsPlaced;
    private int citiesPlaced;
    private int roadsPlaced;
    private final Map<ResourceCardType, Integer> resources = new EnumMap<>(ResourceCardType.class);
    private final Map<DevCardType, Integer> playableDevCards = new EnumMap<>(DevCardType.class);
    private final Map<DevCardType, Integer> newDevCards = new EnumMap<>(DevCardType.class);
    private int knightsPlayed;
    private boolean playedDevCardThisTurn;

    public Player(PlayerColor color) {
        if (color == null) {
            throw new IllegalArgumentException("Player color is required");
        }
        this.color = color;
        this.settlementsPlaced = STARTING_SETTLEMENTS;
        this.roadsPlaced = STARTING_ROADS;
    }

    public void placeSettlementPiece() {
        if (settlementsPlaced == MAX_SETTLEMENTS) {
            throw new IllegalStateException(
                    "Player " + color + " has no settlements left to place (max " + MAX_SETTLEMENTS + ")");
        }
        settlementsPlaced++;
    }

    public void placeCityPiece() {
        if (citiesPlaced == MAX_CITIES) {
            throw new IllegalStateException(
                    "Player " + color + " has no cities left to place (max " + MAX_CITIES + ")");
        }
        if (settlementsPlaced == 0) {
            throw new IllegalStateException("Player " + color + " has no settlement to upgrade to a city");
        }
        citiesPlaced++;
        settlementsPlaced--;
    }

    public void placeRoadPiece() {
        if (roadsPlaced == MAX_ROADS) {
            throw new IllegalStateException(
                    "Player " + color + " has no roads left to place (max " + MAX_ROADS + ")");
        }
        roadsPlaced++;
    }

    public void collectResource(ResourceCardType type, int amount) {
        ResourceArguments.requireType(type);
        ResourceArguments.requirePositiveAmount(amount);
        resources.merge(type, amount, Integer::sum);
    }

    public void useResource(ResourceCardType type, int amount) {
        ResourceArguments.requireType(type);
        ResourceArguments.requirePositiveAmount(amount);
        int count = countOf(type);
        if (count < amount) {
            throw new IllegalStateException(
                    "Player " + color + " has " + count + " " + type + " but tried to use " + amount);
        }
        resources.put(type, count - amount);
    }

    public void collectDevCard(DevCardType type) {
        requireDevCardType(type);
        newDevCards.merge(type, 1, Integer::sum);
    }

    public void useDevCard(DevCardType type) {
        requireDevCardType(type);
        if (type == DevCardType.VICTORY_POINT) {
            throw new IllegalArgumentException("Victory Point cards are never played; they count while held");
        }
        if (playedDevCardThisTurn) {
            throw new IllegalStateException("Player " + color + " has already played a development card this turn");
        }
        int playable = playableDevCards.getOrDefault(type, 0);
        if (playable == 0) {
            throw noPlayableCopy(type);
        }
        playableDevCards.put(type, playable - 1);
        playedDevCardThisTurn = true;
        knightsPlayed++;
    }

    public void endTurn() {
        newDevCards.forEach((type, count) -> playableDevCards.merge(type, count, Integer::sum));
        newDevCards.clear();
    }

    public int getDevCardCount(DevCardType type) {
        requireDevCardType(type);
        return playableDevCards.getOrDefault(type, 0) + newDevCards.getOrDefault(type, 0);
    }

    public int getKnightsPlayed() {
        return knightsPlayed;
    }

    public int getVictoryPointCount() {
        return settlementsPlaced + citiesPlaced * CITY_VICTORY_POINTS;
    }

    public PlayerColor getColor() {
        return color;
    }

    public int getSettlementsRemaining() {
        return MAX_SETTLEMENTS - settlementsPlaced;
    }

    public int getCitiesRemaining() {
        return MAX_CITIES - citiesPlaced;
    }

    public int getRoadsRemaining() {
        return MAX_ROADS - roadsPlaced;
    }

    public int getResourceCount(ResourceCardType type) {
        ResourceArguments.requireType(type);
        return countOf(type);
    }

    public int getTotalResourceCount() {
        int total = 0;
        for (int count : resources.values()) {
            total += count;
        }
        return total;
    }

    private IllegalStateException noPlayableCopy(DevCardType type) {
        if (newDevCards.containsKey(type)) {
            return new IllegalStateException("Player " + color + " cannot play a " + type + " on the turn it was bought");
        }
        return new IllegalStateException("Player " + color + " has no " + type + " to play");
    }

    private static void requireDevCardType(DevCardType type) {
        if (type == null) {
            throw new IllegalArgumentException("Development card type is required");
        }
    }

    private int countOf(ResourceCardType type) {
        return resources.getOrDefault(type, 0);
    }
}
