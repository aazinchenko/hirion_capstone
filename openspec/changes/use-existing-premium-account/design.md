## Context

Until 2026-11-10 the premium suite registered a disposable Free user (`reg9`), started the 7-day trial in Plan &
Billing (`prem1`), ran the Premium checks and deleted the user (`JourneyCleanup`). The site now opens a Stripe
checkout for that button and AGENTS.md forbids any checkout, so the suite moves to one permanent Premium account
granted by the site owner.

## Decisions

- **Credentials.** Environment variables `QA_PREMIUM_EMAIL` and `QA_PREMIUM_PASSWORD` (locally set by the human, in CI
  GitHub secrets). Not system properties: a `-D` value would be visible in the command line and the Maven log. The
  values are never printed; logs show only "premium account". The agent never types or reads them.
- **Safety guard.** `PremiumSignInTest` refuses (fails before opening `/login`) when the email matches
  `\+hirion-qa-\d+@`: that pattern is reserved for disposable users that `JourneyCleanup` deletes. The premium suite
  does not contain `JourneyCleanup`.
- **Session.** `PREM-15` signs in on `/login` (email, exact label "Password", "Sign in"), waits for `/dashboard` and
  saves the session to `.auth/premium.json` (git-ignored like `.auth/`). A new base class `PremiumAccountTest`
  (support package, human-reviewed) uses that file as storage state; `PremiumDashboardTest` and `PremiumAiTest`
  extend it instead of `AuthenticatedTest`.
- **Gate.** `prem15` and `prem16` are in group `premium`; every other Premium test has `dependsOnGroups = "premium"`
  (no more "registered"). If sign-in fails or the account is no longer Premium (owner removed it, it expired), all
  other tests are skipped with one clear failure.
- **Never clicked on this account:** "Delete account", "Manage subscription", "Cancel subscription", any plan or
  payment button, "Update password", "Save preferences", profile and CV changes. Only the feed actions below.
- **State of the permanent account.**
  - PREM-5 saves the first feed job, PREM-9 reads the Save rate, then PREM-5b clicks "Saved" on that job in the Saved
    tab and checks the Saved count is back to the value before PREM-5. If a run breaks in between, one job stays
    saved: all checks are relative (counts before/after), so the next run still works.
  - PREM-9: S = number of "Saved" buttons in the Saved tab, F = "Jobs in feed" on the Analytics page; expected
    round(S / (S + F) x 100) % (0 % when S + F = 0). Waits until the figures have loaded (as the 2026-10-25 fix).
  - PREM-6 / PREM-7 (irreversible) throw `SkipException` unless `-Dqa.feedActions=true`, like the AI tests with
    `-Dqa.ai`. CI: a manual checkbox "feed actions"; nightly never.
- **Feed size.** PREM-3 waits up to 60 s for "Tailor my CV" (no activation delay any more). Actions that need a card
  still throw `SkipException` when the feed has no card.
- **Order.** Priorities: 101 sign-in, 102 plan state, 103-107 read-only (PREM-3, 4, 8, 8-badge, 10), 108-110 AI,
  111 save, 112 save rate, 113 unsave, 114 applied, 115 not relevant.
- **Hook.** `scripts/guard-live-run.mjs` asks the human also for commands with `testng-premium.xml` (a real account
  is changed even if no account is created).

## Risks

- Owner removes Premium or the session is blocked (rate limit, captcha on login): PREM-15/16 fail clearly, the rest
  is skipped.
- Feed of the permanent account changes daily (new matches): tests only count relative changes and skip when empty.
- Leftover saved job after an interrupted run: tolerated by relative checks; can be removed by hand.
