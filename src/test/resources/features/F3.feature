Feature: F3 Take a turn
  As a Catan player,
  I want to roll the dice at the start of my turn and have everyone collect resources from the hexes with that number,
  so that we all get cards to build with and the game moves to the next player.

  Scenario: Rolling an 8 pays every settlement next to an 8 hex
    Given a 3-player game with turn order WHITE, BLUE, ORANGE where the dice will roll 2 and 6
    When the current player rolls the dice
    Then the dice show 2 and 6 for a total of 8
    And the players now hold these resources:
      | color  | LUMBER | WOOL | GRAIN | BRICK | ORE |
      | WHITE  | 1      | 0    | 1     | 1     | 1   |
      | BLUE   | 2      | 0    | 0     | 1     | 1   |
      | ORANGE | 0      | 0    | 2     | 0     | 1   |
    And the bank now holds these resources:
      | LUMBER | WOOL | GRAIN | BRICK | ORE |
      | 16     | 19   | 16    | 17    | 16  |

  Scenario: Rolling a 7 pays nobody
    Given a 3-player game with turn order WHITE, BLUE, ORANGE where the dice will roll 3 and 4
    When the current player rolls the dice
    Then the dice show 3 and 4 for a total of 7
    And the players now hold these resources:
      | color  | LUMBER | WOOL | GRAIN | BRICK | ORE |
      | WHITE  | 1      | 0    | 1     | 1     | 0   |
      | BLUE   | 1      | 0    | 0     | 1     | 1   |
      | ORANGE | 0      | 0    | 2     | 0     | 1   |

  Scenario: Ending the turn passes the dice to the next player
    Given a 3-player game with turn order WHITE, BLUE, ORANGE where the dice will roll 3 and 4
    When the current player rolls the dice
    And the current player ends the turn
    Then the turn passes to BLUE

  Scenario: A player cannot end the turn before rolling
    Given a 3-player game with turn order WHITE, BLUE, ORANGE where the dice will roll 3 and 4
    When the current player tries to end the turn
    Then the action is rejected with the message "WHITE must roll before ending the turn"
    And the turn passes to WHITE

  Scenario: A player cannot roll twice in one turn
    Given a 3-player game with turn order WHITE, BLUE, ORANGE where the dice will roll 3 and 4
    When the current player rolls the dice
    And the current player tries to roll again
    Then the action is rejected with the message "WHITE already rolled this turn"
