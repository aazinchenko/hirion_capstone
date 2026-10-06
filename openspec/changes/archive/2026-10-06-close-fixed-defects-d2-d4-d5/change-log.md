# Change log -- close-fixed-defects-d2-d4-d5

## 2026-10-06: D2, D4, D5 fixed by the site

- Trigger: public run 2026-10-06 17:43-17:44 (`allure-results/`): the KNOWN BUG tests for D2, D4, D5 were
  "broken" with "should have thrown an exception of type class org.opentest4j.AssertionFailedError".
- tasks.md 1.1: the `-Dtest` value was corrected from `+` to `,` between classes (Surefire syntax); same tests.

## Evidence for tasks

- 1.1 [x]: `mvn -q test -Dtest="LocalizationTest#i18n4_htmlLangAfterGerman,SmokeTest#pub4_selectedPeriodIsAriaPressed,AuthPagesTest#auth3_loginAutocomplete"` ->
  `Method AuthPagesTest.auth3_loginAutocomplete()[...] should have thrown an exception of type class org.opentest4j.AssertionFailedError`;
  `Method LocalizationTest.i18n4_htmlLangAfterGerman()[...] should have thrown an exception of type class org.opentest4j.AssertionFailedError`;
  `Method SmokeTest.pub4_selectedPeriodIsAriaPressed()[...] should have thrown an exception of type class org.opentest4j.AssertionFailedError`;
  `Tests run: 3, Failures: 3, Errors: 0, Skipped: 0`. No TimeoutError, no attribute mismatch: all three fixed.
- 2.1 [x]: `grep -n "D2\|AssertionFailedError" src/test/java/ch/hirion/pub/LocalizationTest.java` -> no output (exit 1).
- 2.2 [x]: `grep -n "D4\|expectedExceptions" src/test/java/ch/hirion/pub/SmokeTest.java` -> no output (exit 1);
  unused imports `LocatorAssertions`, `AssertionFailedError` removed.
- 2.3 [x]: `grep -n "D5\|expectedExceptions" src/test/java/ch/hirion/pub/AuthPagesTest.java` -> no output (exit 1);
  both `waitFor()` preconditions kept.
- 2.4 [x]: same `mvn test -Dtest=...` as 1.1 -> `Tests run: 3, Failures: 0, Errors: 0, Skipped: 0` / `BUILD SUCCESS`;
  `grep -rn "KNOWN BUG D[245]" src/test/java` -> no output (exit 1);
  `pnpm check:locators` -> "PASS: no forbidden patterns".
- 3.1 [x]: `grep -n "D2\|D4\|D5" docs/intent.md docs/decisions.md docs/current-state.md` -> `intent.md:94`, `:98`,
  `:100` (D2, D4, D5, each followed by "FIXED (seen live 2026-10-06): regular test now ..."); `decisions.md:49`
  ("Fixed by the site: D2, D4, D5 ..."; line 48 now "KNOWN BUG tests: D1, D6, D10, D14"); `current-state.md:44`
  ("Defects: D1, D6, D10 KNOWN BUG tests; D2, D4, D5 FIXED by the site ...").
  `grep -n "KNOWN BUG" README.md` -> `11: ... 7 pinned by KNOWN BUG tests; 3 of them (D2, D4, D5) fixed by the site, now regular tests`.
- 4.1 [x]: `pnpm exec openspec validate close-fixed-defects-d2-d4-d5 --strict` -> "Change 'close-fixed-defects-d2-d4-d5' is valid".
  First `pnpm check`: exit 1 -- known flake, not this change: `ContactFormTest>BaseTest.closeContext:55 TargetClosed
  Error { message='Target page, context or browser has been closed'` in `@AfterMethod` after `cont1_formRendered`
  passed (`tests="58" errors="0" skipped="4" failures="1"`, the 4 skips are the rest of ContactFormTest; human-owned
  `support/BaseTest`, not fixed here). Rerun `pnpm check`: exit 0 -- "PASS: no forbidden patterns"; public suite
  `TEST-TestSuite.xml tests="57" errors="0" skipped="0" failures="0"`;
  "spec:check ok — specs: 9 · active changes: 1 · archived: 5".
