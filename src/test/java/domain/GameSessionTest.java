package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    public void Constructor_TwoPlayers_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new GameSession(2, new Random()));

        assertEquals("A game needs 3 or 4 players (was 2)", exception.getMessage());
    }
}
