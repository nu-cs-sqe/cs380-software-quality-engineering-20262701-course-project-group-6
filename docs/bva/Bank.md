# BVA: `Bank`

The bank holds the supply of resource cards (19 of each `ResourceCardType`, rulebook) and the development card deck.
Players are paid from the bank when a number is rolled, and they pay into the bank when they build or buy a
development card.

The bank cannot pay out more cards of a type than it has. It also cannot hold more than 19 of a type: since cards only
move between the bank and the players, a deposit above 19 means a bug somewhere else, so it throws instead of hiding it.

The bank builds its own `DevelopmentDeck` from a `Random` passed into the constructor. Nothing outside the bank holds
the deck, so cards can only be drawn through the bank (this also keeps SpotBugs from flagging an outside reference to
a mutable deck, EI_EXPOSE_REP2).

Most tests pass a normal `Random`, since the deck itself is tested in `DevelopmentDeck.md`. TC15 passes a stub so
the card drawn is known.

## Step 1: Inputs and outputs

| | |
|---|---|
| **Inputs** | `random` (constructor), `type`, `amount` |
| **State** | how many cards of each `ResourceCardType` the bank holds |
| **Outputs** | the bank's new count of that type, the development card returned, or an exception |

## Steps 2–3: Catalog types and boundary values

For whole numbers, the catalog's "LOW − ε" and "HIGH + ε" are LOW − 1 and HIGH + 1. "Can't set" (catalog term) means
the code makes that value impossible to reach, so there is nothing to test.

### Intervals

| Input / state | Interval | LOW − 1 | LOW | HIGH | HIGH + 1 |
|---|---|---|---|---|---|
| `amount` in `withdrawResource` (bank holds 19) | [1, count] | 0 → TC6 (throws) | 1 → TC3 | 19 → TC4 | 20 → TC5 (throws) |
| `amount` in `depositResource` | [1, 19 − count] | 0 → TC12 (throws) | 1 → TC9 (count 16) | 2 → TC10 (count 17) | 1 → TC11 (count 19, throws) |
| bank count of one type | [0, 19] | -1: can't set (withdraw throws first, TC5) | 0 → TC4 | 19 → TC1, TC10 | 20: deposit throws, TC11 |

Negative amounts are in the same invalid partition as 0 (the same `amount < 1` check), so they have no separate test.

### Pointers

| Input | Valid value | `null` |
|---|---|---|
| `random` | a `Random` → TC1 | TC2 (throws) |
| `type` | a `ResourceCardType` → TC3–TC13 | TC7, TC13, TC14 (throw) |

### Cases

| Input | Values tested | Why |
|---|---|---|
| `type` | all 5 in TC1; `BRICK` elsewhere | every type goes through the same code, so one type stands in for all 5 after setup |
| other types | `ORE` after withdrawing `BRICK` → TC8 | a change to one type must not change another |

## Step 4: Test cases

### Method under test: `Bank(Random random)`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC1  | new bank | 19 of every `ResourceCardType`; 25 development cards left | :white_check_mark: |
| TC2  | `random` = `null` | `IllegalArgumentException` from the deck, with a message saying a `Random` is required | :white_check_mark: |

### Method under test: `withdrawResource(ResourceCardType type, int amount)`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC3  | 19 `BRICK`, withdraw 1 | 18 `BRICK` | :white_check_mark: |
| TC4  | 19 `BRICK`, withdraw 19 | 0 `BRICK` (withdraws the whole supply) | :white_check_mark: |
| TC5  | 19 `BRICK`, withdraw 20 | `IllegalStateException` with a message naming the type, how many the bank has, and how many were asked for; still 19 `BRICK` | :white_check_mark: |
| TC6  | 19 `BRICK`, withdraw 0 | `IllegalArgumentException` with a message saying the amount must be at least 1; still 19 `BRICK` | :white_check_mark: |
| TC7  | `type` = `null` | `IllegalArgumentException` with a message saying the resource type is required | :white_check_mark: |
| TC8  | 19 of each, withdraw 2 `BRICK` | 19 `ORE` (other types unchanged) | :white_check_mark: |

### Method under test: `depositResource(ResourceCardType type, int amount)`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC9  | 16 `BRICK` (3 withdrawn), deposit 1 | 17 `BRICK` | :white_check_mark: |
| TC10 | 17 `BRICK` (2 withdrawn), deposit 2 | 19 `BRICK` (back to the max) | :white_check_mark: |
| TC11 | 19 `BRICK`, deposit 1 | `IllegalStateException` with a message naming the type and the max of 19; still 19 `BRICK` | :white_check_mark: |
| TC12 | 17 `BRICK`, deposit 0 | `IllegalArgumentException` with a message saying the amount must be at least 1; still 17 `BRICK` | :white_check_mark: |
| TC13 | `type` = `null` | `IllegalArgumentException` with a message saying the resource type is required | :white_check_mark: |

### Method under test: `getResourceCount(ResourceCardType type)`

Checked in every test case above. Only the error case needs its own test.

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC14 | `type` = `null` | `IllegalArgumentException` with a message saying the resource type is required | :white_check_mark: |

### Method under test: `withdrawDevelopmentCard()`

Hands the call to the deck. The deck's own boundaries (empty deck, contents) are tested in `DevelopmentDeck.md`.

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC15 | new bank built with a `Random` stub whose `nextInt` always returns 0 | returns the top card of the deck (`KNIGHT`, see `DevelopmentDeck.md` TC4); 24 development cards left | :white_check_mark: |

### Method under test: `getDevelopmentCardsRemaining()`

Simple getter. Checked in TC1 and TC15.
