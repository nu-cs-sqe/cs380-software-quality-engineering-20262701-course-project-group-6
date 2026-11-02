# Team 6 Coding Standard

This is the standard every PR is reviewed against. Rules marked **(enforced)** are checked by
Checkstyle or SpotBugs, and the build fails if they are broken. The rest are checked by the reviewer
during code review.

Sources: Clean Code ch. 2–7, 9, and 17 (Smells and Heuristics), the course slides on code review,
error handling, BVA, and TDD, and the Google Java Style Guide.

## 1. Tools

| Tool | What it checks | Config |
|---|---|---|
| Checkstyle 10.12.5 | Formatting, naming, Clean Code size limits | `config/checkstyle/checkstyle.xml` |
| SpotBugs | Likely bugs (null problems, bad equals, exposed internals, ...) | `build.gradle.kts` |

- Run everything locally before pushing: `./gradlew check`
- Reports: `build/reports/checkstyle/main.html` and `build/reports/spotbugs/main.html`
- CI runs `./gradlew build`, which includes `check`, so a PR cannot merge with violations.
- Checkstyle allows **0 warnings**. SpotBugs must report **no bugs** at medium confidence or higher.

## 2. Formatting

- 4 spaces per indent, no tabs **(enforced)**
- Lines at most 120 characters **(enforced)**
- Braces on every `if`, `else`, `for`, `while`, and `do`, even for one line **(enforced)**
- Opening brace on the same line **(enforced)**
- One statement per line and one variable per declaration **(enforced)**
- No wildcard imports such as `import java.util.*`, and no unused imports **(enforced)**
- One blank line between methods. No double blank lines **(enforced)**
- Wrapped lines start with the operator or `.` and are indented 8 spaces **(enforced)**
- Newspaper order: fields, then constructors, then public methods, then the private methods they call, placed
  below their caller.

## 3. Naming (Clean Code ch. 2, ch. 17 N1–N7)

- Classes and enums are nouns in `UpperCamelCase`: `Player`, `GameBoard` **(enforced)**
- Methods are verbs in `lowerCamelCase`: `placeSettlement`, `getResourceCount` **(enforced)**
- Constants are `UPPER_SNAKE_CASE`: `MAX_SETTLEMENTS` **(enforced)**
- Packages are lowercase: `domain`, `ui` **(enforced)**
- Names say what something is or does. No abbreviations or single letters except loop indexes
  (`settlementsPlaced`, not `sp` or `numS`).
- No encodings or prefixes (`m_`, `I` for interfaces, type names in variable names).
- One word per concept across the codebase. If it is `place` in `placeSettlement`, it is not `build` or `add`
  in `buildRoad`.
- Names describe side effects. A method that creates something if it is missing is not just `get...`.

## 4. Functions and classes (Clean Code ch. 3, 6, ch. 17 F1–F4, G5–G36)

- Methods are at most 30 lines **(enforced)**. Aim for under 10.
- At most 3 parameters **(enforced)**. If you need more, group them into an object.
- At most one level of nested `if` and one level of nested `for` **(enforced)**
- Cyclomatic complexity at most 10 per method **(enforced)**
- No magic numbers in domain code. Use named constants **(enforced)**: `MAX_ROADS = 15`, not `15`.
- A method does one thing at one level of abstraction.
- No flag (boolean) arguments. Write two methods instead.
- No output arguments. Return the result.
- Command-query separation: a method either changes state or returns information, not both.
- No duplication (DRY). Repeated `switch` on a type usually means it should be polymorphism or an enum method.
- Prefer enums to `int` or `String` constants (J3).
- Hide data: fields are `private`. Expose behavior, not blind getters and setters.
- Law of Demeter: talk to your direct collaborators, not `a.getB().getC().doThing()`.
- No dead code or commented-out code.
- Inject dependencies that are random or external, such as dice (`Random`), so tests can stub them.

## 5. Error handling (Clean Code ch. 7, error handling slides)

- Use exceptions, not error codes or "funny" return values like `-1`.
- Use unchecked exceptions: `IllegalArgumentException` for a bad argument and `IllegalStateException` when the
  object is in the wrong state for the call.
