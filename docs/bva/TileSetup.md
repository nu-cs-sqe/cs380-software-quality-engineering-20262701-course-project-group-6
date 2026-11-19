# BVA: `TileSetup`

A `TileSetup` represents the initial terrain and dice token configuration for a single hex tile in a `BoardLayout`. It
acts as an immutable domain data structure holding an optional `ResourceCardType` and an optional dice number (both
empty for a desert tile).

Because `TileSetup` is a setup data structure, it does not validate full board rules (like dice token ranges or tile
adjacency)—that responsibility belongs to `Tile` and `GameBoard`. However, it strictly enforces that a resource tile
cannot be constructed with a `null` resource.

## Steps 1–3 summary

| Input / state | Catalog type | Boundary values |
| ----- | ----- | ----- |
| `resource` (resource constructor) | Pointer + Cases (enum) | `null` (throws: TC2), any valid `ResourceCardType` (e.g., `ORE` / returns `Optional.of(ORE)`) |
| `diceNumber` (resource constructor) | Integer | valid dice token (e.g., `10` / returns `OptionalInt.of(10)`) |
| desert state (no-arg constructor) | Empty / absent | no arguments (`getResource()` = `Optional.empty()`, `getDiceNumber()` = `OptionalInt.empty()`) |

Only package-private classes in `domain` (such as `BoardLayout` implementations) can construct `TileSetup` instances.
Once constructed, all fields are final and read-only through `getResource()` and `getDiceNumber()`.

## Step 4: Test cases

### Method under test: `TileSetup(ResourceCardType resource, int diceNumber)`

|  | State of the System | Expected output | Implemented? |
| ----- | ----- | ----- | ----- |
| TC1 | `resource` = `ResourceCardType.ORE`, `diceNumber` = `10` | `getResource()` = `Optional.of(ResourceCardType.ORE)`, `getDiceNumber()` = `OptionalInt.of(10)` | :white_check_mark: |
| TC2 | `resource` = `null`, `diceNumber` = `10` | `IllegalArgumentException` with a message saying `resource` cannot be null | :white_check_mark: |

### Method under test: `TileSetup()`

|  | State of the System | Expected output | Implemented? |
| ----- | ----- | ----- | ----- |
| TC3 | no-arg constructor (desert tile) | `getResource()` = `Optional.empty()`, `getDiceNumber()` = `OptionalInt.empty()` | :white_check_mark: |

### Methods under test: `getResource()`, `getDiceNumber()`

Simple getters. They are verified as part of the constructor state initialization tests (implemented in TC1 and TC3).