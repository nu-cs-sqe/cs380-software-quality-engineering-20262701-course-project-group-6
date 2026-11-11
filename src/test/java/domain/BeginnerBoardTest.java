package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
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

    private static String describeTile(BeginnerBoard.TileSetup tile) {
        return tile.getResource().map(resource -> resource.name() + " " + tile.getDiceNumber().orElseThrow())
                .orElse("DESERT");
    }
}