- **Every exception has a meaningful message** that says what went wrong and with what values:
  `throw new IllegalStateException("Player " + color + " has no settlements left to place (max " + MAX_SETTLEMENTS + ")");`
- Never "eat" an exception with an empty `catch` **(enforced)**
- Do not catch `Exception`, `RuntimeException`, or `Throwable` **(enforced)**. Catch the specific type.
- Do not return `null` and do not pass `null`. Return an empty collection, an `Optional`, or throw.
  `null` may exist inside a class, but it never leaves through a public method.
- Validate input at the edge, where the UI calls into the domain.
- Every error case appears in the BVA and has its own test.

## 6. Comments (Clean Code ch. 4, ch. 17 C1–C5)

- Explain with code first: a good name beats a comment.
- Javadoc is only required on public methods whose behavior is not obvious from the name, for example what they
  throw. If Javadoc is written, it must be well-formed **(enforced)**.
- OK: intent, warnings, `TODO` with a reason, and public API docs.
- Not OK: redundant comments, commented-out code, change logs or bylines, and comments that are out of date.

## 7. User-facing text (i18n)

- No hardcoded user-visible text in UI code. Every label, button, and message shown to the player comes from a
  `ResourceBundle` (`messages_<language>.properties`).
- Adding a language means adding one properties file, with no changes to existing code.
- Domain exception messages are for developers and stay in English. The UI translates errors into a
  user-facing message from the bundle.

## 8. Tests (Clean Code ch. 9, ch. 17 T1–T9, BVA and TDD slides)

- Test names follow `Feature_StateUnderTest_ExpectedBehavior`, for example
  `PlaceSettlement_FiveAlreadyPlaced_ThrowsIllegalStateException`.
- Every public method has a BVA table in `docs/bva/<ClassName>.md`. Each row has an ID, the method, the state,
  the expected output, and an implemented flag. Every row has a matching test.
- TDD: write the failing test first, then the minimum code to pass, then refactor. Commit each passing test
  together with its code and the BVA "implemented" update so the history shows the cycle.
- Build-operate-check: arrange, act, assert. One concept per test.
- F.I.R.S.T.: fast, independent, repeatable, self-validating, timely.
- Test the boundaries (0, 1, max, max + 1) and every error case.
- Use EasyMock only when the interaction itself must be verified. Otherwise use a stub or the real object.
- Goal: 100% mutation kill and cyclomatic coverage, except View, Main, and enums.

## 9. Git and pull requests

- Never push to `main`. All work goes through a PR with 1 approval and passing CI.
- Branch names: `<type>-<short-description>` in lowercase with hyphens. Types: `feature`, `fix`, `docs`,
  `build`, `test`, `refactor`. Examples: `feature-player`, `docs-design-cleanup`.
- Pull `main` into your branch before opening a PR, and fix conflicts on your branch.
- PR title: short imperative summary, for example "Add Player piece placement limits".
- PR description: what changed, why, which user story it belongs to, and whether the BVA was updated.
- PR lifecycle: open as a **draft** while working, mark **Ready for review** when done, reviewers
  approve or request changes, the author addresses comments, and the author merges once approved.
- Reviewers comment on specific lines, explain the reason, and propose a fix. Review the code, not the person.
- For a large PR, the reviewer can ask the author for a code walkthrough.

## 10. Review checklist (Clean Code ch. 17)

Before approving a PR, the reviewer checks:

- [ ] `./gradlew check` passes in CI (Checkstyle and SpotBugs clean)
- [ ] BVA file updated and every BVA row has a test
- [ ] Tests were written first (commit history) and test names follow the convention
- [ ] Names are descriptive and consistent (N1–N7)
- [ ] Methods are small, do one thing, and have no flag or output arguments (F1–F4, G30)
- [ ] No duplication, dead code, or commented-out code (G5, G9, C5)
- [ ] No magic numbers (G25), and enums are used instead of constants (J3)
- [ ] Every exception has a meaningful message, and no `null` is returned or passed
- [ ] No hardcoded user-visible text in UI code
- [ ] Code matches the design doc, or the design doc is updated in the same PR
