package ui;

import domain.BuildingType;
import domain.Edge;
import domain.GameBoard;
import domain.HarborType;
import domain.Node;
import domain.PlayerColor;
import domain.ResourceCardType;
import domain.Tile;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.IntConsumer;

final class BoardMap extends StackPane {
    private static final double MAP_PADDING = 22;
    private static final double TERRAIN_LABEL_OFFSET = 24;
    private static final double TILE_SYMBOL_OFFSET = -9;
    private static final double TOKEN_RADIUS = 19;
    private static final double TOKEN_NUMBER_OFFSET = -3;
    private static final double TOKEN_PIPS_OFFSET = 9;
    private static final int MOST_LIKELY_ROLL = 7;
    private static final int TOKEN_PIP_BASE = 6;
    private static final int MAX_TOKEN_PIPS = 5;
    private static final double ROAD_WIDTH = 8;
    private static final double ROAD_OUTLINE_WIDTH = 11;
    private static final double PIECE_OUTLINE_WIDTH = 2;
    private static final double TILE_OUTLINE_WIDTH = 2;
    private static final double RESOURCE_STAR_OFFSET = -19;
    private static final double HARBOR_DISTANCE = 37;
    private static final double HARBOR_CONNECTOR_WIDTH = 2;
    private static final double HARBOR_LABEL_HEIGHT = 23;
    private static final double HARBOR_LABEL_PADDING = 8;
    private static final double HARBOR_LABEL_RADIUS = 10;
    private static final double ROBBER_HEAD_RADIUS = 8;
    private static final double ROBBER_HEAD_OFFSET = -12;
    private static final double[] SETTLEMENT_POINTS = {-10, 8, -10, -2, 0, -11, 10, -2, 10, 8};
    private static final double[] CITY_POINTS = {-14, 10, -14, -4, -6, -12, 2, -4, 2, -6, 14, -6, 14, 10};
    private static final double SPOT_RADIUS = 11;
    private static final double SPOT_ROAD_WIDTH = 12;
    private static final Color SPOT = Color.web("#ffe066", 0.85);
    private static final double[] ROBBER_POINTS = {-11, 12, -8, -5, -4, -10, 4, -10, 8, -5, 11, 12};
    private static final Color SAND = Color.web("#ecd7a6");
    private static final Color TOKEN_FILL = Color.web("#ffedbe");
    private static final Color INK = Color.web("#352820");
    private static final Color DESERT = Color.web("#a47b44");
    private static final Color ROBBER = Color.web("#353c46");
    private static final Color HIGH_ODDS = Color.web("#9a2425");
    private static final Font TERRAIN_FONT = Font.font("System", FontWeight.BOLD, 14);
    private static final Font TOKEN_FONT = Font.font("System", FontWeight.BOLD, 19);
    private static final Font PIPS_FONT = Font.font(11);
    private static final Font HARBOR_FONT = Font.font("System", FontWeight.BOLD, 13);
    private static final Font STAR_FONT = Font.font(17);
    private static final Map<ResourceCardType, Color> TERRAIN_COLORS = Map.of(
            ResourceCardType.LUMBER, Color.web("#315b43"), ResourceCardType.WOOL, Color.web("#617539"),
            ResourceCardType.GRAIN, Color.web("#936421"), ResourceCardType.BRICK, Color.web("#8c4232"),
            ResourceCardType.ORE, Color.web("#515b70"));

    private final GameBoard board;
    private final ResourceBundle messages;
    private BuildMode spotMode;
    private PlayerColor spotColor;
    private IntConsumer pickSpot;

    BoardMap(GameBoard board, ResourceBundle messages) {
        this.board = board;
        this.messages = messages;
        setMinSize(0, 0);
        setPadding(new Insets(MAP_PADDING));
        setStyle("-fx-background-color: linear-gradient(to bottom right, #39788e, #183f58); "
                + "-fx-background-radius: 16;");
        redraw();
    }

    boolean showSpots(BuildMode mode, PlayerColor color, IntConsumer onPick) {
        if (spotsFor(mode, color).isEmpty()) {
            clearSpots();
            return false;
        }
        spotMode = mode;
        spotColor = color;
        pickSpot = onPick;
        redraw();
        return true;
    }

    void clearSpots() {
        spotMode = null;
        redraw();
    }

    void redraw() {
        Group map = createMap(board);
        getChildren().setAll(map);
        Bounds bounds = map.getLayoutBounds();
        NumberBinding widthScale = widthProperty().subtract(2 * MAP_PADDING).divide(bounds.getWidth());
        NumberBinding heightScale = heightProperty().subtract(2 * MAP_PADDING).divide(bounds.getHeight());
        NumberBinding scale = Bindings.max(0, Bindings.min(widthScale, heightScale));
        map.scaleXProperty().bind(scale);
        map.scaleYProperty().bind(scale);
    }

