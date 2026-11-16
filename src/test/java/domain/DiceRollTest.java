package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class DiceRollTest {

    @Test
    public void Constructor_FourAndThree_FacesAndTotalOfSeven() {
        DiceRoll roll = new DiceRoll(4, 3);

        assertEquals(4, roll.getFirstDie());
        assertEquals(3, roll.getSecondDie());
        assertEquals(7, roll.getTotal());
    }
}
