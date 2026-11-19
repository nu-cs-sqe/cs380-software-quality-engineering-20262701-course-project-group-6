package domain;

import static domain.PlayerColor.BLUE;
import static domain.PlayerColor.ORANGE;
import static domain.PlayerColor.RED;
import static domain.PlayerColor.WHITE;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class BeginnerPlacementsTest {
    private static final int FOUR_PLAYER_PIECE_COUNT = 8;
    private static final int FOUR_PLAYER_STAR_COUNT = 4;
    private static final int RED_STAR_NODE = 29;
    private static final int ORANGE_STAR_NODE = 42;
    private static final int BLUE_STAR_NODE = 40;
    private static final int WHITE_STAR_NODE = 19;

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
}