    private Group createMap(GameBoard board) {
        BoardGeometry geometry = new BoardGeometry();
        Point2D boardCenter = geometry.getCenter();
        Map<Integer, Point2D> nodePositions = new HashMap<>();
        Group map = new Group();
        for (Tile tile : board.getTiles()) {
            List<Point2D> corners = geometry.getTileCorners(tile.getId());
            List<Node> cornerNodes = tile.getCornerNodes();
            for (int corner = 0; corner < corners.size(); corner++) {
                nodePositions.put(cornerNodes.get(corner).getId(), corners.get(corner));
            }
            map.getChildren().add(createTile(tile, corners, geometry.getTileCenter(tile.getId())));
        }
        board.getEdges().forEach(edge -> addEdge(map, edge, nodePositions, boardCenter));
        for (Node node : board.getNodes()) {
            node.getBuildingColor().ifPresent(owner -> map.getChildren().add(
                    createBuilding(nodePositions.get(node.getId()), owner, node.getBuildingType())));
        }
        board.getStartingResourceNodes().values().forEach(id -> map.getChildren().add(
                createResourceStar(nodePositions.get(id))));
        map.getChildren().add(createRobber(geometry.getTileCenter(board.getRobberTileId())));
        if (spotMode != null) {
            addSpots(map, nodePositions);
        }
        return map;
    }

    private void addEdge(Group map, Edge edge, Map<Integer, Point2D> nodePositions, Point2D boardCenter) {
        Point2D start = nodePositions.get(edge.getNode1().getId());
        Point2D end = nodePositions.get(edge.getNode2().getId());
        HarborType harbor = edge.getNode1().getHarborType();
        if (harbor != HarborType.NONE && harbor == edge.getNode2().getHarborType()) {
            map.getChildren().add(createHarbor(start, end, harbor, boardCenter));
        }
        edge.getRoadColor().ifPresent(owner -> map.getChildren().add(createRoad(start, end, owner)));
    }

    private Group createTile(Tile tile, List<Point2D> corners, Point2D center) {
        Polygon hex = new Polygon();
        for (Point2D corner : corners) {
            hex.getPoints().addAll(corner.getX(), corner.getY());
        }
        hex.setFill(tile.getResourceProduced().map(TERRAIN_COLORS::get).orElse(DESERT));
        hex.setStroke(SAND);
        hex.setStrokeWidth(TILE_OUTLINE_WIDTH);

        String key = tile.getResourceProduced().map(resource -> "board.terrain." + resource.name())
                .orElse("board.terrain.desert");
        Point2D labelCenter = center.add(0, TERRAIN_LABEL_OFFSET);
        Text label = createText(messages.getString(key), labelCenter, TERRAIN_FONT, Color.WHITE);
        Group group = new Group(hex, label);
        if (tile.getResourceProduced().isPresent()) {
            group.getChildren().add(createToken(center.add(0, TILE_SYMBOL_OFFSET), tile.getDiceNumber()));
        }
        return group;
    }

    private Group createToken(Point2D center, int number) {
        Circle token = new Circle(center.getX(), center.getY(), TOKEN_RADIUS, TOKEN_FILL);
        token.setStroke(INK);
        int pips = TOKEN_PIP_BASE - Math.abs(MOST_LIKELY_ROLL - number);
        Color textColor = pips == MAX_TOKEN_PIPS ? HIGH_ODDS : INK;
        String numberText = NumberFormat.getIntegerInstance(messages.getLocale()).format(number);
        Text roll = createText(numberText, center.add(0, TOKEN_NUMBER_OFFSET), TOKEN_FONT, textColor);
        Text dots = createText("•".repeat(pips), center.add(0, TOKEN_PIPS_OFFSET), PIPS_FONT, textColor);
        return new Group(token, roll, dots);
    }

    private Group createHarbor(Point2D start, Point2D end, HarborType type, Point2D boardCenter) {
        Point2D midpoint = start.midpoint(end);
        Point2D outward = outwardNormal(start, end, boardCenter);
        Point2D harborCenter = midpoint.add(outward.multiply(HARBOR_DISTANCE));
        Text label = createText(messages.getString("board.harbor." + type.name()), harborCenter, HARBOR_FONT, INK);
        Rectangle badge = createHarborBadge(label, harborCenter);
        return new Group(createLine(start, harborCenter, SAND, HARBOR_CONNECTOR_WIDTH),
                createLine(end, harborCenter, SAND, HARBOR_CONNECTOR_WIDTH), badge, label);
    }

