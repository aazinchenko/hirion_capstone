## Why

On 2026-10-06 (17:43-17:44, `allure-results/`) the public KNOWN BUG tests for D2, D4 and D5 were "broken" with
"should have thrown an exception of type class org.opentest4j.AssertionFailedError": the correct assertions now
pass on the live site, so hirion.ch has fixed the three defects. A KNOWN BUG test that no longer fails is a red
suite and hides regressions, so the tests must become regular checks and the specs must stop calling the
behaviour a known defect.

## What Changes

- MODIFIED `I18N-4 Document language follows the chosen language` (spec `localization`): same SHALL and
  scenarios, the note "(Known defect D2.)" is removed.
- MODIFIED `PUB-4 Pricing period toggle exposes its state` (spec `home-page`): same SHALL and scenario, the note
  "(Known defect D4.)" is removed.
- MODIFIED `AUTH-3 Login fields support autofill` (spec `access-and-quality`): same SHALL and scenario, the note
  "(Known defect D5.)" is removed.
- Tests `LocalizationTest.i18n4_htmlLangAfterGerman`, `SmokeTest.pub4_selectedPeriodIsAriaPressed`,
  `AuthPagesTest.auth3_loginAutocomplete` lose `expectedExceptions` and "KNOWN BUG Dn" in the description; their
  assertions stay exactly as strict as now (same attributes, same values).
- `docs/intent.md`, `docs/decisions.md`, `docs/current-state.md`, `README.md`: D2, D4, D5 marked
  "FIXED (seen live 2026-10-06)"; KNOWN BUG list and counts updated.

## Capabilities

### New Capabilities

(none)

### Modified Capabilities

- `localization`: I18N-4 is no longer a known defect (text and scenarios unchanged otherwise).
- `home-page`: PUB-4 is no longer a known defect.
- `access-and-quality`: AUTH-3 is no longer a known defect.

## Impact

- Code: three test methods in `src/test/java/ch/hirion/pub/` (`LocalizationTest`, `SmokeTest`,
  `AuthPagesTest`); no support or journey code.
- Test counts unchanged: public 57, full suite 97. KNOWN BUG tests: D1 x2, D6, D10, D14 (5 instead of 8).
- No live account and no journey run: only the public suite and `pnpm check`.
- `pnpm spec:check` after archive: `specs: 9 · active changes: 0 · archived: 6`; requirement and scenario counts
  unchanged (59 / 93).
