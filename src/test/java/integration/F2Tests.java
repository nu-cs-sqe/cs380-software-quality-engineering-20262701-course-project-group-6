package integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import domain.GameSession;
import domain.PlayerColor;
import domain.ResourceCardType;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

// Step definitions for F2.feature (F2: choose starting positions). Uses the real classes, no mocks; the dice are real,
// so the steps read the rolled order instead of expecting particular rolls.
public class F2Tests {
    private GameSession game;
    private List<Integer> rollOrder;
    private final List<Integer> seatsThatPicked = new ArrayList<>();
    private IllegalStateException pickError;

    @Given("a {int}-player game where everyone has rolled for starting order")
    public void aGameWhereEveryoneHasRolled(int players) {
        game = new GameSession(players, new Random());
        rollOrder = game.rollForStartingOrder();
    }

    @Given("the first seat has picked {word}")
    public void theFirstSeatHasPicked(String color) {
        pick(PlayerColor.valueOf(color));
    }

    @When("^the seats pick (.+) in roll order$")
    public void theSeatsPickInRollOrder(String colors) {
        for (PlayerColor color : ColorList.parse(colors)) {
            pick(color);
        }
    }

    @When("the next seat tries to pick {word}")
    public void theNextSeatTriesToPick(String color) {
        try {
            pick(PlayerColor.valueOf(color));
        } catch (IllegalStateException error) {
            pickError = error;
        }
    }

    @Then("^the turn order is (.+)$")
    public void theTurnOrderIs(String colors) {
        assertEquals(ColorList.parse(colors), game.getTurnOrder());
        assertEquals(rollOrder, seatsThatPicked);
    }

    @Then("^it is ([A-Z]+)'s turn$")
    public void itIsTurn(String color) {
        assertEquals(PlayerColor.valueOf(color), game.getCurrentColor());
    }

    @Then("the players hold these resources:")
    public void thePlayersHoldTheseResources(DataTable table) {
        for (Map<String, String> row : table.asMaps()) {
            PlayerColor color = PlayerColor.valueOf(row.get("color"));
            for (ResourceCardType type : ResourceCardType.values()) {
                assertEquals(Integer.parseInt(row.get(type.name())), game.getResourceCount(color, type),
                        color + " " + type);
            }
        }
    }

    @Then("the bank holds these resources:")
    public void theBankHoldsTheseResources(DataTable table) {
        Map<String, String> counts = table.asMaps().get(0);
        for (ResourceCardType type : ResourceCardType.values()) {
            assertEquals(Integer.parseInt(counts.get(type.name())), game.getBankResourceCount(type), type.name());
        }
    }

    @Then("every player has {int} victory points")
    public void everyPlayerHasVictoryPoints(int points) {
        for (PlayerColor color : game.getTurnOrder()) {
            assertEquals(points, game.getVictoryPointCount(color), color.name());
        }
    }

    @Then("the next seat to pick is the second seat in the roll order")
    public void theNextSeatToPickIsTheSecondSeat() {
        assertEquals(rollOrder.get(0), seatsThatPicked.get(0));
        assertEquals(rollOrder.get(1), game.getSeatToPick());
    }

    @Then("the pick is rejected with the message {string}")
    public void thePickIsRejectedWithTheMessage(String message) {
        assertNotNull(pickError);
        assertEquals(message, pickError.getMessage());
    }

    @Then("^the colors left to pick are (.+)$")
    public void theColorsLeftToPickAre(String colors) {
        assertEquals(ColorList.parse(colors), game.getAvailableColors());
    }

    private void pick(PlayerColor color) {
        int seat = game.getSeatToPick();
        game.pickColor(color);
        seatsThatPicked.add(seat);
    }
}
