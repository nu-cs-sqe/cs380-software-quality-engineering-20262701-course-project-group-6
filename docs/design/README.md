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
4. Initializing the starting map for beginners (Illustration A on page 3 of the rulebook).
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
Runs one game: sets up the bank and the beginner board (Story 1), lets the players roll for starting order and pick
colors (Story 2), and then controls the turn order. Turn play (rolling for resources, building, buying and playing
development cards, the robber, winning) is added in a later PR once `GameBoard` exists.

* **Responsibilities:**
  * Accept only 3 or 4 players. Create the `Bank` and the `BeginnerBoard` (without red for 3 players).
  * Run the roll for starting order through `StartingRollOff`: each seat rolls 2 dice, highest picks first, ties
    re-roll among themselves (house rule, Story 2).
  * Let each seat, in roll order, pick one of the colors still available. Picking creates that color's `Player`
    (Players are identified only by color, team decision 11/01) and pays its starting resources from the bank: one
    card for each hex next to the color's starred settlement.
  * Once every seat has picked, the pick order becomes the turn order for the whole game (house rule).
  * Later: track which color holds Longest Road and Largest Army (Ryan's suggestion on #26), so `Player` does not
    need to know about them.
* **Key Fields:**
  * `private final Bank bank`
  * `private final BeginnerBoard board` (replaced by `GameBoard` built from `BeginnerBoard`, issue #25)
  * `private final StartingRollOff rollOff`
  * `private final int playerCount`
  * `private List<Integer> pickOrder` (seat numbers 1..playerCount, empty until rolled)
  * `private final Map<PlayerColor, Player> players` (insertion order = pick order = turn order)
  * `private int currentTurnIndex`
* **Key Methods:**
  * `public GameSession(int playerCount, Random random)`: `IllegalArgumentException` unless `playerCount` is 3 or 4,
    or if `random` is `null`. The same `Random` shuffles the deck and rolls the dice, so tests can stub it.
  * `public List<Integer> rollForStartingOrder()`: rolls once and returns the seats in pick order;
    `IllegalStateException` if already rolled.
  * `public int getSeatToPick()`: the seat whose turn it is to pick; `IllegalStateException` before the roll or after
    everyone has picked.
  * `public List<PlayerColor> getAvailableColors()`: colors in play that nobody has picked yet.
  * `public void pickColor(PlayerColor color)`: `IllegalArgumentException` for `null`; `IllegalStateException` if it
    is not time to pick, the color is already taken, or the color is not in play (red with 3 players).
  * `public List<PlayerColor> getTurnOrder()`: colors in pick order (the full turn order once everyone has picked).
  * `public PlayerColor getCurrentColor()`: whose turn it is; `IllegalStateException` while players are still picking.
  * `public int getResourceCount(PlayerColor color, ResourceCardType type)`,
    `public int getVictoryPointCount(PlayerColor color)`: for the UI; `IllegalArgumentException` if no player has
    that color.
  * `public int getBankResourceCount(ResourceCardType type)`: for the UI and for Story 2 criterion 6.

* **`StartingRollOff`:** rolls for starting order with an injected `Random` (two dice, each `nextInt(6) + 1`).
  * `public StartingRollOff(Random random)`: `IllegalArgumentException` if `random` is `null`.
  * `public List<Integer> rollOrder(int seatCount)`: seats `1..seatCount`, highest roll first. Seats that tie roll
    again among themselves (repeating until the tie is broken) and keep their place relative to everyone else.
    `IllegalArgumentException` if `seatCount` is below 2.

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
  * `private int knightsPlayed`
  * `private boolean hasLongestRoad`
  * `private boolean hasLargestArmy`
  * `private final Map<ResourceCardType, Integer> resources`
  * `private final Map<DevCardType, Integer> playableDevCards`
  * `private final Map<DevCardType, Integer> newDevCards` (bought this turn)
  * `private boolean playedDevCardThisTurn`
* **`Player` Key Methods:**
  * `public Player(PlayerColor color)`: Throws `IllegalArgumentException` if `color` is `null`.
  * `public void placeSettlementPiece()`: Increments `settlementsPlaced`; throws `IllegalStateException` if already 5.
  * `public void placeCityPiece()`: Increments `citiesPlaced` and decrements `settlementsPlaced` (the settlement piece
    returns to the supply); throws `IllegalStateException` if already 4 cities or if there is no settlement to upgrade.
  * `public void placeRoadPiece()`: Increments `roadsPlaced`; throws `IllegalStateException` if already 15.
  * `public int getVictoryPointCount()`: Totals up victory points for win condition check (settlements 1, cities 2).
  * `public int getSettlementsRemaining()`, `getCitiesRemaining()`, `getRoadsRemaining()`: Pieces left in the
    supply (max − placed), for the UI. The placed counts themselves are not exposed.
  * `public void collectResource(ResourceCardType type, int amount)`: Adds to the count for `type`.
  * `public void useResource(ResourceCardType type, int amount)`: Throws `IllegalStateException` if the player does
    not have enough.
  * `public int getResourceCount(ResourceCardType type)`: 0 if the player has none of that type.
  * The resource methods throw `IllegalArgumentException` for a `null` type or an `amount` below 1.
  * `public int getTotalResourceCount()`: When robber is rolled, check if >7
  * `public void collectDevCard(DevCardType type)`: Adds one card. It cannot be played until the next turn.
  * `public void useDevCard(DevCardType type)`: Removes one playable card; a Knight adds 1 to the army. Throws
    `IllegalStateException` if the player has no playable copy (none, or only one bought this turn) or has already
    played a card this turn, and `IllegalArgumentException` for `VICTORY_POINT` (never played).
  * `public int getDevCardCount(DevCardType type)`: Every copy held, including ones bought this turn.
  * `public int getKnightsPlayed()`: For Largest Army.
  * `public void endTurn()`: Makes cards bought this turn playable and resets the one-card-per-turn limit.
  * `getVictoryPointCount()` also adds 1 for each Victory Point card held.
  * The development card methods throw `IllegalArgumentException` for a `null` type.

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
#### Story 1: Game setup UI

`BeginnerBoard` holds the starting layout from [Illustration A](https://cdn.1j1ju.com/medias/7a/18/fd-catan-rulebook.pdf#page=3).
The five rows contain the terrain and dice numbers in tile ID order, with the robber on tile 9.
The setup includes starting roads and settlements and the nine harbors. The white star marks the settlement used
to determine each color's starting resources, corresponding to the second settlement in the regular setup.
`BeginnerBoard()` includes all four colors. `createForThreePlayers()` omits red's pieces and starting resource marker.
`getStartingResources(color)` lists the resources of the hexes next to that color's starred settlement, in tile ID order
(white: grain, brick, lumber; red: grain, lumber, lumber; blue: lumber, ore, brick; orange: ore, grain, grain). It is fixed
data from Illustration A until `GameBoard` can find the hexes next to a corner (issue #25).
Settlements and resource markers use corner IDs, while roads and harbors use edge IDs.

`BoardGeometry` derives the 54 corners and 72 sides from the same tile rows. Shared corners and sides get one ID each.
Corners are numbered from top to bottom and then left to right. Sides follow the same order by midpoint.
The home screen's **New Game** button opens `GameSetupView` in the selected language. Players choose 3 or 4 and a
board setup. **Start Game** becomes available after a count is selected with **Beginner board**. Selecting **Manual
setup** disables **Start Game**. Starting a beginner game opens `BoardView`
with the board for the selected count. Color selection and the award of starting resources belong to Story 2.

---

### 3.4 Bank, Deck, and Card Model
Handles game card reserves and initial resource payouts. 

* **Cards (Resource and development):**
  * `ResourceCardType` (Enum): (`LUMBER`, `WOOL`, `GRAIN`, `BRICK`, `ORE`).
  * `DevCardType` (Enum): (`KNIGHT`, `VICTORY_POINT`, `ROAD_BUILDING`, `YEAR_OF_PLENTY`, `MONOPOLY`)
* **`DevelopmentDeck`: Stack of `DevCardType` values, shuffled upon initialization:**
  * Contains 14 knights, 5 victory points, 2 road building, 2 year of plenty, and 2 monopoly
  * `public DevelopmentDeck(Random random)`: The `Random` is passed in so tests can control the shuffle.
  * `public DevCardType draw()`: Removes and returns top card; throws exception if depleted.
  * `public int getRemainingCount()`
* **`Bank` Responsibilities:**
  * Maintain resource card supply stacks (19 of each type: Lumber, Wool, Grain, Brick, Ore).
  * Maintain the 25-card development card deck.
  * Execute initial resource payouts based on tiles surrounding a player's second settlement.
* **`Bank` Key Methods:**
  * `public Bank(Random random)`: Builds its own `DevelopmentDeck` with this `Random`, so only the bank can draw from it.
  * `public void withdrawResource(ResourceCardType type, int amount)`: Removes `amount` cards from that pile; throws
    `IllegalStateException` if the bank has fewer than `amount`.
  * `public void depositResource(ResourceCardType type, int amount)`: Returns `amount` cards to that pile (when a
    player builds or trades); throws `IllegalStateException` if the pile would go above 19.
  * `public int getResourceCount(ResourceCardType type)`
  * `public DevCardType withdrawDevelopmentCard()`: Removes and returns one development card from the top of the pile.
  * `public int getDevelopmentCardsRemaining()`
  * Throws `IllegalArgumentException` for a null type or `Random`, or an amount below 1.


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
