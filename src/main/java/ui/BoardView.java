package ui;

import domain.BeginnerBoard;
import domain.BeginnerBoard.TileSetup;
import domain.HarborType;
import domain.PlayerColor;
import domain.ResourceCardType;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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
import ui.BoardGeometry.Side;

import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

final class BoardView extends BorderPane {
    private static final double SCREEN_PADDING = 20;
    private static final double SECTION_SPACING = 14;
    private static final double MAP_PADDING = 22;
    private static final double TERRAIN_LABEL_OFFSET = 24;
    private static final double TOKEN_OFFSET = -9;
    private static final double TOKEN_RADIUS = 19;
    private static final double TOKEN_NUMBER_OFFSET = -3;
    private static final double TOKEN_PIPS_OFFSET = 9;
    private static final int MOST_LIKELY_ROLL = 7;
    private static final int TOKEN_PIP_BASE = 6;
    private static final int MAX_TOKEN_PIPS = 5;
    private static final double ROAD_WIDTH = 8;
    private static final double ROAD_OUTLINE_WIDTH = 11;
    private static final double RESOURCE_STAR_OFFSET = -19;
    private static final double HARBOR_DISTANCE = 37;
    private static final double HARBOR_LABEL_HEIGHT = 23;
    private static final double HARBOR_LABEL_PADDING = 8;
    private static final double HARBOR_LABEL_RADIUS = 10;
    private static final double ROBBER_HEAD_RADIUS = 8;
    private static final double ROBBER_HEAD_OFFSET = -12;
    private static final double[] SETTLEMENT_POINTS = {-10, 8, -10, -2, 0, -11, 10, -2, 10, 8};
    private static final double[] ROBBER_POINTS = {-11, 12, -8, -5, -4, -10, 4, -10, 8, -5, 11, 12};
    private static final Color SAND = Color.web("#ecd7a6");
    private static final Color TOKEN_FILL = Color.web("#ffedbe");
    private static final Color INK = Color.web("#352820");
    private static final Color DESERT = Color.web("#a47b44");
    private static final Color ROBBER = Color.web("#353c46");
    private static final Color HIGH_ODDS = Color.web("#9a2425");
    private static final Font TITLE_FONT = Font.font("System", FontWeight.BOLD, 23);
    private static final Font TERRAIN_FONT = Font.font("System", FontWeight.BOLD, 14);
    private static final Font TOKEN_FONT = Font.font("System", FontWeight.BOLD, 19);
    private static final Font PIPS_FONT = Font.font(11);
    private static final Font HARBOR_FONT = Font.font("System", FontWeight.BOLD, 13);
    private static final Font STAR_FONT = Font.font(17);
    private static final Map<ResourceCardType, Color> TERRAIN_COLORS = Map.of(
            ResourceCardType.LUMBER, Color.web("#315b43"), ResourceCardType.WOOL, Color.web("#617539"),
            ResourceCardType.GRAIN, Color.web("#936421"), ResourceCardType.BRICK, Color.web("#8c4232"),
            ResourceCardType.ORE, Color.web("#515b70"));
    private static final Map<PlayerColor, Color> PLAYER_COLORS = Map.of(
            PlayerColor.RED, Color.web("#e74142"), PlayerColor.ORANGE, Color.web("#fb9c34"),
            PlayerColor.BLUE, Color.web("#43a2eb"), PlayerColor.WHITE, Color.web("#fff8e8"));

    private final ResourceBundle messages;

    BoardView(BeginnerBoard board, ResourceBundle messages, Runnable goBack) {
        this.messages = messages;
        setPadding(new Insets(SCREEN_PADDING));
        setStyle("-fx-background-color: #f5ecdc;");

        HBox header = createHeader(goBack);
        setTop(header);
        setMargin(header, new Insets(0, 0, SECTION_SPACING, 0));
        setCenter(createMapViewport(createMap(board)));

        Label legend = new Label(messages.getString("board.startingResources"));
        legend.setTextFill(INK);
        setBottom(legend);
        setAlignment(legend, Pos.CENTER);
        setMargin(legend, new Insets(SECTION_SPACING, 0, 0, 0));
    }

