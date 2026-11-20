package domain;

import java.util.*;

public class GameBoard {
    private static final int NUM_NODES = 54;
    private static final int NUM_EDGES = 72;
    private static final int NUM_TILES = 19;
    private static final int MAX_NODE_ID = 53;
    private static final int UNSET_DESERT_ID = -1;
    private static final int CORNER_0 = 0;
    private static final int CORNER_1 = 1;
    private static final int CORNER_2 = 2;
    private static final int CORNER_3 = 3;
    private static final int CORNER_4 = 4;
    private static final int CORNER_5 = 5;

    private static final Map<Integer, HarborType> HARBOR_NODES = Map.ofEntries(
            Map.entry(0, HarborType.GENERIC_THREE_FOR_ONE),
            Map.entry(1, HarborType.GENERIC_THREE_FOR_ONE),
            Map.entry(3, HarborType.GRAIN_TWO_FOR_ONE),
            Map.entry(4, HarborType.GRAIN_TWO_FOR_ONE),
            Map.entry(14, HarborType.ORE_TWO_FOR_ONE),
            Map.entry(15, HarborType.ORE_TWO_FOR_ONE),
            Map.entry(26, HarborType.GENERIC_THREE_FOR_ONE),
            Map.entry(37, HarborType.GENERIC_THREE_FOR_ONE),
            Map.entry(45, HarborType.WOOL_TWO_FOR_ONE),
            Map.entry(46, HarborType.WOOL_TWO_FOR_ONE),
            Map.entry(50, HarborType.GENERIC_THREE_FOR_ONE),
            Map.entry(51, HarborType.GENERIC_THREE_FOR_ONE),
            Map.entry(47, HarborType.GENERIC_THREE_FOR_ONE),
            Map.entry(48, HarborType.GENERIC_THREE_FOR_ONE),
            Map.entry(28, HarborType.BRICK_TWO_FOR_ONE),
            Map.entry(38, HarborType.BRICK_TWO_FOR_ONE),
            Map.entry(7, HarborType.LUMBER_TWO_FOR_ONE),
            Map.entry(17, HarborType.LUMBER_TWO_FOR_ONE));

    private static final int[][] EDGE_ENDPOINTS = {
            {0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}, {5, 6},
            {0, 8}, {2, 10}, {4, 12}, {6, 14},
            {7, 8}, {8, 9}, {9, 10}, {10, 11}, {11, 12}, {12, 13}, {13, 14}, {14, 15},
            {7, 17}, {9, 19}, {11, 21}, {13, 23}, {15, 25},
            {16, 17}, {17, 18}, {18, 19}, {19, 20}, {20, 21}, {21, 22}, {22, 23}, {23, 24}, {24, 25}, {25, 26},
            {16, 27}, {18, 29}, {20, 31}, {22, 33}, {24, 35}, {26, 37},
            {27, 28}, {28, 29}, {29, 30}, {30, 31}, {31, 32}, {32, 33}, {33, 34}, {34, 35}, {35, 36}, {36, 37},
            {28, 38}, {30, 40}, {32, 42}, {34, 44}, {36, 46},
            {38, 39}, {39, 40}, {40, 41}, {41, 42}, {42, 43}, {43, 44}, {44, 45}, {45, 46},
            {39, 47}, {41, 49}, {43, 51}, {45, 53},
            {47, 48}, {48, 49}, {49, 50}, {50, 51}, {51, 52}, {52, 53}
    };

    private static final int[][] TILE_CORNERS = {
            {0, 1, 2, 10, 9, 8}, {2, 3, 4, 12, 11, 10}, {4, 5, 6, 14, 13, 12},
            {7, 8, 9, 19, 18, 17}, {9, 10, 11, 21, 20, 19}, {11, 12, 13, 23, 22, 21}, {13, 14, 15, 25, 24, 23},
            {16, 17, 18, 29, 28, 27}, {18, 19, 20, 31, 30, 29}, {20, 21, 22, 33, 32, 31}, {22, 23, 24, 35, 34, 33},
            {24, 25, 26, 37, 36, 35},
            {28, 29, 30, 40, 39, 38}, {30, 31, 32, 42, 41, 40}, {32, 33, 34, 44, 43, 42}, {34, 35, 36, 46, 45, 44},
            {39, 40, 41, 49, 48, 47}, {41, 42, 43, 51, 50, 49}, {43, 44, 45, 53, 52, 51}
    };

    private final Map<Integer, Node> nodes = new LinkedHashMap<>();
    private final Map<Integer, Edge> edges = new LinkedHashMap<>();
    private final Map<Integer, Tile> tiles = new LinkedHashMap<>();
    private final Map<PlayerColor, Integer> startingResourceNodes = new LinkedHashMap<>();
    private int robberTileId;
    private boolean generated;

    public void generateBoard(TileLayout layout, BeginnerPlacements placements) {
        validateGenerateInputs(layout, placements);
        createNodes();
        createEdges();
        createTiles(layout.getTiles());
        applyBeginnerPlacements(placements);
        generated = true;
    }

