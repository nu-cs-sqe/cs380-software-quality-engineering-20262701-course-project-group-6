# BVA: `BeginnerBoard`

The beginner board has one fixed configuration and takes no input. Tile IDs start at 0 and follow the five
rows from top to bottom, left to right. Illustration A on page 3 of the
[rulebook](https://cdn.1j1ju.com/medias/7a/18/fd-catan-rulebook.pdf#page=3) defines the expected values.

| ID | Method | State of the system | Expected output | Implemented? |
|----|--------|---------------------|-----------------|--------------|
| TC1 | `getTileRows()`, `getRobberTileId()`, `TileSetup.getResource()`, `TileSetup.getDiceNumber()` | New beginner board | Five rows of 3, 4, 5, 4, and 3 tiles with the exact resources and tokens from Illustration A. Center tile 9 is the desert, has neither a resource nor a token, and starts with the robber. | :white_check_mark: |
| TC2 | `getSettlements()`, `getRoads()`, `getStartingResourceNodes()`, `getHarbors()` | New beginner board with all four colors | Eight settlements and eight roads in their rulebook positions, four settlements marked for starting resources, and nine harbors with the trade types and coastal positions from Illustration A. | :white_check_mark: |
