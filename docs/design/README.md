This folder should include the system design. It can be in the form of design diagrams or a textual description of what classes the system shall have and their relationships.

It is optional for the team to document the design in this folder. A popular alternative is to document the design in the Project Management Board.

## GUI Library: Java FX

# Catan: Game Design Document

## 1. Overview & Scope

This document specifies the software architecture, domain model, and class responsibilities for the **Game Setup Phase** of Catan.

The Game Setup Phase encompasses:
1. Initializing player identities, colors, and game configuration (3–4 players).
2. Generating and laying out the 19 hexagonal board tiles, resource types, and dice roll number tokens.
3. Establishing the game bank, resource card decks, and development card deck.
4. Initializing starting set-up for beginners (found on page 13 of rule book).
5. Handing off state to the turn manager ready for Player 1's first standard turn.

---

## 2. Core Domain Architecture

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
| Tile |<-->| Node  |         |  Player  |             |   Deck   |
+------+    +-------+         +----------+             +----------+
                |                   |                        |
                v                   v                        v
            +------+          +-----------+             +----------+
            | Edge |          | Inventory |------------>| CardType |
            +------+          +-----------+             +----------+

---

## 3. Class Specifications

### 3.1 GameSession (Orchestrator & Turn Controller)
Coordinates session lifecycle, game initialization, setup phase, turn taking, and resource distribution.

* **Responsibilities:**
  * Validate player registration count (enforcing 3–4 players) and instantiate `Player` models.
  * Initializes beginners' map set-up including pre-placed settlements and roads
  * Coordinates board, bank, and player updates during setup actions and gameplay.
  * Trigger initial resource card payouts after starting settlement placements conclude.
* **Key Fields:**
  * `private final GameBoard board`
  * `private final Bank bank`
  * `private final List<Player> players`
  * `private int currentPlayerIndex`
  * `private GamePhase currentPhase` (`SET_UP`, `GAMEPLAY`, `GAME_OVER`)
* **Key Methods:**
  * `public void initializeGame(List<String> playerNames)`: Validates names/counts, constructs `Player` objects, builds `board`, and fills `bank`.
  * `public Player getCurrentPlayer()`: Returns `players.get(currentPlayerIndex)`.
  * `public void placeSettlement(int nodeId)`: Validates ownership and board rules, updates `Node`, and decrements the active player's settlement reserve.
  * `public void placeRoad(int edgeId, int connectedNodeId)`: Enforces road adjacency to the just-placed settlement, updates `Edge`, and decrements road reserve.

---

### 3.2 Player
Encapsulates one color's state: placed structural pieces, victory points, and in-hand cards. One `Player` is created
for each color in play during setup (Story 1); in Story 2, picking a color means taking that color's `Player`.
`PlayerInventory` was merged into `Player` (team decision 11/04).

* **`Player` Responsibilities:**
  * Identified by its assigned piece color (no separate id or display name).
  * Maintain counts of placed pieces (max 5 settlements, max 4 cities, max 15 roads). Starts with the 2 beginner
    settlements and 2 beginner roads already placed.
  * Maintain counts from other sources of victory points (longest road length, army size, number of victory point dev cards).
  * Hold resource and development card counts. The card maps are never returned directly; other classes use the
    methods below or get a read-only copy.
* **`Player` Key Fields:**
  * `private final PlayerColor color`
  * `private int settlementsPlaced` (init: 2)
  * `private int citiesPlaced` (init: 0)
  * `private int roadsPlaced` (init: 2)
  * `private int longestRoadLength`
  * `private int armySize`
  * `private boolean hasLongestRoad`
  * `private boolean hasLargestArmy`
  * `private int victoryPointDevCards`
  * `private final Map<ResourceCardType, Integer> resources`
  * `private final Map<DevCardType, Integer> devCards`
* **`Player` Key Methods:**
  * `public Player(PlayerColor color)`: Throws `IllegalArgumentException` if `color` is `null`.
  * `public void placeSettlementPiece()`: Increments `settlementsPlaced`; throws `IllegalStateException` if already 5.
  * `public void placeCityPiece()`: Increments `citiesPlaced` and decrements `settlementsPlaced` (the settlement piece
    returns to the supply); throws `IllegalStateException` if already 4 cities or if there is no settlement to upgrade.
  * `public void placeRoadPiece()`: Increments `roadsPlaced`; throws `IllegalStateException` if already 15.
  * `public int getVictoryPointCount()`: Totals up victory points for win condition check (settlements 1, cities 2).
  * `public int getSettlementsRemaining()`, `getCitiesRemaining()`, `getRoadsRemaining()`: Pieces left in the
    supply (max − placed), for the UI. The placed counts themselves are not exposed.
  * `public void collectResource(ResourceCardType type, int amount)`
  * `public void useResource(ResourceCardType type, int amount)`: Throws `IllegalStateException` if the player does
    not have enough.
  * `public int getResourceCount(ResourceCardType type)`
  * `public int getTotalResourceCount()`: When robber is rolled, check if >7
  * `public void collectDevCard(DevCardType type, int amount)`
  * `public void useDevCard(DevCardType type, int amount)`
  * `public int getDevCardCount()`

