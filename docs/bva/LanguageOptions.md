# BVA: `LanguageOptions`

`LanguageOptions` reads the list of languages the player can pick from at the start of the game. The list lives in
`src/main/resources/languages.properties`, for example `languages=en,es`. Adding a language means adding its code to
that list and adding its `messages_<code>.properties` file, with no Java changes (A rubric, issue #15).

The UI loads the file into a `java.util.Properties` and passes it in, so tests can build the `Properties` directly
without touching the file system.

## Step 1: Inputs and outputs

| | |
|---|---|
| **Inputs** | `languageFile` (constructor) |
| **State** | the list of locales read from the file |
| **Outputs** | the list returned by `getLocales()`, or an exception |

## Steps 2–3: Catalog types and boundary values

There are no numeric inputs, so there are no intervals or counts.

### Collections

| Input / state | Empty | One | Many |
|---|---|---|---|
| codes in the `languages` entry | TC4 (throws) | TC1 | TC2 |

### Pointers

| Input | Valid value | `null` |
|---|---|---|
| `languageFile` | a loaded `Properties` → TC1 | TC6 (throws) |

### Cases

| Input / state | Values tested | Why |
|---|---|---|
| `languages` entry | present → TC1; missing → TC5 (throws) | a file without the entry is a setup mistake that should be reported clearly |
| spacing around codes | none → TC2; a space after the comma → TC3 | people often type `en, es`, so spaces are trimmed |
| returned list | try to change it → TC7 | callers get a copy, so they cannot change `LanguageOptions` (Clean Code ch. 6) |

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
| TC7 | `languages=en,es`, then try to add a locale to the list returned by `getLocales()` | `UnsupportedOperationException`; `getLocales()` still returns `[en, es]` | :white_check_mark: |
