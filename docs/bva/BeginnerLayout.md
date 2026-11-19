# BVA: `BeginnerLayout`

`BeginnerLayout` implements `TileLayout` to provide the 19-tile terrain and dice number token configuration for
Catan's beginner board layout (Illustration A, ordered by tile ID `0` through `18`).

Because `BeginnerLayout` encapsulates a fixed, immutable `Map<Integer, TileSetup>` initialized via `Map.ofEntries(...)`,
callers receive the preconfigured tile mapping without mutating layout state.

## Steps 1–3 summary

| Input / state | Catalog type | Boundary values |
| ----- | ----- | ----- |
| Constructor `BeginnerLayout()` | Default constructor | No arguments; initializes `tiles` with all 19 tile setups (`0`..`18`) |
| `tiles` map entries | Map contents (`0`..`18`) | Tile `0` (`ORE`, `10`), Tile `9` (desert: empty resource & dice number), Tile `18` (`WOOL`, `11`), all 19 entries present |
| Mutability of returned map | Collection state | Unmodifiable `Map` (`Map.ofEntries(...)`); `.put(...)` throws `UnsupportedOperationException` |

`BeginnerLayout()` exposes its tile configuration through `getTiles()` so `GameBoard` can construct the `Tile`
instances on the board graph.

## Step 4: Test cases

### Method under test: `BeginnerLayout()` & `getTiles()`

|  | State of the System | Expected output | Implemented? |
| ----- | ----- | ----- | ----- |
| TC1 | Construct `BeginnerLayout` and call `getTiles()` | Returns non-null map with 19 entries (`0`..`18`) matching the beginner layout (including Tile `0` = `ORE` / `10`, desert Tile `9` = empty resource & dice number, Tile `18` = `WOOL` / `11`) | :x: |
| TC2 | Construct `BeginnerLayout`, call `getTiles()`, and attempt to insert a new entry | Throws `UnsupportedOperationException` without modifying the layout map | :x: |

## Next section (to be added before that code is written)

None planned for `BeginnerLayout` unless additional tile layouts are introduced.