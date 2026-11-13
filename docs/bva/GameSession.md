# BVA: `GameSession`

This PR covers the end of Story 1 (a game starts with 3 or 4 players, a full bank, and the beginner board) and Story 2
(roll for starting order, pick colors, receive starting resources, turn order). Turn play comes in a later PR.

The game moves through three phases: **rolling** (nobody has rolled yet), **picking** (seats pick colors in roll
order), and **playing** (everyone has picked; the pick order is the turn order).

The roll itself is tested in `StartingRollOff.md` with a stubbed `Random`. These tests use a real `Random` and read the
order that `rollForStartingOrder()` returns, so they check that `GameSession` follows that order whatever the dice say.

Starting resources come from `BeginnerBoard.getStartingResources(color)`. Blue's are lumber, ore, and brick (the Story 2
example); red's are grain and 2 lumber.

## Step 1: Inputs and outputs

| | |
|---|---|
| **Inputs** | `playerCount` and `random` (constructor), `color`, `type` |
| **State** | the phase, the pick order, which colors are taken, each player's cards and points, the bank's cards |
| **Outputs** | the pick order, the seat to pick, available colors, turn order, current color, card and point counts, or an exception |

## Steps 2–3: Catalog types and boundary values

For whole numbers, the catalog's "LOW − ε" and "HIGH + ε" are LOW − 1 and HIGH + 1.

### Intervals

| Input / state | Interval | LOW − 1 | LOW | HIGH | HIGH + 1 |
|---|---|---|---|---|---|
| `playerCount` | [3, 4] | 2 → TC2 (throws) | 3 → TC3 | 4 → TC1 | 5 → TC4 (throws) |
| colors picked so far | [0, playerCount] | can't set | 0 → TC8 (first seat picks) | playerCount → TC16 (last pick ends picking) | playerCount + 1 → TC17 (throws) |

### Collections

| State | Empty | One | Many | Full |
|---|---|---|---|---|
| available colors | TC16 (everyone picked) | — | TC11 (one taken) | TC1 (4 players), TC3 (3 players, no red) |
| turn order | TC10 (before anyone picks) | — | — | TC16 (all picked, in pick order) |

### Pointers

| Input | Valid value | `null` |
|---|---|---|
| `random` | a `Random` → TC1 | TC5 (throws) |
| `color` in `pickColor` | a color → TC12 | TC15 (throws) |
| `color` in the count getters | a picked color → TC12 | TC21 (throws) |
| `type` in the count getters | a resource type → TC12 | TC22 (throws) |

### Cases

| Input / state | Values tested | Why |
|---|---|---|
| phase when rolling | rolling → TC6; picking → TC7 (throws) | the roll happens once |
| phase when picking | rolling → TC23 (throws); picking → TC12; playing → TC17 (throws) | colors are picked only after the roll and only once per seat |
| color picked | free → TC12; taken → TC13 (throws); red with 3 players → TC14 (throws) | each color once, only colors in play |
| starting resources | 3 different types (blue) → TC12; 2 of one type (red) → TC18 | one card per hex, so a type can come twice |
| phase when asking for the current color | picking → TC19 (throws); playing → TC16 | there is no turn until everyone has picked |
| color in the count getters | picked → TC12; not picked → TC20 (throws) | only players in the game have cards |

## Step 4: Test cases

### Method under test: `GameSession(int playerCount, Random random)`

|     | State of the System | Expected output | Implemented? |
|-----|---------------------|-----------------|--------------|
| TC1 | `playerCount` = 4 | available colors: `BLUE, ORANGE, WHITE, RED`; bank has 19 of every resource | :x: |
| TC2 | `playerCount` = 2 | `IllegalArgumentException` with a message saying a game needs 3 or 4 players (was 2) | :x: |
| TC3 | `playerCount` = 3 | available colors: `BLUE, ORANGE, WHITE` (no red) | :x: |
| TC4 | `playerCount` = 5 | `IllegalArgumentException` with a message saying a game needs 3 or 4 players (was 5) | :x: |
| TC5 | `random` = `null` | `IllegalArgumentException` with a message saying a `Random` is required | :x: |

### Method under test: `rollForStartingOrder()`

|     | State of the System | Expected output | Implemented? |
|-----|---------------------|-----------------|--------------|
| TC6 | new 4-player game | a list of seats 1–4, each once | :x: |
| TC7 | already rolled | `IllegalStateException` with a message saying the starting order was already rolled | :x: |

### Method under test: `getSeatToPick()`

|     | State of the System | Expected output | Implemented? |
|-----|---------------------|-----------------|--------------|
| TC8 | rolled, nobody has picked | the first seat in the rolled order | :x: |
| TC9 | not rolled yet | `IllegalStateException` with a message saying players are not picking colors | :x: |

### Method under test: `getTurnOrder()`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC10 | rolled, nobody has picked | empty list | :x: |

### Method under test: `getAvailableColors()`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC11 | 4 players, first seat picked `BLUE` | `ORANGE, WHITE, RED` | :x: |

### Method under test: `pickColor(PlayerColor color)`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC12 | 4 players, rolled, first seat picks `BLUE` | blue has 1 lumber, 1 ore, 1 brick, 0 grain, 0 wool, and 2 victory points; the bank has 18 lumber, 18 ore, 18 brick, 19 grain, 19 wool; the second seat in the rolled order picks next | :x: |
| TC13 | first seat picked `BLUE`, second seat picks `BLUE` | `IllegalStateException` with a message saying blue is already taken; still the second seat to pick | :x: |
| TC14 | 3 players, first seat picks `RED` | `IllegalStateException` with a message saying red is not in play; still the first seat to pick | :x: |
| TC15 | `color` = `null` | `IllegalArgumentException` with a message saying a color is required | :x: |
| TC16 | 3 players, seats pick `WHITE`, `BLUE`, `ORANGE` in rolled order | turn order `WHITE, BLUE, ORANGE`; current color `WHITE`; no colors available | :x: |
| TC17 | 3 players, all 3 have picked, `pickColor(RED)` | `IllegalStateException` with a message saying players are not picking colors | :x: |
| TC18 | 4 players, first seat picks `RED` | red has 1 grain and 2 lumber; the bank has 18 grain and 17 lumber | :x: |
| TC23 | not rolled yet, `pickColor(BLUE)` | `IllegalStateException` with a message saying players are not picking colors; blue is still available | :x: |

### Method under test: `getCurrentColor()`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC19 | rolled, still picking | `IllegalStateException` with a message saying players are still picking colors | :x: |

### Methods under test: `getResourceCount(PlayerColor, ResourceCardType)`, `getVictoryPointCount(PlayerColor)`, `getBankResourceCount(ResourceCardType)`

Checked in TC1, TC12, TC16 and TC18. Only the error cases need their own tests.

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC20 | nobody picked `ORANGE`, ask for orange's lumber | `IllegalArgumentException` with a message saying no player has picked orange | :x: |
| TC21 | `color` = `null` in `getVictoryPointCount` | `IllegalArgumentException` with a message saying a color is required | :x: |
| TC22 | `type` = `null` in `getBankResourceCount` | `IllegalArgumentException` with a message saying the resource type is required | :x: |
