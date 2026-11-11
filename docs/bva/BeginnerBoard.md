# BVA: `BeginnerBoard`

The beginner board has fixed terrain and harbor positions. Tile IDs start at 0 and follow the five rows from top
to bottom, left to right. Illustration A on page 3 of the
[rulebook](https://cdn.1j1ju.com/medias/7a/18/fd-catan-rulebook.pdf#page=3) defines the expected values.

## Steps 1–3 summary

| Input / state | Catalog type | Boundary values |
|---|---|---|
| Player count (setup menu) | Cases | 3 (TC3), 4 (TC2). The menu offers only these values. |
| Starting placements and resource markers | Cases | Include red for four players (TC2), omit red for three (TC3). |
| Terrain, harbors, and robber | Cases | Same fixed layout for both player counts (TC1–TC3). |

## Step 4: Test cases

| ID | Method | State of the system | Expected output | Implemented? |
|----|--------|---------------------|-----------------|--------------|
| TC1 | `getTileRows()`, `getRobberTileId()`, `TileSetup.getResource()`, `TileSetup.getDiceNumber()` | New beginner board | Five rows of 3, 4, 5, 4, and 3 tiles with the exact resources and tokens from Illustration A. Center tile 9 is the desert, has neither a resource nor a token, and starts with the robber. | :white_check_mark: |
| TC2 | `BeginnerBoard()`, `getSettlements()`, `getRoads()`, `getStartingResourceNodes()`, `getHarbors()` | New beginner board with all four colors | Eight settlements and eight roads in their rulebook positions, four settlements marked for starting resources, and nine harbors with the trade types and coastal positions from Illustration A. | :white_check_mark: |
| TC3 | `createForThreePlayers()`, `getSettlements()`, `getRoads()`, `getStartingResourceNodes()`, `getTileRows()`, `getRobberTileId()`, `getHarbors()` | New beginner board for three players | Six settlements and six roads in the blue, orange, and white starting positions, and three resource markers. Terrain, robber, and harbors match the four-player layout. | :white_check_mark: |
