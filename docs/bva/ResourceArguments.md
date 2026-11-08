# BVA: `ResourceArguments`

`ResourceArguments` is a package-private helper pulled out of `Player` and `Bank` during a TDD refactor step, so both
classes check resource arguments the same way and the checks are written once (Clean Code: DRY). It has no public
methods, and its boundaries are tested through the public methods that call it.

| Check | Boundary values | Tested in |
|---|---|---|
| `requireType(type)` | `null` (throws) | Player TC21, TC26, TC29; Bank TC7, TC13, TC14 |
| `requirePositiveAmount(amount)` | 0 (throws), 1 | Player TC19, TC25, TC17; Bank TC6, TC12, TC3 |