    public List<ResourceCardType> getAdjacentResources(int nodeId) {
        requireGenerated();
        Node targetNode = requireValidNode(nodeId);
        List<ResourceCardType> adjacentResources = new ArrayList<>();
        for (int tileId = 0; tileId < NUM_TILES; tileId++) {
            addTileResourceIfAdjacent(tiles.get(tileId), targetNode, adjacentResources);
        }
        return Collections.unmodifiableList(adjacentResources);
    }

    public List<ResourceCardType> getStartingResources(PlayerColor color) {
        requireGenerated();
        if (color == null) {
            throw new IllegalArgumentException("Player color is required");
        }
        Integer secondPlacedSettlement = startingResourceNodes.get(color);
        if (secondPlacedSettlement == null) {
            throw new IllegalArgumentException(color + " has no 2nd starting settlement on this board");
        }
        return getAdjacentResources(secondPlacedSettlement);
    }

    public List<Tile> getTiles() {
        return List.copyOf(tiles.values());
    }

    public List<Node> getNodes() {
        return List.copyOf(nodes.values());
    }

    public List<Edge> getEdges() {
        return List.copyOf(edges.values());
    }

    public int getRobberTileId() {
        return robberTileId;
    }

    public Map<Integer, HarborType> getHarborNodes() {
        return Map.copyOf(HARBOR_NODES);
    }

    private void validateGenerateInputs(TileLayout layout, BeginnerPlacements placements) {
        if (layout == null) {
            throw new IllegalArgumentException("layout cannot be null");
        }
        if (placements == null) {
            throw new IllegalArgumentException("placements cannot be null");
        }
    }

    private void createNodes() {
        for (int nodeId = 0; nodeId < NUM_NODES; nodeId++) {
            HarborType harbor = HARBOR_NODES.getOrDefault(nodeId, HarborType.NONE);
            nodes.put(nodeId, new Node(nodeId, harbor));
        }
    }

    private void createEdges() {
        for (int edgeId = 0; edgeId < NUM_EDGES; edgeId++) {
            Node node1 = nodes.get(EDGE_ENDPOINTS[edgeId][0]);
            Node node2 = nodes.get(EDGE_ENDPOINTS[edgeId][1]);
            Edge edge = new Edge(edgeId, node1, node2);
            node1.addIncidentEdge(edge);
            node2.addIncidentEdge(edge);
            edges.put(edgeId, edge);
        }
    }

    private void createTiles(Map<Integer, TileSetup> tileMap) {
        if (tileMap == null) {
            throw new IllegalArgumentException("layout tiles cannot be null");
        }
        if (tileMap.size() != NUM_TILES) {
            throw new IllegalArgumentException("Expected " + NUM_TILES + " tiles, got " + tileMap.size());
        }
        int desertTileId = UNSET_DESERT_ID;
        for (int tileId = 0; tileId < NUM_TILES; tileId++) {
            TileSetup setup = requireTileSetup(tileMap, tileId);
            tiles.put(tileId, buildTile(tileId, setup));
            if (setup.getResource().isEmpty()) {
                desertTileId = tileId;
            }
        }
        robberTileId = desertTileId == UNSET_DESERT_ID ? 0 : desertTileId;
    }

    private TileSetup requireTileSetup(Map<Integer, TileSetup> tileMap, int tileId) {
        TileSetup setup = tileMap.get(tileId);
        if (setup == null) {
            throw new IllegalArgumentException("Missing tile setup for tileId " + tileId);
        }
        return setup;
    }

    private Tile buildTile(int tileId, TileSetup setup) {
        List<Node> corners = List.of(
                nodes.get(TILE_CORNERS[tileId][CORNER_0]), nodes.get(TILE_CORNERS[tileId][CORNER_1]),
                nodes.get(TILE_CORNERS[tileId][CORNER_2]), nodes.get(TILE_CORNERS[tileId][CORNER_3]),
                nodes.get(TILE_CORNERS[tileId][CORNER_4]), nodes.get(TILE_CORNERS[tileId][CORNER_5]));
        Optional<ResourceCardType> resource = setup.getResource();
        if (resource.isPresent()) {
            return new Tile(tileId, resource.get(), setup.getDiceNumber().getAsInt(), corners);
        }
        return new Tile(tileId, corners);
    }

    private void applyBeginnerPlacements(BeginnerPlacements placements) {
        placements.getSettlements().forEach((nodeId, color) ->
                nodes.get(nodeId).placeBuilding(BuildingType.SETTLEMENT, color));
        placements.getRoads().forEach((edgeId, color) ->
                edges.get(edgeId).setRoadColor(color));
        placements.getStartingResourceNodeIds().forEach((color, starredNodeId) -> {
            requireValidNode(starredNodeId);
            startingResourceNodes.put(color, starredNodeId);
        });
    }

    private void addTileResourceIfAdjacent(Tile tile, Node targetNode, List<ResourceCardType> resources) {
        if (tile.getCornerNodes().contains(targetNode)) {
            tile.getResourceProduced().ifPresent(resources::add);
        }
    }

    private void requireGenerated() {
        if (!generated) {
            throw new IllegalStateException("Board has not been generated yet");
        }
    }

    private Node requireValidNode(int nodeId) {
        Node node = nodes.get(nodeId);
        if (node == null) {
            throw new IllegalArgumentException("nodeId must be between 0 and " + MAX_NODE_ID + " (was " + nodeId + ")");
        }
        return node;
    }
}