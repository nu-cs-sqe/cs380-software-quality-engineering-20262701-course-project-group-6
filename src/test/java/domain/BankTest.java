package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;
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

    private static Bank newBank() {
        return new Bank(new Random());
    }
}
