package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Random;
import java.util.Set;
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

    @Test
    public void Constructor_ThreePlayers_RedNotAvailable() {
        GameSession game = new GameSession(3, new Random());

        assertEquals(List.of(PlayerColor.BLUE, PlayerColor.ORANGE, PlayerColor.WHITE), game.getAvailableColors());
    }

    @Test
    public void Constructor_FivePlayers_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new GameSession(5, new Random()));

        assertEquals("A game needs 3 or 4 players (was 5)", exception.getMessage());
    }

    @Test
    public void Constructor_NullRandom_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new GameSession(4, null));

        assertEquals("Random is required to start a game", exception.getMessage());
    }

    @Test
    public void RollForStartingOrder_FourPlayers_EachSeatOnce() {
        GameSession game = new GameSession(4, new Random());

        List<Integer> order = game.rollForStartingOrder();

        assertEquals(Set.of(1, 2, 3, 4), Set.copyOf(order));
        assertEquals(4, order.size());
    }
}
