package domain;

import static domain.ResourceCardType.ORE;
import static domain.ResourceCardType.WOOL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import org.junit.jupiter.api.Test;

class BeginnerLayoutTest {
    private static final int EXPECTED_TILE_COUNT = 19;
    private static final int FIRST_TILE_ID = 0;
    private static final int FIRST_TILE_DICE = 10;
    private static final int DESERT_TILE_ID = 9;
    private static final int LAST_TILE_ID = 18;
    private static final int LAST_TILE_DICE = 11;

    @Test
    void GetTiles_DefaultBeginnerLayout_ReturnsAllNineteenTilesIncludingDesert() {
        BeginnerLayout layout = new BeginnerLayout();

        Map<Integer, TileSetup> tiles = layout.getTiles();

        assertNotNull(tiles);
        assertEquals(EXPECTED_TILE_COUNT, tiles.size());

        TileSetup firstTile = tiles.get(FIRST_TILE_ID);
        assertNotNull(firstTile);
        assertEquals(Optional.of(ORE), firstTile.getResource());
        assertEquals(OptionalInt.of(FIRST_TILE_DICE), firstTile.getDiceNumber());

        TileSetup desertTile = tiles.get(DESERT_TILE_ID);
        assertNotNull(desertTile);
        assertTrue(desertTile.getResource().isEmpty());
        assertTrue(desertTile.getDiceNumber().isEmpty());

        TileSetup lastTile = tiles.get(LAST_TILE_ID);
        assertNotNull(lastTile);
        assertEquals(Optional.of(WOOL), lastTile.getResource());
        assertEquals(OptionalInt.of(LAST_TILE_DICE), lastTile.getDiceNumber());
    }
}