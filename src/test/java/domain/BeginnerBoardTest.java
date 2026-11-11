package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class BeginnerBoardTest {
    @Test
    public void Initialize_BeginnerBoard_MatchesRulebookTerrain() {
        BeginnerBoard board = new BeginnerBoard();

        List<List<String>> rows = board.getTileRows().stream()
                .map(row -> row.stream().map(BeginnerBoardTest::describeTile).collect(Collectors.toList()))
                .collect(Collectors.toList());

        assertEquals(List.of(
                List.of("ORE 10", "WOOL 2", "LUMBER 9"),
                List.of("GRAIN 12", "BRICK 6", "WOOL 4", "BRICK 10"),
                List.of("GRAIN 9", "LUMBER 11", "DESERT", "LUMBER 3", "ORE 8"),
                List.of("LUMBER 8", "ORE 3", "GRAIN 4", "WOOL 5"),
                List.of("BRICK 5", "GRAIN 6", "WOOL 11")), rows);
        assertEquals(9, board.getRobberTileId());
        assertFalse(board.getTileRows().get(2).get(2).getDiceNumber().isPresent());
    }

    @Test
    public void Initialize_BeginnerBoard_MatchesRulebookPlacements() {
        BeginnerBoard board = new BeginnerBoard();

        assertEquals(Map.of(8, PlayerColor.RED, 28, PlayerColor.RED, 14, PlayerColor.ORANGE,
                40, PlayerColor.ORANGE, 39, PlayerColor.BLUE, 41, PlayerColor.BLUE,
                17, PlayerColor.WHITE, 31, PlayerColor.WHITE), board.getSettlements());
        assertEquals(Map.of(13, PlayerColor.RED, 41, PlayerColor.RED, 15, PlayerColor.ORANGE,
                58, PlayerColor.ORANGE, 56, PlayerColor.BLUE, 52, PlayerColor.BLUE,
                25, PlayerColor.WHITE, 37, PlayerColor.WHITE), board.getRoads());
        assertEquals(Set.of(17, 28, 39, 40), board.getStartingResourceNodes());
        assertEquals(Map.of(0, HarborType.GENERIC_THREE_FOR_ONE, 3, HarborType.GRAIN_TWO_FOR_ONE,
                17, HarborType.ORE_TWO_FOR_ONE, 18, HarborType.LUMBER_TWO_FOR_ONE,
                38, HarborType.GENERIC_THREE_FOR_ONE, 49, HarborType.BRICK_TWO_FOR_ONE,
                61, HarborType.WOOL_TWO_FOR_ONE, 66, HarborType.GENERIC_THREE_FOR_ONE,
                69, HarborType.GENERIC_THREE_FOR_ONE), board.getHarbors());
    }

    private static String describeTile(BeginnerBoard.TileSetup tile) {
        return tile.getResource().map(resource -> resource.name() + " " + tile.getDiceNumber().orElseThrow())
                .orElse("DESERT");
    }
}
