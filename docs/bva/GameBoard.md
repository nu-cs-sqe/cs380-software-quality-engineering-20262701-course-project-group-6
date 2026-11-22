# BVA: `GameBoard`

A `GameBoard` encapsulates Catan's hexagonal topological graph of 54 intersections (`Node`), 72 paths (`Edge`), and
19 terrain hexes (`Tile`), along with fixed harbor node assignments, initial piece placements, and the robber's
location.

It constructs the graph from a `TileLayout` and `BeginnerPlacements`, dynamically resolves adjacent terrain resources
for any intersection (excluding the desert), and exposes read-only views of the board state for `GameSession` and
`BoardView`.

## Steps 1–3 summary

| Input / state | Catalog type | Boundary values |
| ----- | ----- | ----- |
| `generated` state | Boolean state | `false` (before `generateBoard` / throws `IllegalStateException`: TC7, TC14, TC21), `true` (after `generateBoard`) |
| `layout` (`generateBoard`) | Pointer | `null` (throws: TC2), valid `TileLayout` with 19 `TileSetup` entries (e.g., `BeginnerLayout`) |
| `placements` (`generateBoard`) | Pointer | `null` (throws: TC3), valid `BeginnerPlacements` (`forFourPlayers()` and `forThreePlayers()`) |
| `layout.getTiles()` (`generateBoard`) | Pointer + Count | `null` (throws: TC4), 18 entries (throws: TC5), 19 entries (valid), 20 entries (throws: TC6), missing tile ID key (throws: TC7) |
| `nodeId` (`getAdjacentResources`) | Interval \[0, 53\] | \-1 (throws: TC9), 0 (1 coastal tile), 19 (3 tiles: White star), 18 (2 tiles + desert), 53 (1 coastal tile), 54 (throws: TC10) |
| `color` (`getStartingResources`) | Cases (enum) + Pointer | `null` (throws: TC15), `RED` when omitted in 3-player setup (throws: TC16), active `PlayerColor` (`WHITE`, `RED`, `BLUE`, `ORANGE`) |
| `HARBOR_NODES` | Fixed mapping | 18 harbor nodes (`0, 1, 3, 4, 7, 14, 15, 17, 26, 28, 37, 38, 45, 46, 47, 48, 50, 51`); all other nodes default to `HarborType.NONE` |

Only `GameBoard` constructs and wires `Node`, `Edge`, and `Tile` instances and assigns fixed harbor types to nodes.
All collection getters return unmodifiable copies.

## Step 4: Test cases

### Method under test: `generateBoard(TileLayout layout, BeginnerPlacements placements)`

|  | State of the System | Expected output | Implemented? |
| ----- | ----- | ----- | ----- |
| TC1 | Valid `BeginnerLayout` and `BeginnerPlacements.forFourPlayers()` | Creates 54 nodes, 72 edges, 19 tiles, robber on desert tile 9, fixed harbor nodes set, settlements/roads/starred nodes applied | :white_check_mark: |
| TC2 | Valid `BeginnerLayout` and `BeginnerPlacements.forFourPlayers()` | Every edge's endpoints share a tile, each node borders 1–3 tiles, and harbor nodes match HARBOR_NODES on perimeter nodes | :white_check_mark: |
| TC3 | `layout` = `null`, valid `placements` | `IllegalArgumentException` with message `"layout cannot be null"` | :white_check_mark: |
| TC4 | Valid `layout`, `placements` = `null` | `IllegalArgumentException` with message `"placements cannot be null"` | :white_check_mark: |
| TC5 | `layout.getTiles()` returns `null` | `IllegalArgumentException` with message `"layout tiles cannot be null"` | :white_check_mark: |
| TC6 | `layout.getTiles()` contains 18 entries (below 19) | `IllegalArgumentException` with message `"Expected 19 tiles, got 18"` | :white_check_mark: |
| TC7 | `layout.getTiles()` contains 20 entries (above 19) | `IllegalArgumentException` with message `"Expected 19 tiles, got 20"` | :white_check_mark: |
| TC8 | `layout.getTiles()` has 19 entries but is missing a required tile ID (`0..18`) | `IllegalArgumentException` indicating missing tile setup | :white_check_mark: |

