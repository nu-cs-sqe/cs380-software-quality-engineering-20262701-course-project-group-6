package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class BankTest {

    @ParameterizedTest
    @EnumSource(ResourceCardType.class)
    public void Constructor_NewBank_NineteenOfEachResourceAndFullDeck(ResourceCardType type) {
        Bank bank = newBank();

        assertEquals(19, bank.getResourceCount(type));
        assertEquals(25, bank.getDevelopmentCardsRemaining());
    }

    @Test
    public void Constructor_NullRandom_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Bank(null));

        assertEquals("Random is required to shuffle the development deck", exception.getMessage());
    }

    @Test
    public void WithdrawResource_NineteenBricksWithdrawOne_EighteenBricks() {
        Bank bank = newBank();

        bank.withdrawResource(ResourceCardType.BRICK, 1);

        assertEquals(18, bank.getResourceCount(ResourceCardType.BRICK));
    }

    @Test
    public void WithdrawResource_NineteenBricksWithdrawNineteen_NoBricks() {
        Bank bank = newBank();

        bank.withdrawResource(ResourceCardType.BRICK, 19);

        assertEquals(0, bank.getResourceCount(ResourceCardType.BRICK));
    }

    @Test
    public void WithdrawResource_NineteenBricksWithdrawTwenty_ThrowsIllegalStateException() {
        Bank bank = newBank();

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> bank.withdrawResource(ResourceCardType.BRICK, 20));

        assertEquals("Bank has 19 BRICK but 20 were requested", exception.getMessage());
        assertEquals(19, bank.getResourceCount(ResourceCardType.BRICK));
    }

    @Test
    public void WithdrawResource_ZeroAmount_ThrowsIllegalArgumentException() {
        Bank bank = newBank();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bank.withdrawResource(ResourceCardType.BRICK, 0));

        assertEquals("Resource amount must be at least 1 (was 0)", exception.getMessage());
        assertEquals(19, bank.getResourceCount(ResourceCardType.BRICK));
    }

    @Test
    public void WithdrawResource_NullType_ThrowsIllegalArgumentException() {
        Bank bank = newBank();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bank.withdrawResource(null, 1));

        assertEquals("Resource type is required", exception.getMessage());
    }

    @Test
    public void WithdrawResource_TwoBricks_OreUnchanged() {
        Bank bank = newBank();

        bank.withdrawResource(ResourceCardType.BRICK, 2);

        assertEquals(19, bank.getResourceCount(ResourceCardType.ORE));
    }

    @Test
    public void DepositResource_SixteenBricksDepositOne_SeventeenBricks() {
        Bank bank = newBank();
        bank.withdrawResource(ResourceCardType.BRICK, 3);

        bank.depositResource(ResourceCardType.BRICK, 1);

        assertEquals(17, bank.getResourceCount(ResourceCardType.BRICK));
    }

    @Test
    public void DepositResource_SeventeenBricksDepositTwo_NineteenBricks() {
        Bank bank = newBank();
        bank.withdrawResource(ResourceCardType.BRICK, 2);

        bank.depositResource(ResourceCardType.BRICK, 2);

        assertEquals(19, bank.getResourceCount(ResourceCardType.BRICK));
    }

    @Test
    public void DepositResource_NineteenBricksDepositOne_ThrowsIllegalStateException() {
        Bank bank = newBank();

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> bank.depositResource(ResourceCardType.BRICK, 1));

        assertEquals("Bank cannot hold more than 19 BRICK (has 19, tried to deposit 1)", exception.getMessage());
        assertEquals(19, bank.getResourceCount(ResourceCardType.BRICK));
    }

    @Test
    public void DepositResource_ZeroAmount_ThrowsIllegalArgumentException() {
        Bank bank = newBank();
        bank.withdrawResource(ResourceCardType.BRICK, 2);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bank.depositResource(ResourceCardType.BRICK, 0));

        assertEquals("Resource amount must be at least 1 (was 0)", exception.getMessage());
        assertEquals(17, bank.getResourceCount(ResourceCardType.BRICK));
    }

    @Test
    public void DepositResource_NullType_ThrowsIllegalArgumentException() {
        Bank bank = newBank();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bank.depositResource(null, 1));

        assertEquals("Resource type is required", exception.getMessage());
    }

    @Test
    public void GetResourceCount_NullType_ThrowsIllegalArgumentException() {
        Bank bank = newBank();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bank.getResourceCount(null));

        assertEquals("Resource type is required", exception.getMessage());
    }

    private static Bank newBank() {
        return new Bank(new Random());
    }
}
