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
| TC2 | `layout` = `null`, valid `placements` | `IllegalArgumentException` with message `"layout cannot be null"` | :white_check_mark: |
| TC3 | Valid `layout`, `placements` = `null` | `IllegalArgumentException` with message `"placements cannot be null"` | :white_check_mark: |
| TC4 | `layout.getTiles()` returns `null` | `IllegalArgumentException` with message `"layout tiles cannot be null"` | :white_check_mark: |
| TC5 | `layout.getTiles()` contains 18 entries (below 19) | `IllegalArgumentException` with message `"Expected 19 tiles, got 18"` | :white_check_mark: |
| TC6 | `layout.getTiles()` contains 20 entries (above 19) | `IllegalArgumentException` with message `"Expected 19 tiles, got 20"` | :white_check_mark: |
| TC7 | `layout.getTiles()` has 19 entries but is missing a required tile ID (`0..18`) | `IllegalArgumentException` indicating missing tile setup | :white_check_mark: |

### Method under test: `getAdjacentResources(int nodeId)`

|  | State of the System | Expected output | Implemented? |
| ----- | ----- | ----- | ----- |
| TC8 | Board generated with `BeginnerLayout`, `nodeId` = 0 (top-left coastal boundary) | Returns `[ORE]` (1 adjacent tile) | :white_check_mark: |
| TC9 | Board generated with `BeginnerLayout`, `nodeId` = 19 (interior node bordering 3 resource tiles) | Returns `[GRAIN, BRICK, LUMBER]` in tile ID order | :white_check_mark: |
| TC10 | Board generated with `BeginnerLayout`, `nodeId` = 18 (interior node bordering 2 resource tiles + desert) | Returns `[LUMBER, GRAIN]` (desert tile 9 excluded) | :white_check_mark: |
| TC11 | Board has not been generated yet (`generated` = `false`) | `IllegalStateException` with message `"Board has not been generated yet"` | :white_check_mark: |
| TC12 | Board generated with `BeginnerLayout`, `nodeId` = -1 | `IllegalArgumentException` indicating `nodeId` must be between 0 and 53 | :white_check_mark: |
| TC13 | Board generated with `BeginnerLayout`, `nodeId` = 54 | `IllegalArgumentException` indicating `nodeId` must be between 0 and 53 | :white_check_mark: |

### Method under test: `getStartingResources(PlayerColor color)`

|  | State of the System | Expected output | Implemented? |
| ----- | ----- | ----- | ----- |
| TC14 | Board has not been generated yet (`generated` = `false`) | `IllegalStateException` with message `"Board has not been generated yet"` | :x: |
| TC15 | Board generated with `BeginnerLayout`, `color` = `null` | `IllegalArgumentException` with message `"Player color is required"` | :x: |
| TC16 | Board generated with `BeginnerPlacements.forThreePlayers()`, `color` = `PlayerColor.RED` | `IllegalArgumentException` with message `"RED has no starting settlement on this board"` | :x: |
| TC17 | Board generated with `BeginnerPlacements.forFourPlayers()`, `color` = `PlayerColor.WHITE` | Returns `[GRAIN, BRICK, LUMBER]` | :x: |
| TC18 | Board generated with `BeginnerPlacements.forFourPlayers()`, `color` = `PlayerColor.RED` | Returns `[GRAIN, LUMBER, LUMBER]` | :x: |
| TC19 | Board generated with `BeginnerPlacements.forFourPlayers()`, `color` = `PlayerColor.BLUE` | Returns `[LUMBER, ORE, BRICK]` | :x: |
| TC20 | Board generated with `BeginnerPlacements.forFourPlayers()`, `color` = `PlayerColor.ORANGE` | Returns `[ORE, GRAIN, GRAIN]` | :x: |

### Methods under test: `getTiles()`, `getNodes()`, `getEdges()`, `getRobberTileId()`, `getStartingResourceNodeIds()`, `getHarborNodes()`

Read-only getters for `BoardView` and `GameSession`. Verified in TC1 and harbor assertions, plus `IllegalStateException` checks when called before `generateBoard(...)` on generated-state getters (TC22).

## Next section (to be added before that code is written)

Placement validation and gameplay mutations (`isValidSettlementLocation`, `isValidRoadLocation`, `buildSettlement`, `buildRoad`, `moveRobber`). Deferred for later development. Once added, BVA will cover distance rules, road attachment, vacancy checks, and boundary IDs.