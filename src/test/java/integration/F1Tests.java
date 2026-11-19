package integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import domain.GameSession;
import domain.PlayerColor;
import domain.ResourceCardType;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

// Step definitions for F1.feature (F1: set up a new game). Uses the real classes, no mocks.
public class F1Tests {
    private GameSession game;
    private IllegalArgumentException startError;

    @When("a new game is started for {int} players")
    public void aNewGameIsStartedFor(int players) {
        try {
            game = new GameSession(players, new Random());
        } catch (IllegalArgumentException error) {
            startError = error;
        }
    }

    @Then("^the colors to pick from are (.+)$")
    public void theColorsToPickFromAre(String colors) {
        assertNull(startError);
        assertEquals(parseColors(colors), game.getAvailableColors());
    }

    @Then("the bank has {int} of every resource")
    public void theBankHasOfEveryResource(int count) {
        for (ResourceCardType type : ResourceCardType.values()) {
            assertEquals(count, game.getBankResourceCount(type));
        }
    }

    @Then("the game is rejected with the message {string}")
    public void theGameIsRejectedWithTheMessage(String message) {
        assertNotNull(startError);
        assertEquals(message, startError.getMessage());
    }

    static List<PlayerColor> parseColors(String colors) {
        return Arrays.stream(colors.split(","))
                .map(String::trim)
                .map(PlayerColor::valueOf)
                .collect(Collectors.toList());
    }
}
