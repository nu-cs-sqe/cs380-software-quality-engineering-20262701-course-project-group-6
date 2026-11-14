package domain;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class GameSession {
    private static final int MIN_PLAYERS = 3;
    private static final int MAX_PLAYERS = 4;

    private final Bank bank;
    private final BeginnerBoard board;
    private final StartingRollOff rollOff;
    private final int playerCount;
    private List<Integer> pickOrder = List.of();

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
        board = playerCount == MIN_PLAYERS ? BeginnerBoard.createForThreePlayers() : new BeginnerBoard();
    }

    public List<Integer> rollForStartingOrder() {
        if (!pickOrder.isEmpty()) {
            throw new IllegalStateException("The starting order was already rolled");
        }
        pickOrder = rollOff.rollOrder(playerCount);
        return List.copyOf(pickOrder);
    }

    public int getSeatToPick() {
        if (pickOrder.isEmpty()) {
            throw new IllegalStateException("Players are not picking colors");
        }
        return pickOrder.get(0);
    }

    public List<PlayerColor> getAvailableColors() {
        return Arrays.stream(PlayerColor.values())
                .filter(board.getSettlements()::containsValue)
                .collect(Collectors.toUnmodifiableList());
    }

    public int getBankResourceCount(ResourceCardType type) {
        return bank.getResourceCount(type);
    }
}
