# BVA: `Edge`

An `Edge` represents a path on the Catan board's topological grid. It acts as a pure domain data structure holding its
ID, its two fixed endpoint `Node` references, and the `PlayerColor` of the player who built a road on it.

Because `Edge` is a data structure, it does not validate domain logic (like whether a road is legally placed)—that 
responsibility belongs to `GameBoard`. However, it strictly enforces that its endpoint nodes cannot be null and that 
its `id` falls within the valid physical bounds of a standard Catan board.

## Steps 1–3 summary

| Input / state | Catalog type | Boundary values | 
| ----- | ----- | ----- | 
| `id` (constructor) | Interval \[0, 71\] | \-1 (throws: TC3), 0, 71, 72 (throws: TC4) | 
| `node1` (constructor) | Pointer | `null` (throws: TC5), valid `Node` object | 
| `node2` (constructor) | Pointer | `null` (throws: TC6), valid `Node` object | 
| `roadColor` | Cases (enum) + Pointer | `null` (unoccupied / returns `Optional.empty()`), any valid `PlayerColor` (e.g., `BLUE` / returns `Optional.of(PlayerColor)`) | 

Callers can inspect the topology via getters and check if a road exists via `isOccupied()`. Only package-private classes
(like `GameBoard`) can mutate the `roadColor` state via `setRoadColor()`. Once set, the roadColor assignment is final.

## Step 4: Test cases

### Method under test: `Edge(int id, Node node1, Node node2)`

|  | State of the System | Expected output | Implemented? | 
| ----- | ----- | ----- | ----- | 
| TC1 | `id` = 0, `node1` = valid `Node`, `node2` = valid `Node` | `getEdgeId()` = 0, endpoints match inputs, `getRoadColor()` = `Optional.empty()`, `isOccupied()` = `false` | :x: | 
| TC2 | `id` = 71, `node1` = valid `Node`, `node2` = valid `Node` | `getEdgeId()` = 71, endpoints match inputs, `getRoadColor()` = `Optional.empty()`, `isOccupied()` = `false` | :x: | 
| TC3 | `id` = -1, `node1` = valid `Node`, `node2` = valid `Node` | `IllegalArgumentException` with a message saying id is out of bounds | :x: | 
| TC4 | `id` = 72, `node1` = valid `Node`, `node2` = valid `Node` | `IllegalArgumentException` with a message saying id is out of bounds | :x: | 
| TC5 | `id` = 0, `node1` = `null`, `node2` = valid `Node` | `IllegalArgumentException` with a message saying endpoint `node1` cannot be null | :x: | 
| TC6 | `id` = 0, `node1` = valid `Node`, `node2` = `null` | `IllegalArgumentException` with a message saying endpoint `node2` cannot be null | :x: | 

### Method under test: `setRoadColor(PlayerColor roadColor)` & `getRoadColor()`

|  | State of the System | Expected output | Implemented? | 
| ----- | ----- | ----- | ----- | 
| TC7 | new edge (`roadColor` = `null`), call `setRoadColor(PlayerColor.BLUE)` | `getRoadColor()` returns `Optional.of(PlayerColor.BLUE)` | :x: | 
| TC8 | new edge (`roadColor` = `null`), call `setRoadColor(null)` | `IllegalArgumentException` with a message saying `roadColor` cannot be null | :x: | 

### Method under test: `isOccupied()`

|  | State of the System | Expected output | Implemented? | 
| ----- | ----- | ----- | ----- | 
| TC9 | `roadColor` = `null` (default state) | `false` | :x: | 
| TC10 | `roadColor` = `PlayerColor.BLUE` | `true` | :x: | 

### Methods under test: `getEdgeId()`, `getNode1()`, `getNode2()`

Simple getters. They are verified as part of the constructor boundary and state initialization tests (implemented in TC1 and TC2).

## Next section (to be added before that code is written)

Topological graph traversal helper: `getOppositeNode(Node node)`. Deferred for later development. Once added, BVA will cover valid endpoint arguments vs invalid/unrelated node references throwing `IllegalArgumentException`.