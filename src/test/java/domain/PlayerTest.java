package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class PlayerTest {

    @Test
    public void Constructor_BlueColor_StartsWithBeginnerPieces() {
        Player player = new Player(PlayerColor.BLUE);

        assertEquals(PlayerColor.BLUE, player.getColor());
        assertEquals(2, player.getSettlementsPlaced());
        assertEquals(2, player.getRoadsPlaced());
    }

    @Test
    public void Constructor_NullColor_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Player(null));

        assertEquals("Player color is required", exception.getMessage());
    }

    @Test
    public void PlaceSettlementPiece_TwoPlaced_SettlementsPlacedIsThree() {
        Player player = new Player(PlayerColor.BLUE);

        player.placeSettlementPiece();

        assertEquals(3, player.getSettlementsPlaced());
    }

    @Test
    public void PlaceSettlementPiece_FourPlaced_SettlementsPlacedIsFive() {
        Player player = new Player(PlayerColor.BLUE);
        player.placeSettlementPiece();
        player.placeSettlementPiece();
        player.placeSettlementPiece();

        assertEquals(5, player.getSettlementsPlaced());
    }

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

    @Test
    public void PlaceCityPiece_TwoSettlementsNoCities_OneCityOneSettlement() {
        Player player = new Player(PlayerColor.BLUE);

        player.placeCityPiece();

        assertEquals(1, player.getCitiesPlaced());
        assertEquals(1, player.getSettlementsPlaced());
    }
}
