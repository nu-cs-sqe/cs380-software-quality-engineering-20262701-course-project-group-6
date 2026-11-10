# BVA: `Node`

A `Node` represents an intersection on the Catan board's topological grid. It acts as a pure domain data structure holding its ID, its physical harbor feature, the edges connected to it, and dynamic game state indicating if a player has built a settlement or city on it.

Because `Node` is a data structure, it does not validate domain logic (like whether a settlement is legally placed or if the distance rule is respected)—that responsibility belongs to `GameBoard`. It strictly enforces that its `id` falls within valid physical bounds and that its fixed `harborType` cannot be null.

## Steps 1–3 summary

| Input / state | Catalog type | Boundary values |
|---|---|---|
| `id` (constructor) | Interval [0, 53] | -1 (throws: TC3), 0, 53, 54 (throws: TC4) |
| `harborType` (constructor) | Cases (enum) + Pointer | `null` (throws: TC5), valid `HarborType` (e.g., `NONE`, `GENERIC_THREE_FOR_ONE`) |
| `edge` (`addIncidentEdge`) | Pointer | `null` (ignored: TC8), valid unique `Edge` (added: TC6), duplicate `Edge` (ignored: TC7) |
| `buildingType` | Cases (enum) | `NONE` (default), `SETTLEMENT`, `CITY` |
| `buildingColor` | Cases (enum) + Pointer | `null` (unoccupied / returns `Optional.empty()`), any valid `PlayerColor` (returns `Optional.of(PlayerColor)`) |

Callers can inspect the topology via getters (which return immutable copies of collections to satisfy SpotBugs). Only package-private classes (like `GameBoard`) can mutate the building state and edge connections.

## Step 4: Test cases

### Method under test: `Node(int id, HarborType harborType)`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC1  | `id` = 0, `harborType` = `NONE` | `getId()` = 0, `getHarborType()` = `NONE`, `getIncidentEdges()` is empty, `getBuildingColor()` = `Optional.empty()`, `getBuildingType()` = `BuildingType.NONE`, `isOccupied()` = `false` | :white_check_mark: |
| TC2  | `id` = 53, `harborType` = `GENERIC_THREE_FOR_ONE` | `getId()` = 53, `getHarborType()` = `GENERIC_THREE_FOR_ONE` | :white_check_mark: |
| TC3  | `id` = -1, `harborType` = `NONE` | `IllegalArgumentException` with a message saying id is out of bounds | :white_check_mark: |
| TC4  | `id` = 54, `harborType` = `NONE` | `IllegalArgumentException` with a message saying id is out of bounds | :white_check_mark: |
| TC5  | `id` = 0, `harborType` = `null` | `IllegalArgumentException` with a message saying harborType cannot be null | :white_check_mark: |

### Method under test: `addIncidentEdge(Edge edge)`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC6  | Node has 0 edges, add valid new `Edge` | `getIncidentEdges()` returns list of size 1 containing the edge | :white_check_mark: |
| TC7  | Node has 1 edge, add the *same* `Edge` again | `IllegalArgumentException` with a message saying that the edge was already added | :white_check_mark: |
| TC8  | Node has 0 edge, add `null` edge | `IllegalArgumentException` with a message saying that edge cannot be null, `getIncidentEdges()` returns empty list | :white_check_mark: |

### Methods under test: `setBuildingType(BuildingType)`, `setBuildingColor(PlayerColor)` & `getBuildingColor()`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC9  | new node (`buildingColor` = `null`), call `setBuildingType(SETTLEMENT)` and `setBuildingColor(PlayerColor.BLUE)` | `getBuildingType()` returns `SETTLEMENT`, `getBuildingColor()` returns `Optional.of(PlayerColor.BLUE)` | :x: |
| TC10 | new node (`buildingColor` = `null`), call `setBuildingColor(null)` | `IllegalArgumentException` with a message saying buildingColor cannot be null | :x: |
| TC11 | new node (`buildingType` = `BuildingType.NONE`), call `setBuildingType(null)` | `IllegalArgumentException` with a message saying buildingType cannot be null | :x: |

### Method under test: `isOccupied()`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC12 | `buildingType` = `NONE`, `buildingColor` = `null` (default) | `false` | :x: |
| TC13 | `buildingType` = `SETTLEMENT`, `buildingColor` = `PlayerColor.BLUE` | `true` | :x: |

### Methods under test: `getId()`, `getHarborType()`, `getBuildingType()`, `getIncidentEdges()`

Simple getters. They are verified as part of the constructor boundary and state initialization tests, as well as the edge addition tests.