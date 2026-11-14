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

    @Test
    public void RollForStartingOrder_AlreadyRolled_ThrowsIllegalStateException() {
        GameSession game = new GameSession(4, new Random());
        game.rollForStartingOrder();

        IllegalStateException exception = assertThrows(IllegalStateException.class, game::rollForStartingOrder);

        assertEquals("The starting order was already rolled", exception.getMessage());
    }

    @Test
    public void GetSeatToPick_RolledNobodyPicked_FirstSeatInOrder() {
        GameSession game = new GameSession(4, new Random());
        List<Integer> order = game.rollForStartingOrder();

        int seat = game.getSeatToPick();

        assertEquals(order.get(0), seat);
    }

    @Test
    public void GetSeatToPick_NotRolledYet_ThrowsIllegalStateException() {
        GameSession game = new GameSession(4, new Random());

        IllegalStateException exception = assertThrows(IllegalStateException.class, game::getSeatToPick);

        assertEquals("Players are not picking colors", exception.getMessage());
    }

    @Test
    public void GetTurnOrder_RolledNobodyPicked_Empty() {
        GameSession game = new GameSession(4, new Random());
        game.rollForStartingOrder();

        List<PlayerColor> turnOrder = game.getTurnOrder();

        assertEquals(List.of(), turnOrder);
    }

    @Test
    public void GetAvailableColors_FirstSeatPickedBlue_BlueNoLongerAvailable() {
        GameSession game = new GameSession(4, new Random());
        game.rollForStartingOrder();
        game.pickColor(PlayerColor.BLUE);

        List<PlayerColor> available = game.getAvailableColors();

        assertEquals(List.of(PlayerColor.ORANGE, PlayerColor.WHITE, PlayerColor.RED), available);
    }

    @Test
    public void PickColor_FirstSeatPicksBlue_GetsStartingResourcesFromBank() {
        GameSession game = new GameSession(4, new Random());
        List<Integer> order = game.rollForStartingOrder();

        game.pickColor(PlayerColor.BLUE);

        assertEquals(1, game.getResourceCount(PlayerColor.BLUE, ResourceCardType.LUMBER));
        assertEquals(1, game.getResourceCount(PlayerColor.BLUE, ResourceCardType.ORE));
        assertEquals(1, game.getResourceCount(PlayerColor.BLUE, ResourceCardType.BRICK));
        assertEquals(0, game.getResourceCount(PlayerColor.BLUE, ResourceCardType.GRAIN));
        assertEquals(0, game.getResourceCount(PlayerColor.BLUE, ResourceCardType.WOOL));
        assertEquals(2, game.getVictoryPointCount(PlayerColor.BLUE));
        assertEquals(18, game.getBankResourceCount(ResourceCardType.LUMBER));
        assertEquals(18, game.getBankResourceCount(ResourceCardType.ORE));
        assertEquals(18, game.getBankResourceCount(ResourceCardType.BRICK));
        assertEquals(19, game.getBankResourceCount(ResourceCardType.GRAIN));
        assertEquals(19, game.getBankResourceCount(ResourceCardType.WOOL));
        assertEquals(order.get(1), game.getSeatToPick());
    }

    @Test
    public void PickColor_ColorAlreadyTaken_ThrowsIllegalStateException() {
        GameSession game = new GameSession(4, new Random());
        List<Integer> order = game.rollForStartingOrder();
        game.pickColor(PlayerColor.BLUE);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> game.pickColor(PlayerColor.BLUE));

        assertEquals("BLUE is already taken", exception.getMessage());
        assertEquals(order.get(1), game.getSeatToPick());
    }

    @Test
    public void PickColor_RedInThreePlayerGame_ThrowsIllegalStateException() {
        GameSession game = new GameSession(3, new Random());
        List<Integer> order = game.rollForStartingOrder();

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> game.pickColor(PlayerColor.RED));

        assertEquals("RED is not in play", exception.getMessage());
        assertEquals(order.get(0), game.getSeatToPick());
    }

    @Test
    public void PickColor_NullColor_ThrowsIllegalArgumentException() {
        GameSession game = new GameSession(4, new Random());
        game.rollForStartingOrder();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> game.pickColor(null));

        assertEquals("Player color is required", exception.getMessage());
    }

    @Test
    public void PickColor_LastSeatPicks_TurnOrderFollowsPickOrder() {
        GameSession game = new GameSession(3, new Random());
        game.rollForStartingOrder();
        game.pickColor(PlayerColor.WHITE);
        game.pickColor(PlayerColor.BLUE);

        game.pickColor(PlayerColor.ORANGE);

        assertEquals(List.of(PlayerColor.WHITE, PlayerColor.BLUE, PlayerColor.ORANGE), game.getTurnOrder());
        assertEquals(PlayerColor.WHITE, game.getCurrentColor());
        assertEquals(List.of(), game.getAvailableColors());
    }

    @Test
    public void PickColor_EveryonePicked_ThrowsIllegalStateException() {
        GameSession game = new GameSession(3, new Random());
        game.rollForStartingOrder();
        game.pickColor(PlayerColor.WHITE);
        game.pickColor(PlayerColor.BLUE);
        game.pickColor(PlayerColor.ORANGE);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> game.pickColor(PlayerColor.RED));

        assertEquals("Players are not picking colors", exception.getMessage());
    }

    @Test
    public void PickColor_FirstSeatPicksRed_GetsTwoLumber() {
        GameSession game = new GameSession(4, new Random());
        game.rollForStartingOrder();

        game.pickColor(PlayerColor.RED);

        assertEquals(1, game.getResourceCount(PlayerColor.RED, ResourceCardType.GRAIN));
        assertEquals(2, game.getResourceCount(PlayerColor.RED, ResourceCardType.LUMBER));
        assertEquals(18, game.getBankResourceCount(ResourceCardType.GRAIN));
        assertEquals(17, game.getBankResourceCount(ResourceCardType.LUMBER));
    }

    @Test
    public void GetCurrentColor_StillPicking_ThrowsIllegalStateException() {
        GameSession game = new GameSession(4, new Random());
        game.rollForStartingOrder();
        game.pickColor(PlayerColor.BLUE);

        IllegalStateException exception = assertThrows(IllegalStateException.class, game::getCurrentColor);

        assertEquals("Players are still picking colors", exception.getMessage());
    }
}
