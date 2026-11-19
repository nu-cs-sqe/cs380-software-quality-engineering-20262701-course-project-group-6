package domain;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TileSetupTest {
    private static final int SAMPLE_DICE_NUMBER = 10;

    @Test
    void Constructor_ValidResourceTile_SetsResourceAndDiceNumber() {
        TileSetup tileSetup = new TileSetup(ResourceCardType.ORE, SAMPLE_DICE_NUMBER);

        assertEquals(Optional.of(ResourceCardType.ORE), tileSetup.getResource());
        assertEquals(OptionalInt.of(SAMPLE_DICE_NUMBER), tileSetup.getDiceNumber());
    }

    @Test
    void Constructor_NullResource_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new TileSetup(null, SAMPLE_DICE_NUMBER));

        assertEquals("resource cannot be null", exception.getMessage());
    }
}
