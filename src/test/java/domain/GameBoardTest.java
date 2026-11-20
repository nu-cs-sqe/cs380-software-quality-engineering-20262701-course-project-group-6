package domain;

import static domain.PlayerColor.ORANGE;
import static domain.PlayerColor.RED;
import static domain.ResourceCardType.ORE;
import static domain.ResourceCardType.WOOL;
import static domain.ResourceCardType.GRAIN;
import static domain.ResourceCardType.BRICK;
import static domain.ResourceCardType.LUMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.LinkedHashMap;
import org.junit.jupiter.api.Test;

class GameBoardTest {
    private static final int EXPECTED_TILES = 19;
    private static final int EXPECTED_NODES = 54;
    private static final int EXPECTED_EDGES = 72;
    private static final int EXPECTED_HARBOR_NODES = 18;
    private static final int BEGINNER_ROBBER_TILE = 9;
    private static final int TOP_LEFT_NODE = 0;
    private static final int TOP_LEFT_INCIDENT_EDGE_1 = 0;
    private static final int TOP_LEFT_INCIDENT_EDGE_2 = 6;
    private static final int GRAIN_HARBOR_NODE = 3;
    private static final int ORE_HARBOR_NODE = 14;
    private static final int LUMBER_HARBOR_NODE = 17;
    private static final int DESERT_ADJACENT_NODE = 33;
    private static final int WHITE_STAR_NODE = 19;
    private static final int THREE_FOR_ONE_SOUTH_NODE = 50;
    private static final int BOTTOM_RIGHT_NODE = 53;
    private static final int BOTTOM_RIGHT_INCIDENT_EDGE_1 = 65;
    private static final int BOTTOM_RIGHT_INCIDENT_EDGE_2 = 71;
    private static final int RED_ROAD_EDGE = 13;
    private static final int ORANGE_ROAD_EDGE = 15;
    private static final int MISSING_TILE_GAP_ID = 99;
    private static final int SAMPLE_DICE = 6;
    private static final int INVALID_LOW_ID = -1;
    private static final int INVALID_HIGH_NODE_ID = 54;

    @Test
    void GenerateBoard_ValidBeginnerLayoutAndPlacements_BuildsFullGraphAndHarbors() {
        GameBoard gameBoard = new GameBoard();
        BeginnerLayout layout = new BeginnerLayout();
        BeginnerPlacements placements = BeginnerPlacements.forFourPlayers();

        gameBoard.generateBoard(layout, placements);
        List<Tile> tiles = gameBoard.getTiles();
        List<Node> nodes = gameBoard.getNodes();
        List<Edge> edges = gameBoard.getEdges();

        assertEquals(EXPECTED_TILES, tiles.size());
        assertEquals(EXPECTED_NODES, nodes.size());
        assertEquals(EXPECTED_EDGES, edges.size());
        assertEquals(EXPECTED_HARBOR_NODES, gameBoard.getHarborNodes().size());
        assertEquals(BEGINNER_ROBBER_TILE, gameBoard.getRobberTileId());
        assertEquals(HarborType.GENERIC_THREE_FOR_ONE, gameBoard.getNodes().get(TOP_LEFT_NODE).getHarborType());
        assertEquals(HarborType.GRAIN_TWO_FOR_ONE, gameBoard.getNodes().get(GRAIN_HARBOR_NODE).getHarborType());
        assertEquals(HarborType.ORE_TWO_FOR_ONE, gameBoard.getNodes().get(ORE_HARBOR_NODE).getHarborType());
        assertEquals(HarborType.LUMBER_TWO_FOR_ONE, gameBoard.getNodes().get(LUMBER_HARBOR_NODE).getHarborType());
        assertEquals(HarborType.GENERIC_THREE_FOR_ONE,
                gameBoard.getNodes().get(THREE_FOR_ONE_SOUTH_NODE).getHarborType());
        assertEquals(HarborType.NONE, gameBoard.getNodes().get(DESERT_ADJACENT_NODE).getHarborType());
        assertTrue(gameBoard.getNodes().get(WHITE_STAR_NODE).isOccupied());
        assertEquals(TOP_LEFT_INCIDENT_EDGE_1, nodes.get(TOP_LEFT_NODE).getIncidentEdges().get(0).getId());
        assertEquals(TOP_LEFT_INCIDENT_EDGE_2, nodes.get(TOP_LEFT_NODE).getIncidentEdges().get(1).getId());
        assertEquals(BOTTOM_RIGHT_INCIDENT_EDGE_1, nodes.get(BOTTOM_RIGHT_NODE).getIncidentEdges().get(0).getId());
        assertEquals(BOTTOM_RIGHT_INCIDENT_EDGE_2, nodes.get(BOTTOM_RIGHT_NODE).getIncidentEdges().get(1).getId());
        assertEquals(Optional.of(RED), edges.get(RED_ROAD_EDGE).getRoadColor());
        assertEquals(Optional.of(ORANGE), edges.get(ORANGE_ROAD_EDGE).getRoadColor());
    }

