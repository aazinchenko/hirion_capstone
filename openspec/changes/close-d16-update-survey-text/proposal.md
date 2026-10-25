## Why

Two CI runs on 2026-10-25 failed on changes of the site, not of the suite:

- Free suite, 07:38 UTC: `RegistrationTest.reg7_planBasicPreselectsBasic` "should have thrown an exception of type
  AssertionFailedError" -- the KNOWN BUG test of D16 found Basic selected. Reproduced locally by a guest run (no
  account, submit never clicked, `../d16-check-2026-10-25.log`: `Tests run: 4, Failures: 1` = only the KNOWN BUG
  test); the browser keeps `https://hirion.ch/signup?plan=basic` in the URL (before: dropped). D16 is fixed.
- Public suite, 12:01 UTC: `EmailLinkPagesTest.link1_surveyWithoutIdSendsNothing` did not find "This survey link is
  missing its id — please use the link from the email.". Reproduced locally 3/3 (`../link1-run1..3.log`); the
  trace snapshot after "Send feedback" holds the new text "This survey link is missing its id. Please use the link
  from the email.".

Human (2026-10-25): "Да, сделай один change весь цикл сам: LINK-1 новый текст, D16 закрыть (REG-7 plan=basic
обычным тестом, PUB-5 проверять переход по Choose Basic), гостевые проверки, pnpm check, архив и push."

## What Changes

- MODIFIED `LINK-1` (spec `email-link-pages`): the notification text without an id.
- MODIFIED `REG-7` (spec `signup`): D16 fixed; scenario "plan=basic preselects Basic" also checks Premium not
  selected; `reg7_planBasicPreselectsBasic` becomes a regular test.
- MODIFIED `PUB-5` (spec `home-page`): scenario "Basic plan CTA" follows the link and checks the signup URL keeps
  `plan=basic` (was: href only, because of D16).
- docs: D16 FIXED in intent.md / decisions.md / current-state.md / README; no KNOWN BUG test left.

## Capabilities

### New Capabilities

(none)

### Modified Capabilities

- `email-link-pages`: LINK-1 text.
- `signup`: REG-7 D16 closed.
- `home-page`: PUB-5 Basic CTA followed.

## Impact

- Test counts unchanged (public 63, journey 50, full 113); specs 72 requirements, 110 scenarios.
- All affected tests are guest tests: no account, no journey run needed.
