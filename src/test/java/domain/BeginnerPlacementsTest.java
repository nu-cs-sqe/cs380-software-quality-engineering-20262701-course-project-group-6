package domain;

import static domain.PlayerColor.BLUE;
import static domain.PlayerColor.ORANGE;
import static domain.PlayerColor.RED;
import static domain.PlayerColor.WHITE;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BeginnerPlacementsTest {
    private static final int FOUR_PLAYER_PIECE_COUNT = 8;
    private static final int THREE_PLAYER_PIECE_COUNT = 6;
    private static final int FOUR_PLAYER_STAR_COUNT = 4;
    private static final int THREE_PLAYER_STAR_COUNT = 3;
    private static final int RED_STAR_NODE = 29;
    private static final int ORANGE_STAR_NODE = 42;
    private static final int BLUE_STAR_NODE = 40;
    private static final int WHITE_STAR_NODE = 19;
    private static final int SAMPLE_NODE_ID = 99;
    private static final int SAMPLE_EDGE_ID = 99;

    private static final int RED_SETTLEMENT_NODE_1 = 10;
    private static final int RED_SETTLEMENT_NODE_2 = 29;
    private static final int ORANGE_SETTLEMENT_NODE_1 = 13;
    private static final int ORANGE_SETTLEMENT_NODE_2 = 42;
    private static final int BLUE_SETTLEMENT_NODE_1 = 40;
    private static final int BLUE_SETTLEMENT_NODE_2 = 44;
    private static final int WHITE_SETTLEMENT_NODE_1 = 19;
    private static final int WHITE_SETTLEMENT_NODE_2 = 35;

    private static final int RED_ROAD_EDGE_1 = 13;
    private static final int RED_ROAD_EDGE_2 = 41;
    private static final int ORANGE_ROAD_EDGE_1 = 15;
    private static final int ORANGE_ROAD_EDGE_2 = 58;
    private static final int BLUE_ROAD_EDGE_1 = 56;
    private static final int BLUE_ROAD_EDGE_2 = 52;
    private static final int WHITE_ROAD_EDGE_1 = 25;
    private static final int WHITE_ROAD_EDGE_2 = 37;

    @Test
    void ForFourPlayers_DefaultState_ReturnsAllFourColorsAndExpectedCounts() {
        BeginnerPlacements placements = BeginnerPlacements.forFourPlayers();

        assertNotNull(placements);
        assertEquals(FOUR_PLAYER_PIECE_COUNT, placements.getSettlements().size());
        assertEquals(FOUR_PLAYER_PIECE_COUNT, placements.getRoads().size());
        assertEquals(FOUR_PLAYER_STAR_COUNT, placements.getStartingResourceNodeIds().size());
        assertEquals(RED_STAR_NODE, placements.getStartingResourceNodeIds().get(RED));
        assertEquals(ORANGE_STAR_NODE, placements.getStartingResourceNodeIds().get(ORANGE));
        assertEquals(BLUE_STAR_NODE, placements.getStartingResourceNodeIds().get(BLUE));
        assertEquals(WHITE_STAR_NODE, placements.getStartingResourceNodeIds().get(WHITE));
    }

    @Test
    void ForFourPlayers_RepeatedCalls_ReturnsSameInstance() {
        BeginnerPlacements first = BeginnerPlacements.forFourPlayers();
        BeginnerPlacements second = BeginnerPlacements.forFourPlayers();

        assertSame(first, second);
    }

    @Test
    void ForThreePlayers_DefaultState_OmitsRedFromPlacementsAndStartingNodes() {
        BeginnerPlacements placements = BeginnerPlacements.forThreePlayers();

        assertNotNull(placements);
        assertEquals(THREE_PLAYER_PIECE_COUNT, placements.getSettlements().size());
        assertEquals(THREE_PLAYER_PIECE_COUNT, placements.getRoads().size());
        assertEquals(THREE_PLAYER_STAR_COUNT, placements.getStartingResourceNodeIds().size());
        assertFalse(placements.getSettlements().containsValue(RED));
        assertFalse(placements.getRoads().containsValue(RED));
        assertFalse(placements.getStartingResourceNodeIds().containsKey(RED));
        assertEquals(ORANGE_STAR_NODE, placements.getStartingResourceNodeIds().get(ORANGE));
        assertEquals(BLUE_STAR_NODE, placements.getStartingResourceNodeIds().get(BLUE));
        assertEquals(WHITE_STAR_NODE, placements.getStartingResourceNodeIds().get(WHITE));
    }

    @Test
    void GetPlacements_ForFourPlayers_MatchesExpectedEntriesAndIsUnmodifiable() {
        BeginnerPlacements placements = BeginnerPlacements.forFourPlayers();
        Map<Integer, PlayerColor> settlements = placements.getSettlements();
        Map<Integer, PlayerColor> roads = placements.getRoads();
        Map<PlayerColor, Integer> startingResourceNodeIds = placements.getStartingResourceNodeIds();

        assertEquals(FOUR_PLAYER_PIECE_COUNT, settlements.size());
        assertEquals(RED, settlements.get(RED_SETTLEMENT_NODE_1));
        assertEquals(RED, settlements.get(RED_SETTLEMENT_NODE_2));
        assertEquals(ORANGE, settlements.get(ORANGE_SETTLEMENT_NODE_1));
        assertEquals(ORANGE, settlements.get(ORANGE_SETTLEMENT_NODE_2));
        assertEquals(BLUE, settlements.get(BLUE_SETTLEMENT_NODE_1));
        assertEquals(BLUE, settlements.get(BLUE_SETTLEMENT_NODE_2));
        assertEquals(WHITE, settlements.get(WHITE_SETTLEMENT_NODE_1));
        assertEquals(WHITE, settlements.get(WHITE_SETTLEMENT_NODE_2));

        assertEquals(FOUR_PLAYER_PIECE_COUNT, roads.size());
        assertEquals(RED, roads.get(RED_ROAD_EDGE_1));
        assertEquals(RED, roads.get(RED_ROAD_EDGE_2));
        assertEquals(ORANGE, roads.get(ORANGE_ROAD_EDGE_1));
        assertEquals(ORANGE, roads.get(ORANGE_ROAD_EDGE_2));
        assertEquals(BLUE, roads.get(BLUE_ROAD_EDGE_1));
        assertEquals(BLUE, roads.get(BLUE_ROAD_EDGE_2));
        assertEquals(WHITE, roads.get(WHITE_ROAD_EDGE_1));
        assertEquals(WHITE, roads.get(WHITE_ROAD_EDGE_2));

        assertEquals(FOUR_PLAYER_STAR_COUNT, startingResourceNodeIds.size());
        assertEquals(RED_STAR_NODE, startingResourceNodeIds.get(RED));
        assertEquals(ORANGE_STAR_NODE, startingResourceNodeIds.get(ORANGE));
        assertEquals(BLUE_STAR_NODE, startingResourceNodeIds.get(BLUE));
        assertEquals(WHITE_STAR_NODE, startingResourceNodeIds.get(WHITE));

        assertThrows(UnsupportedOperationException.class, () -> settlements.put(SAMPLE_NODE_ID, WHITE));
        assertThrows(UnsupportedOperationException.class, () -> roads.put(SAMPLE_EDGE_ID, WHITE));
        assertThrows(UnsupportedOperationException.class, () -> startingResourceNodeIds.put(WHITE, SAMPLE_NODE_ID));
    }

    @Test
    void GetPlacements_ForThreePlayers_MatchesNonRedEntriesOmitsRedAndIsUnmodifiable() {
        BeginnerPlacements placements = BeginnerPlacements.forThreePlayers();
        Map<Integer, PlayerColor> settlements = placements.getSettlements();
        Map<Integer, PlayerColor> roads = placements.getRoads();
        Map<PlayerColor, Integer> startingResourceNodeIds = placements.getStartingResourceNodeIds();

        assertEquals(THREE_PLAYER_PIECE_COUNT, settlements.size());
        assertFalse(settlements.containsValue(RED));
        assertEquals(ORANGE, settlements.get(ORANGE_SETTLEMENT_NODE_1));
        assertEquals(ORANGE, settlements.get(ORANGE_SETTLEMENT_NODE_2));
        assertEquals(BLUE, settlements.get(BLUE_SETTLEMENT_NODE_1));
        assertEquals(BLUE, settlements.get(BLUE_SETTLEMENT_NODE_2));
        assertEquals(WHITE, settlements.get(WHITE_SETTLEMENT_NODE_1));
        assertEquals(WHITE, settlements.get(WHITE_SETTLEMENT_NODE_2));

        assertEquals(THREE_PLAYER_PIECE_COUNT, roads.size());
        assertFalse(roads.containsValue(RED));
        assertEquals(ORANGE, roads.get(ORANGE_ROAD_EDGE_1));
        assertEquals(ORANGE, roads.get(ORANGE_ROAD_EDGE_2));
        assertEquals(BLUE, roads.get(BLUE_ROAD_EDGE_1));
        assertEquals(BLUE, roads.get(BLUE_ROAD_EDGE_2));
        assertEquals(WHITE, roads.get(WHITE_ROAD_EDGE_1));
        assertEquals(WHITE, roads.get(WHITE_ROAD_EDGE_2));

        assertEquals(THREE_PLAYER_STAR_COUNT, startingResourceNodeIds.size());
        assertFalse(startingResourceNodeIds.containsKey(RED));
        assertEquals(ORANGE_STAR_NODE, startingResourceNodeIds.get(ORANGE));
        assertEquals(BLUE_STAR_NODE, startingResourceNodeIds.get(BLUE));
        assertEquals(WHITE_STAR_NODE, startingResourceNodeIds.get(WHITE));

        assertThrows(UnsupportedOperationException.class, () -> settlements.put(SAMPLE_NODE_ID, WHITE));
        assertThrows(UnsupportedOperationException.class, () -> roads.put(SAMPLE_EDGE_ID, WHITE));
        assertThrows(UnsupportedOperationException.class, () -> startingResourceNodeIds.put(WHITE, SAMPLE_NODE_ID));
    }
}
