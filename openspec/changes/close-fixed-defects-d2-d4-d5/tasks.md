## 1. Scenario tests first (confirm the fix on the live site)

- [ ] 1.1 Run the three existing scenario tests unchanged: `mvn -q test -Dtest="LocalizationTest#i18n4_htmlLangAfterGerman+SmokeTest#pub4_selectedPeriodIsAriaPressed+AuthPagesTest#auth3_loginAutocomplete"` (public only, no `-Dqa.mailbox`). Verify: each fails with "should have thrown an exception of type class org.opentest4j.AssertionFailedError"; quote the three failing lines in `change-log.md`. If any of them fails with a TimeoutError or an attribute mismatch instead, stop: that defect is not fixed and stays KNOWN BUG.

## 2. Turn the KNOWN BUG tests into regular tests

- [ ] 2.1 `LocalizationTest`: in `i18n4_htmlLangAfterGerman` remove `expectedExceptions` and "-- KNOWN BUG D2 ..." from the description, drop `QUICK_ATTR`, keep the assertion `lang="de"`; section comment `// I18N-4`; the `switchTo` Javadoc and comment no longer mention bug D2; remove the unused `AssertionFailedError` import. Verify: `grep -n "D2\|AssertionFailedError" src/test/java/ch/hirion/pub/LocalizationTest.java` finds nothing.
- [ ] 2.2 `SmokeTest`: same for `pub4_selectedPeriodIsAriaPressed` (D4); drop the local `quick` timeout, keep both `aria-pressed` assertions; remove imports left unused. Verify: `grep -n "D4\|expectedExceptions" src/test/java/ch/hirion/pub/SmokeTest.java` finds nothing.
- [ ] 2.3 `AuthPagesTest`: same for `auth3_loginAutocomplete` (D5); keep both `waitFor()` preconditions and both `autocomplete` assertions, drop `QUICK_ATTR`; remove imports left unused. Verify: `grep -n "D5\|expectedExceptions" src/test/java/ch/hirion/pub/AuthPagesTest.java` finds nothing.
- [ ] 2.4 Rerun the command from 1.1. Verify: `Tests run: 3, Failures: 0, Errors: 0`; `grep -rn "KNOWN BUG D[245]" src/test/java` finds nothing; `pnpm check:locators` prints "PASS: no forbidden patterns". Quote all three in `change-log.md`.

## 3. Docs

- [ ] 3.1 `docs/intent.md`: under D2, D4, D5 add "FIXED (seen live 2026-10-06): regular test now"; `docs/decisions.md`: KNOWN BUG tests become D1, D6, D10, D14, plus a line "Fixed by the site: D2, D4, D5 (change `close-fixed-defects-d2-d4-d5`)"; `docs/current-state.md` and `README.md` defect lines and KNOWN BUG counts updated. Verify: `grep -n "D2\|D4\|D5" docs/intent.md docs/decisions.md docs/current-state.md` and `grep -n "KNOWN BUG" README.md`, quoted in `change-log.md`.

## 4. Gate

- [ ] 4.1 Run `pnpm exec openspec validate close-fixed-defects-d2-d4-d5 --strict` and `pnpm check`; quote the summary lines in `change-log.md` (validate "is valid"; public suite 57 tests, 0 failures; spec:check ok).
