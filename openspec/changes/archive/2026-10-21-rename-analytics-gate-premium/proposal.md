## Why

The GitHub Actions journey run on 2026-10-21 (13:19 UTC) failed 35/36: `DashboardTest.dash7_analyticsProGate`
"Locator expected to be visible" for the heading "Unlock Hirion Pro" (30 s). After the site's new plans
(change `update-plans-and-pricing`) the paid plan is called Premium, and the Analytics gate was renamed:

- Site code, supporting (`https://hirion.ch/assets/index-D8wCQ5XQ.js` and `dashboard-DIUGP6tg.js`, fetched
  2026-10-21): `analytics_progate_title:"Unlock Hirion Premium"`, `analytics_progate_btn:"Upgrade to Premium"`,
  `analytics_progate_desc` unchanged; the button is a link `to:"/settings",search:{section:"plan"}`; the Analytics
  tab still gets `badge:"Pro"` for a user without the paid plan.
- Live confirmation: one journey run in this change (task 2.1).

## What Changes

- MODIFIED `DASH-7 Analytics is Pro-only for a Free user` (spec `dashboard`): heading "Unlock Hirion Premium",
  link "Upgrade to Premium"; text and link target unchanged. Requirement and scenario names stay (the tab badge
  is still "Pro").
- `DashboardTest.dash7_analyticsProGate` checks the new texts; comments updated.
- `docs/intent.md`, `docs/current-state.md`: the Free user sees "Unlock Hirion Premium".

## Capabilities

### New Capabilities

(none)

### Modified Capabilities

- `dashboard`: DASH-7 texts.

## Impact

- Test counts unchanged (journey 36, full 93); specs 54 requirements, 89 scenarios.
- One journey run with a real test account, deleted by `JourneyCleanup` (the human asked to run the whole cycle,
  2026-10-21).
