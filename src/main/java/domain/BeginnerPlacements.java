package domain;

import java.util.Map;

import static domain.PlayerColor.RED;
import static domain.PlayerColor.ORANGE;
import static domain.PlayerColor.BLUE;
import static domain.PlayerColor.WHITE;

public class BeginnerPlacements {
    private static final Map<Integer, PlayerColor> SETTLEMENTS = Map.of(
            10, RED, 29, RED,
            13, ORANGE, 42, ORANGE,
            40, BLUE, 44, BLUE,
            19, WHITE, 35, WHITE);
    private static final Map<Integer, PlayerColor> ROADS = Map.of(
            13, RED, 41, RED,
            15, ORANGE, 58, ORANGE,
            56, BLUE, 52, BLUE,
            25, WHITE, 37, WHITE);
    private static final Map<PlayerColor, Integer> STARTING_RESOURCE_NODE_IDS = Map.of(
            RED, 29, ORANGE, 42, BLUE, 40, WHITE, 19);

    private static final BeginnerPlacements FOUR_PLAYERS =
            new BeginnerPlacements(SETTLEMENTS, ROADS, STARTING_RESOURCE_NODE_IDS);

    private final Map<Integer, PlayerColor> settlements;
    private final Map<Integer, PlayerColor> roads;
    private final Map<PlayerColor, Integer> startingResourceNodeIds;

    private BeginnerPlacements(Map<Integer, PlayerColor> settlements,
                               Map<Integer, PlayerColor> roads,
                               Map<PlayerColor, Integer> startingResourceNodeIds) {
        this.settlements = settlements;
        this.roads = roads;
        this.startingResourceNodeIds = startingResourceNodeIds;
    }

    public static BeginnerPlacements forFourPlayers() {
        return FOUR_PLAYERS;
    }

    Map<Integer, PlayerColor> getSettlements() {
        return settlements;
    }

    Map<Integer, PlayerColor> getRoads() {
        return roads;
    }

    Map<PlayerColor, Integer> getStartingResourceNodeIds() {
        return startingResourceNodeIds;
    }
}