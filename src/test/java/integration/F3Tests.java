package integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import domain.Dice;
import domain.DiceRoll;
import domain.GameSession;
import domain.PlayerColor;
import domain.ResourceCardType;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import java.util.Random;

public class F3Tests {
    private static final int SIDES = 6;
    private static final Random SEED_PROBE = new Random();

    private GameSession game;
    private DiceRoll roll;
    private IllegalStateException actionError;

    @Given("^a 3-player game with turn order (.+) where the dice will roll ([1-6]) and ([1-6])$")
    public void aThreePlayerGameWhereTheDiceWillRoll(String colors, int firstDie, int secondDie) {
        game = new GameSession(3, new Random(), new Dice(randomRolling(firstDie, secondDie)));
        game.rollForStartingOrder();
        for (PlayerColor color : ColorList.parse(colors)) {
            game.pickColor(color);
        }
    }

    @When("the current player rolls the dice")
    public void theCurrentPlayerRollsTheDice() {
        roll = game.rollDice();
    }

    @When("the current player ends the turn")
    public void theCurrentPlayerEndsTheTurn() {
        game.endTurn();
    }

    @When("the current player tries to end the turn")
    public void theCurrentPlayerTriesToEndTheTurn() {
        try {
            game.endTurn();
        } catch (IllegalStateException error) {
            actionError = error;
        }
    }

    @When("the current player tries to roll again")
    public void theCurrentPlayerTriesToRollAgain() {
        try {
            game.rollDice();
        } catch (IllegalStateException error) {
            actionError = error;
        }
    }

    @Then("the dice show {int} and {int} for a total of {int}")
    public void theDiceShow(int firstDie, int secondDie, int total) {
        assertEquals(firstDie, roll.getFirstDie());
        assertEquals(secondDie, roll.getSecondDie());
        assertEquals(total, roll.getTotal());
    }

    @Then("the players now hold these resources:")
    public void thePlayersNowHoldTheseResources(DataTable table) {
        for (Map<String, String> row : table.asMaps()) {
            PlayerColor color = PlayerColor.valueOf(row.get("color"));
            for (ResourceCardType type : ResourceCardType.values()) {
                assertEquals(Integer.parseInt(row.get(type.name())), game.getResourceCount(color, type),
                        color + " " + type);
            }
        }
    }

    @Then("the bank now holds these resources:")
    public void theBankNowHoldsTheseResources(DataTable table) {
        Map<String, String> counts = table.asMaps().get(0);
        for (ResourceCardType type : ResourceCardType.values()) {
            assertEquals(Integer.parseInt(counts.get(type.name())), game.getBankResourceCount(type), type.name());
        }
    }

    @Then("the turn passes to {word}")
    public void theTurnPassesTo(String color) {
        assertEquals(PlayerColor.valueOf(color), game.getCurrentColor());
    }

    @Then("the action is rejected with the message {string}")
    public void theActionIsRejectedWithTheMessage(String message) {
        assertNotNull(actionError);
        assertEquals(message, actionError.getMessage());
    }

    // A real Random with a seed whose first two dice are the ones asked for, so no class is mocked.
    private static Random randomRolling(int firstDie, int secondDie) {
        for (long seed = 0; ; seed++) {
            SEED_PROBE.setSeed(seed);
            if (SEED_PROBE.nextInt(SIDES) + 1 == firstDie && SEED_PROBE.nextInt(SIDES) + 1 == secondDie) {
                return new Random(seed);
            }
        }
    }
}
