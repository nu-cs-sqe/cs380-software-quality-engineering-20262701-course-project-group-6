package domain;

public class Player {
    private static final int STARTING_SETTLEMENTS = 2;
    private static final int STARTING_ROADS = 2;

    private final PlayerColor color;
    private int settlementsPlaced;
    private int roadsPlaced;

    public Player(PlayerColor color) {
        if (color == null) {
            throw new IllegalArgumentException("Player color is required");
        }
        this.color = color;
        this.settlementsPlaced = STARTING_SETTLEMENTS;
        this.roadsPlaced = STARTING_ROADS;
    }

    public PlayerColor getColor() {
        return color;
    }

    public int getSettlementsPlaced() {
        return settlementsPlaced;
    }

    public int getRoadsPlaced() {
        return roadsPlaced;
    }
}
