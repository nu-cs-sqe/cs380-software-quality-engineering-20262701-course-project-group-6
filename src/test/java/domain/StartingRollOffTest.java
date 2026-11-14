package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;
import org.junit.jupiter.api.Test;

public class StartingRollOffTest {

    @Test
    public void Constructor_NullRandom_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new StartingRollOff(null));

        assertEquals("Random is required to roll for starting order", exception.getMessage());
    }

    @Test
    public void RollOrder_OneSeat_ThrowsIllegalArgumentException() {
        StartingRollOff rollOff = new StartingRollOff(new Random());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> rollOff.rollOrder(1));

        assertEquals("At least 2 seats are needed to roll for starting order (was 1)", exception.getMessage());
    }
}
