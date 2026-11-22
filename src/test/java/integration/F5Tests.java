package integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import domain.BuildingType;
import domain.GameSession;
import domain.Node;
import domain.PlayerColor;
import domain.ResourceCardType;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class F5Tests {
    private GameSession game;
    private IllegalStateException buildError;

    @Given("^a 3-player game with turn order (.+) and dice that roll ([1-6](?:, [1-6])*)$")
    public void aThreePlayerGameWithDiceThatRoll(String colors, String dice) {
        int[] faces = Arrays.stream(dice.split(", ")).mapToInt(Integer::parseInt).toArray();
        game = new GameSession(3, new Random(), SeededDice.rolling(faces));
        game.rollForStartingOrder();
        for (PlayerColor color : ColorList.parse(colors)) {
            game.pickColor(color);
        }
    }

    @When("{word} rolls the dice")
    public void rollsTheDice(String color) {
        assertEquals(PlayerColor.valueOf(color), game.getCurrentColor());
        game.rollDice();
    }

    @When("{word} ends the turn")
    public void endsTheTurn(String color) {
        assertEquals(PlayerColor.valueOf(color), game.getCurrentColor());
        game.endTurn();
    }

    @When("{word} builds a road on edge {int}")
    public void buildsARoad(String color, int edgeId) {
        assertEquals(PlayerColor.valueOf(color), game.getCurrentColor());
        game.buildRoad(edgeId);
    }

    @When("{word} builds a settlement on corner {int}")
    public void buildsASettlement(String color, int nodeId) {
        assertEquals(PlayerColor.valueOf(color), game.getCurrentColor());
        game.buildSettlement(nodeId);
    }

    @When("{word} builds a city on corner {int}")
    public void buildsACity(String color, int nodeId) {
        assertEquals(PlayerColor.valueOf(color), game.getCurrentColor());
        game.buildCity(nodeId);
    }

    @When("{word} tries to build a road on edge {int}")
    public void triesToBuildARoad(String color, int edgeId) {
        assertEquals(PlayerColor.valueOf(color), game.getCurrentColor());
        try {
            game.buildRoad(edgeId);
        } catch (IllegalStateException error) {
            buildError = error;
        }
    }

    @Then("edge {int} has a {word} road")
    public void edgeHasARoad(int edgeId, String color) {
        assertEquals(Optional.of(PlayerColor.valueOf(color)), game.getBoard().getEdges().get(edgeId).getRoadColor());
    }

    @Then("edge {int} has no road")
    public void edgeHasNoRoad(int edgeId) {
        assertEquals(Optional.empty(), game.getBoard().getEdges().get(edgeId).getRoadColor());
    }

    @Then("corner {int} has a {word} {word}")
    public void cornerHasABuilding(int nodeId, String color, String building) {
        Node corner = game.getBoard().getNodes().get(nodeId);
        assertEquals(Optional.of(PlayerColor.valueOf(color)), corner.getBuildingColor());
        assertEquals(BuildingType.valueOf(building), corner.getBuildingType());
    }

    @Then("{word} has {int} victory points")
    public void hasVictoryPoints(String color, int points) {
        assertEquals(points, game.getVictoryPointCount(PlayerColor.valueOf(color)));
    }

    @Then("{word} holds these resources:")
    public void holdsTheseResources(String color, DataTable table) {
        Map<String, String> counts = table.asMaps().get(0);
        for (ResourceCardType type : ResourceCardType.values()) {
            assertEquals(Integer.parseInt(counts.get(type.name())),
                    game.getResourceCount(PlayerColor.valueOf(color), type), type.name());
        }
    }

    @Then("the bank has {int} {word} and {int} {word}")
    public void theBankHas(int firstCount, String firstType, int secondCount, String secondType) {
        assertEquals(firstCount, game.getBankResourceCount(ResourceCardType.valueOf(firstType)));
        assertEquals(secondCount, game.getBankResourceCount(ResourceCardType.valueOf(secondType)));
    }

    @Then("the build is rejected with the message {string}")
    public void theBuildIsRejectedWithTheMessage(String message) {
        assertNotNull(buildError);
        assertEquals(message, buildError.getMessage());
    }
}
