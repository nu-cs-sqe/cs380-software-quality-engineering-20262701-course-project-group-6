package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;
import org.junit.jupiter.api.Test;

public class DevelopmentDeckTest {

    @Test
    public void Constructor_NewDeck_TwentyFiveCardsRemaining() {
        DevelopmentDeck deck = new DevelopmentDeck(new Random());

        int remaining = deck.getRemainingCount();

        assertEquals(25, remaining);
    }
}
