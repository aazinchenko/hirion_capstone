# Change log -- sync-specs-with-site-scan

## 2026-10-23: full site scan

- Human (2026-10-23): "На сайте Хирион.ЦХ новая поведение ... просканировал сайт ... по всему функционалу ... проапдейтил спеки".
- Guest suites against the live site, no account created: `mvn test` (public) -> `Tests run: 57, Failures: 0,
  Errors: 0, Skipped: 0` (`../public-scan-2026-10-23.log`); `mvn test -Dsuite=testng-journey.xml
  -Dtest=RegistrationTest` without `-Dqa.mailbox` -> `Tests run: 17, Failures: 0, Errors: 0, Skipped: 3`
  (`../reg-guest-scan-2026-10-23.log`).
- Nightly CI run 39918465621 (2026-10-23 07:32 UTC): journey job failure, public job success; Allure summary
  `failed 1, passed 92, total 93`; failed test "DASH-2 Locked teaser for a Free user": "Locator expected to have
  attribute 'href': /settings?section=plan / Received: /settings?section=plan&buy=basic".
- Live browser, guest (2026-10-23):
  - `/login`: buttons "Continue with Google", "Continue with Apple", "Show password", "Sign in"; text "OR"; links
    "Back to home" -> `/`, "Forgot password?", "Create one".
  - `/signup` step 1: "Continue with Google", "Continue with Apple", "OR", "Already have an account? Sign in".
  - `/forgot-password`: link "Back to home" -> `/login` (D19 candidate).
  - `/reset-password`: title "Set a new password — Hirion", h1 "Set a new password", "This reset link is invalid
    or has expired. Request a new one.", link "Send reset link" -> `/forgot-password`.
  - `/survey` -> `/survey?id=`: title "Share your feedback, Hirion", h1 "Help us make Hirion better", questions
    1-6, button "Send feedback" (not clicked); radios `q2-4`, `q2-3`, `q2-2` have empty labels (D17 candidate).
  - `/unsubscribe`: title "Unsubscribe — Hirion", h1 "Invalid link", "This unsubscribe link is invalid or has
    expired."
  - `/checkout/return`: h1 "No session found", "We couldn't find a checkout session in this URL.", link "Go to my
    account" -> `/settings?section=plan&checkout=success` (D18 candidate).
  - `/onboarding`, `/employer/`, `/employer/abc`, `/admin` -> `/login` "Welcome back".
  - `/imprint` still has "[FILL: ...]" and `mailto:[FILL: e.g. info@hirion.ch]` (D1 open).
- Site code, supporting only: see proposal.md "Why".

## Evidence for tasks

- 1.1 [x]: `DashboardTest.dash2_freeJobFeedIsLockedTeaser` checks "Unlock with Basic" `href="/settings?section=plan&buy=basic"`
  and "See all plans" `href="/settings?section=plan"` (map `TEASER_LINKS`, nothing clicked); `mvn -q test-compile`
  exit 0; `pnpm check:locators` -> "PASS: no forbidden patterns". Live confirmation: task 2.1.
- 1.2 [x]: `AuthPagesTest` + `auth1_guestOtherPrivatePagesRedirect`, `auth7_socialButtonsOnLogin`,
  `auth7_socialButtonsOnSignup`, `auth8_resetPasswordWithoutLink`. `mvn test -Dtest=EmailLinkPagesTest,AuthPagesTest`
  (`../sync-scan-guest-tests-2026-10-23.log`): `Tests run: 16, Failures: 0, Errors: 0, Skipped: 0`. The tests were
  green at the first run: they describe behaviour the site already had (no RED step possible).
- 1.3 [x]: new `pub/EmailLinkPagesTest` (picked up by `testng-public.xml`, package `ch.hirion.pub`):
  `link1_surveyRendered`, `link1_surveyWithoutIdSendsNothing`, `link2_unsubscribeWithoutLink`,
  `link3_checkoutReturnWithoutSession` -- all PASS in the same run (surefire testcase names listed). LINK-1 "Survey
  without an id sends nothing" is now CONFIRMED live (mocked network, toast seen, 0 requests to
  `/api/public/survey-response`); intent.md moved it from ASSUMED to CONFIRMED.
- 1.4 [x]: `grep -n "D17\|D18\|D19\|LINK" docs/intent.md` -> group LINK (line 18), D17/D18/D19 candidates;
  README: 101 tests (public 65 + journey 36), new row "Email link pages"; current-state: change 12 bullet, suites
  public 65, full 101. Spec counts in README stay 54/89 until the archive.
- 2.2 [x]: `pnpm exec openspec validate sync-specs-with-site-scan --strict` -> "Change 'sync-specs-with-site-scan' is
  valid"; `pnpm check` exit 0 (`../pnpm-check-2026-10-23.log`): "PASS: no forbidden patterns", public
  `total="65" passed="65" failed="0" skipped="0"`, "spec:check ok — specs: 9 · active changes: 1 · archived: 11".
- 2.1 [x]: human "да, запускай journey-прогон (один аккаунт, удаляется JourneyCleanup)" (2026-10-23). Local live
  journey run 2026-10-23, finished 20:09:05 CEST, 03:01 min (`../journey-run-2026-10-23.log`): `Tests run: 36,
  Failures: 0, Errors: 0, Skipped: 0`, BUILD SUCCESS; `testng-results.xml` total="36" passed="36";
  `dash2_freeJobFeedIsLockedTeaser` status="PASS" -- the new link targets are seen live; JourneyCleanup "deleted:
  ...+hirion-qa-...@gmail.com no longer signs in"; `.auth` holds only `manual-user.json`.
