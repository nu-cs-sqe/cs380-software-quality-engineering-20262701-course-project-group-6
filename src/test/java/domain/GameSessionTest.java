package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import org.easymock.EasyMock;
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

    @Test
    public void GetResourceCount_ColorNotPicked_ThrowsIllegalArgumentException() {
        GameSession game = new GameSession(4, new Random());
        game.rollForStartingOrder();
        game.pickColor(PlayerColor.BLUE);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> game.getResourceCount(PlayerColor.ORANGE, ResourceCardType.LUMBER));

        assertEquals("No player has picked ORANGE", exception.getMessage());
    }

    @Test
    public void GetVictoryPointCount_NullColor_ThrowsIllegalArgumentException() {
        GameSession game = new GameSession(4, new Random());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> game.getVictoryPointCount(null));

        assertEquals("Player color is required", exception.getMessage());
    }

    @Test
    public void GetBankResourceCount_NullType_ThrowsIllegalArgumentException() {
        GameSession game = new GameSession(4, new Random());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> game.getBankResourceCount(null));

        assertEquals("Resource type is required", exception.getMessage());
    }

    @Test
    public void PickColor_NotRolledYet_ThrowsIllegalStateException() {
        GameSession game = new GameSession(4, new Random());

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> game.pickColor(PlayerColor.BLUE));

        assertEquals("Players are not picking colors", exception.getMessage());
        assertEquals(List.of(PlayerColor.BLUE, PlayerColor.ORANGE, PlayerColor.WHITE, PlayerColor.RED),
                game.getAvailableColors());
    }

    @Test
    public void RollDice_StillPicking_ThrowsIllegalStateException() {
        GameSession game = new GameSession(3, new Random());
        game.rollForStartingOrder();
        game.pickColor(PlayerColor.WHITE);

        IllegalStateException exception = assertThrows(IllegalStateException.class, game::rollDice);

        assertEquals("Players are still picking colors", exception.getMessage());
    }

    @Test
    public void RollDice_SevenRolled_ReturnsBothDiceAndNobodyCollects() {
        GameSession game = startedThreePlayerGame(diceRolling(3, 4));

        DiceRoll roll = game.rollDice();

        assertEquals(3, roll.getFirstDie());
        assertEquals(4, roll.getSecondDie());
        assertEquals(7, roll.getTotal());
        for (PlayerColor color : game.getTurnOrder()) {
            assertEquals(3, totalResources(game, color), color.name());
        }
        assertEquals(17, game.getBankResourceCount(ResourceCardType.ORE));
        assertEquals(17, game.getBankResourceCount(ResourceCardType.LUMBER));
    }

    @Test
    public void RollDice_EightRolled_SettlementsOnEightHexesCollect() {
        GameSession game = startedThreePlayerGame(diceRolling(2, 6));

        game.rollDice();

        assertEquals(1, game.getResourceCount(PlayerColor.WHITE, ResourceCardType.ORE));
        assertEquals(2, game.getResourceCount(PlayerColor.BLUE, ResourceCardType.LUMBER));
        assertEquals(4, totalResources(game, PlayerColor.WHITE));
        assertEquals(4, totalResources(game, PlayerColor.BLUE));
        assertEquals(3, totalResources(game, PlayerColor.ORANGE));
        assertEquals(16, game.getBankResourceCount(ResourceCardType.ORE));
        assertEquals(16, game.getBankResourceCount(ResourceCardType.LUMBER));
    }

    @Test
    public void RollDice_AlreadyRolledThisTurn_ThrowsIllegalStateException() {
        GameSession game = startedThreePlayerGame();
        game.rollDice();

        IllegalStateException exception = assertThrows(IllegalStateException.class, game::rollDice);

        assertEquals("WHITE already rolled this turn", exception.getMessage());
    }

    @Test
    public void RollDice_NextPlayersTurn_CanRollAgain() {
        GameSession game = startedThreePlayerGame();
        game.rollDice();
        game.endTurn();

        DiceRoll roll = game.rollDice();

        assertTrue(roll.getTotal() >= 2 && roll.getTotal() <= 12);
    }

    @Test
    public void EndTurn_NotRolledYet_ThrowsIllegalStateException() {
        GameSession game = startedThreePlayerGame();

        IllegalStateException exception = assertThrows(IllegalStateException.class, game::endTurn);

        assertEquals("WHITE must roll before ending the turn", exception.getMessage());
        assertEquals(PlayerColor.WHITE, game.getCurrentColor());
    }

    @Test
    public void EndTurn_WhiteRolled_BluesTurn() {
        GameSession game = startedThreePlayerGame();
        game.rollDice();

        game.endTurn();

        assertEquals(PlayerColor.BLUE, game.getCurrentColor());
    }

    @Test
    public void EndTurn_LastPlayerEndsTurn_WrapsToFirstPlayer() {
        GameSession game = startedThreePlayerGame();
        for (int turn = 0; turn < 3; turn++) {
            game.rollDice();
            game.endTurn();
        }

        PlayerColor current = game.getCurrentColor();

        assertEquals(PlayerColor.WHITE, current);
    }

    @Test
    public void EndTurn_StillPicking_ThrowsIllegalStateException() {
        GameSession game = new GameSession(3, new Random());
        game.rollForStartingOrder();
        game.pickColor(PlayerColor.WHITE);

        IllegalStateException exception = assertThrows(IllegalStateException.class, game::endTurn);

        assertEquals("Players are still picking colors", exception.getMessage());
    }

    @Test
    public void Constructor_NullDice_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new GameSession(3, new Random(), null));

        assertEquals("Dice are required to start a game", exception.getMessage());
    }

    @Test
    public void GetBoard_ThreePlayers_BoardWithoutRed() {
        GameSession game = new GameSession(3, new Random());

        GameBoard board = game.getBoard();

        assertEquals(Set.of(PlayerColor.ORANGE, PlayerColor.BLUE, PlayerColor.WHITE),
                board.getStartingResourceNodes().keySet());
    }

    @Test
    public void GetStartingRolls_FourPlayersRolled_FirstRoundHasEverySeat() {
        GameSession game = new GameSession(4, new Random());
        game.rollForStartingOrder();

        List<Map<Integer, DiceRoll>> rolls = game.getStartingRolls();

        assertEquals(Set.of(1, 2, 3, 4), rolls.get(0).keySet());
    }

    @Test
    public void BuildRoad_NotRolledYet_ThrowsIllegalStateException() {
        GameSession game = startedThreePlayerGame();

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> game.buildRoad(24));

        assertEquals("WHITE must roll before building", exception.getMessage());
        assertEquals(Optional.empty(), game.getBoard().getEdges().get(24).getRoadColor());
    }

    @Test
    public void BuildRoad_LegalEdgeAndExactCost_PaysBankAndPlacesRoad() {
        GameSession game = startedThreePlayerGame(diceRolling(3, 4));
        game.rollDice();

        game.buildRoad(24);

        assertEquals(Optional.of(PlayerColor.WHITE), game.getBoard().getEdges().get(24).getRoadColor());
        assertEquals(0, game.getResourceCount(PlayerColor.WHITE, ResourceCardType.BRICK));
        assertEquals(0, game.getResourceCount(PlayerColor.WHITE, ResourceCardType.LUMBER));
        assertEquals(18, game.getBankResourceCount(ResourceCardType.BRICK));
        assertEquals(18, game.getBankResourceCount(ResourceCardType.LUMBER));
        assertEquals(12, game.getRoadsRemaining(PlayerColor.WHITE));
    }

    @Test
    public void BuildRoad_IllegalEdge_ThrowsAndKeepsCards() {
        GameSession game = startedThreePlayerGame(diceRolling(3, 4));
        game.rollDice();

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> game.buildRoad(0));

        assertEquals("Edge 0 is not a legal spot for a WHITE road", exception.getMessage());
        assertEquals(1, game.getResourceCount(PlayerColor.WHITE, ResourceCardType.BRICK));
        assertEquals(1, game.getResourceCount(PlayerColor.WHITE, ResourceCardType.LUMBER));
        assertEquals(13, game.getRoadsRemaining(PlayerColor.WHITE));
    }

    @Test
    public void BuildRoad_OneCardShort_ThrowsAndChangesNothing() {
        GameSession game = startedThreePlayerGame(diceRolling(3, 4));
        game.rollDice();
        game.buildRoad(24);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> game.buildRoad(23));

        assertEquals("WHITE cannot afford a road", exception.getMessage());
        assertEquals(Optional.empty(), game.getBoard().getEdges().get(23).getRoadColor());
        assertEquals(12, game.getRoadsRemaining(PlayerColor.WHITE));
    }

    @Test
    public void BuildSettlement_CollectedTheCost_PlacesSettlementAndScoresPoint() {
        GameSession game = startedThreePlayerGame(diceRolling(2, 3, 3, 3, 5, 6, 3, 4));
        game.rollDice();
        game.buildRoad(24);
        game.endTurn();
        game.rollDice();
        game.endTurn();
        game.rollDice();
        game.endTurn();
        game.rollDice();

        game.buildSettlement(17);

        Node corner = game.getBoard().getNodes().get(17);
        assertEquals(BuildingType.SETTLEMENT, corner.getBuildingType());
        assertEquals(Optional.of(PlayerColor.WHITE), corner.getBuildingColor());
        assertEquals(0, totalResources(game, PlayerColor.WHITE));
        assertEquals(3, game.getVictoryPointCount(PlayerColor.WHITE));
        assertEquals(2, game.getSettlementsRemaining(PlayerColor.WHITE));
    }

    @Test
    public void BuildSettlement_IllegalCorner_ThrowsIllegalStateException() {
        GameSession game = startedThreePlayerGame(diceRolling(3, 4));
        game.rollDice();

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> game.buildSettlement(0));

        assertEquals("Node 0 is not a legal spot for a WHITE settlement", exception.getMessage());
    }

    @Test
    public void BuildCity_CollectedTheCost_UpgradesSettlementAndScoresPoint() {
        GameSession game = startedThreePlayerGame(diceRolling(2, 6, 2, 6, 2, 6, 6, 6));
        for (int turn = 0; turn < 3; turn++) {
            game.rollDice();
            game.endTurn();
        }
        game.rollDice();

        game.buildCity(19);

        Node corner = game.getBoard().getNodes().get(19);
        assertEquals(BuildingType.CITY, corner.getBuildingType());
        assertEquals(Optional.of(PlayerColor.WHITE), corner.getBuildingColor());
        assertEquals(0, game.getResourceCount(PlayerColor.WHITE, ResourceCardType.ORE));
        assertEquals(0, game.getResourceCount(PlayerColor.WHITE, ResourceCardType.GRAIN));
        assertEquals(3, game.getVictoryPointCount(PlayerColor.WHITE));
        assertEquals(3, game.getCitiesRemaining(PlayerColor.WHITE));
        assertEquals(4, game.getSettlementsRemaining(PlayerColor.WHITE));
        assertEquals(17, game.getBankResourceCount(ResourceCardType.ORE));
        assertEquals(17, game.getBankResourceCount(ResourceCardType.GRAIN));
    }

    private static GameSession startedThreePlayerGame() {
        return startedThreePlayerGame(new Dice(new Random()));
    }

    private static GameSession startedThreePlayerGame(Dice dice) {
        GameSession game = new GameSession(3, new Random(), dice);
        game.rollForStartingOrder();
        game.pickColor(PlayerColor.WHITE);
        game.pickColor(PlayerColor.BLUE);
        game.pickColor(PlayerColor.ORANGE);
        return game;
    }

    private static Dice diceRolling(int... dice) {
        Random random = EasyMock.createStrictMock(Random.class);
        for (int die : dice) {
            EasyMock.expect(random.nextInt(6)).andReturn(die - 1);
        }
        EasyMock.replay(random);
        return new Dice(random);
    }

    private static int totalResources(GameSession game, PlayerColor color) {
        int total = 0;
        for (ResourceCardType type : ResourceCardType.values()) {
            total += game.getResourceCount(color, type);
        }
        return total;
    }
}
