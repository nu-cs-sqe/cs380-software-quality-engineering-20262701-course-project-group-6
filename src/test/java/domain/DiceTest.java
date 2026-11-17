package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

public class DiceTest {

    @Test
    public void Constructor_NullRandom_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Dice(null));

        assertEquals("Random is required to roll dice", exception.getMessage());
    }

    @Test
    public void Roll_BothDiceShowOne_OnesTotalTwo() {
        Random random = randomReturning(0, 0);
        Dice dice = new Dice(random);

        DiceRoll roll = dice.roll();

        assertEquals(1, roll.getFirstDie());
        assertEquals(1, roll.getSecondDie());
        assertEquals(2, roll.getTotal());
        EasyMock.verify(random);
    }

    @Test
    public void Roll_BothDiceShowSix_SixesTotalTwelve() {
        Random random = randomReturning(5, 5);
        Dice dice = new Dice(random);

        DiceRoll roll = dice.roll();

        assertEquals(6, roll.getFirstDie());
        assertEquals(6, roll.getSecondDie());
        assertEquals(12, roll.getTotal());
        EasyMock.verify(random);
    }

    @Test
    public void Roll_TwoThenFive_FacesInOrderTotalSeven() {
        Random random = randomReturning(1, 4);
        Dice dice = new Dice(random);

        DiceRoll roll = dice.roll();

        assertEquals(2, roll.getFirstDie());
        assertEquals(5, roll.getSecondDie());
        assertEquals(7, roll.getTotal());
        EasyMock.verify(random);
    }

    private static Random randomReturning(int firstDie, int secondDie) {
        Random random = EasyMock.createStrictMock(Random.class);
        EasyMock.expect(random.nextInt(6)).andReturn(firstDie);
        EasyMock.expect(random.nextInt(6)).andReturn(secondDie);
        EasyMock.replay(random);
        return random;
    }
}
