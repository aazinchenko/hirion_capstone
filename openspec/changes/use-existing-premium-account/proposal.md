## Why

CI premium run 2026-11-10 18:55 UTC: `PremiumUpgradeTest.prem1_trialStartsWithoutCheckout` failed with "Stripe
checkout opened instead of the trial"; the guard pressed Escape, nothing was typed, the 13 other Premium tests were
skipped, the account was deleted by `JourneyCleanup`. Site code of the same day (`settings-K0ouh2tF.js`): a Free
user's click on the Premium card in Plan & Billing calls `openCheckout({priceId: "pro_monthly" | "pro_yearly"})`, the
no-card trial notification is no longer in the Settings code. The premium CI job was paused (7a1c2fd).

Human decisions (2026-11-10):

- The Premium trial is available only after entering card data (product rule). A trial without a card anywhere on
  the site would be a defect.
- The Premium tests use one existing Premium account instead of a trial: Premium granted by the site owner, the
  owner agrees, sign-in by email and password.
- "Already applied" (PREM-6) and "Not relevant" (PREM-7) cannot be undone on the site, so on the permanent account
  they run only on a manual run, never at night.

## What Changes

- REMOVED `PREM-1` (trial in Plan & Billing without checkout), `PREM-2` (trial plan state) and `PREM-14` (deleting an
  account with an active trial): no trial is started and the account is never deleted.
- ADDED `PREM-15` the suite signs in to the existing Premium account by email and password; `PREM-16` Plan & Billing
  shows the Premium plan (precondition of every Premium test).
- MODIFIED `PREM-3`, `PREM-4`, `PREM-8`, `PREM-10`, `PREM-11`, `PREM-12`, `PREM-13`: "the Premium test account"
  instead of "the test user whose trial has started"; PREM-3 no longer waits minutes after an activation.
- MODIFIED `PREM-5`: Save is undone at the end (the "Saved" button in the Saved tab removes the job again), so the
  permanent account keeps its state; new scenario "Unsave a job".
- MODIFIED `PREM-9`: Save rate from the numbers of the same moment (saved S, jobs in feed F), not from "exactly one
  saved job".
- MODIFIED `PREM-6`, `PREM-7`: run only with `-Dqa.feedActions=true` (manual CI run with the box ticked).
- Suite `testng-premium.xml`: no registration (`reg9`), no `PremiumUpgradeTest`, no `JourneyCleanup`.
- CI: premium job active again (nightly + manual), credentials from secrets `QA_PREMIUM_EMAIL` /
  `QA_PREMIUM_PASSWORD`, new checkbox for the feed actions, no `QA_MAILBOX`.
- AGENTS.md: exception for the one Premium account and what is never clicked on it; `scripts/guard-live-run.mjs`
  also asks before a premium run.

## Capabilities

### New Capabilities

(none)

### Modified Capabilities

- `premium-trial`: from "journey user on a 7-day trial" to "existing Premium account" (the capability name is kept;
  its Purpose is rewritten at archive).

## Impact

- `premium-trial`: 14 -> 13 requirements (-3 +2), scenarios 15 -> 15 (-3 +2 +1 unsave); totals expected 71
  requirements / 110 scenarios (verify with `pnpm exec openspec`).
- Tests: `PremiumUpgradeTest` deleted; new `PremiumSignInTest`; `PremiumDashboardTest`, `PremiumAiTest` use the
  Premium session; Free suite unchanged.
- No account is created or deleted by the premium suite. It changes data of a real account (Save, undone at the
  end; PREM-6/7 only manually), so a live run needs the human's "да".

## Open questions

1. ASSUMED from site code (`dashboard-D8StQNd5.js`, Saved tab cards use the same `toggleSave`): clicking "Saved" in
   the Saved tab removes the job from Saved. To be seen on the first live run.
2. The no-card trial text "Your 7-day Premium trial is active" is still in the signup code (step 4, `plan=trial`).
   Candidate defect (ASSUMED, not seen live; no probe planned by the human).
