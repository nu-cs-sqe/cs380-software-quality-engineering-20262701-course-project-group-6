# BVA: `Bank`

The bank holds the supply of resource cards (19 of each `ResourceCardType`, rulebook) and the development card deck.
Players are paid from the bank when a number is rolled, and they pay into the bank when they build or buy a
development card.

The bank cannot pay out more cards of a type than it has. It also cannot hold more than 19 of a type: since cards only
move between the bank and the players, a deposit above 19 means a bug somewhere else, so it throws instead of hiding it.

The deck is passed into the constructor. Bank tests use a real `DevelopmentDeck` (coding standard: use the real object
unless the interaction itself must be verified).

## Steps 1–3 summary

| Input / state | Catalog type | Boundary values |
|---|---|---|
| `deck` (constructor) | Pointer | a `DevelopmentDeck`; `null` (throws: TC2) |
| `type` | Cases (enum) + Pointer | any `ResourceCardType`; `null` (throws: TC7, TC13, TC14) |
| `amount` (withdraw and deposit) | Count | 0 (throws: TC6, TC12), 1, >1. -1 is in the same invalid partition as 0 |
| bank count of one type, withdraw | Interval [0, 19] | amount = count (TC4), amount = count + 1 (throws: TC5) |
| bank count of one type, deposit | Interval [0, 19] | count + amount = 19 (TC10), count + amount = 20 (throws: TC11) |
| other types | Cases | unchanged by a withdraw or deposit of a different type (TC8) |

## Step 4: Test cases

### Method under test: `Bank(DevelopmentDeck deck)`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC1  | new bank | 19 of every `ResourceCardType`; 25 development cards left | :x: |
| TC2  | `deck` = `null` | `IllegalArgumentException` with a message saying the deck is required | :x: |

### Method under test: `withdrawResource(ResourceCardType type, int amount)`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC3  | 19 `BRICK`, withdraw 1 | 18 `BRICK` | :x: |
| TC4  | 19 `BRICK`, withdraw 19 | 0 `BRICK` (withdraws the whole supply) | :x: |
| TC5  | 19 `BRICK`, withdraw 20 | `IllegalStateException` with a message naming the type, how many the bank has, and how many were asked for; still 19 `BRICK` | :x: |
| TC6  | 19 `BRICK`, withdraw 0 | `IllegalArgumentException` with a message saying the amount must be at least 1; still 19 `BRICK` | :x: |
| TC7  | `type` = `null` | `IllegalArgumentException` with a message saying the resource type is required | :x: |
| TC8  | 19 of each, withdraw 2 `BRICK` | 19 `ORE` (other types unchanged) | :x: |

### Method under test: `depositResource(ResourceCardType type, int amount)`

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC9  | 16 `BRICK` (3 withdrawn), deposit 1 | 17 `BRICK` | :x: |
| TC10 | 17 `BRICK` (2 withdrawn), deposit 2 | 19 `BRICK` (back to the max) | :x: |
| TC11 | 19 `BRICK`, deposit 1 | `IllegalStateException` with a message naming the type and the max of 19; still 19 `BRICK` | :x: |
| TC12 | 17 `BRICK`, deposit 0 | `IllegalArgumentException` with a message saying the amount must be at least 1; still 17 `BRICK` | :x: |
| TC13 | `type` = `null` | `IllegalArgumentException` with a message saying the resource type is required | :x: |

### Method under test: `getResourceCount(ResourceCardType type)`

Checked in every test case above. Only the error case needs its own test.

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC14 | `type` = `null` | `IllegalArgumentException` with a message saying the resource type is required | :x: |

### Method under test: `withdrawDevelopmentCard()`

Hands the call to the deck. The deck's own boundaries (empty deck, contents) are tested in `DevelopmentDeck.md`.

|      | State of the System | Expected output | Implemented? |
|------|---------------------|-----------------|--------------|
| TC15 | new bank | returns a card; 24 development cards left | :x: |

### Method under test: `getDevelopmentCardsRemaining()`

Simple getter. Checked in TC1 and TC15.
