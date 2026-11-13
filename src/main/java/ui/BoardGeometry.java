package ui;

import domain.BeginnerBoard.TileSetup;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

final class BoardGeometry {
    private static final double HEX_RADIUS = 58;
    private static final double HALF_HEX_WIDTH = Math.sqrt(3) * HEX_RADIUS / 2;
    private static final double HALF_HEX_HEIGHT = HEX_RADIUS / 2;
    private static final int ROW_STEP = 3;
    private static final int COLUMN_STEP = 2;
    // Integer coordinates keep shared corners and edge midpoints identical before conversion to pixels.
    private static final List<Point2D> CORNER_OFFSETS = List.of(new Point2D(0, -2),
                                                                new Point2D(1, -1),
                                                                new Point2D(1, 1),
                                                                new Point2D(0, 2),
                                                                new Point2D(-1, 1),
                                                                new Point2D(-1, -1));
    private static final Comparator<Point2D> BOARD_ORDER = Comparator.comparingDouble(Point2D::getY)
            .thenComparingDouble(Point2D::getX);

    private final List<Point2D> tileCenters;
    private final List<Point2D> nodes;
    private final List<Side> edges;

    BoardGeometry(List<List<TileSetup>> rows) {
        tileCenters = createTileCenters(rows);
        Set<Point2D> sortedNodes = new TreeSet<>(BOARD_ORDER);
        Map<Point2D, Side> sortedEdges = new TreeMap<>(BOARD_ORDER);
        for (Point2D center : tileCenters) {
            List<Point2D> corners = getCorners(center);
            sortedNodes.addAll(corners);
            addEdges(sortedEdges, corners);
        }
        nodes = List.copyOf(sortedNodes);
        edges = List.copyOf(sortedEdges.values());
    }

    Point2D getTileCenter(int tileId) {
        return toPixels(tileCenters.get(tileId));
    }

    Point2D getCenter() {
        Point2D total = tileCenters.stream().reduce(Point2D.ZERO, Point2D::add);
        return toPixels(total.multiply(1.0 / tileCenters.size()));
    }

    List<Point2D> getTileCorners(int tileId) {
        return getCorners(tileCenters.get(tileId)).stream().map(BoardGeometry::toPixels).collect(Collectors.toList());
    }

    Point2D getNode(int nodeId) {
        return toPixels(nodes.get(nodeId));
    }

    Side getEdge(int edgeId) {
        return edges.get(edgeId);
    }

    private static List<Point2D> createTileCenters(List<List<TileSetup>> rows) {
        int widestRow = rows.stream().mapToInt(List::size).max().orElseThrow();
        List<Point2D> centers = new ArrayList<>();
        for (int row = 0; row < rows.size(); row++) {
            int rowSize = rows.get(row).size();
            int left = widestRow - rowSize;
            for (int column = 0; column < rowSize; column++) {
                centers.add(new Point2D(left + column * COLUMN_STEP, row * ROW_STEP));
            }
        }
        return centers;
    }

    private static List<Point2D> getCorners(Point2D center) {
        return CORNER_OFFSETS.stream().map(center::add).collect(Collectors.toList());
    }

    private static void addEdges(Map<Point2D, Side> edges, List<Point2D> corners) {
        for (int index = 0; index < corners.size(); index++) {
            Point2D start = corners.get(index);
            Point2D end = corners.get((index + 1) % corners.size());
            edges.putIfAbsent(start.midpoint(end), new Side(toPixels(start), toPixels(end)));
        }
    }

    private static Point2D toPixels(Point2D position) {
        return new Point2D(position.getX() * HALF_HEX_WIDTH, position.getY() * HALF_HEX_HEIGHT);
    }

    static final class Side {
        private final Point2D start;
        private final Point2D end;

        private Side(Point2D start, Point2D end) {
            this.start = start;
            this.end = end;
        }

        Point2D getStart() {
            return start;
        }

        Point2D getEnd() {
            return end;
        }
    }
}