    private Point2D outwardNormal(Point2D start, Point2D end, Point2D boardCenter) {
        Point2D direction = end.subtract(start).normalize();
        Point2D normal = new Point2D(-direction.getY(), direction.getX());
        Point2D toCoast = start.midpoint(end).subtract(boardCenter);
        return normal.dotProduct(toCoast) < 0 ? normal.multiply(-1) : normal;
    }

    private Rectangle createHarborBadge(Text label, Point2D center) {
        double width = label.getLayoutBounds().getWidth() + 2 * HARBOR_LABEL_PADDING;
        Rectangle badge = new Rectangle(center.getX() - width / 2, center.getY() - HARBOR_LABEL_HEIGHT / 2,
                width, HARBOR_LABEL_HEIGHT);
        badge.setArcWidth(HARBOR_LABEL_RADIUS);
        badge.setArcHeight(HARBOR_LABEL_RADIUS);
        badge.setFill(TOKEN_FILL);
        badge.setStroke(INK);
        return badge;
    }

    private Group createRoad(Point2D start, Point2D end, PlayerColor owner) {
        return new Group(createLine(start, end, INK, ROAD_OUTLINE_WIDTH),
                createLine(start, end, PlayerColors.of(owner), ROAD_WIDTH));
    }

    private Line createLine(Point2D start, Point2D end, Color color, double width) {
        Line line = new Line(start.getX(), start.getY(), end.getX(), end.getY());
        line.setStroke(color);
        line.setStrokeWidth(width);
        line.setStrokeLineCap(StrokeLineCap.ROUND);
        return line;
    }

    private void addSpots(Group map, Map<Integer, Point2D> nodePositions) {
        Set<Integer> spots = spotsFor(spotMode, spotColor);
        if (spotMode == BuildMode.ROAD) {
            for (int edgeId : spots) {
                Edge edge = board.getEdges().get(edgeId);
                Line spot = createLine(nodePositions.get(edge.getNode1().getId()),
                        nodePositions.get(edge.getNode2().getId()), SPOT, SPOT_ROAD_WIDTH);
                map.getChildren().add(clickable(spot, edgeId));
            }
            return;
        }
        for (int nodeId : spots) {
            Point2D position = nodePositions.get(nodeId);
            Circle spot = new Circle(position.getX(), position.getY(), SPOT_RADIUS, SPOT);
            spot.setStroke(INK);
            map.getChildren().add(clickable(spot, nodeId));
        }
    }

    private Set<Integer> spotsFor(BuildMode mode, PlayerColor color) {
        if (mode == BuildMode.ROAD) {
            return board.getRoadSpots(color);
        }
        return mode == BuildMode.SETTLEMENT ? board.getSettlementSpots(color) : board.getCitySpots(color);
    }

    private javafx.scene.Node clickable(javafx.scene.Node spot, int id) {
        spot.setCursor(Cursor.HAND);
        spot.setOnMouseClicked(event -> pickSpot.accept(id));
        return spot;
    }

    private Polygon createBuilding(Point2D position, PlayerColor owner, BuildingType type) {
        Polygon house = new Polygon(type == BuildingType.CITY ? CITY_POINTS : SETTLEMENT_POINTS);
        house.setFill(PlayerColors.of(owner));
        house.setStroke(INK);
        house.setStrokeWidth(PIECE_OUTLINE_WIDTH);
        house.setTranslateX(position.getX());
        house.setTranslateY(position.getY());
        return house;
    }

    private Text createResourceStar(Point2D position) {
        Text star = createText("★", position.add(0, RESOURCE_STAR_OFFSET), STAR_FONT, Color.WHITE);
        star.setStroke(INK);
        return star;
    }

    private Group createRobber(Point2D center) {
        Polygon body = new Polygon(ROBBER_POINTS);
        body.setFill(ROBBER);
        body.setStroke(TOKEN_FILL);
        Circle head = new Circle(0, ROBBER_HEAD_OFFSET, ROBBER_HEAD_RADIUS, ROBBER);
        head.setStroke(TOKEN_FILL);
        Group robber = new Group(body, head);
        robber.setTranslateX(center.getX());
        robber.setTranslateY(center.getY() + TILE_SYMBOL_OFFSET);
        String robberName = messages.getString("board.robber");
        robber.setAccessibleText(robberName);
        Tooltip.install(robber, new Tooltip(robberName));
        return robber;
    }

    private Text createText(String value, Point2D center, Font font, Color color) {
        Text text = new Text(value);
        text.setFont(font);
        text.setFill(color);
        text.setTextOrigin(VPos.CENTER);
        text.setX(center.getX() - text.getLayoutBounds().getWidth() / 2);
        text.setY(center.getY());
        return text;
    }
}
