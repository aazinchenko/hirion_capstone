## 1. Scenario tests first (confirm the fixes on the live site)

- [ ] 1.1 Guest run, no account: `mvn test -Dtest="RegistrationTest#reg10_showPasswordInTabOrder"` (no `-Dqa.mailbox`) on the unchanged code. Verify: it fails with "should have thrown an exception of type class org.opentest4j.AssertionFailedError"; quote the line in `change-log.md`.
- [x] 1.2 Journey on the unchanged code: GitHub Actions run 2026-10-06 16:32 UTC (journey code last changed in `af1fb90`). Verify: `reg10_showPasswordInTabOrder`, `set2_requiredProfileFields` and `set4_invalidEmailNotSent` failed with "should have thrown", 37 other tests passed, JourneyCleanup "deleted"; quoted in `change-log.md` with the run URL. Nothing is run locally with `-Dqa.mailbox`.

## 2. Turn the KNOWN BUG tests into regular tests

- [ ] 2.1 `RegistrationTest.reg10_showPasswordInTabOrder` (D6): remove `expectedExceptions`, `expectedExceptionsMessageRegExp` and "-- KNOWN BUG D6 ..." from the description, drop `QUICK_ATTR`; keep the `Assert.assertEquals` precondition and the `tabindex` assertion; section comment `// REG-10`; remove imports left unused. Verify: `grep -n "D6\|KNOWN BUG" src/test/java/ch/hirion/journey/RegistrationTest.java` finds nothing.
- [ ] 2.2 `SettingsTest.set2_requiredProfileFields` (D14): same removals; keep the `REQUIRED_FIELDS` loop and its `AssertionFailedError`. Verify: `grep -n "KNOWN BUG D14" src/test/java/ch/hirion/journey/SettingsTest.java` finds nothing.
- [ ] 2.3 `SettingsTest.set4_invalidEmailNotSent` (D10): same removals; section comment `// SET-4`; keep the catch-all route, the 3 s `waitForCondition` and the safety `finally`; the route comment points to "change add-user-journey design.md" instead of the bug. Verify: `grep -n "KNOWN BUG\|known defect" src/test/java/ch/hirion/journey/SettingsTest.java` finds nothing; `mvn -q test-compile` exits 0; `pnpm check:locators` prints "PASS: no forbidden patterns".
- [ ] 2.4 Rerun 1.1 as a guest (no `-Dqa.mailbox`). Verify: `Tests run: 1, Failures: 0`; quote in `change-log.md`.

## 3. Docs

- [ ] 3.1 `docs/intent.md`: under D6, D10, D14 add "FIXED (seen live 2026-10-06): regular test now (<id>, change close-fixed-defects-d6-d10-d14)", and fix the stale "known defects D1-D10 are KNOWN BUG tests" line; `docs/decisions.md`: KNOWN BUG tests become D1 only, "Fixed by the site" line extended; `docs/current-state.md` defect line and `README.md` defect row updated. Verify: `grep -n "D6\|D10\|D14" docs/intent.md docs/decisions.md docs/current-state.md` and `grep -n "KNOWN BUG" README.md`, quoted in `change-log.md`.

## 4. Gate

- [ ] 4.1 Human: after the test commit and push, start the journey in GitHub Actions (Run workflow). Verify: run URL, "Tests run: 40, Failures: 0" and the JourneyCleanup "deleted" line, quoted in `change-log.md`. The archive waits for this green run.
- [ ] 4.2 Run `pnpm exec openspec validate close-fixed-defects-d6-d10-d14 --strict` and `pnpm check`; quote the summary lines in `change-log.md` (validate "is valid"; public suite 57 tests, 0 failures; spec:check ok).
