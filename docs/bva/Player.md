# BVA: `Player`

A `Player` is created for each color in play during game setup (Story 1). It starts with its 2 beginner settlements
and 2 beginner roads already placed, so its remaining supply is 3 settlements, 4 cities, and 13 roads.

Piece limits (rulebook): 5 settlements, 4 cities, 15 roads.

## Step 1: Inputs and outputs

| | |
|---|---|
| **Inputs** | `color` (constructor), `type` and `amount` (resource cards) |
| **State** | settlements, cities, and roads placed; how many cards of each `ResourceCardType` the player holds |
| **Outputs** | pieces remaining, victory points, card counts, or an exception |

## Steps 2–3: Catalog types and boundary values (pieces and victory points)

For whole numbers, the catalog's "LOW − ε" and "HIGH + ε" are LOW − 1 and HIGH + 1. "Can't set" (catalog term) means
the code makes that value impossible to reach, so there is nothing to test.

### Intervals

| State | Interval | LOW − 1 | LOW | HIGH | HIGH + 1 |
|---|---|---|---|---|---|
| settlements placed | [0, 5] | -1: upgrading with no settlement throws → TC10 | 0 → TC10, TC16 | 5 → TC4, TC6 | 6 → TC5 (throws) |
| cities placed | [0, 4] | -1: can't set (nothing removes a city) | 0 → TC1, TC7 | 4 → TC8, TC16 | 5 → TC9 (throws) |
| roads placed | [2, 15] | 1: can't set (roads start at 2 and are never removed) | 2 → TC11 | 15 → TC12 | 16 → TC13 (throws) |

### Pointers

| Input | Valid value | `null` |
|---|---|---|
| `color` | `BLUE` → TC1 | TC2 (throws) |

### Cases

| Input / state | Values tested | Why |
|---|---|---|
| `color` | `BLUE` | every color goes through the same code, so one color stands in for all 4 |
| pieces counted for victory points | settlements only → TC14; both → TC15; cities only (max 4) → TC16 | settlements are worth 1 and cities 2, so each kind of piece is checked on its own and together |

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

## Resource cards

Resource cards were merged in from `PlayerInventory` (team decision 11/04). `Player` keeps a count for each
`ResourceCardType`. The counts are never returned as a map; callers ask for one count or the total.

### Steps 2–3: Catalog types and boundary values (resource cards)

#### Intervals

| Input | Interval | LOW − 1 | LOW | HIGH | HIGH + 1 |
|---|---|---|---|---|---|
| `amount` in `useResource` | [1, count held] | 0 → TC25 (throws) | 1 → TC22 (holds 3) | 2 → TC23 (holds 2) | 3 → TC24 (holds 2, throws) |

#### Counts

| Input / state | -1 | 0 | 1 | >1 | max | max + 1 |
|---|---|---|---|---|---|---|
| `amount` in `collectResource` | same partition as 0 (same `amount < 1` check) | TC19 (throws) | TC17 | TC18 | none in `Player` (see below) | none |
| cards held of one type | can't set (`useResource` throws first, TC24) | TC27 | TC17 | TC18 | none | none |

#### Collections

| State | Empty | One | Many |
|---|---|---|---|
| hand (all types) | TC30 | TC17 (one type) | TC31 (every type) |

#### Pointers

| Input | Valid value | `null` |
|---|---|---|
| `type` | a `ResourceCardType` → TC17–TC28 | TC21, TC26, TC29 (throw) |

#### Cases

| Input / state | Values tested | Why |
|---|---|---|
| `type` | all 5 in TC27; `BRICK` elsewhere | every type goes through the same code, so one type stands in for all 5 |
| other types | `BRICK` after collecting `ORE` → TC20; `ORE` when holding only `BRICK` → TC28 | a change to one type must not change another |

There is no upper limit in `Player`. The bank holds 19 cards of each type and cannot pay out more, so the bank
enforces that limit.

### Method under test: `collectResource(ResourceCardType type, int amount)`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC17 | new player, collect 1 `BRICK` | 1 `BRICK`; total 1 | :white_check_mark: |
| TC18 | 1 `BRICK`, collect 2 `BRICK` | 3 `BRICK` (added, not replaced) | :white_check_mark: |
| TC19 | new player, collect 0 `BRICK` | `IllegalArgumentException` with a message saying the amount must be at least 1; still 0 `BRICK` | :white_check_mark: |
| TC20 | 1 `BRICK`, collect 1 `ORE` | 1 `BRICK` and 1 `ORE` (other types unchanged) | :white_check_mark: |
| TC21 | `type` = `null` | `IllegalArgumentException` with a message saying the resource type is required | :white_check_mark: |

### Method under test: `useResource(ResourceCardType type, int amount)`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC22 | 3 `BRICK`, use 1 `BRICK` | 2 `BRICK` | :white_check_mark: |
| TC23 | 2 `BRICK`, use 2 `BRICK` | 0 `BRICK` (uses the whole count) | :white_check_mark: |
| TC24 | 2 `BRICK`, use 3 `BRICK` | `IllegalStateException` with a message naming the color, the type, how many it has, and how many were asked for; still 2 `BRICK` | :white_check_mark: |
| TC25 | 2 `BRICK`, use 0 `BRICK` | `IllegalArgumentException` with a message saying the amount must be at least 1; still 2 `BRICK` | :white_check_mark: |
| TC26 | `type` = `null` | `IllegalArgumentException` with a message saying the resource type is required | :white_check_mark: |

### Method under test: `getResourceCount(ResourceCardType type)`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC27 | new player | 0 for every `ResourceCardType` | :white_check_mark: |
| TC28 | 2 `BRICK` | 0 `ORE` (asking for a type the player has none of) | :white_check_mark: |
| TC29 | `type` = `null` | `IllegalArgumentException` with a message saying the resource type is required | :white_check_mark: |

### Method under test: `getTotalResourceCount()`

Used later to check whether a player holds more than 7 cards when a 7 is rolled.

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC30 | new player | 0 | :white_check_mark: |
| TC31 | 2 `BRICK` and 1 of each other type | 6 (sum across all types) | :white_check_mark: |

## Next section (to be added before that code is written)

Development cards: `collectDevCard`, `useDevCard`, `getDevCardCount`.
