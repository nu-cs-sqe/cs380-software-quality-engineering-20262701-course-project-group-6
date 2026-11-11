package domain;

import static domain.HarborType.BRICK_TWO_FOR_ONE;
import static domain.HarborType.GENERIC_THREE_FOR_ONE;
import static domain.HarborType.GRAIN_TWO_FOR_ONE;
import static domain.HarborType.LUMBER_TWO_FOR_ONE;
import static domain.HarborType.ORE_TWO_FOR_ONE;
import static domain.HarborType.WOOL_TWO_FOR_ONE;
import static domain.PlayerColor.BLUE;
import static domain.PlayerColor.ORANGE;
import static domain.PlayerColor.RED;
import static domain.PlayerColor.WHITE;
import static domain.ResourceCardType.BRICK;
import static domain.ResourceCardType.GRAIN;
import static domain.ResourceCardType.LUMBER;
import static domain.ResourceCardType.ORE;
import static domain.ResourceCardType.WOOL;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;

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
    // Corner and edge IDs are ordered by height, then left to right.
    private static final Map<Integer, PlayerColor> SETTLEMENTS = Map.of(
            8, RED, 28, RED, 14, ORANGE, 40, ORANGE, 39, BLUE, 41, BLUE, 17, WHITE, 31, WHITE);
    private static final Map<Integer, PlayerColor> ROADS = Map.of(
            13, RED, 41, RED, 15, ORANGE, 58, ORANGE, 56, BLUE, 52, BLUE, 25, WHITE, 37, WHITE);
    private static final Set<Integer> STARTING_RESOURCE_NODES = Set.of(17, 28, 39, 40);
    private static final Map<Integer, HarborType> HARBORS = Map.of(
            0, GENERIC_THREE_FOR_ONE, 3, GRAIN_TWO_FOR_ONE, 17, ORE_TWO_FOR_ONE, 18, LUMBER_TWO_FOR_ONE,
            38, GENERIC_THREE_FOR_ONE, 49, BRICK_TWO_FOR_ONE, 61, WOOL_TWO_FOR_ONE,
            66, GENERIC_THREE_FOR_ONE, 69, GENERIC_THREE_FOR_ONE);

    private final Map<Integer, PlayerColor> settlements;
    private final Map<Integer, PlayerColor> roads;

    public BeginnerBoard() {
        this(SETTLEMENTS, ROADS);
    }

    private BeginnerBoard(Map<Integer, PlayerColor> settlements, Map<Integer, PlayerColor> roads) {
        this.settlements = Map.copyOf(settlements);
        this.roads = Map.copyOf(roads);
    }

    public static BeginnerBoard createForThreePlayers() {
        return new BeginnerBoard(omitRed(SETTLEMENTS), omitRed(ROADS));
    }

    public List<List<TileSetup>> getTileRows() {
        return List.copyOf(TILE_ROWS);
    }

    public int getRobberTileId() {
        return ROBBER_TILE_ID;
    }

    public Map<Integer, PlayerColor> getSettlements() {
        return Map.copyOf(settlements);
    }

    public Map<Integer, PlayerColor> getRoads() {
        return Map.copyOf(roads);
    }

    public Set<Integer> getStartingResourceNodes() {
        return STARTING_RESOURCE_NODES.stream()
                .filter(settlements::containsKey)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Map<Integer, HarborType> getHarbors() {
        return Map.copyOf(HARBORS);
    }

    private static Map<Integer, PlayerColor> omitRed(Map<Integer, PlayerColor> placements) {
        return placements.entrySet().stream().filter(placement -> placement.getValue() != RED)
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
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
