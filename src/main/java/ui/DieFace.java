package ui;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.Map;

final class DieFace extends Pane {
    private static final Color INK = Color.web("#352820");
    private static final double CORNER_RATIO = 0.22;
    private static final double PIP_RATIO = 0.09;
    private static final double NEAR = 0.27;
    private static final double MIDDLE = 0.5;
    private static final double FAR = 0.73;
    private static final double[] CENTER = {MIDDLE, MIDDLE};
    private static final double[] TOP_LEFT = {NEAR, NEAR};
    private static final double[] TOP_RIGHT = {FAR, NEAR};
    private static final double[] MIDDLE_LEFT = {NEAR, MIDDLE};
    private static final double[] MIDDLE_RIGHT = {FAR, MIDDLE};
    private static final double[] BOTTOM_LEFT = {NEAR, FAR};
    private static final double[] BOTTOM_RIGHT = {FAR, FAR};
    private static final Map<Integer, double[][]> PIPS = Map.of(
            1, new double[][] {CENTER},
            2, new double[][] {TOP_LEFT, BOTTOM_RIGHT},
            3, new double[][] {TOP_LEFT, CENTER, BOTTOM_RIGHT},
            4, new double[][] {TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT},
            5, new double[][] {TOP_LEFT, TOP_RIGHT, CENTER, BOTTOM_LEFT, BOTTOM_RIGHT},
            6, new double[][] {TOP_LEFT, TOP_RIGHT, MIDDLE_LEFT, MIDDLE_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT});

    private final double size;

    DieFace(double size) {
        this.size = size;
        setPrefSize(size, size);
        setMinSize(size, size);
        setMaxSize(size, size);
        clear();
    }

    void show(int value) {
        getChildren().setAll(square());
        for (double[] pip : PIPS.getOrDefault(value, new double[0][])) {
            getChildren().add(new Circle(pip[0] * size, pip[1] * size, PIP_RATIO * size, INK));
        }
    }

    void clear() {
        show(0);
    }

    private Rectangle square() {
        Rectangle square = new Rectangle(size, size, Color.WHITE);
        square.setArcWidth(CORNER_RATIO * size);
        square.setArcHeight(CORNER_RATIO * size);
        square.setStroke(INK);
        return square;
    }
}
