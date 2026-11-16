# BVA: `Dice`

Two six-sided dice. `roll()` returns their total. Each die is `random.nextInt(6) + 1`, with the `Random` passed in so
tests can stub it (coding standard: inject random dependencies). `StartingRollOff` uses it now; turn rolls in a later
`GameSession` PR will use it too, where the total matters for payouts and the robber's 7.

Pulled out of `StartingRollOff` because the roll-off only compares totals: changing `+ 1` to `- 1` shifts every total
the same way and never changes the order, so PIT could not kill that mutant there (equivalent mutant). Testing the total
directly kills it.

## Step 1: Inputs and outputs

| | |
|---|---|
| **Inputs** | `random` (constructor) |
| **State** | the next two values `random.nextInt(6)` returns |
| **Outputs** | the total of the two dice, or an exception |

## Steps 2–3: Catalog types and boundary values

### Intervals

| Input / state | Interval | LOW − 1 | LOW | HIGH | HIGH + 1 |
|---|---|---|---|---|---|
| one die | [1, 6] | 0: can't set (`nextInt(6) + 1` is at least 1) | 1 → TC2 | 6 → TC3 | 7: can't set (`nextInt(6)` is at most 5) |
| total of 2 dice | [2, 12] | 1: can't set | 2 → TC2 | 12 → TC3 | 13: can't set |

### Pointers

| Input | Valid value | `null` |
|---|---|---|
| `random` | a stubbed `Random` → TC2–TC4 | TC1 (throws) |

### Cases

| State | Values tested | Why |
|---|---|---|
| two different dice | 2 + 5 → TC4 | the total adds both dice, not one die twice |

## Step 4: Test cases

Tests stub `Random` with EasyMock and expect `nextInt(6)` exactly, so the number of sides is checked too.

### Method under test: `Dice(Random random)`

|     | State of the System | Expected output | Implemented? |
|-----|---------------------|-----------------|--------------|
| TC1 | `random` = `null` | `IllegalArgumentException` with a message saying a `Random` is required | :white_check_mark: |

### Method under test: `roll()`

|     | State of the System | Expected output | Implemented? |
|-----|---------------------|-----------------|--------------|
| TC2 | `nextInt(6)` returns 0, 0 | 2 (1 + 1) | :x: |
| TC3 | `nextInt(6)` returns 5, 5 | 12 (6 + 6) | :x: |
| TC4 | `nextInt(6)` returns 1, 4 | 7 (2 + 5) | :x: |
