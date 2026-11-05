package domain;

public class Player {
    private static final int STARTING_SETTLEMENTS = 2;
    private static final int STARTING_ROADS = 2;
    private static final int MAX_SETTLEMENTS = 5;
    private static final int MAX_CITIES = 4;
    private static final int MAX_ROADS = 15;
    private static final int CITY_VICTORY_POINTS = 2;

    private final PlayerColor color;
    private int settlementsPlaced;
    private int citiesPlaced;
    private int roadsPlaced;

    public Player(PlayerColor color) {
        if (color == null) {
            throw new IllegalArgumentException("Player color is required");
        }
        this.color = color;
        this.settlementsPlaced = STARTING_SETTLEMENTS;
        this.roadsPlaced = STARTING_ROADS;
    }

    public void placeSettlementPiece() {
        if (settlementsPlaced == MAX_SETTLEMENTS) {
            throw new IllegalStateException(
                    "Player " + color + " has no settlements left to place (max " + MAX_SETTLEMENTS + ")");
        }
        settlementsPlaced++;
    }

    public void placeCityPiece() {
        if (citiesPlaced == MAX_CITIES) {
            throw new IllegalStateException(
                    "Player " + color + " has no cities left to place (max " + MAX_CITIES + ")");
        }
        if (settlementsPlaced == 0) {
            throw new IllegalStateException("Player " + color + " has no settlement to upgrade to a city");
        }
        citiesPlaced++;
        settlementsPlaced--;
    }

    public void placeRoadPiece() {
        if (roadsPlaced == MAX_ROADS) {
            throw new IllegalStateException(
                    "Player " + color + " has no roads left to place (max " + MAX_ROADS + ")");
        }
        roadsPlaced++;
    }

    public int getVictoryPointCount() {
        return settlementsPlaced + citiesPlaced * CITY_VICTORY_POINTS;
    }

    public PlayerColor getColor() {
        return color;
    }

    public int getSettlementsRemaining() {
        return MAX_SETTLEMENTS - settlementsPlaced;
    }

    public int getCitiesRemaining() {
        return MAX_CITIES - citiesPlaced;
    }

    public int getRoadsRemaining() {
        return MAX_ROADS - roadsPlaced;
    }
}
