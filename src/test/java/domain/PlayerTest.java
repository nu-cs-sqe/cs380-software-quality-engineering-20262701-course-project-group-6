package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

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
}
