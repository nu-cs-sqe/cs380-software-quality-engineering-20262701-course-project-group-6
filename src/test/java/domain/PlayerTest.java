package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class PlayerTest {

    @Test
    public void Constructor_BlueColor_StartsWithBeginnerPieces() {
        Player player = new Player(PlayerColor.BLUE);

        assertEquals(PlayerColor.BLUE, player.getColor());
        assertEquals(3, player.getSettlementsRemaining());
        assertEquals(4, player.getCitiesRemaining());
        assertEquals(13, player.getRoadsRemaining());
    }

    @Test
    public void Constructor_NullColor_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Player(null));

        assertEquals("Player color is required", exception.getMessage());
    }

    @Test
    public void PlaceSettlementPiece_ThreeRemaining_TwoRemaining() {
        Player player = new Player(PlayerColor.BLUE);

        player.placeSettlementPiece();

        assertEquals(2, player.getSettlementsRemaining());
    }

    @Test
    public void PlaceSettlementPiece_OneRemaining_NoneRemaining() {
        Player player = new Player(PlayerColor.BLUE);
        player.placeSettlementPiece();
        player.placeSettlementPiece();
        player.placeSettlementPiece();

        assertEquals(0, player.getSettlementsRemaining());
    }

    @Test
    public void PlaceSettlementPiece_NoneRemaining_ThrowsIllegalStateException() {
        Player player = new Player(PlayerColor.BLUE);
        player.placeSettlementPiece();
        player.placeSettlementPiece();
        player.placeSettlementPiece();

        IllegalStateException exception = assertThrows(IllegalStateException.class, player::placeSettlementPiece);

        assertEquals("Player BLUE has no settlements left to place (max 5)", exception.getMessage());
        assertEquals(0, player.getSettlementsRemaining());
    }

    @Test
    public void PlaceSettlementPiece_NoneRemainingThenOneUpgraded_NoneRemainingAgain() {
        Player player = new Player(PlayerColor.BLUE);
        player.placeSettlementPiece();
        player.placeSettlementPiece();
        player.placeSettlementPiece();
        player.placeCityPiece();

        player.placeSettlementPiece();

        assertEquals(0, player.getSettlementsRemaining());
    }

    @Test
    public void PlaceCityPiece_NewPlayer_OneCityUsedOneSettlementReturned() {
        Player player = new Player(PlayerColor.BLUE);

        player.placeCityPiece();

        assertEquals(3, player.getCitiesRemaining());
        assertEquals(4, player.getSettlementsRemaining());
    }

    @Test
    public void PlaceCityPiece_OneCityRemaining_NoCitiesRemaining() {
        Player player = new Player(PlayerColor.BLUE);
        player.placeSettlementPiece();
        player.placeSettlementPiece();
        player.placeCityPiece();
        player.placeCityPiece();
        player.placeCityPiece();

        player.placeCityPiece();

        assertEquals(0, player.getCitiesRemaining());
    }

    @Test
    public void PlaceCityPiece_NoCitiesRemaining_ThrowsIllegalStateException() {
        Player player = new Player(PlayerColor.BLUE);
        player.placeSettlementPiece();
        player.placeSettlementPiece();
        player.placeSettlementPiece();
        player.placeCityPiece();
        player.placeCityPiece();
        player.placeCityPiece();
        player.placeCityPiece();

        IllegalStateException exception = assertThrows(IllegalStateException.class, player::placeCityPiece);

        assertEquals("Player BLUE has no cities left to place (max 4)", exception.getMessage());
        assertEquals(0, player.getCitiesRemaining());
        assertEquals(4, player.getSettlementsRemaining());
    }

    @Test
    public void PlaceCityPiece_NoSettlementsPlaced_ThrowsIllegalStateException() {
        Player player = new Player(PlayerColor.BLUE);
        player.placeCityPiece();
        player.placeCityPiece();

        IllegalStateException exception = assertThrows(IllegalStateException.class, player::placeCityPiece);

        assertEquals("Player BLUE has no settlement to upgrade to a city", exception.getMessage());
        assertEquals(2, player.getCitiesRemaining());
        assertEquals(5, player.getSettlementsRemaining());
    }

    @Test
    public void PlaceRoadPiece_ThirteenRemaining_TwelveRemaining() {
        Player player = new Player(PlayerColor.BLUE);

        player.placeRoadPiece();

        assertEquals(12, player.getRoadsRemaining());
    }

    @Test
    public void PlaceRoadPiece_OneRemaining_NoneRemaining() {
        Player player = new Player(PlayerColor.BLUE);
        for (int roadsPlaced = 2; roadsPlaced < 14; roadsPlaced++) {
            player.placeRoadPiece();
        }

        player.placeRoadPiece();

        assertEquals(0, player.getRoadsRemaining());
    }

    @Test
    public void PlaceRoadPiece_NoneRemaining_ThrowsIllegalStateException() {
        Player player = new Player(PlayerColor.BLUE);
        for (int roadsPlaced = 2; roadsPlaced < 15; roadsPlaced++) {
            player.placeRoadPiece();
        }

        IllegalStateException exception = assertThrows(IllegalStateException.class, player::placeRoadPiece);

        assertEquals("Player BLUE has no roads left to place (max 15)", exception.getMessage());
        assertEquals(0, player.getRoadsRemaining());
    }

    @Test
    public void GetVictoryPointCount_NewPlayer_ReturnsTwo() {
        Player player = new Player(PlayerColor.BLUE);

        int victoryPoints = player.getVictoryPointCount();

        assertEquals(2, victoryPoints);
    }

    @Test
    public void GetVictoryPointCount_OneSettlementOneCity_ReturnsThree() {
        Player player = new Player(PlayerColor.BLUE);
        player.placeCityPiece();

        int victoryPoints = player.getVictoryPointCount();

        assertEquals(3, victoryPoints);
    }

    @Test
    public void GetVictoryPointCount_NoSettlementsFourCities_ReturnsEight() {
        Player player = new Player(PlayerColor.BLUE);
        player.placeSettlementPiece();
        player.placeSettlementPiece();
        player.placeCityPiece();
        player.placeCityPiece();
        player.placeCityPiece();
        player.placeCityPiece();

        int victoryPoints = player.getVictoryPointCount();

        assertEquals(8, victoryPoints);
    }

    @Test
    public void CollectResource_NewPlayerOneBrick_OneBrickInHand() {
        Player player = new Player(PlayerColor.BLUE);

        player.collectResource(ResourceCardType.BRICK, 1);

        assertEquals(1, player.getResourceCount(ResourceCardType.BRICK));
        assertEquals(1, player.getTotalResourceCount());
    }

    @Test
    public void CollectResource_OneBrickCollectTwo_ThreeBricks() {
        Player player = new Player(PlayerColor.BLUE);
        player.collectResource(ResourceCardType.BRICK, 1);

        player.collectResource(ResourceCardType.BRICK, 2);

        assertEquals(3, player.getResourceCount(ResourceCardType.BRICK));
    }

    @Test
    public void CollectResource_ZeroAmount_ThrowsIllegalArgumentException() {
        Player player = new Player(PlayerColor.BLUE);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> player.collectResource(ResourceCardType.BRICK, 0));

        assertEquals("Resource amount must be at least 1 (was 0)", exception.getMessage());
        assertEquals(0, player.getResourceCount(ResourceCardType.BRICK));
    }

    @Test
    public void CollectResource_OneBrickCollectOneOre_BrickUnchanged() {
        Player player = new Player(PlayerColor.BLUE);
        player.collectResource(ResourceCardType.BRICK, 1);

        player.collectResource(ResourceCardType.ORE, 1);

        assertEquals(1, player.getResourceCount(ResourceCardType.BRICK));
        assertEquals(1, player.getResourceCount(ResourceCardType.ORE));
    }

    @Test
    public void CollectResource_NullType_ThrowsIllegalArgumentException() {
        Player player = new Player(PlayerColor.BLUE);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> player.collectResource(null, 1));

        assertEquals("Resource type is required", exception.getMessage());
    }

    @Test
    public void UseResource_ThreeBricksUseOne_TwoBricks() {
        Player player = new Player(PlayerColor.BLUE);
        player.collectResource(ResourceCardType.BRICK, 3);

        player.useResource(ResourceCardType.BRICK, 1);

        assertEquals(2, player.getResourceCount(ResourceCardType.BRICK));
    }

    @Test
    public void UseResource_TwoBricksUseTwo_NoBricks() {
        Player player = new Player(PlayerColor.BLUE);
        player.collectResource(ResourceCardType.BRICK, 2);

        player.useResource(ResourceCardType.BRICK, 2);

        assertEquals(0, player.getResourceCount(ResourceCardType.BRICK));
    }

    @Test
    public void UseResource_TwoBricksUseThree_ThrowsIllegalStateException() {
        Player player = new Player(PlayerColor.BLUE);
        player.collectResource(ResourceCardType.BRICK, 2);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> player.useResource(ResourceCardType.BRICK, 3));

        assertEquals("Player BLUE has 2 BRICK but tried to use 3", exception.getMessage());
        assertEquals(2, player.getResourceCount(ResourceCardType.BRICK));
    }

    @Test
    public void UseResource_ZeroAmount_ThrowsIllegalArgumentException() {
        Player player = new Player(PlayerColor.BLUE);
        player.collectResource(ResourceCardType.BRICK, 2);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> player.useResource(ResourceCardType.BRICK, 0));

        assertEquals("Resource amount must be at least 1 (was 0)", exception.getMessage());
        assertEquals(2, player.getResourceCount(ResourceCardType.BRICK));
    }

    @Test
    public void UseResource_NullType_ThrowsIllegalArgumentException() {
        Player player = new Player(PlayerColor.BLUE);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> player.useResource(null, 1));

        assertEquals("Resource type is required", exception.getMessage());
    }

    @ParameterizedTest
    @EnumSource(ResourceCardType.class)
    public void GetResourceCount_NewPlayer_ReturnsZero(ResourceCardType type) {
        Player player = new Player(PlayerColor.BLUE);

        int count = player.getResourceCount(type);

        assertEquals(0, count);
    }

    @Test
    public void GetResourceCount_TwoBricksAskForOre_ReturnsZero() {
        Player player = new Player(PlayerColor.BLUE);
        player.collectResource(ResourceCardType.BRICK, 2);

        int count = player.getResourceCount(ResourceCardType.ORE);

        assertEquals(0, count);
    }

    @Test
    public void GetResourceCount_NullType_ThrowsIllegalArgumentException() {
        Player player = new Player(PlayerColor.BLUE);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> player.getResourceCount(null));

        assertEquals("Resource type is required", exception.getMessage());
    }

    @Test
    public void GetTotalResourceCount_NewPlayer_ReturnsZero() {
        Player player = new Player(PlayerColor.BLUE);

        int total = player.getTotalResourceCount();

        assertEquals(0, total);
    }

    @Test
    public void GetTotalResourceCount_TwoBricksOneOfEachOther_ReturnsSix() {
        Player player = new Player(PlayerColor.BLUE);
        for (ResourceCardType type : ResourceCardType.values()) {
            player.collectResource(type, 1);
        }
        player.collectResource(ResourceCardType.BRICK, 1);

        int total = player.getTotalResourceCount();

        assertEquals(6, total);
    }

    @Test
    public void CollectDevCard_NewPlayerOneKnight_OneKnight() {
        Player player = new Player(PlayerColor.BLUE);

        player.collectDevCard(DevCardType.KNIGHT);

        assertEquals(1, player.getDevCardCount(DevCardType.KNIGHT));
    }

    @Test
    public void CollectDevCard_OneKnightCollectAnother_TwoKnights() {
        Player player = new Player(PlayerColor.BLUE);
        player.collectDevCard(DevCardType.KNIGHT);

        player.collectDevCard(DevCardType.KNIGHT);

        assertEquals(2, player.getDevCardCount(DevCardType.KNIGHT));
    }

    @Test
    public void CollectDevCard_NewPlayerOneKnight_MonopolyUnchanged() {
        Player player = new Player(PlayerColor.BLUE);

        player.collectDevCard(DevCardType.KNIGHT);

        assertEquals(0, player.getDevCardCount(DevCardType.MONOPOLY));
    }

    @Test
    public void CollectDevCard_NullType_ThrowsIllegalArgumentException() {
        Player player = new Player(PlayerColor.BLUE);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> player.collectDevCard(null));

        assertEquals("Development card type is required", exception.getMessage());
    }

    @ParameterizedTest
    @EnumSource(DevCardType.class)
    public void GetDevCardCount_NewPlayer_ReturnsZero(DevCardType type) {
        Player player = new Player(PlayerColor.BLUE);

        int count = player.getDevCardCount(type);

        assertEquals(0, count);
    }

    @Test
    public void GetDevCardCount_NullType_ThrowsIllegalArgumentException() {
        Player player = new Player(PlayerColor.BLUE);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> player.getDevCardCount(null));

        assertEquals("Development card type is required", exception.getMessage());
    }

    @Test
    public void UseDevCard_KnightFromEarlierTurn_NoKnightsAndOneKnightPlayed() {
        Player player = new Player(PlayerColor.BLUE);
        player.collectDevCard(DevCardType.KNIGHT);
        player.endTurn();

        player.useDevCard(DevCardType.KNIGHT);

        assertEquals(0, player.getDevCardCount(DevCardType.KNIGHT));
        assertEquals(1, player.getKnightsPlayed());
    }

    @Test
    public void UseDevCard_TwoKnightsFromEarlierTurn_OneKnightLeft() {
        Player player = new Player(PlayerColor.BLUE);
        player.collectDevCard(DevCardType.KNIGHT);
        player.collectDevCard(DevCardType.KNIGHT);
        player.endTurn();

        player.useDevCard(DevCardType.KNIGHT);

        assertEquals(1, player.getDevCardCount(DevCardType.KNIGHT));
    }

    @Test
    public void UseDevCard_KnightBoughtThisTurn_ThrowsIllegalStateException() {
        Player player = new Player(PlayerColor.BLUE);
        player.collectDevCard(DevCardType.KNIGHT);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> player.useDevCard(DevCardType.KNIGHT));

        assertEquals("Player BLUE cannot play a KNIGHT on the turn it was bought", exception.getMessage());
        assertEquals(1, player.getDevCardCount(DevCardType.KNIGHT));
    }
}