    private HBox createHeader(Runnable goBack) {
        Button back = new Button(messages.getString("board.back"));
        back.setOnAction(event -> goBack.run());

        Label title = new Label(messages.getString("board.title"));
        title.setFont(TITLE_FONT);
        title.setTextFill(INK);
        title.setWrapText(true);
        HBox header = new HBox(SECTION_SPACING, back, title);
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    private StackPane createMapViewport(Group map) {
        StackPane viewport = new StackPane(map);
        viewport.setMinSize(0, 0);
        viewport.setPadding(new Insets(MAP_PADDING));
        viewport.setStyle("-fx-background-color: linear-gradient(to bottom right, #39788e, #183f58); "
                + "-fx-background-radius: 16;");

        Bounds bounds = map.getLayoutBounds();
        NumberBinding widthScale = viewport.widthProperty().subtract(2 * MAP_PADDING).divide(bounds.getWidth());
        NumberBinding heightScale = viewport.heightProperty().subtract(2 * MAP_PADDING).divide(bounds.getHeight());
        NumberBinding scale = Bindings.max(0, Bindings.min(widthScale, heightScale));
        map.scaleXProperty().bind(scale);
        map.scaleYProperty().bind(scale);
        return viewport;
    }

    private Group createMap(BeginnerBoard board) {
        List<List<TileSetup>> rows = board.getTileRows();
        BoardGeometry geometry = new BoardGeometry(rows);
        Point2D robberCenter = geometry.getTileCenter(board.getRobberTileId());
        Group map = new Group(createTerrain(rows, geometry));
        board.getHarbors().forEach((id, type) -> map.getChildren().add(
                createHarbor(geometry.getEdge(id), type, robberCenter)));
        board.getRoads().forEach((id, owner) -> map.getChildren().add(createRoad(geometry.getEdge(id), owner)));
        board.getSettlements().forEach((id, owner) -> map.getChildren().add(
                createSettlement(geometry.getNode(id), owner)));
        board.getStartingResourceNodes().forEach(id -> map.getChildren().add(createResourceStar(geometry.getNode(id))));
        map.getChildren().add(createRobber(robberCenter));
        return map;
    }

    private Group createTerrain(List<List<TileSetup>> rows, BoardGeometry geometry) {
        Group terrain = new Group();
        int tileId = 0;
        for (List<TileSetup> row : rows) {
            for (TileSetup tile : row) {
                terrain.getChildren().add(createTile(tile, geometry.getTileCorners(tileId),
                        geometry.getTileCenter(tileId)));
                tileId++;
            }
        }
        return terrain;
    }

    private Group createTile(TileSetup tile, List<Point2D> corners, Point2D center) {
        Polygon hex = new Polygon();
        for (Point2D corner : corners) {
            hex.getPoints().addAll(corner.getX(), corner.getY());
        }
        hex.setFill(tile.getResource().map(TERRAIN_COLORS::get).orElse(DESERT));
        hex.setStroke(SAND);
        hex.setStrokeWidth(2);

        String key = tile.getResource().map(resource -> "board.terrain." + resource.name())
                .orElse("board.terrain.desert");
        Point2D labelCenter = center.add(0, TERRAIN_LABEL_OFFSET);
        Text label = createText(messages.getString(key), labelCenter, TERRAIN_FONT, Color.WHITE);
        Group group = new Group(hex, label);
        tile.getDiceNumber().ifPresent(number -> group.getChildren().add(
                createToken(center.add(0, TOKEN_OFFSET), number)));
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

    private Group createHarbor(Side edge, HarborType type, Point2D boardCenter) {
        Point2D start = edge.getStart();
        Point2D end = edge.getEnd();
        Point2D midpoint = start.midpoint(end);
        Point2D outward = outwardNormal(start, end, boardCenter);
        Point2D harborCenter = midpoint.add(outward.multiply(HARBOR_DISTANCE));
        Text label = createText(messages.getString("board.harbor." + type.name()), harborCenter, HARBOR_FONT, INK);
        Rectangle badge = createHarborBadge(label, harborCenter);
        return new Group(createLine(start, harborCenter, SAND, 2),
                createLine(end, harborCenter, SAND, 2), badge, label);
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

    private Group createRoad(Side edge, PlayerColor owner) {
        return new Group(createLine(edge.getStart(), edge.getEnd(), INK, ROAD_OUTLINE_WIDTH),
                createLine(edge.getStart(), edge.getEnd(), PLAYER_COLORS.get(owner), ROAD_WIDTH));
    }

    private Line createLine(Point2D start, Point2D end, Color color, double width) {
        Line line = new Line(start.getX(), start.getY(), end.getX(), end.getY());
        line.setStroke(color);
        line.setStrokeWidth(width);
        line.setStrokeLineCap(StrokeLineCap.ROUND);
        return line;
    }

    private Polygon createSettlement(Point2D position, PlayerColor owner) {
        Polygon house = new Polygon(SETTLEMENT_POINTS);
        house.setFill(PLAYER_COLORS.get(owner));
        house.setStroke(INK);
        house.setStrokeWidth(2);
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
        robber.setTranslateY(center.getY() + TOKEN_OFFSET);
        robber.setAccessibleText(messages.getString("board.robber"));
        Tooltip.install(robber, new Tooltip(messages.getString("board.robber")));
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
