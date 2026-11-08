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

    private static Bank newBank() {
        return new Bank(new Random());
    }
}
