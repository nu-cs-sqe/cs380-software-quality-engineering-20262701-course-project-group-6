package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class StartingRollOffTest {

    @Test
    public void Constructor_NullRandom_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new StartingRollOff(null));

        assertEquals("Random is required to roll for starting order", exception.getMessage());
    }
}
