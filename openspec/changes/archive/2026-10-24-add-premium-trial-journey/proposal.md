## Why

The suite covers two users: a guest (public suite) and a registered Free user (journey). The human asked on
2026-10-24 for a third one: "зарегистрированный юзер с активированным премиум семидневным. И весь сопутствующий
функционал, который появляется у премиум-юзера". Decisions of the same day (docs/premium-journey-plan.md, section 8):
variant A -- the journey's own Free user activates the trial in Settings > Plan & Billing; the site owner consents
to trial accounts; AI buttons only with `-Dqa.ai=true`, never in CI; the Premium part runs in the nightly CI without
AI; the live probe was done by the agent. Then: "Да, делай change add-premium-trial-journey: спека PREM-1…14 и
тесты; живой прогон — после моего «да». Значок PRO у Analytics запиши как KNOWN BUG D20."

Facts come from the live probe of 2026-10-24 (`../premium-probe-2026-10-24.log`, one account, no AI, deleted),
recorded in docs/intent.md "CONFIRMED live 2026-10-24 (Premium Trial probe)". The AI dialogs (PREM-11..13) were not
opened in the probe: their texts are read from the site code and stay ASSUMED until the first `-Dqa.ai` run.

## What Changes

- New capability `premium-trial` (test group PREM), 14 requirements, 15 scenarios: activation without checkout
  (PREM-1), trial state in Plan & Billing (PREM-2), job cards instead of the teaser (PREM-3), card actions (PREM-4),
  Save / Already applied / Not relevant (PREM-5..7), Analytics unlocked and its tab without the "Pro" badge
  (PREM-8; D20 closed by the human after the first live run), Save rate (PREM-9), home page without pricing (PREM-10), AI dialogs behind `-Dqa.ai=true`
  (PREM-11..13), deletion of a trial account by `JourneyCleanup` (PREM-14).
- New tests in `journey/`: `PremiumUpgradeTest` (PREM-1, 2), `PremiumDashboardTest` (PREM-3..10),
  `PremiumAiTest` (PREM-11..13, skipped without `-Dqa.ai=true`); `testng-journey.xml` runs them after the Free tests
  and before `JourneyCleanup`.
- `SettingsTest.set5_newPasswordSignsIn` gets a priority after the Premium tests, so it still runs last (the Premium
  tests reuse the saved session).
- docs: intent.md (D20 closed), current-state.md, README counts, decisions.md.

## Capabilities

### New Capabilities

- `premium-trial`: what the journey user sees and can do after starting the 7-day Premium trial.

### Modified Capabilities

(none)

## Impact

- Journey 36 -> 50 tests (47 without `-Dqa.ai`, the 3 AI tests are skipped); full suite 99 -> 113. Public suite
  unchanged (63).
- The journey run still creates exactly one account; it now also starts its trial (no card, no Stripe). The nightly
  CI journey does the same (owner consent 2026-10-24), without AI.
- Run time: + about 1-2 min (or up to 6 min if the job cards are slow to appear).
- Specs: 10 -> 11 capabilities, 58 -> 72 requirements, 95 -> 110 scenarios (to verify with `openspec`).
- Safety: the activation test fails at once if a Stripe checkout appears instead of the trial and types nothing;
  "Manage subscription", "Subscribe to Basic" and promo codes are never clicked.
