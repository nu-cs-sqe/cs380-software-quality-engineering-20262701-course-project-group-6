package domain;

public final class DiceRoll {
    private final int firstDie;
    private final int secondDie;

    public DiceRoll(int firstDie, int secondDie) {
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
}
