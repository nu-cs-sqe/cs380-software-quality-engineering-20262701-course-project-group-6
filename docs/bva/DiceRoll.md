# BVA: `DiceRoll`

The result of rolling two dice: both faces and their total. `Dice.roll()` returns one so the UI can show both dice
(team decision 11/16, from Ryan's review on #29). It is immutable, so a roll cannot be changed after it happens.

Two six-sided dice are not one twelve-sided die: a total of 1 is impossible and 7 is the most common total, which is
why the game rolls two dice and keeps both faces.

## Step 1: Inputs and outputs

| | |
|---|---|
| **Inputs** | `firstDie`, `secondDie` (constructor) |
| **State** | none beyond the two faces |
| **Outputs** | each face, the total, or an exception |

## Steps 2–3: Catalog types and boundary values

### Intervals

| Input | Interval | LOW − 1 | LOW | HIGH | HIGH + 1 |
|---|---|---|---|---|---|
| `firstDie` | [1, 6] | 0 → TC2 (throws) | 1 → TC6 | 6 → TC7 | 7 → TC3 (throws) |
| `secondDie` | [1, 6] | 0 → TC4 (throws) | 1 → TC6 | 6 → TC7 | 7 → TC5 (throws) |
| total | [2, 12] | 1: can't set (each face is at least 1) | 2 → TC6 | 12 → TC7 | 13: can't set (each face is at most 6) |

### Cases

| State | Values tested | Why |
|---|---|---|
| two different faces | 4 and 3 → TC1 | each getter returns its own face, and the total adds both |

## Step 4: Test cases

### Methods under test: `DiceRoll(int firstDie, int secondDie)`, `getFirstDie()`, `getSecondDie()`, `getTotal()`

|     | State of the System | Expected output | Implemented? |
|-----|---------------------|-----------------|--------------|
| TC1 | `firstDie` = 4, `secondDie` = 3 | first die 4, second die 3, total 7 | :white_check_mark: |
| TC2 | `firstDie` = 0 | `IllegalArgumentException` with a message saying a die must be between 1 and 6 (was 0) | :white_check_mark: |
| TC3 | `firstDie` = 7 | `IllegalArgumentException` with a message saying a die must be between 1 and 6 (was 7) | :x: |
| TC4 | `secondDie` = 0 | `IllegalArgumentException` with a message saying a die must be between 1 and 6 (was 0) | :x: |
| TC5 | `secondDie` = 7 | `IllegalArgumentException` with a message saying a die must be between 1 and 6 (was 7) | :x: |
| TC6 | `firstDie` = 1, `secondDie` = 1 | total 2 | :x: |
| TC7 | `firstDie` = 6, `secondDie` = 6 | total 12 | :x: |
