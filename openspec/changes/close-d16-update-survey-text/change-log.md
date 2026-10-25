# Change log -- close-d16-update-survey-text

## 2026-10-25: two site changes found by CI

- CI Free run 2026-10-25 07:38 UTC (pasted by the human): `Tests run: 36, Failures: 1` --
  `reg7_planBasicPreselectsBasic ... should have thrown an exception of type class org.opentest4j.AssertionFailedError`;
  JourneyCleanup "deleted: ***+hirion-qa-...@gmail.com no longer signs in".
- Local guest check (`../d16-check-2026-10-25.log`): `Tests run: 4, Failures: 1` (only the KNOWN BUG test);
  browser: `{"url":"https://hirion.ch/signup?plan=basic","step":"Step 1 of 4"}` (plan kept).
- CI public run 2026-10-25 12:01 UTC (pasted): `Tests run: 63, Failures: 1` -- `link1_surveyWithoutIdSendsNothing`
  "waiting for getByText("This survey link is missing its id — please use the link from the email.", ...)".
- Local 3/3 (`../link1-run1..3.log`): same failure; trace snapshot texts: {'This survey link is missing its id.
  Please use the link from the email.', ...}.

## Evidence for tasks

- 1.1 [x], 1.3 [x]: `EmailLinkPagesTest` new text, `SmokeTest.pub5_basicPlanCta` clicks "Choose Basic" and checks
  `/signup?...plan=basic`. `mvn test -Dtest=EmailLinkPagesTest,SmokeTest` (`../close-d16-pub-2026-10-25.log`):
  `Tests run: 16, Failures: 0, Errors: 0, Skipped: 0`.
- 1.2 [x]: `reg7_planBasicPreselectsBasic` regular test (`assertSelected("Basic")`: Basic true, Free and Premium
  false); unused imports `Issue`, `AssertionFailedError` removed. Guest run `-Dsuite=testng-free.xml
  -Dtest=RegistrationTest#reg7_*` without `-Dqa.mailbox` (`../close-d16-reg7-2026-10-25.log`): `Tests run: 7,
  Failures: 0, Errors: 0, Skipped: 0`; `.auth` holds only `manual-user.json`. `mvn -q test-compile` exit 0,
  `pnpm check:locators` -> "PASS: no forbidden patterns".
- 1.4 [x]: intent.md D16 "FIXED (CI Free run 2026-10-25 ...)", survey text updated; decisions.md "KNOWN BUG
  tests: none open"; current-state.md change 17 bullet; README "D16 fixed by the site 2026-10-25".
- 2.1 [x]: `pnpm exec openspec validate close-d16-update-survey-text --strict` -> "Change
  'close-d16-update-survey-text' is valid"; `pnpm check` exit 0 (`../pnpm-check-2026-10-25.log`): "PASS: no forbidden
  patterns", public `total="63" passed="63" failed="0" skipped="0"`, "spec:check ok — specs: 11 · active changes:
  1 · archived: 15".
