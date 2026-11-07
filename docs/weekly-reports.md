# Week 3 (10/12/2026-10/18/2026)
**Planning and Progress Tracking**:
1. [done] Omar: Set up project README, GitHub Actions, Management Tool, and Gradle build badge (no links to PR, just pushed to main instead since we could not set up branch protection)
2. [done] Ryan: Scheduled team meeting with professor for Friday, 10/23/2026 at 10:00 AM (no links to PR)
3. [done] Omar: Set up branch protection rules (steps 3, 4, and 6) in GitHub Settings (no PR, configured in repo settings).

# Week 4 (10/19/2026-10/25/2026)
**Planning and Progress Tracking**:
1. [done] Omar: Updated week 3 report to mark branch protection as done (https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-6/pull/1)
2. [done] Ryan: Reviewed and approved Omar's week 3 report update (https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-6/pull/1)
3. [done] All of us: Decided to use the fixed beginner board layout (rulebook p. 3) for the setup phase (no PR, team decision)
4. [done] Ryan: Drafted a setup phase design document and added link to game-rules document (https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-6/pull/2)
5. [done] Omar: Reviewed and merged Ryan's setup phase design document (https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-6/pull/2)
6. [done] Omar: Wrote user story 1 (set up board, pieces, and bank) with acceptance criteria and use case on the project board (no PR, added in GitHub Projects)

# Week 5 (10/26/2026-11/01/2026)
**Planning and Progress Tracking**:
1. [done] Ryan: Updated design document for the beginners' map set-up and enum types for resource and development cards (https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-6/pull/4)
2. [done] Daniel: Reviewed and approved Ryan's design document update (https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-6/pull/4)
3. [done] Daniel: Set up JavaFX and implemented the initial home screen (https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-6/pull/5)
4. [done] Ryan: Reviewed and approved Daniel's JavaFX home screen (https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-6/pull/5)
5. [done] Omar: Wrote user story 2 (roll for starting positions and receive starting resources) with acceptance criteria and use case on the project board (no PR, added in GitHub Projects)
6. [90% done] Omar: Cleaned up design document (removed DESERT resource, Card class leftovers, and typos), waiting on review (https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-6/pull/7)
7. [90% done] Ryan: Implemented PlayerColor, ResourceCardType, DevCardType, HarborType, and BuildingType enums, fixing review comments (https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-6/pull/8)
8. [done] Omar: Reviewed Ryan's enum PR and requested changes (https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-6/pull/8)
9. [not started] Omar: Implement Player class with BVA and TDD (no PR yet)
10. [not started] Ryan: Ask professor about how the desert tile should represent having no resource (no PR, question for professor)

# Week 6 (11/02/2026-11/08/2026)
**Planning and Progress Tracking**:
1. [done] Ryan: Reviewed and approved Omar's design document cleanup, then merged it (https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-6/pull/7)
2. [done] Omar: Filled in the week 4 and 5 reports (https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-6/pull/9)
3. [done] Ryan: Reviewed and approved Omar's week 4 and 5 reports (https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-6/pull/9)
4. [done] Ryan: Asked the professor how the desert tile should show it has no resource. She said not to use null, so Tile's getter will return Optional<ResourceCardType> (empty for the desert) and the TerrainType enum was dropped (no PR, question for professor)
5. [done] Ryan: Fixed the review comments on the enum PR (LUMBER typo, removed NONE and TerrainType) (https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-6/pull/8)
6. [done] Omar: Reviewed and approved Ryan's enum PR, then merged it (https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-6/pull/8)
7. [done] Omar: Wrote the team coding standard and set up Checkstyle, SpotBugs, JaCoCo, PIT (100% mutation threshold), and EasyMock in the Gradle build (https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-6/pull/12)
8. [done] Ryan: Reviewed and approved the coding standard and build tools (https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-6/pull/12)
9. [done] Omar: Asked the professor about the coding standard. Answers: at most 4 parameters per method is required for an A, a remove-and-return method like DevelopmentDeck.draw() is fine under command-query separation, named constants are required in UI and Main too, and an equivalent mutant can be excluded from PIT. All four were added to the coding standard (no PR, question for professor; standard updated in https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-6/pull/12)
10. [done] Omar: Added the tool setup tasks (Checkstyle, SpotBugs, JaCoCo/PIT) and the integration testing and i18n plan tasks to the project board (no PR, added in GitHub Projects)
11. [done] All of us: Decided to merge PlayerInventory into Player, as long as Player never exposes its card map directly (no PR, team decision)
12. [done] Omar: Implemented the Player class (piece placement limits and victory points) with BVA and TDD, one commit per test case (https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-6/pull/16)
13. [done] Ryan: Reviewed Omar's Player PR and requested changes (getters should return pieces remaining instead of pieces placed), then approved after the fix (https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-6/pull/16)
14. [done] Omar: Reviewed and merged the professor's week 6 code review (no violations found) (https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-6/pull/17)
15. [done] Omar: Added resource cards to Player (collect, use, and count) with BVA and TDD, one commit per test case (https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-6/pull/18)
16. [done] Ryan: Reviewed and approved Omar's resource cards PR (https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-6/pull/18)
17. [done] Omar: Added language support (English and Spanish): a language dropdown on the home screen, all UI text moved into language files, and the LanguageOptions class built with BVA and TDD. A new language can be added with only properties files (https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-6/pull/19)
18. [done] Ryan: Reviewed and approved Omar's language support PR (https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-6/pull/19)
19. [in progress] Ryan: Implementing the Tile, Node, and Edge classes with BVA and TDD (https://github.com/nu-cs-sqe/cs380-software-quality-engineering-20262701-course-project-group-6/pull/20)
20. [not started] Daniel: Build the beginner board layout in the UI (no PR yet)
21. [not started] Omar: Ask the professor about the language file approach, integration test scope, and null in private fields (no PR, questions for professor)
22. [not started] Unassigned: Implement Bank and DevelopmentDeck (no PR yet)

# One section per week until the last week of class (the week before the Finals week)


# Week X (XX/XX/2026-XX/XX/2026) TEMPLATE (You can change the format to whatever the team likes better)
**Planning and Progress Tracking**:
1. [done] Person: Task (Links to PR)
2. [not started] Person: Task (Links to PR)
3. [80% done] Person: Task (Links to PR)
