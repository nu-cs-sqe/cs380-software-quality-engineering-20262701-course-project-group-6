package domain;

import static domain.ResourceCardType.BRICK;
import static domain.ResourceCardType.GRAIN;
import static domain.ResourceCardType.LUMBER;
import static domain.ResourceCardType.ORE;
import static domain.ResourceCardType.WOOL;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public final class BeginnerBoard {
    private static final int ROBBER_TILE_ID = 9;
    // Illustration A, read from top to bottom and left to right within each row.
    private static final List<List<TileSetup>> TILE_ROWS = List.of(
            List.of(new TileSetup(ORE, 10), new TileSetup(WOOL, 2), new TileSetup(LUMBER, 9)),
            List.of(new TileSetup(GRAIN, 12), new TileSetup(BRICK, 6),
                    new TileSetup(WOOL, 4), new TileSetup(BRICK, 10)),
            List.of(new TileSetup(GRAIN, 9), new TileSetup(LUMBER, 11), new TileSetup(),
                    new TileSetup(LUMBER, 3), new TileSetup(ORE, 8)),
            List.of(new TileSetup(LUMBER, 8), new TileSetup(ORE, 3),
                    new TileSetup(GRAIN, 4), new TileSetup(WOOL, 5)),
            List.of(new TileSetup(BRICK, 5), new TileSetup(GRAIN, 6), new TileSetup(WOOL, 11)));

    public List<List<TileSetup>> getTileRows() {
        return List.copyOf(TILE_ROWS);
    }

    public int getRobberTileId() {
        return ROBBER_TILE_ID;
    }

    public static final class TileSetup {
        private final Optional<ResourceCardType> resource;
        private final OptionalInt diceNumber;

        private TileSetup(ResourceCardType resource, int diceNumber) {
            this.resource = Optional.of(resource);
            this.diceNumber = OptionalInt.of(diceNumber);
        }

        private TileSetup() {
            resource = Optional.empty();
            diceNumber = OptionalInt.empty();
        }

        public Optional<ResourceCardType> getResource() {
            return resource;
        }

        public OptionalInt getDiceNumber() {
            return diceNumber;
        }
    }
}