### Method under test: `getAdjacentResources(int nodeId)`

|  | State of the System | Expected output | Implemented? |
| ----- | ----- | ----- | ----- |
| TC9 | Board generated with `BeginnerLayout`, `nodeId` = 0 (top-left coastal boundary) | Returns `[ORE]` (1 adjacent tile) | :white_check_mark: |
| TC10 | Board generated with `BeginnerLayout`, `nodeId` = 19 (interior node bordering 3 resource tiles) | Returns `[GRAIN, BRICK, LUMBER]` in tile ID order | :white_check_mark: |
| TC11 | Board generated with `BeginnerLayout`, `nodeId` = 18 (interior node bordering 2 resource tiles + desert) | Returns `[LUMBER, GRAIN]` (desert tile 9 excluded) | :white_check_mark: |
| TC12 | Board generated with `BeginnerLayout`, `nodeId` = -1 | `IllegalArgumentException` indicating `nodeId` must be between 0 and 53 | :white_check_mark: |
| TC13 | Board generated with `BeginnerLayout`, `nodeId` = 54 | `IllegalArgumentException` indicating `nodeId` must be between 0 and 53 | :white_check_mark: |

### Method under test: `getStartingResources(PlayerColor color)`

|  | State of the System | Expected output | Implemented? |
| ----- | ----- | ----- | ----- |
| TC14 | Board generated with `BeginnerPlacements.forFourPlayers()`, `color` = `PlayerColor.WHITE` | Returns `[GRAIN, BRICK, LUMBER]` | :white_check_mark: |
| TC15 | Board generated with `BeginnerPlacements.forFourPlayers()`, `color` = `PlayerColor.RED` | Returns `[GRAIN, LUMBER, LUMBER]` | :white_check_mark: |
| TC16 | Board generated with `BeginnerPlacements.forFourPlayers()`, `color` = `PlayerColor.BLUE` | Returns `[LUMBER, ORE, BRICK]` | :white_check_mark: |
| TC17 | Board generated with `BeginnerPlacements.forFourPlayers()`, `color` = `PlayerColor.ORANGE` | Returns `[ORE, GRAIN, GRAIN]` | :white_check_mark: |
| TC18 | Board generated with `BeginnerLayout`, `color` = `null` | `IllegalArgumentException` with message `"Player color is required"` | :white_check_mark: |
| TC19 | Board generated with `BeginnerPlacements.forThreePlayers()`, `color` = `PlayerColor.RED` | `IllegalArgumentException` with message `"RED has no starting settlement on this board"` | :white_check_mark: |

### Method under test: `getStartingResourceNodes()`

