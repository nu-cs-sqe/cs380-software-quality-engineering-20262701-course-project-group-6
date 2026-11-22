# BVA: `GameSession`

This PR covers the end of Story 1 (a game starts with 3 or 4 players, a full bank, and the beginner board) and Story 2
(roll for starting order, pick colors, receive starting resources, turn order). Turn play comes in a later PR.

The game moves through three phases: **rolling** (nobody has rolled yet), **picking** (seats pick colors in roll
order), and **playing** (everyone has picked; the pick order is the turn order).

The roll itself is tested in `StartingRollOff.md` with a stubbed `Random`. These tests use a real `Random` and read the
order that `rollForStartingOrder()` returns, so they check that `GameSession` follows that order whatever the dice say.

Starting resources come from `GameBoard.getStartingResources(color)`. Blue's are lumber, ore, and brick (the Story 2
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
| TC1 | `playerCount` = 4 | available colors: `BLUE, ORANGE, WHITE, RED`; bank has 19 of every resource | :white_check_mark: |
| TC2 | `playerCount` = 2 | `IllegalArgumentException` with a message saying a game needs 3 or 4 players (was 2) | :white_check_mark: |
| TC3 | `playerCount` = 3 | available colors: `BLUE, ORANGE, WHITE` (no red) | :white_check_mark: |
| TC4 | `playerCount` = 5 | `IllegalArgumentException` with a message saying a game needs 3 or 4 players (was 5) | :white_check_mark: |
| TC5 | `random` = `null` | `IllegalArgumentException` with a message saying a `Random` is required | :white_check_mark: |

### Method under test: `rollForStartingOrder()`

|     | State of the System | Expected output | Implemented? |
|-----|---------------------|-----------------|--------------|
| TC6 | new 4-player game | a list of seats 1–4, each once | :white_check_mark: |
| TC7 | already rolled | `IllegalStateException` with a message saying the starting order was already rolled | :white_check_mark: |

### Method under test: `getSeatToPick()`

|     | State of the System | Expected output | Implemented? |
|-----|---------------------|-----------------|--------------|
| TC8 | rolled, nobody has picked | the first seat in the rolled order | :white_check_mark: |
| TC9 | not rolled yet | `IllegalStateException` with a message saying players are not picking colors | :white_check_mark: |

### Method under test: `getTurnOrder()`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC10 | rolled, nobody has picked | empty list | :white_check_mark: |

### Method under test: `getAvailableColors()`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC11 | 4 players, first seat picked `BLUE` | `ORANGE, WHITE, RED` | :white_check_mark: |

### Method under test: `pickColor(PlayerColor color)`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC12 | 4 players, rolled, first seat picks `BLUE` | blue has 1 lumber, 1 ore, 1 brick, 0 grain, 0 wool, and 2 victory points; the bank has 18 lumber, 18 ore, 18 brick, 19 grain, 19 wool; the second seat in the rolled order picks next | :white_check_mark: |
| TC13 | first seat picked `BLUE`, second seat picks `BLUE` | `IllegalStateException` with a message saying blue is already taken; still the second seat to pick | :white_check_mark: |
| TC14 | 3 players, first seat picks `RED` | `IllegalStateException` with a message saying red is not in play; still the first seat to pick | :white_check_mark: |
| TC15 | `color` = `null` | `IllegalArgumentException` with a message saying a color is required | :white_check_mark: |
| TC16 | 3 players, seats pick `WHITE`, `BLUE`, `ORANGE` in rolled order | turn order `WHITE, BLUE, ORANGE`; current color `WHITE`; no colors available | :white_check_mark: |
| TC17 | 3 players, all 3 have picked, `pickColor(RED)` | `IllegalStateException` with a message saying players are not picking colors | :white_check_mark: |
| TC18 | 4 players, first seat picks `RED` | red has 1 grain and 2 lumber; the bank has 18 grain and 17 lumber | :white_check_mark: |
| TC23 | not rolled yet, `pickColor(BLUE)` | `IllegalStateException` with a message saying players are not picking colors; blue is still available | :white_check_mark: |

### Method under test: `getCurrentColor()`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC19 | rolled, still picking | `IllegalStateException` with a message saying players are still picking colors | :white_check_mark: |

### Methods under test: `getResourceCount(PlayerColor, ResourceCardType)`, `getVictoryPointCount(PlayerColor)`, `getBankResourceCount(ResourceCardType)`

