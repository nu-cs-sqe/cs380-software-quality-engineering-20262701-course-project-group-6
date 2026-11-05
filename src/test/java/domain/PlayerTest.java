package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class PlayerTest {

    // TC1
    @Test
    public void Constructor_BlueColor_StartsWithBeginnerPieces() {
        Player player = new Player(PlayerColor.BLUE);

        assertEquals(PlayerColor.BLUE, player.getColor());
        assertEquals(2, player.getSettlementsPlaced());
        assertEquals(2, player.getRoadsPlaced());
    }

    // TC2
    @Test
    public void Constructor_NullColor_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Player(null));

        assertEquals("Player color is required", exception.getMessage());
    }

    // TC3
    @Test
    public void PlaceSettlementPiece_TwoPlaced_SettlementsPlacedIsThree() {
        Player player = new Player(PlayerColor.BLUE);

        player.placeSettlementPiece();

        assertEquals(3, player.getSettlementsPlaced());
    }

    // TC4
    @Test
    public void PlaceSettlementPiece_FourPlaced_SettlementsPlacedIsFive() {
        Player player = new Player(PlayerColor.BLUE);
        player.placeSettlementPiece();
        player.placeSettlementPiece();
        player.placeSettlementPiece();

        assertEquals(5, player.getSettlementsPlaced());
    }

    // TC5
    @Test
    public void PlaceSettlementPiece_FivePlaced_ThrowsIllegalStateException() {
        Player player = new Player(PlayerColor.BLUE);
        player.placeSettlementPiece();
        player.placeSettlementPiece();
        player.placeSettlementPiece();

        IllegalStateException exception = assertThrows(IllegalStateException.class, player::placeSettlementPiece);

        assertEquals("Player BLUE has no settlements left to place (max 5)", exception.getMessage());
        assertEquals(5, player.getSettlementsPlaced());
    }

    // TC7
    @Test
    public void PlaceCityPiece_TwoSettlementsNoCities_OneCityOneSettlement() {
        Player player = new Player(PlayerColor.BLUE);

        player.placeCityPiece();

        assertEquals(1, player.getCitiesPlaced());
        assertEquals(1, player.getSettlementsPlaced());
    }
}
