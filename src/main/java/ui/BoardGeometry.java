package ui;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

final class BoardGeometry {
    private static final double HEX_RADIUS = 58;
    private static final double HALF_HEX_WIDTH = Math.sqrt(3) * HEX_RADIUS / 2;
    private static final double HALF_HEX_HEIGHT = HEX_RADIUS / 2;
    private static final int ROW_STEP = 3;
    private static final int COLUMN_STEP = 2;
    private static final int[] ROW_SIZES = {3, 4, 5, 4, 3};
    // Same order as Tile.getCornerNodes(): clockwise from the upper-left corner (docs/design/map_ids_reference.jpg).
    // Integer coordinates keep shared corners identical before conversion to pixels.
    private static final List<Point2D> CORNER_OFFSETS = List.of(new Point2D(-1, -1),
                                                                new Point2D(0, -2),
                                                                new Point2D(1, -1),
                                                                new Point2D(1, 1),
                                                                new Point2D(0, 2),
                                                                new Point2D(-1, 1));

    private final List<Point2D> tileCenters = createTileCenters();

    Point2D getTileCenter(int tileId) {
        return toPixels(tileCenters.get(tileId));
    }

    Point2D getCenter() {
        Point2D total = tileCenters.stream().reduce(Point2D.ZERO, Point2D::add);
        return toPixels(total.multiply(1.0 / tileCenters.size()));
    }

    List<Point2D> getTileCorners(int tileId) {
        Point2D center = tileCenters.get(tileId);
        return CORNER_OFFSETS.stream().map(center::add).map(BoardGeometry::toPixels).collect(Collectors.toList());
    }

    private static List<Point2D> createTileCenters() {
        int widestRow = 0;
        for (int rowSize : ROW_SIZES) {
            widestRow = Math.max(widestRow, rowSize);
        }
        List<Point2D> centers = new ArrayList<>();
        for (int row = 0; row < ROW_SIZES.length; row++) {
            int left = widestRow - ROW_SIZES[row];
            for (int column = 0; column < ROW_SIZES[row]; column++) {
                centers.add(new Point2D(left + column * COLUMN_STEP, row * ROW_STEP));
            }
        }
        return centers;
    }

    private static Point2D toPixels(Point2D position) {
        return new Point2D(position.getX() * HALF_HEX_WIDTH, position.getY() * HALF_HEX_HEIGHT);
    }
}
