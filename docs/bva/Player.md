# BVA: `Player`

A `Player` is created for each color in play during game setup (Story 1). It starts with its 2 beginner settlements
and 2 beginner roads already placed, so its remaining supply is 3 settlements, 4 cities, and 13 roads.

Piece limits (rulebook): 5 settlements, 4 cities, 15 roads.

## Steps 1–3 summary

| Input / state | Catalog type | Boundary values |
|---|---|---|
| `color` (constructor) | Cases (enum) + Pointer | any `PlayerColor`; `null` |
| `settlementsPlaced` | Interval [0, 5] | -1 (only reachable by upgrading with 0 settlements, throws: TC10), 0, 5, 6 (throws: TC5) |
| `citiesPlaced` | Interval [0, 4] | -1 (can't set: nothing removes a city), 0, 4, 5 (throws: TC9) |
| `roadsPlaced` | Interval [2, 15] | 1 (can't set: roads start at 2 and are never removed), 2, 15, 16 (throws: TC13) |

Placing a city replaces one of the player's settlements on the board, so `placeCityPiece()` needs at least one placed
settlement and gives that settlement piece back to the supply. Player only tracks how many pieces are placed; which
settlement is upgraded (its location) is tracked by the board.

Callers see the remaining supply (`get...Remaining()` = max − placed), not the placed counts, so the piece limits
stay inside `Player` (review feedback on PR #16, Clean Code ch. 6).

## Step 4: Test cases

### Method under test: `Player(PlayerColor color)`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC1  | `color` = `BLUE` | `getColor()` = `BLUE`; remaining: 3 settlements, 4 cities, 13 roads | :white_check_mark: |
| TC2  | `color` = `null` | `IllegalArgumentException` with a message saying the color is required | :white_check_mark: |

### Method under test: `placeSettlementPiece()`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC3  | settlements placed = 2 (new player) | 2 settlements remaining | :white_check_mark: |
| TC4  | settlements placed = 4 | 0 settlements remaining (max reached) | :white_check_mark: |
| TC5  | settlements placed = 5 (max) | `IllegalStateException` with a message naming the color and the max of 5; 0 settlements remaining | :white_check_mark: |
| TC6  | settlements placed = 5, then `placeCityPiece()` once (settlements placed = 4) | placing a settlement succeeds again; 0 settlements remaining | :white_check_mark: |

### Method under test: `placeCityPiece()`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC7  | settlements placed = 2, cities placed = 0 (new player) | 3 cities remaining; 4 settlements remaining (the upgraded settlement returns to the supply) | :white_check_mark: |
| TC8  | cities placed = 3, settlements placed ≥ 1 | 0 cities remaining (max reached) | :white_check_mark: |
| TC9  | cities placed = 4 (max), settlements placed ≥ 1 | `IllegalStateException` with a message naming the color and the max of 4; still 0 cities and 4 settlements remaining | :white_check_mark: |
| TC10 | new player, then `placeCityPiece()` twice (settlements placed = 0, cities placed = 2) | `IllegalStateException` with a message saying there is no settlement to upgrade; still 2 cities and 5 settlements remaining | :white_check_mark: |

### Method under test: `placeRoadPiece()`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC11 | roads placed = 2 (new player) | 12 roads remaining | :white_check_mark: |
| TC12 | roads placed = 14 | 0 roads remaining (max reached) | :white_check_mark: |
| TC13 | roads placed = 15 (max) | `IllegalStateException` with a message naming the color and the max of 15; 0 roads remaining | :white_check_mark: |

### Method under test: `getVictoryPointCount()`

Settlements are worth 1 point and cities 2 points. Longest Road, Largest Army, and victory point cards are added
in a later story.

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC14 | new player (2 settlements, 0 cities) | 2 | :white_check_mark: |
| TC15 | new player, then `placeCityPiece()` once (1 settlement, 1 city) | 3 | :white_check_mark: |
| TC16 | new player, `placeSettlementPiece()` twice, then `placeCityPiece()` 4 times (0 settlements, 4 cities) | 8 | :white_check_mark: |

### Methods under test: `getColor()`, `getSettlementsRemaining()`, `getCitiesRemaining()`, `getRoadsRemaining()`

Simple getters. They are checked in every test case above (implemented in TC1–TC13).

## Next section (to be added before that code is written)

Resource cards, merged in from `PlayerInventory` (team decision 11/04): `collectResource`, `useResource`,
`getResourceCount`, `getTotalResourceCount`. The card map is never returned directly.
