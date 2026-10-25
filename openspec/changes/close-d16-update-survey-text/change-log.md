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

(none yet)
