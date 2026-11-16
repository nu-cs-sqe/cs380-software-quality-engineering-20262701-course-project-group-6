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

    @Test
    public void Constructor_FirstDieZero_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new DiceRoll(0, 3));

        assertEquals("A die must be between 1 and 6 (was 0)", exception.getMessage());
    }

    @Test
    public void Constructor_FirstDieSeven_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new DiceRoll(7, 3));

        assertEquals("A die must be between 1 and 6 (was 7)", exception.getMessage());
    }

    @Test
    public void Constructor_SecondDieZero_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new DiceRoll(4, 0));

        assertEquals("A die must be between 1 and 6 (was 0)", exception.getMessage());
    }

    @Test
    public void Constructor_SecondDieSeven_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new DiceRoll(4, 7));

        assertEquals("A die must be between 1 and 6 (was 7)", exception.getMessage());
    }

    @Test
    public void GetTotal_TwoOnes_ReturnsTwo() {
        DiceRoll roll = new DiceRoll(1, 1);

        int total = roll.getTotal();

        assertEquals(2, total);
    }
}
