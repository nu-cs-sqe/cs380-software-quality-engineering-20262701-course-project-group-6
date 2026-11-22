Feature: F5 Build roads, settlements, and cities
  As a Catan player,
  I want to spend resources on my turn to build roads, settlements, and cities,
  so that I can expand across the board, collect more resources, and earn victory points.

  White starts with 1 lumber, 1 grain and 1 brick, settlements on corners 19 and 35,
  and roads on edges 25 and 37.

  Scenario: Building a road pays the bank
    Given a 3-player game with turn order WHITE, BLUE, ORANGE and dice that roll 3, 4
    When WHITE rolls the dice
    And WHITE builds a road on edge 24
    Then edge 24 has a WHITE road
    And WHITE holds these resources:
      | LUMBER | WOOL | GRAIN | BRICK | ORE |
      | 0      | 0    | 1     | 0     | 0   |
    And the bank has 18 LUMBER and 18 BRICK

  Scenario: Building a settlement after collecting its cost scores a point
    Given a 3-player game with turn order WHITE, BLUE, ORANGE and dice that roll 2, 3, 3, 3, 5, 6, 3, 4
    When WHITE rolls the dice
    And WHITE builds a road on edge 24
    And WHITE ends the turn
    And BLUE rolls the dice
    And BLUE ends the turn
    And ORANGE rolls the dice
    And ORANGE ends the turn
    And WHITE rolls the dice
    And WHITE builds a settlement on corner 17
    Then corner 17 has a WHITE SETTLEMENT
    And WHITE has 3 victory points

  Scenario: Upgrading a settlement to a city scores another point
    Given a 3-player game with turn order WHITE, BLUE, ORANGE and dice that roll 2, 6, 2, 6, 2, 6, 6, 6
    When WHITE rolls the dice
    And WHITE ends the turn
    And BLUE rolls the dice
    And BLUE ends the turn
    And ORANGE rolls the dice
    And ORANGE ends the turn
    And WHITE rolls the dice
    And WHITE builds a city on corner 19
    Then corner 19 has a WHITE CITY
    And WHITE has 3 victory points

  Scenario: A player cannot build before rolling
    Given a 3-player game with turn order WHITE, BLUE, ORANGE and dice that roll 3, 4
    When WHITE tries to build a road on edge 24
    Then the build is rejected with the message "WHITE must roll before building"

  Scenario: A player cannot build what they cannot afford
    Given a 3-player game with turn order WHITE, BLUE, ORANGE and dice that roll 3, 4
    When WHITE rolls the dice
    And WHITE builds a road on edge 24
    And WHITE tries to build a road on edge 23
    Then the build is rejected with the message "WHITE cannot afford a road"
    And edge 23 has no road
