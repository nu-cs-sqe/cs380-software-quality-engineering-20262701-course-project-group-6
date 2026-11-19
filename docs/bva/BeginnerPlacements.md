# BVA: `BeginnerPlacements`

`BeginnerPlacements` provides the pre-placed starting settlements, roads, and starred starting resource node IDs
for the Catan beginner setup for either four players or three players (omitting `RED`).

Because `BeginnerPlacements` encapsulates immutable setup maps via static factory methods (`forFourPlayers()` and
`forThreePlayers()`), callers receive unmodifiable map views without constructing or mutating player placement
state directly.

## Steps 1–3 summary

| Input / state | Catalog type | Boundary values |
| ----- | ----- | ----- |
| Player count configuration | Cases (static factory) | `forFourPlayers()` (all 4 colors: `RED`, `ORANGE`, `BLUE`, `WHITE`), `forThreePlayers()` (3 colors: `ORANGE`, `BLUE`, `WHITE`; `RED` omitted) |
| `settlements` map | Map contents | 8 entries (4 players) or 6 entries (3 players, no `RED` values); unmodifiable (`Map.of` / `toUnmodifiableMap`) |
| `roads` map | Map contents | 8 entries (4 players) or 6 entries (3 players, no `RED` values); unmodifiable (`Map.of` / `toUnmodifiableMap`) |
| `startingResourceNodeIds` map | Map contents | 4 entries (4 players) or 3 entries (3 players, no `RED` key); unmodifiable (`Map.of` / `toUnmodifiableMap`) |

`forFourPlayers()` and `forThreePlayers()` return pre-built immutable instances. The package-private
`getSettlements()`, `getRoads()`, and `getStartingResourceNodeIds()` methods expose those maps to `domain` callers
such as `GameBoard`.

## Step 4: Test cases

### Method under test: `forFourPlayers()`

|  | State of the System | Expected output | Implemented? |
| ----- | ----- | ----- | ----- |
| TC1 | Call `BeginnerPlacements.forFourPlayers()` | Returns non-null instance with 8 settlements, 8 roads, and 4 starting resource node IDs across `RED`, `ORANGE`, `BLUE`, and `WHITE` | :white_check_mark: |
| TC2 | Call `BeginnerPlacements.forFourPlayers()` twice | Returns consistent 4-player placements without mutability | :white_check_mark: |

### Method under test: `forThreePlayers()`

|  | State of the System | Expected output | Implemented? |
| ----- | ----- | ----- | ----- |
| TC3 | Call `BeginnerPlacements.forThreePlayers()` | Returns non-null instance with 6 settlements, 6 roads, and 3 starting resource node IDs (`ORANGE`, `BLUE`, `WHITE`), with `RED` absent | :white_check_mark: |

### Methods under test: `getSettlements()`, `getRoads()`, `getStartingResourceNodeIds()`

|  | State of the System | Expected output | Implemented? |
| ----- | ----- | ----- | ----- |
| TC4 | `forFourPlayers()` settlements, roads, and starting resource node IDs | Matches expected node/edge IDs and colors for all 4 colors; attempting to mutate returns maps throws `UnsupportedOperationException` | :white_check_mark: |
| TC5 | `forThreePlayers()` settlements, roads, and starting resource node IDs | Matches non-`RED` settlements, roads, and starting resource node IDs; contains no `RED` placements or key; attempting to mutate returns maps throws `UnsupportedOperationException` | :white_check_mark: |

## Next section (to be added before that code is written)

None planned for `BeginnerPlacements` unless additional beginner setup variants are introduced.