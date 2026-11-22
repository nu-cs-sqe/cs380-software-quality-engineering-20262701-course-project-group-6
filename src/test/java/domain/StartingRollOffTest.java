package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Random;
import org.easymock.EasyMock;
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

    @Test
    public void RollOrder_TwoSeatsRollThreeAndEight_SecondSeatFirst() {
        Random dice = diceRolling(3, 8);
        StartingRollOff rollOff = new StartingRollOff(dice);

        List<Integer> order = rollOff.rollOrder(2);

        assertEquals(List.of(2, 1), order);
        EasyMock.verify(dice);
    }

    @Test
    public void RollOrder_ThreeSeatsWithTwelveAndTwo_TwelveFirstTwoLast() {
        Random dice = diceRolling(5, 12, 2);
        StartingRollOff rollOff = new StartingRollOff(dice);

        List<Integer> order = rollOff.rollOrder(3);

        assertEquals(List.of(2, 1, 3), order);
        EasyMock.verify(dice);
    }

    @Test
    public void RollOrder_FourSeatsNoTies_HighestFirst() {
        Random dice = diceRolling(6, 9, 4, 11);
        StartingRollOff rollOff = new StartingRollOff(dice);

        List<Integer> order = rollOff.rollOrder(4);

        assertEquals(List.of(4, 2, 1, 3), order);
        EasyMock.verify(dice);
    }

    @Test
    public void RollOrder_TieForFirst_TiedSeatsReRoll() {
        Random dice = diceRolling(8, 5, 8, 4, 10);
        StartingRollOff rollOff = new StartingRollOff(dice);

        List<Integer> order = rollOff.rollOrder(3);

        assertEquals(List.of(3, 1, 2), order);
        EasyMock.verify(dice);
    }

    @Test
    public void RollOrder_TieInTheMiddle_TiedPairKeepsItsPlace() {
        Random dice = diceRolling(10, 6, 3, 6, 9, 7);
        StartingRollOff rollOff = new StartingRollOff(dice);

        List<Integer> order = rollOff.rollOrder(4);

        assertEquals(List.of(1, 2, 4, 3), order);
        EasyMock.verify(dice);
    }

    @Test
    public void RollOrder_AllSeatsTie_AllReRoll() {
        Random dice = diceRolling(7, 7, 7, 4, 9, 6);
        StartingRollOff rollOff = new StartingRollOff(dice);

        List<Integer> order = rollOff.rollOrder(3);

        assertEquals(List.of(2, 3, 1), order);
        EasyMock.verify(dice);
    }

    @Test
    public void RollOrder_TieTwiceInARow_ReRollsUntilBroken() {
        Random dice = diceRolling(5, 5, 8, 8, 3, 11);
        StartingRollOff rollOff = new StartingRollOff(dice);

        List<Integer> order = rollOff.rollOrder(2);

        assertEquals(List.of(2, 1), order);
        EasyMock.verify(dice);
    }

    @Test
    public void GetRounds_NothingRolled_Empty() {
        StartingRollOff rollOff = new StartingRollOff(new Random());

        assertEquals(List.of(), rollOff.getRounds());
    }

    private static Random diceRolling(int... totals) {
        Random dice = EasyMock.createStrictMock(Random.class);
        for (int total : totals) {
            int firstDie = total / 2;
            EasyMock.expect(dice.nextInt(6)).andReturn(firstDie - 1);
            EasyMock.expect(dice.nextInt(6)).andReturn(total - firstDie - 1);
        }
        EasyMock.replay(dice);
        return dice;
    }
}
