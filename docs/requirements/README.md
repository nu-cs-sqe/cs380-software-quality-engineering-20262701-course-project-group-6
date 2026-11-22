# System Features

Integration tests follow **thread testing** (lecture 11/09): each feature below is one thread of execution through the
real classes working together, with no mocks. Each tested feature has a Cucumber feature file in
`src/test/resources/features/` (`F1.feature`, ...) and one step-definition class in `src/test/java/integration/`
(`F1Tests.java`, ...). `IntegrationTestSuite` runs them all as part of `./gradlew test`.

Features are numbered in the order the game uses them. Each one matches a user story on the project board.

| ID | Feature | User story | Classes it runs through | Integration test |
|----|---------|------------|-------------------------|------------------|
| F1 | Ability to set up a new game for 3 or 4 players with a full bank and the beginner board (red left out for 3 players) | Story 1 (https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-6/issues/3) | `GameSession`, `GameBoard`, `Bank`, `DevelopmentDeck` | `F1.feature`, `F1Tests.java` |
| F2 | Ability for players to roll for starting order, pick colors in that order, and receive starting resources from the bank, with the pick order becoming the turn order | Story 2 (https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-6/issues/6) | `GameSession`, `StartingRollOff`, `Dice`, `DiceRoll`, `GameBoard`, `Bank`, `Player` | `F2.feature`, `F2Tests.java` |
| F3 | Ability for the current player to roll the dice once per turn, for everyone to collect resources from hexes with that number, and to end the turn | Story 3 (https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-6/issues/31) | `GameSession`, `Dice`, `DiceRoll`, `GameBoard`, `Bank`, `Player` | `F3.feature`, `F3Tests.java` |
| F4 | Ability, on a roll of 7, for players with more than 7 cards to discard half, and for the roller to move the robber and steal a card | Story 4 (https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-6/issues/32) | `GameSession`, `GameBoard`, `Bank`, `Player` | planned |
| F5 | Ability to build roads, settlements, and cities by paying their cost, following the placement rules and piece limits | Story 5 (https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-6/issues/33) | `GameSession`, `GameBoard`, `Bank`, `Player` | `F5.feature`, `F5Tests.java` |
| F6 | Ability to buy development cards and play them on a later turn (Knight, Road Building, Year of Plenty, Monopoly; Victory Point cards count while held) | Story 6 (https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-6/issues/34) | `GameSession`, `Bank`, `DevelopmentDeck`, `Player`, `GameBoard` | planned |
| F7 | Ability to award Longest Road and Largest Army and to detect when a player reaches 10 victory points on their turn | Story 7 (https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-6/issues/35) | `GameSession`, `GameBoard`, `Player` | planned |
