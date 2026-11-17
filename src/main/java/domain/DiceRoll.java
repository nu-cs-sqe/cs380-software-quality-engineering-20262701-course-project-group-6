package domain;

public final class DiceRoll {
    private static final int LOWEST_FACE = 1;
    private static final int HIGHEST_FACE = 6;

    private final int firstDie;
    private final int secondDie;

    public DiceRoll(int firstDie, int secondDie) {
        requireFace(firstDie);
        requireFace(secondDie);
        this.firstDie = firstDie;
        this.secondDie = secondDie;
    }

    public int getFirstDie() {
        return firstDie;
    }

    public int getSecondDie() {
        return secondDie;
    }

    public int getTotal() {
        return firstDie + secondDie;
    }

    private static void requireFace(int face) {
        if (face < LOWEST_FACE || face > HIGHEST_FACE) {
            throw new IllegalArgumentException(
                    "A die must be between " + LOWEST_FACE + " and " + HIGHEST_FACE + " (was " + face + ")");
        }
    }
}
