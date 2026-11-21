package integration;

import domain.PlayerColor;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// Turns a step's "BLUE, ORANGE, WHITE" text into a list of colors, shared by the step definitions.
final class ColorList {

    private ColorList() {
    }

    static List<PlayerColor> parse(String colors) {
        return Arrays.stream(colors.split(","))
                .map(String::trim)
                .map(PlayerColor::valueOf)
                .collect(Collectors.toList());
    }
}