---

### 3.3 GameBoard, Tile, Node, Edge (Topological Model)
Encapsulates Catan's hexagonal board topology using an intersection/edge graph representation to enforce structural placement rules.

* **`GameBoard` Responsibilities:**
  * Construct standard board tiles (4 Forests, 4 Pastures, 4 Fields, 3 Hills, 3 Mountains, 1 Desert).
  * Assign dice roll tokens (2 through 12, excluding 7) adhering to standard layout.
  * Enforce the **Distance Rule**: a settlement cannot be placed on a node adjacent to an already occupied node.
  * Enforce setup road attachment: roads must attach directly to an existing settlement or road, and cannot cross enemy roads in the way.
  * Keeps track of robber location with `robberTileId` field. 
  * Maintains dictionaries for tiles, nodes, and edges keyed by their ids.
* **`GameBoard` Key Methods:**
  * `public void generateBoard(BoardLayoutStrategy strategy)`
  * `public boolean isValidSettlementLocation(int nodeId)`: Confirms node is unoccupied and all 2–3 neighboring nodes have no existing settlements/cities.
  * `public boolean isValidRoadLocation(int edgeId, int connectedNodeId, int playerId)`: Checks edge vacancy and validates direct attachment to `connectedNodeId`.
  * `public void buildSettlement(int nodeId, int playerId)`: Updates target `Node` occupancy.
  * `public void buildRoad(int edgeId, int playerId)`: Updates target `Edge` occupancy.
  * `public List<Tile> getAdjacentTiles(int nodeId)`: Returns tiles bordering an intersection for resource distribution during setup phase.
  * `public void moveRobber(int tileId)`: Moves robber to different tile.

* **Board Subcomponents:**
  * `Tile`: Holds `ResourceCardType` (`LUMBER`, `WOOL`, `GRAIN`, `BRICK`, `ORE`) or unassigned (`null`) for the desert tile, `int diceNumber`, `int tileId`,  and bounded `Node` references.
  * **`BuildingType` (Enum)**: `NONE`, `SETTLEMENT`, `CITY`.
  * **`HarborType` (Enum)**: `NONE`, `GENERIC_THREE_FOR_ONE`, `LUMBER_TWO_FOR_ONE`, `WOOL_TWO_FOR_ONE`, `GRAIN_TWO_FOR_ONE`, `BRICK_TWO_FOR_ONE`, `ORE_TWO_FOR_ONE`.
  * **`Node` (Intersections)**: Holds `int nodeId`, incident `Edge` references, neighboring `Tile` references, `BuildingType buildingType` (default `NONE`), `Integer ownerPlayerId` (`null` when vacant, or the integer ID of the player who owns the settlement/city), and `HarborType harborType` (default `NONE`).
  * **`Edge` (Paths)**: Holds `int edgeId`, the two endpoint `Node` references, and `Integer roadOwnerPlayerId` (`null` when no road is built, or the integer ID of the player who owns the road).
---

### 3.4 Bank, Deck, and Card Model
Handles game card reserves and initial resource payouts. 

* **Cards (Resource and development):**
  * `ResourceCardType` (Enum): (`LUMBER`, `WOOL`, `GRAIN`, `BRICK`, `ORE`).
  * `DevCardType` (Enum): (`KNIGHT`, `VICTORY_POINT`, `ROAD_BUILDING`, `YEAR_OF_PLENTY`, `MONOPOLY`)
* **`DevelopmentDeck`: Stack of `DevCardType` values, shuffled upon initialization:**
  * Contains 14 knights, 5 victory points, 2 road building, 2 year of plenty, and 2 monopoly
  * `public DevCardType draw()`: Removes and returns top card; throws exception if depleted.
  * `public int remainingCount()`
* **`Bank` Responsibilities:**
  * Maintain resource card supply stacks (19 of each type: Lumber, Wool, Grain, Brick, Ore).
  * Maintain the 25-card development card deck.
  * Execute initial resource payouts based on tiles surrounding a player's second settlement.
* **`Bank` Key Methods:**
  * `public void withdrawResource(ResourceCardType type)`: Removes one resource card from the respective pile.
  * `public DevCardType withdrawDevelopmentCard()`: Removes and returns one development card from the top of the pile.


---

### 3.5 Language support (i18n)
The player picks a language on the home screen. Adding a language needs no Java changes (issue #15).

* **`src/main/resources/languages.properties`**: `languages=en,es`, in menu order. The first language is selected at startup.
* **`messages_<code>.properties`**: one file per language with every piece of user-visible text (`home.newGameButton=New Game`).
  `MessageBundlesTest` fails the build if a listed language is missing its file or any key that English has.
* **`LanguageOptions`** (package `language`): reads the language list and returns it as a read-only `List<Locale>`.
  Throws `IllegalArgumentException` if the file is `null`, has no `languages` entry, or lists no languages.
* **UI**: every screen gets its text from the `ResourceBundle` for the chosen `Locale`, never from hardcoded strings.
  The language menu shows each language in its own language ("English", "Español").
* **To add a language**: add its code to `languages.properties` and add `messages_<code>.properties` with every key.