Checked in TC1, TC12, TC16 and TC18. Only the error cases need their own tests.

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC20 | nobody picked `ORANGE`, ask for orange's lumber | `IllegalArgumentException` with a message saying no player has picked orange | :white_check_mark: |
| TC21 | `color` = `null` in `getVictoryPointCount` | `IllegalArgumentException` with a message saying a color is required | :white_check_mark: |
| TC22 | `type` = `null` in `getBankResourceCount` | `IllegalArgumentException` with a message saying the resource type is required | :white_check_mark: |

## Story 3: Turn loop (#31)

This part adds rolling the dice, paying out resources, and ending the turn. It covers Story 3 criteria 1–8. Who is
owed what comes from `GameBoard.getProduction` (`GameBoard.md`), and the shortage rule is in `Bank.payProduction`
(`Bank.md`), so the tests here only check that a roll hands out those cards, and that a 7 hands out nothing. Skipping
the robber's hex (part of criterion 4) comes with Story 4. Making bought development cards playable at the end of a turn (criterion 9) comes with
Story 6, since nothing in `GameSession` gives out development cards yet.

New state: the index of the current player in the turn order, and whether that player has rolled this turn.

The dice themselves are tested in `Dice.md` and `DiceRoll.md` with a stubbed `Random`. Tests that need a known roll pass
`new Dice(stub)` to the constructor `GameSession(playerCount, random, dice)`; the rest use a real `Random`.

Tests set up a 3-player game where the seats pick `WHITE`, `BLUE`, `ORANGE`, so the turn order is
`WHITE, BLUE, ORANGE`.

### Steps 2–3: Catalog types and boundary values (turn loop)

#### Intervals

| State | Interval | LOW − 1 | LOW | HIGH | HIGH + 1 |
|---|---|---|---|---|---|
| current player's place in the turn order | [0, playerCount − 1] | -1: can't set (starts at 0, only goes up) | 0 → TC29 (first player ends turn) | playerCount − 1 → TC30 (last player ends turn) | playerCount: can't set (wraps back to 0, TC30) |
| rolls this turn | [0, 1] | -1: can't set | 0 → TC25, TC28 | 1 → TC26, TC29 | 2: can't set (second roll throws, TC26) |

#### Cases

| State | Values tested | Why |
|---|---|---|
| `dice` (constructor) | `null` → TC32 (throws) | same as `random` (TC5) |
| roll total | 7 → TC25 (nobody collects); 8 → TC33 (two players collect) | a 7 pays nothing (criterion 6) |
| phase when rolling | picking → TC24 (throws); playing → TC25 | turns start only after everyone has picked |
| phase when ending the turn | picking → TC31 (throws); playing → TC29 | same |
| rolled this turn when ending | no → TC28 (throws); yes → TC29 | the player must roll before ending the turn |
| a new turn | next player rolls after the turn passes → TC27 | the "already rolled" flag resets for the next player |

### Method under test: `rollDice()`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC24 | rolled for order, still picking | `IllegalStateException` with a message saying players are still picking colors | :white_check_mark: |
| TC25 | `WHITE`'s turn, dice stubbed to roll 3 and 4 | a `DiceRoll` of 3 and 4 (total 7); nobody's resources and the bank change | :white_check_mark: |
| TC26 | `WHITE` already rolled this turn | `IllegalStateException` with a message saying white already rolled this turn | :white_check_mark: |
| TC27 | `WHITE` rolled and ended the turn; `BLUE` rolls | returns a `DiceRoll` (the flag reset for the new turn) | :white_check_mark: |
| TC33 | `WHITE`'s turn, dice stubbed to roll 2 and 6 (total 8) | white gets 1 ore (hex 11) and blue 1 lumber (hex 12); bank has 16 ore and 16 lumber | :white_check_mark: |

### Method under test: `endTurn()`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC28 | `WHITE`'s turn, not rolled yet | `IllegalStateException` with a message saying white must roll before ending the turn; still `WHITE`'s turn | :white_check_mark: |
| TC29 | `WHITE` rolled | current color is `BLUE` | :white_check_mark: |
| TC30 | `WHITE`, `BLUE`, `ORANGE` each rolled and ended their turn | current color is `WHITE` again (wraps after the last player) | :white_check_mark: |
| TC31 | rolled for order, still picking | `IllegalStateException` with a message saying players are still picking colors | :white_check_mark: |

### Method under test: `GameSession(int playerCount, Random random, Dice dice)`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC32 | `dice` = `null` | `IllegalArgumentException` with a message saying dice are required | :white_check_mark: |

### Method under test: `getBoard()`

