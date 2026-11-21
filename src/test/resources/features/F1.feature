Feature: F1 Set up a new game
  As a Catan player,
  I want the bank and the beginner board to be set up when a new game starts,
  so that we are ready to choose our starting positions.

  Scenario: Starting a 4-player game
    When a new game is started for 4 players
    Then the colors to pick from are BLUE, ORANGE, WHITE, RED
    And the bank has 19 of every resource

  Scenario: Starting a 3-player game leaves red out
    When a new game is started for 3 players
    Then the colors to pick from are BLUE, ORANGE, WHITE
    And the bank has 19 of every resource

  Scenario Outline: A game needs 3 or 4 players
    When a new game is started for <players> players
    Then the game is rejected with the message "A game needs 3 or 4 players (was <players>)"

    Examples:
      | players |
      | 2       |
      | 5       |
