# BVA: `StartingRollOff`

Story 2 (house rule): every seat rolls 2 dice, the highest roll picks a color first, then the next highest, and so on.
Seats that tie roll again among themselves until the tie is broken, and the tied group keeps its place relative to
everyone else (for example, two seats tied for second place re-roll to decide second and third).

Seats are numbered 1 to `seatCount`. The dice come from `Dice` (each die is `random.nextInt(6) + 1`).
Seats roll in seat order (die 1, then die 2),
and a tied group re-rolls in seat order too. Tests stub `Random` with EasyMock so every roll is known, and expect
`nextInt(6)` exactly, so the number of sides is checked too.

## Step 1: Inputs and outputs

| | |
|---|---|
| **Inputs** | `random` (constructor), `seatCount` |
| **State** | the dice rolled (from `random`) |
| **Outputs** | the seats in pick order, or an exception |

## Steps 2–3: Catalog types and boundary values

For whole numbers, the catalog's "LOW − ε" and "HIGH + ε" are LOW − 1 and HIGH + 1. "Can't set" (catalog term) means
the code makes that value impossible to reach, so there is nothing to test.

### Intervals

| Input / state | Interval | LOW − 1 | LOW | HIGH | HIGH + 1 |
|---|---|---|---|---|---|
| one die | [1, 6] | 0: can't set (`nextInt(6) + 1` is at least 1) | 1 → TC4 | 6 → TC4 | 7: can't set (`nextInt(6)` is at most 5) |
| total of 2 dice | [2, 12] | 1: can't set | 2 → TC4 (rolls last) | 12 → TC4 (rolls first) | 13: can't set |

### Counts

| Input | 1 | 2 | >2 | max |
|---|---|---|---|---|
| `seatCount` | TC2 (throws; 1 and below are the same partition) | TC3 | TC4 (3 seats), TC5 (4 seats) | none in this class; `GameSession` allows at most 4 |

### Collections

| State | Empty | One | Many |
|---|---|---|---|
| seats tied with each other | no ties → TC3–TC5 | one tied pair → TC6, TC7 | all seats tied → TC8 |

### Pointers

| Input | Valid value | `null` |
|---|---|---|
| `random` | a stubbed `Random` → TC3–TC9 | TC1 (throws) |

### Cases

| State | Values tested | Why |
|---|---|---|
| where the tie is | top (TC6), middle (TC7), everyone (TC8) | a tied group must keep its place relative to the seats that did not tie |
| how many re-rolls | one (TC6–TC8), two in a row (TC9) | the re-roll repeats until the tie is broken |

## Step 4: Test cases

Rolls are written as each seat's total; the stub returns the matching die values (for example, 8 = 3 + 5).

### Method under test: `StartingRollOff(Random random)`

|     | State of the System | Expected output | Implemented? |
|-----|---------------------|-----------------|--------------|
| TC1 | `random` = `null` | `IllegalArgumentException` with a message saying a `Random` is required | :white_check_mark: |

### Method under test: `rollOrder(int seatCount)`

|     | State of the System | Expected output | Implemented? |
|-----|---------------------|-----------------|--------------|
| TC2 | `seatCount` = 1 | `IllegalArgumentException` with a message saying at least 2 seats are needed (and the value given) | :white_check_mark: |
| TC3 | 2 seats roll 3, 8 | `[2, 1]` | :white_check_mark: |
| TC4 | 3 seats roll 5, 12 (6 + 6), 2 (1 + 1) | `[2, 1, 3]` | :white_check_mark: |
| TC5 | 4 seats roll 6, 9, 4, 11 | `[4, 2, 1, 3]` | :white_check_mark: |
| TC6 | 3 seats roll 8, 5, 8 (seats 1 and 3 tie for first); re-roll: seat 1 → 4, seat 3 → 10 | `[3, 1, 2]` | :white_check_mark: |
| TC7 | 4 seats roll 10, 6, 3, 6 (seats 2 and 4 tie for second); re-roll: seat 2 → 9, seat 4 → 7 | `[1, 2, 4, 3]` | :white_check_mark: |
| TC8 | 3 seats roll 7, 7, 7 (all tie); re-roll: 4, 9, 6 | `[2, 3, 1]` | :white_check_mark: |
| TC9 | 2 seats roll 5, 5; re-roll 8, 8; re-roll 3, 11 | `[2, 1]` | :white_check_mark: |

### Method under test: `getRounds()`

Lets the UI show every seat's dice instead of only the final order (Story 2 use case, step 1: "System asks each player
to roll the dice"). Each round is one set of rolls: the first round is every seat, and each tie adds a round with only
the tied seats. Rounds are listed in the order they were rolled, and seats within a round in seat order.

| State | Catalog type | Values tested |
|---|---|---|
| rounds rolled | Collection | none → TC10; one (no tie) → TC11; two (one tie) → TC12; three (two ties in a row) → TC13 |
| which seats re-roll | Cases | only the tied seats → TC12 |

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC10 | nothing rolled yet | empty list | :white_check_mark: |
| TC11 | 2 seats roll 3 (1 + 2) and 8 (4 + 4) | one round: seat 1 rolled 1 and 2, seat 2 rolled 4 and 4 | :white_check_mark: |
| TC12 | 3 seats roll 8, 5, 8; re-roll: seat 1 → 4, seat 3 → 10 (same as TC6) | two rounds: `{1=8, 2=5, 3=8}`, then `{1=4, 3=10}` | :white_check_mark: |
| TC13 | 2 seats roll 5, 5; re-roll 8, 8; re-roll 3, 11 (same as TC9) | three rounds: `{1=5, 2=5}`, `{1=8, 2=8}`, `{1=3, 2=11}` | :white_check_mark: |
