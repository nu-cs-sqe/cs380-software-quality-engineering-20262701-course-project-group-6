### BVA: Tile

A Tile represents a terrain hex on the Catan board. It acts as a pure domain data structure holding its ID, the resource it produces, its dice number token, and the `Node` references that form its corners.

Because `Tile` is a data structure, it does not validate overarching domain logic (like board layout strategies or tracking the robber)—that responsibility belongs to `GameBoard`. However, it strictly 
enforces that its `id` falls within the valid physical bounds of a standard Catan board (19 tiles), its `diceNumber` is valid for Catan rules, and its 6 corner nodes are provided entirely at instantiation 
and remain properly encapsulated.

#### Steps 1–3 summary

| Input / state | Catalog type | Boundary values | 
| ----- | ----- | ----- | 
| id (constructors) | Interval \[0, 18\] | \-1 (throws: TC3), 0, 18, 19 (throws: TC4) | 
| resourceProduced (normal constructor) | Cases (enum) + Pointer | null (throws: TC5), valid ResourceCardType (e.g., BRICK), returns `Optional.empty()` for desert and `Optional.of(ResourceCardType)` for resource tiles | 
| diceNumber (normal constructor) | Intervals \[2, 6] \[8, 12] (excludes 7) | 1 (throws: TC6), 2, 6, 7 (throws: TC7), 8, 12, 13 (throws: TC8) | 
| node1 ... node6 (constructors) | Pointer | null (throws: TC9, TC13), valid Node object (with no duplicates among the 6: TC10, TC14) | 

Callers can inspect the hex via getters (which return immutable copies of the node collection to satisfy SpotBugs).

#### Step 4: Test cases

##### Method under test: Tile(int id, ResourceCardType resourceProduced, int diceNumber, Node node1, Node node2, Node node3, Node node4, Node node5, Node node6)

|  | State of the System | Expected output | Implemented? | 
| ----- | ----- | ----- | ----- | 
| TC1 | id = 0, resourceProduced = ORE, diceNumber = 2, nodes = 6 distinct valid nodes | getId() = 0, getResourceProduced() = `Optional.of(ResourceCardType.ORE)`, getDiceNumber() = 2, getCornerNodes().size() = 6 | :white_check_mark: | 
| TC2 | id = 18, resourceProduced = BRICK, diceNumber = 12, nodes = 6 distinct valid nodes | getId() = 18, getResourceProduced() = `Optional.of(ResourceCardType.BRICK)`, getDiceNumber() = 12 | :white_check_mark: | 
| TC3 | id = -1, resourceProduced = LUMBER, diceNumber = 6, nodes = 6 distinct valid nodes | IllegalArgumentException with a message saying id is out of bounds | :white_check_mark: | 
| TC4 | id = 19, resourceProduced = LUMBER, diceNumber = 6, nodes = 6 distinct valid nodes | IllegalArgumentException with a message saying id is out of bounds | :white_check_mark: | 
| TC5 | id = 1, resourceProduced = null, diceNumber = 6, nodes = 6 distinct valid nodes | IllegalArgumentException with a message saying resourceProduced cannot be null for normal tiles | :white_check_mark: | 
| TC6 | id = 1, resourceProduced = ORE, diceNumber = 1, nodes = 6 distinct valid nodes | IllegalArgumentException with a message saying diceNumber is out of bounds | :white_check_mark: | 
| TC7 | id = 1, resourceProduced = GRAIN, diceNumber = 7, nodes = 6 distinct valid nodes | IllegalArgumentException with a message saying 7 is reserved for the desert | :white_check_mark: | 
| TC8 | id = 1, resourceProduced = WOOL, diceNumber = 13, nodes = 6 distinct valid nodes | IllegalArgumentException with a message saying diceNumber is out of bounds | :white_check_mark: | 
| TC9 | id = 1, resourceProduced = LUMBER, diceNumber = 6, node3 is null | IllegalArgumentException with a message saying corner nodes cannot be null | :x: | 
| TC10 | id = 1, resourceProduced = LUMBER, diceNumber = 6, node2 and node5 are the same Node | IllegalArgumentException with a message saying corner nodes must be distinct | :x: | 

##### Method under test: Tile(int id, Node node1, Node node2, Node node3, Node node4, Node node5, Node node6) (Desert Overload)

|  | State of the System | Expected output | Implemented? | 
| ----- | ----- | ----- | ----- | 
| TC11 | id = 0, nodes = 6 distinct valid nodes | getId() = 0, getResourceProduced() = `Optional.empty()`, getDiceNumber() = 7, getCornerNodes().size() = 6 | :x: | 
| TC12 | id = 18, nodes = 6 distinct valid nodes | getId() = 18, getResourceProduced() = `Optional.empty()`, getDiceNumber() = 7, getCornerNodes().size() = 6 | :x: | 
| TC13 | id = -1, nodes = 6 distinct valid nodes | IllegalArgumentException with a message saying id is out of bounds | :x: |
| TC14 | id = 19, nodes = 6 distinct valid nodes | IllegalArgumentException with a message saying id is out of bounds | :x: | 
| TC15 | id = 9, node1 is null | IllegalArgumentException with a message saying corner nodes cannot be null | :x: | 
| TC16 | id = 9, node4 and node6 are the same Node | IllegalArgumentException with a message saying corner nodes must be distinct | :x: | 

##### Method under test: getCornerNodes()

|  | State of the System | Expected output | Implemented? | 
| ----- | ----- | ----- | ----- | 
| TC17 | Valid normal tile created, call getCornerNodes(), then call .clear() on returned list | UnsupportedOperationException is thrown (proves immutability of returned list) | :x: | 

##### Methods under test: getId(), getResourceProduced(), getDiceNumber()

Simple getters. They are verified as part of the constructor boundary and state initialization tests (implemented in TC1, TC2, TC11, and TC12).