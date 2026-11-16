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
    public void Roll_BothDiceShowOne_ReturnsTwo() {
        Random random = randomReturning(0, 0);
        Dice dice = new Dice(random);

        int total = dice.roll();

        assertEquals(2, total);
        EasyMock.verify(random);
    }

    @Test
    public void Roll_BothDiceShowSix_ReturnsTwelve() {
        Random random = randomReturning(5, 5);
        Dice dice = new Dice(random);

        int total = dice.roll();

        assertEquals(12, total);
        EasyMock.verify(random);
    }

    @Test
    public void Roll_TwoAndFive_ReturnsSeven() {
        Random random = randomReturning(1, 4);
        Dice dice = new Dice(random);

        int total = dice.roll();

        assertEquals(7, total);
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
