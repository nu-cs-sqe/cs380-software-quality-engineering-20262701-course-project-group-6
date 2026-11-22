package domain;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class GameSession {
    private static final int MIN_PLAYERS = 3;
    private static final int MAX_PLAYERS = 4;
    private static final Map<ResourceCardType, Integer> ROAD_COST =
            Map.of(ResourceCardType.BRICK, 1, ResourceCardType.LUMBER, 1);

    private final Bank bank;
    private final GameBoard board;
    private final StartingRollOff rollOff;
    private final Dice dice;
    private final int playerCount;
    private List<Integer> pickOrder = List.of();
    private final Map<PlayerColor, Player> players = new LinkedHashMap<>();
    private int currentTurnIndex;
    private boolean rolledThisTurn;

    public GameSession(int playerCount, Random random) {
        this(playerCount, random, diceFor(random));
    }

    public GameSession(int playerCount, Random random, Dice dice) {
        if (playerCount < MIN_PLAYERS || playerCount > MAX_PLAYERS) {
            throw new IllegalArgumentException(
                    "A game needs " + MIN_PLAYERS + " or " + MAX_PLAYERS + " players (was " + playerCount + ")");
        }
        if (random == null) {
            throw new IllegalArgumentException("Random is required to start a game");
        }
        if (dice == null) {
            throw new IllegalArgumentException("Dice are required to start a game");
        }
        bank = new Bank(random);
        rollOff = new StartingRollOff(random);
        this.dice = dice;
        this.playerCount = playerCount;
        BeginnerPlacements placements = playerCount == MIN_PLAYERS
                ? BeginnerPlacements.forThreePlayers()
                : BeginnerPlacements.forFourPlayers();
        board = new GameBoard(new BeginnerLayout(), placements);
    }

    public List<Integer> rollForStartingOrder() {
        if (!pickOrder.isEmpty()) {
            throw new IllegalStateException("The starting order was already rolled");
        }
        pickOrder = rollOff.rollOrder(playerCount);
        return List.copyOf(pickOrder);
    }

    public List<Map<Integer, DiceRoll>> getStartingRolls() {
        return rollOff.getRounds();
    }

    public int getSeatToPick() {
        requirePicking();
        return pickOrder.get(players.size());
    }

    public void pickColor(PlayerColor color) {
        requireColor(color);
        requirePicking();
        if (!isColorInPlay(color)) {
            throw new IllegalStateException(color + " is not in play");
        }
        if (players.containsKey(color)) {
            throw new IllegalStateException(color + " is already taken");
        }
        Player player = new Player(color);
        payStartingResources(player, color);
        players.put(color, player);
    }

    public List<PlayerColor> getTurnOrder() {
        return List.copyOf(players.keySet());
    }

    public PlayerColor getCurrentColor() {
        requirePlaying();
        return getTurnOrder().get(currentTurnIndex);
    }

    public DiceRoll rollDice() {
        requirePlaying();
        if (rolledThisTurn) {
            throw new IllegalStateException(getCurrentColor() + " already rolled this turn");
        }
        rolledThisTurn = true;
        DiceRoll roll = dice.roll();
        payProduction(roll.getTotal());
        return roll;
    }

    public void endTurn() {
        if (!rolledThisTurn) {
            throw new IllegalStateException(getCurrentColor() + " must roll before ending the turn");
        }
        rolledThisTurn = false;
        currentTurnIndex = (currentTurnIndex + 1) % playerCount;
    }

    public void buildRoad(int edgeId) {
        Player player = playerWhoRolled();
        player.placeRoadPiece();
        pay(player, ROAD_COST);
        board.buildRoad(edgeId, player.getColor());
    }

    public List<PlayerColor> getAvailableColors() {
        return Arrays.stream(PlayerColor.values())
                .filter(this::isColorInPlay)
                .filter(color -> !players.containsKey(color))
                .collect(Collectors.toUnmodifiableList());
    }

    public int getResourceCount(PlayerColor color, ResourceCardType type) {
        return playerFor(color).getResourceCount(type);
    }

    public int getRoadsRemaining(PlayerColor color) {
        return playerFor(color).getRoadsRemaining();
    }

    public int getVictoryPointCount(PlayerColor color) {
        return playerFor(color).getVictoryPointCount();
    }

    public int getBankResourceCount(ResourceCardType type) {
        return bank.getResourceCount(type);
    }

    public GameBoard getBoard() {
        return board;
    }

    private static Dice diceFor(Random random) {
        return random == null ? null : new Dice(random);
    }

    private boolean isColorInPlay(PlayerColor color) {
        return board.getNodes().stream()
                .map(Node::getBuildingColor)
                .anyMatch(optionalColor -> optionalColor.equals(Optional.of(color)));
    }

    private void payStartingResources(Player player, PlayerColor color) {
        for (ResourceCardType type : board.getStartingResources(color)) {
            bank.withdrawResource(type, 1);
            player.collectResource(type, 1);
        }
    }

    private void payProduction(int diceTotal) {
        board.getProduction(diceTotal).forEach((type, owed) -> bank.payProduction(type, owed)
                .forEach((color, amount) -> players.get(color).collectResource(type, amount)));
    }

    private Player playerWhoRolled() {
        if (!rolledThisTurn) {
            throw new IllegalStateException(getCurrentColor() + " must roll before building");
        }
        return players.get(getCurrentColor());
    }

    private void pay(Player player, Map<ResourceCardType, Integer> cost) {
        cost.forEach((type, amount) -> {
            player.useResource(type, amount);
            bank.depositResource(type, amount);
        });
    }

    private Player playerFor(PlayerColor color) {
        requireColor(color);
        Player player = players.get(color);
        if (player == null) {
            throw new IllegalArgumentException("No player has picked " + color);
        }
        return player;
    }

    private static void requireColor(PlayerColor color) {
        if (color == null) {
            throw new IllegalArgumentException("Player color is required");
        }
    }

    private void requirePlaying() {
        if (players.size() < playerCount) {
            throw new IllegalStateException("Players are still picking colors");
        }
    }

    private void requirePicking() {
        if (pickOrder.isEmpty() || players.size() == playerCount) {
            throw new IllegalStateException("Players are not picking colors");
        }
    }
}
