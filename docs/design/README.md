This folder should include the system design. It can be in the form of design diagrams or a textual description of what classes the system shall have and their relationships.

It is optional for the team to document the design in this folder. A popular alternative is to document the design in the Project Management Board.

## GUI Library: Java Swing

# Catan: Game Setup Phase Design Document

## 1. Overview & Scope
This document specifies the software architecture, domain model, and class responsibilities for the **Game Setup Phase** of Catan.

The Game Setup Phase encompasses:
1. Initializing player identities, colors, and game configuration (3–4 players).
2. Generating and laying out the 19 hexagonal board tiles, resource types, and dice roll number tokens.
3. Establishing the game bank, resource card decks, and development card deck.
4. Managing the "Snake Draft" initial placement phase (two settlements and two roads per player).
5. Distributing the starting resource cards corresponding to each player's second placed settlement.
6. Handing off state to the turn manager ready for Player 1's first standard turn.

---

+-------------------+
|    GameSession    |
+-------------------+
|
+------------------------------+------------------------+
|                              |                        |
v                              |                        v
+---------------+              |                  +---------------+
|   GameBoard   |              |                  |      Bank     |
+---------------+              |                  +---------------+
|               |              |                            |
v               v              v                            v
+------+    +-------+         +----------+             +----------+
| Tile |--->| Node  |         |  Player  |             |   Deck   |
+------+    +-------+         +----------+             +----------+
                |                   |                        |
                v                   v                        v
            +------+          +-----------+             +----------+
            | Edge |          | Inventory |             |   Card   |
            +------+          +-----------+             +----------+

---

## 3. Class Specifications

### 3.1 GameSession (Orchestrator & Turn Controller)
Coordinates session lifecycle, game initialization, setup state transitions, and the snake-draft turn sequence without an intermediate manager layer.

* **Responsibilities:**
  * Validate player registration count (enforcing 3–4 players) and instantiate `Player` models.
  * Direct the snake-draft placement sequence (Round 1: index `0` $\rightarrow$ `N - 1`; Round 2: index `N - 1` $\rightarrow$ `0`).
  * Enforce setup action constraints (1 settlement + 1 connected road per turn).
  * Coordinate board, bank, and player inventory updates during setup actions.
  * Trigger initial resource card payouts after Round 2 placements conclude.
* **Key Fields:**
  * `private final GameBoard board`
  * `private final Bank bank`
  * `private final List<Player> players`
  * `private int currentPlayerIndex`
  * `private boolean isReverseSetupRound`
  * `private SetupPhase currentPhase` (`REGISTRATION`, `ROUND_ONE`, `ROUND_TWO`, `COMPLETE`)
* **Key Methods:**
  * `public void initializeGame(List<String> playerNames)`: Validates names/counts, constructs `Player` objects, builds `board`, and fills `bank`.
  * `public Player getCurrentPlayer()`: Returns `players.get(currentPlayerIndex)`.
  * `public void placeInitialSettlement(int nodeId)`: Validates ownership and board rules, updates `Node`, and decrements the active player's settlement reserve.
  * `public void placeInitialRoad(int edgeId, int connectedNodeId)`: Enforces road adjacency to the just-placed settlement, updates `Edge`, and decrements road reserve.
  * `public void advanceSetupTurn()`: Progresses indices (`0` to `N - 1`, flips `isReverseSetupRound`, then `N - 1` down to `0`).
  * `public boolean isSetupComplete()`: Returns true once the reverse round concludes.

---

### 3.2 Player & PlayerInventory
Encapsulates individual player state, remaining structural pieces, and in-hand cards.

* **`Player` Responsibilities:**
  * Maintain immutable metadata (ID, display name, assigned piece color).
  * Maintain counts of unplaced physical pieces available (starts at 5 settlements, 4 cities, 15 roads).
  * Expose convenience methods to manipulate resource/development card holdings via `PlayerInventory`.
* **`Player` Key Fields:**
  * `private final int id`
  * `private final String name`
  * `private final PlayerColor color`
  * `private int remainingSettlements` (init: 5)
  * `private int remainingRoads` (init: 15)
  * `private final PlayerInventory inventory`
* **`Player` Key Methods:**
  * `public void deductSettlementPiece()`: Decrements `remainingSettlements`; throws `IllegalStateException` if 0.
  * `public void deductRoadPiece()`: Decrements `remainingRoads`; throws `IllegalStateException` if 0.
  * `public void addCard(Card card)`: Delegates directly to `inventory.add(card)`.
  * `public PlayerInventory getInventory()`: Returns card collection reference.

