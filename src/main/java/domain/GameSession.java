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

    public GameSession(int playerCount, Random random) {
        if (playerCount < MIN_PLAYERS) {
            throw new IllegalArgumentException(
                    "A game needs " + MIN_PLAYERS + " or " + MAX_PLAYERS + " players (was " + playerCount + ")");
        }
        bank = new Bank(random);
        board = new BeginnerBoard();
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