    @Test
    void GenerateBoard_NullLayout_ThrowsIllegalArgumentException() {
        GameBoard gameBoard = new GameBoard();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> gameBoard.generateBoard(null, BeginnerPlacements.forFourPlayers()));

        assertEquals("layout cannot be null", exception.getMessage());
    }

    @Test
    void GenerateBoard_NullPlacements_ThrowsIllegalArgumentException() {
        GameBoard gameBoard = new GameBoard();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> gameBoard.generateBoard(new BeginnerLayout(), null));

        assertEquals("placements cannot be null", exception.getMessage());
    }

    @Test
    void GenerateBoard_NullTileMapInLayout_ThrowsIllegalArgumentException() {
        GameBoard gameBoard = new GameBoard();
        TileLayout nullMapLayout = () -> null;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> gameBoard.generateBoard(nullMapLayout, BeginnerPlacements.forFourPlayers()));

        assertEquals("layout tiles cannot be null", exception.getMessage());
    }

    @Test
    void GenerateBoard_TooFewTiles_ThrowsIllegalArgumentException() {
        GameBoard gameBoard = new GameBoard();
        TileLayout eighteenTiles = stubLayoutWithTileCount(EXPECTED_TILES - 1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> gameBoard.generateBoard(eighteenTiles, BeginnerPlacements.forFourPlayers()));

        assertEquals("Expected 19 tiles, got 18", exception.getMessage());
    }

    @Test
    void GenerateBoard_TooManyTiles_ThrowsIllegalArgumentException() {
        GameBoard gameBoard = new GameBoard();
        TileLayout twentyTiles = stubLayoutWithTileCount(EXPECTED_TILES + 1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> gameBoard.generateBoard(twentyTiles, BeginnerPlacements.forFourPlayers()));

        assertEquals("Expected 19 tiles, got 20", exception.getMessage());
    }

    @Test
    void GenerateBoard_MissingTileIdKey_ThrowsIllegalArgumentException() {
        GameBoard gameBoard = new GameBoard();
        Map<Integer, TileSetup> incompleteMap = new LinkedHashMap<>();
        for (int i = 0; i < EXPECTED_TILES - 1; i++) {
            incompleteMap.put(i, new TileSetup(ORE, SAMPLE_DICE));
        }
        incompleteMap.put(MISSING_TILE_GAP_ID, new TileSetup(ORE, SAMPLE_DICE));
        TileLayout gappedLayout = () -> incompleteMap;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> gameBoard.generateBoard(gappedLayout, BeginnerPlacements.forFourPlayers()));

        assertEquals("Missing tile setup for tileId " + (EXPECTED_TILES - 1), exception.getMessage());
    }

    @Test
    void GetAdjacentResources_CoastalBoundaryNodes_ReturnsSingleResource() {
        GameBoard gameBoard = createGeneratedBeginnerBoard();

        assertEquals(List.of(ORE), gameBoard.getAdjacentResources(TOP_LEFT_NODE));
        assertEquals(List.of(WOOL), gameBoard.getAdjacentResources(BOTTOM_RIGHT_NODE));
    }

    @Test
    void GetAdjacentResources_InteriorNode_ReturnsThreeResourcesInTileOrder() {
        GameBoard gameBoard = createGeneratedBeginnerBoard();

        assertEquals(List.of(GRAIN, BRICK, LUMBER), gameBoard.getAdjacentResources(WHITE_STAR_NODE));
    }

    @Test
    void GetAdjacentResources_NodeBorderingDesert_ExcludesDesert() {
        GameBoard gameBoard = createGeneratedBeginnerBoard();

        assertEquals(List.of(LUMBER, GRAIN), gameBoard.getAdjacentResources(DESERT_ADJACENT_NODE));
    }

    @Test
    void GetAdjacentResources_BeforeGenerateBoard_ThrowsIllegalStateException() {
        GameBoard gameBoard = new GameBoard();

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> gameBoard.getAdjacentResources(TOP_LEFT_NODE));

        assertEquals("Board has not been generated yet", exception.getMessage());
    }

    @Test
    void GetAdjacentResources_NegativeNodeId_ThrowsIllegalArgumentException() {
        GameBoard gameBoard = createGeneratedBeginnerBoard();

        assertThrows(IllegalArgumentException.class, () -> gameBoard.getAdjacentResources(INVALID_LOW_ID));
    }

    @Test
    void GetAdjacentResources_NodeIdAboveMax_ThrowsIllegalArgumentException() {
        GameBoard gameBoard = createGeneratedBeginnerBoard();

        assertThrows(IllegalArgumentException.class, () -> gameBoard.getAdjacentResources(INVALID_HIGH_NODE_ID));
    }

    private TileLayout stubLayoutWithTileCount(int count) {
        Map<Integer, TileSetup> map = new LinkedHashMap<>();
        for (int i = 0; i < count; i++) {
            map.put(i, new TileSetup(ORE, SAMPLE_DICE));
        }
        return () -> map;
    }

    private GameBoard createGeneratedBeginnerBoard() {
        GameBoard gameBoard = new GameBoard();
        gameBoard.generateBoard(new BeginnerLayout(), BeginnerPlacements.forFourPlayers());
        return gameBoard;
    }
}