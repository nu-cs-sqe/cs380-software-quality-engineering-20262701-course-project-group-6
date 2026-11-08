package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

public class DevelopmentDeckTest {

    @Test
    public void Constructor_NewDeck_TwentyFiveCardsRemaining() {
        DevelopmentDeck deck = new DevelopmentDeck(new Random());

        int remaining = deck.getRemainingCount();

        assertEquals(25, remaining);
    }

    @Test
    public void Constructor_DrawAllCards_HasRulebookMix() {
        DevelopmentDeck deck = new DevelopmentDeck(new Random());

        Map<DevCardType, Integer> drawn = new EnumMap<>(DevCardType.class);
        for (int cardsDrawn = 0; cardsDrawn < 25; cardsDrawn++) {
            drawn.merge(deck.draw(), 1, Integer::sum);
        }

        assertEquals(Map.of(
                DevCardType.KNIGHT, 14,
                DevCardType.VICTORY_POINT, 5,
                DevCardType.ROAD_BUILDING, 2,
                DevCardType.YEAR_OF_PLENTY, 2,
                DevCardType.MONOPOLY, 2), drawn);
    }

    @Test
    public void Constructor_NullRandom_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new DevelopmentDeck(null));

        assertEquals("Random is required to shuffle the development deck", exception.getMessage());
    }

    @Test
    public void Constructor_RandomAlwaysPicksFirstCard_KnightOnTop() {
        Random alwaysFirst = EasyMock.createMock(Random.class);
        EasyMock.expect(alwaysFirst.nextInt(EasyMock.anyInt())).andStubReturn(0);
        EasyMock.replay(alwaysFirst);
        DevelopmentDeck deck = new DevelopmentDeck(alwaysFirst);

        DevCardType top = deck.draw();

        assertEquals(DevCardType.KNIGHT, top);
    }
}
