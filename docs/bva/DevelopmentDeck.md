# BVA: `DevelopmentDeck`

The development card deck holds 25 cards (rulebook): 14 Knights, 5 Victory Points, 2 Road Building, 2 Year of Plenty,
and 2 Monopoly. It is shuffled once when it is created, and cards are drawn from the top until it is empty. Cards are
never returned to the deck.

The deck takes a `Random` in its constructor, so tests can stub the shuffle (coding standard: inject random
dependencies). The game passes in a normal `new Random()`.

## Steps 1–3 summary

| Input / state | Catalog type | Boundary values |
|---|---|---|
| `random` (constructor) | Pointer | a `Random`; `null` (throws: TC3) |
| cards in the deck | Collection, Interval [0, 25] | 25 (new deck: TC1), 1 (TC6), 0 (TC7, throws). 26 can't be set: nothing adds cards |
| deck contents | Collection (duplicates) | 14 / 5 / 2 / 2 / 2 copies of the five `DevCardType` values (TC2) |
| shuffle | Cases | shuffled order differs from the unshuffled order (TC4) |

`Collections.shuffle` walks the list from the last card to the second and swaps each with the card at
`random.nextInt(i)`. With a stub whose `nextInt` always returns 0, every card moves up one place and the first card
of the unshuffled list (a Knight) ends up on top. Without a shuffle, the top card would be a Monopoly.

## Step 4: Test cases

### Method under test: `DevelopmentDeck(Random random)`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC1  | new deck | `getRemainingCount()` = 25 | :x: |
| TC2  | new deck, draw all 25 cards | 14 `KNIGHT`, 5 `VICTORY_POINT`, 2 `ROAD_BUILDING`, 2 `YEAR_OF_PLENTY`, 2 `MONOPOLY` | :x: |
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