* **`PlayerInventory` Responsibilities:**
  * Holds dictionary specifying quantities of each resource card (`Map<ResourceType, Integer>`).
  * Holds list of unplayed and played development cards.
* **`PlayerInventory` Key Methods:**
  * `public void addResource(ResourceType type, int amount)`
  * `public void addDevCard(DevCardType type, int amount)`
  * `public void removeResource(ResourceType type, int amount)`
  * `public void removeDevCard(DevCardType type, int amount)`
  * `public int getTotalResourceCount()`: When robber is rolled, check if >7
  * `public int getResourceCount(ResourceType type)`
  * `public int getDevCardCount()`

---

### 3.3 GameBoard, Tile, Node, Edge (Topological Model)
Encapsulates Catan's hexagonal board topology using an intersection/edge graph representation to enforce structural placement rules.

* **`GameBoard` Responsibilities:**
  * Construct standard board tiles (4 Forest, 4 Pasture, 4 Wheat, 3 Clay, 3 Ore, 1 Desert).
  * Assign dice roll tokens (2 through 12, excluding 7) adhering to standard layout.
  * Enforce the **Distance Rule**: a settlement cannot be placed on a node adjacent to an already occupied node.
  * Enforce setup road attachment: roads placed during setup must attach directly to the settlement placed during the current turn.
* **`GameBoard` Key Methods:**
  * `public void generateBoard(BoardLayoutStrategy strategy)`
  * `public boolean isValidSettlementLocation(int nodeId)`: Confirms node is unoccupied and all 2–3 neighboring nodes have no existing settlements/cities.
  * `public boolean isValidRoadLocation(int edgeId, int connectedNodeId, int playerId)`: Checks edge vacancy and validates direct attachment to `connectedNodeId`.
  * `public void buildSettlement(int nodeId, int playerId)`: Updates target `Node` occupancy.
  * `public void buildRoad(int edgeId, int playerId)`: Updates target `Edge` occupancy.
  * `public List<Tile> getAdjacentTiles(int nodeId)`: Returns tiles bordering an intersection for resource distribution.

* **Board Subcomponents:**
  * `Tile`: Holds `ResourceType` (`LUMBER`, `WOOL`, `GRAIN`, `BRICK`, `ORE`, `DESERT`), `int diceNumber`, `int tileId`, and bounded `Node` references.
  * **`BuildingType` (Enum)**: `NONE`, `SETTLEMENT`, `CITY`.
  * **`Node` (Intersections)**: Holds `int nodeId`, incident `Edge` references, neighboring `Tile` references, `BuildingType buildingType` (default `NONE`), and `Integer ownerPlayerId` (`null` when vacant, or the integer ID of the player who owns the settlement/city).
  * **`Edge` (Paths)**: Holds `int edgeId`, the two endpoint `Node` references, and `Integer roadOwnerPlayerId` (`null` when no road is built, or the integer ID of the player who owns the road).
---

### 3.4 Bank, Deck, and Card Model
Handles game card reserves and initial resource payouts.

* **`Card` Hierarchy:**
  * `public abstract class Card`
  * `ResourceCard extends Card`: Holds `ResourceType` (`LUMBER`, `WOOL`, `GRAIN`, `BRICK`, `ORE`, `DESERT`).
  * `DevelopmentCard extends Card`: Holds `DevCardType` (`KNIGHT`, `VICTORY_POINT`, `ROAD_BUILDING`, `YEAR_OF_PLENTY`, `MONOPOLY`).
* **`Deck<T extends Card>`:**
  * `public void shuffle(Random random)`
  * `public T draw()`: Removes and returns top card; throws exception if depleted.
  * `public int remainingCount()`
* **`Bank` Responsibilities:**
  * Maintain resource card supply stacks (19 of each type: Lumber, Wool, Grain, Brick, Ore).
  * Maintain the 25-card development card deck.
  * Execute initial resource payouts based on tiles surrounding a player's second settlement.
* **`Bank` Key Methods:**
  * `public ResourceCard withdrawResource(ResourceType type)`: Removes one resource card from the respective pile.
  * `public void distributeInitialResources(Player player, List<Tile> adjacentTiles)`: Filters out Desert/Water tiles, extracts corresponding `ResourceCard` instances from stock, and deposits them into `player.getInventory()`.

