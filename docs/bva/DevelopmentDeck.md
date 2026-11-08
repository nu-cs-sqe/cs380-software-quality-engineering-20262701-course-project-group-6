# BVA: `DevelopmentDeck`

The development card deck holds 25 cards (rulebook): 14 Knights, 5 Victory Points, 2 Road Building, 2 Year of Plenty,
and 2 Monopoly. It is shuffled once when it is created, and cards are drawn from the top until it is empty. Cards are
never returned to the deck.

The deck takes a `Random` in its constructor, so tests can stub the shuffle (coding standard: inject random
dependencies). The game passes in a normal `new Random()`.

## Step 1: Inputs and outputs

| | |
|---|---|
| **Inputs** | `random` (constructor) |
| **State** | the cards left in the deck, and their order |
| **Outputs** | the card returned by `draw()`, the number of cards left, or an exception |

## Steps 2–3: Catalog types and boundary values

For whole numbers, the catalog's "LOW − ε" and "HIGH + ε" are LOW − 1 and HIGH + 1. "Can't set" (catalog term) means
the code makes that value impossible to reach, so there is nothing to test.

### Intervals

| Input / state | Interval | LOW − 1 | LOW | HIGH | HIGH + 1 |
|---|---|---|---|---|---|
| cards left | [0, 25] | -1: drawing from an empty deck throws → TC7 | 0 → TC6 | 25 → TC1 | 26: can't set (nothing adds cards) |

### Collections

| Input / state | Empty | One | Many | Full | Duplicates |
|---|---|---|---|---|---|
| cards in the deck | TC7 (draw throws) | TC6 (draw leaves it empty) | TC5 (24 after a draw) | TC1 (25) | TC2 (14 / 5 / 2 / 2 / 2 copies) |

### Pointers

| Input | Valid value | `null` |
|---|---|---|
| `random` | a `Random` → TC1, TC4 | TC3 (throws) |

### Cases

| Input / state | Values tested | Why |
|---|---|---|
| shuffle | stubbed `Random` → TC4 | shows the deck is shuffled with the `Random` it was given (see below) |

`Collections.shuffle` walks the list from the last card to the second and swaps each with the card at
`random.nextInt(i)`. With a stub whose `nextInt` always returns 0, every card moves up one place and the first card
of the unshuffled list (a Knight) ends up on top. Without a shuffle, the top card would be a Monopoly.

## Step 4: Test cases

### Method under test: `DevelopmentDeck(Random random)`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC1  | new deck | `getRemainingCount()` = 25 | :white_check_mark: |
| TC2  | new deck, draw all 25 cards | 14 `KNIGHT`, 5 `VICTORY_POINT`, 2 `ROAD_BUILDING`, 2 `YEAR_OF_PLENTY`, 2 `MONOPOLY` | :white_check_mark: |
| TC3  | `random` = `null` | `IllegalArgumentException` with a message saying a `Random` is required | :x: |
| TC4  | `random` stub whose `nextInt` always returns 0 | first card drawn is `KNIGHT` (the unshuffled top card would be `MONOPOLY`) | :x: |

### Method under test: `draw()`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC5  | 25 cards left (new deck) | returns a card; 24 cards left | :x: |
| TC6  | 1 card left (24 drawn) | returns a card; 0 cards left | :x: |
| TC7  | 0 cards left (25 drawn) | `IllegalStateException` with a message saying the deck is empty; still 0 cards left | :x: |

### Method under test: `getRemainingCount()`

Simple getter. Checked in TC1 and TC5–TC7.
