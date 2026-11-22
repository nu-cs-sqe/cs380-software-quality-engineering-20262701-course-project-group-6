package ui;

import domain.PlayerColor;
import javafx.scene.paint.Color;

import java.util.Map;

final class PlayerColors {
    private static final Map<PlayerColor, String> WEB_COLORS = Map.of(
            PlayerColor.RED, "#e74142", PlayerColor.ORANGE, "#fb9c34",
            PlayerColor.BLUE, "#43a2eb", PlayerColor.WHITE, "#fff8e8");

    private PlayerColors() {
    }

    static String web(PlayerColor color) {
        return WEB_COLORS.get(color);
    }

    static Color of(PlayerColor color) {
        return Color.web(web(color));
    }
}