Gives `BoardView` the board this game is played on, so the UI draws the same nodes and edges that `GameSession` pays
out from.

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC34 | new 3-player game | the board's starred settlements are orange's, blue's and white's (no red), so it is the 3-player board | :white_check_mark: |

### Method under test: `getStartingRolls()`

Passes on `StartingRollOff.getRounds()` (tested in `StartingRollOff.md`) so the UI can show each seat's dice.

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC35 | 4 players, rolled for starting order | the first round has a roll for each of seats 1–4 | :white_check_mark: |

## Story 5: Building (#33)

`buildRoad(edgeId)`, `buildSettlement(nodeId)` and `buildCity(nodeId)` build for the current player (Story 5 criteria
1–3 and 6–9). The checks run in this order, and nothing changes unless all of them pass:

1. the player has rolled this turn (Story 4 will add "and the robber is handled");
2. the spot is legal (`GameBoard.getRoadSpots` and the others, tested in `GameBoard.md`);
3. the player has every card the piece costs (road: brick, lumber; settlement: brick, lumber, wool, grain;
   city: 3 ore, 2 grain);
4. the player has a piece left (`Player.placeRoadPiece` and the others throw without changing anything, tested in
   `Player.md`).

Then the cards go from the player to the bank, the piece goes on the board, and the player's piece count and points
change (`Player` counts 1 point per settlement and 2 per city).

All three builds share these steps, so the cost check and the piece check are tested once (on a road). Running out of
pieces needs 13 more roads and is already tested in `Player.md`, so it has no test here.

Tests use the 3-player game with turn order `WHITE, BLUE, ORANGE` and stubbed dice. White starts with 1 grain, 1 brick,
1 lumber, settlements on 19 and 35, and roads on edges 25 (18–19) and 37 (24–35).

### Steps 2–3: Catalog types and boundary values

| Input / state | Catalog type | Values tested |
|---|---|---|
| phase | Cases | picking → TC44 (throws); playing → TC37 |
| rolled this turn | Cases | no → TC36 (throws); yes → TC37 |
| spot | Cases | legal → TC37, TC40, TC42; illegal → TC38, TC41, TC43 (throw, nothing changes) |
| cards vs. cost | Interval [cost, …] | exactly the cost → TC37, TC40, TC42 (left with 0); one short → TC39 (throws) |
| piece built | Cases | road → TC37; settlement → TC40; city → TC42 (settlement goes back to the supply) |

### Methods under test: `buildRoad`, `buildSettlement`, `buildCity`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC36 | white's turn, not rolled, `buildRoad(24)` | `IllegalStateException` with a message saying white must roll before building; edge 24 still empty | :white_check_mark: |
| TC37 | white rolls 7, `buildRoad(24)` (17–18) | edge 24 has a white road; white has 0 brick and 0 lumber; the bank has 18 brick and 18 lumber; white has 12 roads left | :white_check_mark: |
| TC38 | white rolls 7, `buildRoad(0)` | `IllegalStateException` with a message saying edge 0 is not a legal spot for a white road; white still has 1 brick and 1 lumber | :white_check_mark: |
| TC39 | white rolls 7, builds a road on 24, then `buildRoad(23)` | `IllegalStateException` with a message saying white cannot afford a road; edge 23 still empty; white still has 12 roads left | :white_check_mark: |
| TC40 | white rolls 5 (+1 wool) and builds a road on 24; blue rolls 6 (white +1 brick); orange rolls 11 (white +1 lumber); white rolls 7, `buildSettlement(17)` | corner 17 has a white settlement; white has 0 brick, lumber, wool and grain; white has 3 points and 2 settlements left | :white_check_mark: |
| TC41 | white rolls 7, `buildSettlement(0)` | `IllegalStateException` with a message saying node 0 is not a legal spot for a white settlement | :white_check_mark: |
| TC42 | white, blue and orange each roll 8 (white +1 ore each time); white rolls 12 (+1 grain), `buildCity(19)` | corner 19 has a white city; white has 0 ore and 0 grain; white has 3 points (city 2 + settlement 1), 3 cities and 4 settlements left; the bank has 17 ore and 17 grain | :white_check_mark: |
| TC43 | white rolls 7, `buildCity(13)` (orange's settlement) | `IllegalStateException` with a message saying node 13 is not a legal spot for a white city | :white_check_mark: |
| TC44 | still picking colors, `buildRoad(24)` | `IllegalStateException` with a message saying players are still picking colors | :x: |

`getRoadsRemaining`, `getSettlementsRemaining` and `getCitiesRemaining` pass the counts on from `Player` (tested in
`Player.md`); they are checked in TC37, TC40 and TC42.
