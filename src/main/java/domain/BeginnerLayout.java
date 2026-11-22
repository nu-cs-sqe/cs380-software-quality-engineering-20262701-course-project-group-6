package domain;

import java.util.Map;

import static domain.ResourceCardType.BRICK;
import static domain.ResourceCardType.GRAIN;
import static domain.ResourceCardType.LUMBER;
import static domain.ResourceCardType.ORE;
import static domain.ResourceCardType.WOOL;

public class BeginnerLayout implements TileLayout {
    private static final Map<Integer, TileSetup> TILES = Map.ofEntries(
            Map.entry(0, new TileSetup(ORE, 10)),
            Map.entry(1, new TileSetup(WOOL, 2)),
            Map.entry(2, new TileSetup(LUMBER, 9)),
            Map.entry(3, new TileSetup(GRAIN, 12)),
            Map.entry(4, new TileSetup(BRICK, 6)),
            Map.entry(5, new TileSetup(WOOL, 4)),
            Map.entry(6, new TileSetup(BRICK, 10)),
            Map.entry(7, new TileSetup(GRAIN, 9)),
            Map.entry(8, new TileSetup(LUMBER, 11)),
            Map.entry(9, new TileSetup()),
            Map.entry(10, new TileSetup(LUMBER, 3)),
            Map.entry(11, new TileSetup(ORE, 8)),
            Map.entry(12, new TileSetup(LUMBER, 8)),
            Map.entry(13, new TileSetup(ORE, 3)),
            Map.entry(14, new TileSetup(GRAIN, 4)),
            Map.entry(15, new TileSetup(WOOL, 5)),
            Map.entry(16, new TileSetup(BRICK, 5)),
            Map.entry(17, new TileSetup(GRAIN, 6)),
            Map.entry(18, new TileSetup(WOOL, 11))
    );

    private final Map<Integer, TileSetup> tiles;

    public BeginnerLayout() {
        tiles = TILES;
    }

    public Map<Integer, TileSetup> getTiles() {
        return Map.copyOf(tiles);
    }
}
