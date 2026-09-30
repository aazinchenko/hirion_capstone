# Change log -- update-signup-steps

## Group 1 "Scenario tests" (plan step 26, 2026-09-30)

- 1.1 `journey/RegistrationTest`: three guest tests for the new REG-7 scenarios, priority 7 (below reg9 = 10),
  reusing `walkToStep`, `planCard`, `SELECTED_CARD`, `continueButton`, `stepLabel`:
  - `reg7_planFreePreselectsFree` -- `walkToStep(4, "/signup?plan=free")`, Free selected, Premium Trial not;
  - `reg7_planTrialPreselectsTrial` -- `walkToStep(4, "/signup?plan=trial")`, Premium Trial selected, Free not;
  - `reg7_backKeepsWizardData` -- step 4 -> Free -> "Back" -> "Step 3 of 4" and the chip "QA / Test Engineer"
    in `<main>` -> "Continue" -> "Step 4 of 4", Free selected, Premium Trial not. `addRole()` is not called
    after "Back" (design.md: the picker then reads "1 selected").
  No new test clicks "See my matches".
  Checks:
  - `grep -c "@Test" RegistrationTest.java` -> `15`
  - `grep -n "See my matches" RegistrationTest.java` -> line 116 `isVisible()` (reg7_stepFourPremiumTrialSelected)
    and line 202 `.click()` (reg9 only)
  - `java scripts/CheckLocatorRules.java src/test/java` -> `PASS: no forbidden patterns`
  Run `mvn -q test -Dtest=RegistrationTest` (no `-Dqa.mailbox`), surefire summary:
  `Tests run: 15, Failures: 0, Errors: 0, Skipped: 3, Time elapsed: 59.81 s -- in ch.hirion.journey.RegistrationTest`
  - passed: reg3_shortPasswordBlocked, reg5_stepTwoRendered, reg5_stepTwoWithoutFileBlocked,
    reg6_stepThreeRendered, reg6_stepThreeWithoutRoleBlocked, reg7_stepFourPremiumTrialSelected,
    reg7_backKeepsWizardData (6.5 s), reg7_freeCanBeSelected, reg7_planFreePreselectsFree (4.4 s),
    reg7_planTrialPreselectsTrial (3.4 s), reg8_stepsOneToThreeSendNothing, reg10_showPasswordInTabOrder
    (KNOWN BUG D6, expected exception)
  - skipped: reg9_freeSignupLandsOnDashboard ("-Dqa.mailbox=<gmail local part> is required for the journey;
    nothing was created"), reg4_takenEmailBlocked and auth6_wrongPasswordStaysOnLogin (depend on reg9)
  No RED phase: the spec text was confirmed live before the change (docs/intent.md, guest probe 2026-09-30),
  so the new tests were green on their first run. No account was created.
- 1.2 No failures in 1.1, so nothing to fix; the 1.1 run above is also the 1.2 check (3 new tests pass,
  reg9 / reg4 / auth6 skipped).

## Group 2 "Journey run" (human "да" 2026-09-30, mailbox given by the human)

- 2.1 `mvn -q test -Dsuite=testng-journey.xml -Dqa.mailbox=<human's mailbox>`, one test account
  `<mailbox>+hirion-qa-1790785300470@gmail.com` (Free), created by reg9 and deleted by JourneyCleanup.
  `testng-results.xml`: `total="40" passed="40" failed="0" skipped="0"` (surefire `TestSuite` 160.5 s) --
  RegistrationTest 15, DashboardTest 12, SettingsTest 13. The KNOWN BUG tests D6 (REG-10), D10 (SET-4) and
  D14 (SET-2) pass through their expected `AssertionFailedError`, i.e. the defects are still present.
  JourneyCleanup output:
  ```
  [JourneyCleanup] deleting this run's user: <mailbox>+hirion-qa-1790785300470@gmail.com (accountCreated=true)
  [JourneyCleanup] dialog: Delete your account?  This permanently removes your profile, CV, preferences and saved matches. It cannot be undone.  Cancel Delete
  [JourneyCleanup] redirected to https://hirion.ch/; page text after delete: ... (Settings page text still rendered)
  [JourneyCleanup] sign-in did not reach /dashboard; page says: ... Welcome back  Sign in to continue to Hirion. ...
  [JourneyCleanup] deleted: <mailbox>+hirion-qa-1790785300470@gmail.com no longer signs in
  ```
  After the run `.auth/` holds only `manual-user.json`: `.auth/test-user.json` is gone.
  Observation for a human (not asserted): the page-text snapshot right after the redirect to `/` still showed
  the Settings Security section, including "You signed in with Google. Set a password below ..." for an
  email/password account -- probably the snapshot caught the SPA before it re-rendered.

## Group 3 "Gate"

- 3.1 `pnpm check`, first run RED: public suite `Tests run: 59, Failures: 1, Errors: 0, Skipped: 2` -- the
  failure is the config method `closeContext` ("Target page, context or browser has been closed"), which made
  TestNG skip `cont3_honeypotAttributes` and `cont4_validSubmitMocked`; no test assertion failed. The change
  touches neither the public suite nor `support/BaseTest` (human-owned), so this is reported as a flaky
  teardown, not fixed here. Second run, unchanged code, GREEN:
  `PASS: no forbidden patterns` · public suite `Tests run: 58, Failures: 0, Errors: 0, Skipped: 0` ·
  `spec:check ok — specs: 9 · active changes: 1 · archived: 3`
