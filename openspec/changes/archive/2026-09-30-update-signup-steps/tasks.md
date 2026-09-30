## 1. Scenario tests (RED first)

- [x] 1.1 In `src/test/java/ch/hirion/journey/RegistrationTest.java` write one guest `@Test` per new REG-7 scenario (3): reg7_planFreePreselectsFree (`walkToStep(4, "/signup?plan=free")`, Free selected, Premium Trial not), reg7_planTrialPreselectsTrial (`walkToStep(4, "/signup?plan=trial")`, Premium Trial selected, Free not), reg7_backKeepsWizardData (step 4 → select Free → "Back" → "Step 3 of 4" and the chip "QA / Test Engineer" in `<main>` → "Continue" → "Step 4 of 4", Free selected, Premium Trial not). Priorities below reg9; reuse `walkToStep`, `planCard`, `SELECTED_CARD`, `continueButton`, `stepLabel`. No test clicks "See my matches". Run `mvn -q test -Dtest=RegistrationTest` WITHOUT `-Dqa.mailbox` and quote the result lines in `change-log.md` (failing lines if RED). Verify: `grep -c "@Test" RegistrationTest.java` gives 15, `grep -n "See my matches" RegistrationTest.java` shows `.click()` only in reg9, and `java scripts/CheckLocatorRules.java src/test/java` prints "PASS: no forbidden patterns".
- [x] 1.2 Fix failures found in 1.1 only in the new tests (a wrong spec string is reported to a human, not changed in the assertion). Verify: `mvn -q test -Dtest=RegistrationTest` without a mailbox shows the 3 new tests passing and reg9 / reg4 / auth6 skipped; the run is quoted in `change-log.md`.

## 2. Journey run (HUMAN approval)

- [x] 2.1 After a human "да": `mvn -q test -Dsuite=testng-journey.xml -Dqa.mailbox=<mailbox>`. Verify: 40 tests, all non-KNOWN-BUG tests pass, JourneyCleanup printed "deleted: ... no longer signs in", `.auth/test-user.json` is gone; the run is quoted in `change-log.md`.

## 3. Gate

- [x] 3.1 Run pnpm check and quote its summary line