|  | State of the System | Expected output | Implemented? |
| ----- | ----- | ----- | ----- |
| TC20 | Board generated with `BeginnerPlacements.forThreePlayers()` | Returns `{ORANGE=42, BLUE=40, WHITE=19}` (each color's starred settlement; no red) | :white_check_mark: |

### Methods under test: `getTiles()`, `getNodes()`, `getEdges()`, `getRobberTileId()`, `getHarborNodes()`

Read-only getters for `BoardView` and `GameSession`. Verified in TC1 and harbor assertions.

## Next section (to be added before that code is written)

Placement validation and gameplay mutations (`isValidSettlementLocation`, `isValidRoadLocation`, `buildSettlement`, `buildRoad`, `moveRobber`). Deferred for later development. Once added, BVA will cover distance rules, road attachment, vacancy checks, and boundary IDs.
## Story 3: Production (#31)

`getProduction(diceTotal)` works out who is owed what when a number is rolled (Story 3 criteria 3 and 4). For every hex
with that number, each settlement on its corners is owed 1 of the hex's resource and each city 2. The result is grouped
by resource, then by color, because the bank decides each resource on its own (Story 3 criterion 5, `Bank.md`). The
board only works out what is owed; `GameSession` and `Bank` hand out the cards.

The desert's number is 7 inside `Tile`, and it has no resource, so it never produces. Skipping the hex the robber is on
needs a way to move the robber, which comes with Story 4; until then the robber stays on the desert, which produces
nothing anyway.

Expected values come from the beginner board (`docs/design/map_ids_reference.jpg`) with all 4 colors: red on 10 and 29,
orange on 13 and 42, blue on 40 and 44, white on 19 and 35. Cities and extra settlements are placed in the test with
`Node.placeBuilding` (package-private), since building comes with Story 5.

### Steps 2–3: Catalog types and boundary values

#### Intervals

| Input | Interval | LOW − 1 | LOW | HIGH | HIGH + 1 |
|---|---|---|---|---|---|
| `diceTotal` | [2, 12] | 1 → TC23 (throws) | 2 → TC21 | 12 → TC22 | 13 → TC24 (throws) |

#### Collections

| State | Empty | One | Many |
|---|---|---|---|
| hexes with the rolled number | — (every number 2–12 is on the board) | 1 hex → TC21 | 2 hexes → TC25 |
| buildings on a producing hex | none → TC28 (3 players, no red) | 1 → TC21 | 2 colors → TC25; 2 of one color → TC27 |

#### Cases

| Case | Values tested | Why |
|---|---|---|
| building type | settlement → TC21; city → TC26 | a city collects 2 |
| roll of 7 | 7 → TC29 (nothing owed) | the desert's number is 7 inside `Tile`, but it has no resource |

### Method under test: `getProduction(int diceTotal)`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC21 | 4 players, `diceTotal` = 2 (wool hex 1, red settlement on 10) | `{WOOL={RED=1}}` | :white_check_mark: |
| TC22 | 4 players, `diceTotal` = 12 (grain hex 3, white settlement on 19) | `{GRAIN={WHITE=1}}` | :white_check_mark: |
| TC23 | `diceTotal` = 1 | `IllegalArgumentException` with a message saying the total must be between 2 and 12 (was 1) | :white_check_mark: |
| TC24 | `diceTotal` = 13 | `IllegalArgumentException` with a message saying the total must be between 2 and 12 (was 13) | :white_check_mark: |
| TC25 | 4 players, `diceTotal` = 8 (ore hex 11: white on 35; lumber hex 12: red on 29, blue on 40) | `{ORE={WHITE=1}, LUMBER={RED=1, BLUE=1}}` | :white_check_mark: |
| TC26 | 4 players, red's settlement on 10 upgraded to a city, `diceTotal` = 2 | `{WOOL={RED=2}}` | :white_check_mark: |
| TC27 | 4 players, extra red settlement on 4 (also on wool hex 1), `diceTotal` = 2 | `{WOOL={RED=2}}` (1 + 1) | :white_check_mark: |
| TC28 | 3 players (no red), `diceTotal` = 2 | empty map (nobody is on wool hex 1) | :white_check_mark: |
| TC29 | 4 players, `diceTotal` = 7 | empty map | :white_check_mark: |

## Story 5: Placement rules (#33)

The board decides where a color may build (Story 5 criteria 4–6). `GameSession` checks turns, costs and pieces; the board
only knows the spots.

- **Road:** an empty edge with one end on the color's own settlement or city, or on a corner that touches one of the
  color's roads. A road cannot connect through a corner where an opponent has a building.
- **Settlement:** an empty corner that touches one of the color's roads, with no building on any neighboring corner
  (the distance rule).
- **City:** a corner with the color's own settlement.

`getRoadSpots`, `getSettlementSpots` and `getCitySpots` list the legal spots (the UI highlights them).
`buildRoad`, `buildSettlement` and `buildCity` place a piece and throw if the spot is not in that list, so the rules
live in one place.

Expected values come from the 4-player beginner board (`docs/design/map_ids_reference.jpg`). Red has settlements on
10 and 29 and roads on edges 13 (10–11) and 41 (29–30). Extra roads and cities are set up in the test with the
package-private `Edge.setRoadColor` and `Node.placeBuilding`.

### Steps 2–3: Catalog types and boundary values

| Input / state | Catalog type | Values tested |
|---|---|---|
| legal spots | Collection | none (red's settlements at the start) → TC33; one → TC34, TC38; many → TC30, TC37 |
| how a road connects | Cases | through own settlement → TC30 (edge 12); through own road → TC30 (edge 42); only through an opponent's building → TC31 (excluded) |
| road ends on an opponent's settlement but connects at the other end | Cases | allowed → TC30 (edge 50, blue on 40) |
| corner for a settlement | Cases | free and 2 away from buildings → TC34; next to a building → TC33, TC45; occupied → TC35 |
| corner for a city | Cases | own settlement → TC37, TC46; own city → TC38 (excluded); opponent's settlement → TC47 |
| `edgeId` in `buildRoad` | Interval [0, 71] | -1 → TC43; 72 → TC42 (both rejected like any illegal spot) |
| `color` | Pointer | `null` → TC32, TC36, TC39 |

### Method under test: `getRoadSpots(PlayerColor color)`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC30 | 4 players, `RED` | `{7, 12, 14, 20, 34, 40, 42, 50}` | :white_check_mark: |
| TC31 | 4 players, extra blue road on edge 46 (34–35, white settlement on 35), `BLUE` | `{45, 50, 55, 57, 59, 60, 63}` (not 47: its only link is through white's settlement) | :white_check_mark: |
| TC32 | `color` = `null` | `IllegalArgumentException` with a message saying a color is required | :white_check_mark: |

### Method under test: `getSettlementSpots(PlayerColor color)`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC33 | 4 players, `RED` | empty (every corner red's roads reach is next to a building) | :white_check_mark: |
| TC34 | 4 players, extra red road on edge 42 (30–31), `RED` | `{31}` | :white_check_mark: |
| TC35 | 4 players, extra red road on edge 16 (13–14, orange settlement on 13), `RED` | empty (13 is occupied, 14 is next to it) | :white_check_mark: |
| TC36 | `color` = `null` | `IllegalArgumentException` with a message saying a color is required | :white_check_mark: |

### Method under test: `getCitySpots(PlayerColor color)`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC37 | 4 players, `RED` | `{10, 29}` | :white_check_mark: |
| TC38 | 4 players, red city on 10, `RED` | `{29}` | :white_check_mark: |
| TC39 | `color` = `null` | `IllegalArgumentException` with a message saying a color is required | :white_check_mark: |

### Methods under test: `buildRoad`, `buildSettlement`, `buildCity`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC40 | 4 players, `buildRoad(42, RED)` | edge 42 has a red road; red's settlement spots are now `{31}` | :white_check_mark: |
| TC41 | 4 players, `buildRoad(0, RED)` | `IllegalStateException` with a message saying edge 0 is not a legal spot for a red road; edge 0 still empty | :white_check_mark: |
| TC42 | `buildRoad(72, RED)` | `IllegalStateException` with a message saying edge 72 is not a legal spot for a red road | :x: |
| TC43 | `buildRoad(-1, RED)` | `IllegalStateException` with a message saying edge -1 is not a legal spot for a red road | :x: |
| TC44 | extra red road on edge 42, `buildSettlement(31, RED)` | corner 31 has a red settlement | :x: |
| TC45 | 4 players, `buildSettlement(11, RED)` | `IllegalStateException` with a message saying node 11 is not a legal spot for a red settlement; still empty | :x: |
| TC46 | 4 players, `buildCity(10, RED)` | corner 10 has a red city | :x: |
| TC47 | 4 players, `buildCity(13, RED)` | `IllegalStateException` with a message saying node 13 is not a legal spot for a red city; still orange's settlement | :x: |
