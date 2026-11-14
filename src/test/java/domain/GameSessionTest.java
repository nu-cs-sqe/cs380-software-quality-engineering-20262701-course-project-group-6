package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

public class GameSessionTest {

    @Test
    public void Constructor_FourPlayers_AllColorsAvailableAndFullBank() {
        GameSession game = new GameSession(4, new Random());

        assertEquals(List.of(PlayerColor.BLUE, PlayerColor.ORANGE, PlayerColor.WHITE, PlayerColor.RED),
                game.getAvailableColors());
        for (ResourceCardType type : ResourceCardType.values()) {
            assertEquals(19, game.getBankResourceCount(type));
        }
    }
}
