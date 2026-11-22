Feature: F2 Choose starting positions
  As a Catan player,
  I want us to roll for who picks first, pick our colors in that order, and receive our starting resources,
  so that choosing colors and turn order is fair and we can begin the first turn.

  Scenario: Every seat picks a color and receives its starting resources
    Given a 4-player game where everyone has rolled for starting order
    When the seats pick BLUE, ORANGE, WHITE, RED in roll order
    Then the turn order is BLUE, ORANGE, WHITE, RED
    And it is BLUE's turn
    And the players hold these resources:
      | color  | LUMBER | WOOL | GRAIN | BRICK | ORE |
      | BLUE   | 1      | 0    | 0     | 1     | 1   |
      | ORANGE | 0      | 0    | 2     | 0     | 1   |
      | WHITE  | 1      | 0    | 1     | 1     | 0   |
      | RED    | 2      | 0    | 1     | 0     | 0   |
    And the bank holds these resources:
      | LUMBER | WOOL | GRAIN | BRICK | ORE |
      | 15     | 19   | 15    | 17    | 17  |
    And every player has 2 victory points

  Scenario: Seats pick in the rolled order
    Given a 4-player game where everyone has rolled for starting order
    When the seats pick ORANGE in roll order
    Then the next seat to pick is the second seat in the roll order

  Scenario: A color that is already taken cannot be picked again
    Given a 4-player game where everyone has rolled for starting order
    And the first seat has picked BLUE
    When the next seat tries to pick BLUE
    Then the pick is rejected with the message "BLUE is already taken"
    And the colors left to pick are ORANGE, WHITE, RED

  Scenario: Red is not in play in a 3-player game
    Given a 3-player game where everyone has rolled for starting order
    When the next seat tries to pick RED
    Then the pick is rejected with the message "RED is not in play"
    And the colors left to pick are BLUE, ORANGE, WHITE
