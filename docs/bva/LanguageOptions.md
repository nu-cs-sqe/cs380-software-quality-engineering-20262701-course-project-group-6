# BVA: `LanguageOptions`

`LanguageOptions` reads the list of languages the player can pick from at the start of the game. The list lives in
`src/main/resources/languages.properties`, for example `languages=en,es`. Adding a language means adding its code to
that list and adding its `messages_<code>.properties` file, with no Java changes (A rubric, issue #15).

The UI loads the file into a `java.util.Properties` and passes it in, so tests can build the `Properties` directly
without touching the file system.

## Steps 1–3 summary

| Input / state | Catalog type | Boundary values |
|---|---|---|
| `languageFile` (constructor) | Pointer | a loaded `Properties`; `null` (throws: TC6) |
| `languages` entry | Cases | present; missing (throws: TC5) |
| codes in the `languages` entry | Collection | empty (throws: TC4), one (TC1), many (TC2) |
| spacing around codes | Cases | none (TC2), spaces after commas (TC3) |
| returned list | Collection (copy) | changing it does not change `LanguageOptions` (TC7) |

Order matters: the first language in the file is the one selected when the game starts.

## Step 4: Test cases

### Method under test: `LanguageOptions(Properties languageFile)` and `getLocales()`

|     | State of the System | Expected output | Implemented? |
|-----|---------------------|-----------------|--------------|
| TC1 | `languages=en` | `[en]` | :white_check_mark: |
| TC2 | `languages=en,es` | `[en, es]`, in file order | :white_check_mark: |
| TC3 | `languages=en, es` (space after the comma) | `[en, es]` (spaces are trimmed) | :white_check_mark: |
| TC4 | `languages=` (empty) | `IllegalArgumentException` with a message saying at least one language is required | :white_check_mark: |
| TC5 | no `languages` entry | `IllegalArgumentException` with a message saying the `languages` entry is missing | :white_check_mark: |
| TC6 | `languageFile` = `null` | `IllegalArgumentException` with a message saying the language file is required | :white_check_mark: |
| TC7 | `languages=en,es`, then try to add a locale to the list returned by `getLocales()` | `UnsupportedOperationException`; `getLocales()` still returns `[en, es]` | :x: |
