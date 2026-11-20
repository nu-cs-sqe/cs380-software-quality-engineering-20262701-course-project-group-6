package domain;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class GameSession {
    private static final int MIN_PLAYERS = 3;
    private static final int MAX_PLAYERS = 4;

    private final Bank bank;
    private final GameBoard board;
    private final StartingRollOff rollOff;
    private final int playerCount;
    private List<Integer> pickOrder = List.of();
    private final Map<PlayerColor, Player> players = new LinkedHashMap<>();

    public GameSession(int playerCount, Random random) {
        if (playerCount < MIN_PLAYERS || playerCount > MAX_PLAYERS) {
            throw new IllegalArgumentException(
                    "A game needs " + MIN_PLAYERS + " or " + MAX_PLAYERS + " players (was " + playerCount + ")");
        }
        if (random == null) {
            throw new IllegalArgumentException("Random is required to start a game");
        }
        bank = new Bank(random);
        rollOff = new StartingRollOff(random);
        this.playerCount = playerCount;
        BeginnerPlacements placements = playerCount == MIN_PLAYERS
                ? BeginnerPlacements.forThreePlayers()
                : BeginnerPlacements.forFourPlayers();
        board = new GameBoard();
        board.generateBoard(new BeginnerLayout(), placements);
    }

    public List<Integer> rollForStartingOrder() {
        if (!pickOrder.isEmpty()) {
            throw new IllegalStateException("The starting order was already rolled");
        }
        pickOrder = rollOff.rollOrder(playerCount);
        return List.copyOf(pickOrder);
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
        if (players.size() < playerCount) {
            throw new IllegalStateException("Players are still picking colors");
        }
        return getTurnOrder().get(0);
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

    public int getVictoryPointCount(PlayerColor color) {
        return playerFor(color).getVictoryPointCount();
    }

    public int getBankResourceCount(ResourceCardType type) {
        return bank.getResourceCount(type);
    }

    private boolean isColorInPlay(PlayerColor color) {
        return board.getNodes().stream()
                .map(Node::getBuildingColor)
                .anyMatch(optionalColor -> optionalColor.equals(java.util.Optional.of(color)));
    }

    private void payStartingResources(Player player, PlayerColor color) {
        for (ResourceCardType type : board.getStartingResources(color)) {
            bank.withdrawResource(type, 1);
            player.collectResource(type, 1);
        }
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

    private void requirePicking() {
        if (pickOrder.isEmpty() || players.size() == playerCount) {
            throw new IllegalStateException("Players are not picking colors");
        }
    }
}