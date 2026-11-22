package domain;

import static domain.PlayerColor.ORANGE;
import static domain.PlayerColor.RED;
import static domain.PlayerColor.WHITE;
import static domain.PlayerColor.BLUE;
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
    private static final int EXPECTED_CORNERS_PER_TILE = 6;
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
    private static final int MAX_ADJACENT_TILES_PER_NODE = 3;

    @Test
    void GenerateBoard_ValidBeginnerLayoutAndPlacements_BuildsFullGraphAndHarbors() {
        BeginnerLayout layout = new BeginnerLayout();
        BeginnerPlacements placements = BeginnerPlacements.forFourPlayers();

        GameBoard gameBoard = new GameBoard(layout, placements);
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
    void GenerateBoard_ValidBeginnerLayout_AllEdgesTilesAndHarborsHaveConsistentTopology() {
        GameBoard gameBoard = new GameBoard(new BeginnerLayout(), BeginnerPlacements.forFourPlayers());
        List<Tile> tiles = gameBoard.getTiles();
        List<Edge> edges = gameBoard.getEdges();
        List<Node> nodes = gameBoard.getNodes();
        Map<Integer, HarborType> harborNodes = gameBoard.getHarborNodes();

        assertEquals(EXPECTED_EDGES, edges.size());
        for (Edge edge : edges) {
            Node first = edge.getNode1();
            Node second = edge.getNode2();
            boolean sharesTile = tiles.stream()
                    .anyMatch(tile -> tile.getCornerNodes().contains(first)
                            && tile.getCornerNodes().contains(second));
            assertTrue(sharesTile,
                    "Edge " + edge.getId() + " endpoints " + first.getId() + ", " + second.getId()
                            + " do not share a tile");
        }

        for (Node node : nodes) {
            long adjacentTileCount = tiles.stream().filter(tile -> tile.getCornerNodes().contains(node)).count();
            assertTrue(adjacentTileCount >= 1 && adjacentTileCount <= MAX_ADJACENT_TILES_PER_NODE);
            HarborType expectedHarbor = harborNodes.getOrDefault(node.getId(), HarborType.NONE);
            assertEquals(expectedHarbor, node.getHarborType());
            if (expectedHarbor != HarborType.NONE) {
                assertTrue(adjacentTileCount < MAX_ADJACENT_TILES_PER_NODE,
                        "Harbor node " + node.getId() + " should be on perimeter, not 3 tiles");
            }
        }
    }

    @Test
    void GenerateBoard_NullLayout_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new GameBoard(null, BeginnerPlacements.forFourPlayers()));

        assertEquals("layout cannot be null", exception.getMessage());
    }

    @Test
    void GenerateBoard_NullPlacements_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new GameBoard(new BeginnerLayout(), null));

        assertEquals("placements cannot be null", exception.getMessage());
    }

    @Test
    void GenerateBoard_NullTileMapInLayout_ThrowsIllegalArgumentException() {
        TileLayout nullMapLayout = () -> null;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new GameBoard(nullMapLayout, BeginnerPlacements.forFourPlayers()));

        assertEquals("layout tiles cannot be null", exception.getMessage());
    }

    @Test
    void GenerateBoard_TooFewTiles_ThrowsIllegalArgumentException() {
        TileLayout eighteenTiles = stubLayoutWithTileCount(EXPECTED_TILES - 1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new GameBoard(eighteenTiles, BeginnerPlacements.forFourPlayers()));

        assertEquals("Expected 19 tiles, got 18", exception.getMessage());
    }

    @Test
    void GenerateBoard_TooManyTiles_ThrowsIllegalArgumentException() {
        TileLayout twentyTiles = stubLayoutWithTileCount(EXPECTED_TILES + 1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new GameBoard(twentyTiles, BeginnerPlacements.forFourPlayers()));

        assertEquals("Expected 19 tiles, got 20", exception.getMessage());
    }

    @Test
    void GenerateBoard_MissingTileIdKey_ThrowsIllegalArgumentException() {
        Map<Integer, TileSetup> incompleteMap = new LinkedHashMap<>();
        for (int i = 0; i < EXPECTED_TILES - 1; i++) {
            incompleteMap.put(i, new TileSetup(ORE, SAMPLE_DICE));
        }
        incompleteMap.put(MISSING_TILE_GAP_ID, new TileSetup(ORE, SAMPLE_DICE));
        TileLayout gappedLayout = () -> incompleteMap;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new GameBoard(gappedLayout, BeginnerPlacements.forFourPlayers()));

        assertEquals("Missing tile setup for tileId " + (EXPECTED_TILES - 1), exception.getMessage());
    }

    @Test
    void GetAdjacentResources_CoastalBoundaryNodes_ReturnsSingleResource() {
        GameBoard gameBoard = new GameBoard(new BeginnerLayout(), BeginnerPlacements.forFourPlayers());

        assertEquals(List.of(ORE), gameBoard.getAdjacentResources(TOP_LEFT_NODE));
        assertEquals(List.of(WOOL), gameBoard.getAdjacentResources(BOTTOM_RIGHT_NODE));
    }

    @Test
    void GetAdjacentResources_InteriorNode_ReturnsThreeResourcesInTileOrder() {
        GameBoard gameBoard = new GameBoard(new BeginnerLayout(), BeginnerPlacements.forFourPlayers());;

        assertEquals(List.of(GRAIN, BRICK, LUMBER), gameBoard.getAdjacentResources(WHITE_STAR_NODE));
    }

    @Test
    void GetAdjacentResources_NodeBorderingDesert_ExcludesDesert() {
        GameBoard gameBoard = new GameBoard(new BeginnerLayout(), BeginnerPlacements.forFourPlayers());

        assertEquals(List.of(LUMBER, GRAIN), gameBoard.getAdjacentResources(DESERT_ADJACENT_NODE));
    }

    @Test
    void GetAdjacentResources_NegativeNodeId_ThrowsIllegalArgumentException() {
        GameBoard gameBoard = new GameBoard(new BeginnerLayout(), BeginnerPlacements.forFourPlayers());

        assertThrows(IllegalArgumentException.class, () -> gameBoard.getAdjacentResources(INVALID_LOW_ID));
    }

    @Test
    void GetAdjacentResources_NodeIdAboveMax_ThrowsIllegalArgumentException() {
        GameBoard gameBoard = new GameBoard(new BeginnerLayout(), BeginnerPlacements.forFourPlayers());

        assertThrows(IllegalArgumentException.class, () -> gameBoard.getAdjacentResources(INVALID_HIGH_NODE_ID));
    }

    @Test
    void GetStartingResources_White_ReturnsMatchingIllustrationAResources() {
        GameBoard gameBoard = new GameBoard(new BeginnerLayout(), BeginnerPlacements.forFourPlayers());

        assertEquals(List.of(GRAIN, BRICK, LUMBER), gameBoard.getStartingResources(WHITE));
    }

    @Test
    void GetStartingResources_Red_ReturnsMatchingIllustrationAResources() {
        GameBoard gameBoard = new GameBoard(new BeginnerLayout(), BeginnerPlacements.forFourPlayers());

        assertEquals(List.of(GRAIN, LUMBER, LUMBER), gameBoard.getStartingResources(RED));
    }

    @Test
    void GetStartingResources_Blue_ReturnsMatchingIllustrationAResources() {
        GameBoard gameBoard = new GameBoard(new BeginnerLayout(), BeginnerPlacements.forFourPlayers());

        assertEquals(List.of(LUMBER, ORE, BRICK), gameBoard.getStartingResources(BLUE));
    }

    @Test
    void GetStartingResources_Orange_ReturnsMatchingIllustrationAResources() {
        GameBoard gameBoard = new GameBoard(new BeginnerLayout(), BeginnerPlacements.forFourPlayers());

        assertEquals(List.of(ORE, GRAIN, GRAIN), gameBoard.getStartingResources(ORANGE));
    }

    @Test
    void GetStartingResources_NullColor_ThrowsIllegalArgumentException() {
        GameBoard gameBoard = new GameBoard(new BeginnerLayout(), BeginnerPlacements.forFourPlayers());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> gameBoard.getStartingResources(null));

        assertEquals("Player color is required", exception.getMessage());
    }

    @Test
    void GetStartingResources_OmittedColorInThreePlayerGame_ThrowsIllegalArgumentException() {
        GameBoard gameBoard = new GameBoard(new BeginnerLayout(), BeginnerPlacements.forThreePlayers());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> gameBoard.getStartingResources(RED));

        assertEquals("RED has no 2nd starting settlement on this board", exception.getMessage());
    }

    private TileLayout stubLayoutWithTileCount(int count) {
        Map<Integer, TileSetup> map = new LinkedHashMap<>();
        for (int i = 0; i < count; i++) {
            map.put(i, new TileSetup(ORE, SAMPLE_DICE));
        }
        return () -> map;
    }
}