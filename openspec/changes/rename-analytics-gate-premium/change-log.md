# Change log -- rename-analytics-gate-premium

## 2026-10-21: the Analytics gate says "Premium"

- CI journey run 2026-10-21 13:19 UTC (pasted by the human): `Tests run: 36, Failures: 1` --
  `dash7_analyticsProGate:126 Locator expected to be visible` for `getByRole(AriaRole.HEADING, ...setName("Unlock
  Hirion Pro").setExact(true))`, "Assert "isVisible" with timeout 30000ms"; JourneyCleanup "deleted: ***+hirion-qa-...
  no longer signs in".
- Site code, supporting only (fetched 2026-10-21): `index-D8wCQ5XQ.js` `analytics_progate_title = Unlock Hirion
  Premium`, `analytics_progate_desc = See your match analytics, Swiss salary benchmarks, and personalized
  opportunities to land roles faster.`, `analytics_progate_btn = Upgrade to Premium`; `dashboard-DIUGP6tg.js` gate
  `h3` `n("analytics_progate_title")`, link `de,{to:"/settings",search:{section:"plan"}}`; tab
  `label:i("dashboard_tab_analytics"),badge:s?void 0:"Pro"`.
- Human (2026-10-21): "да, сделай весь цикл сам" -- spec, test, one live journey run, archive and push by the agent.

## Evidence for tasks

- 1.1 [x]: the grep for the old heading and link texts in `src/test/java` -> no output (exit 1); `mvn -q test-compile` exit 0;
  `pnpm check:locators` -> "PASS: no forbidden patterns". Commit `5d197c6`.
- 1.2 [x]: `grep -n "Unlock Hirion" docs/intent.md docs/current-state.md` -> intent.md:60-61 "Free sees "Unlock Hirion
  Pro"; renamed to "Unlock Hirion Premium" / "Upgrade to Premium" by 2026-10-21."; current-state.md:57 "Free sees
  "Unlock Hirion Premium" since 2026-10-21".
- 2.1 [x]: local live journey run 2026-10-21 15:26-15:30 CEST (`../journey-run-2026-10-21.log`): `Tests run: 36,
  Failures: 0, Errors: 0, Skipped: 0`, BUILD SUCCESS; `dash7_analyticsProGate` PASS (7.1 s) -- the new heading and
  link are seen live; JourneyCleanup "deleted: ... no longer signs in"; `.auth` holds only `manual-user.json`.
- 2.2 [x]: `pnpm exec openspec validate rename-analytics-gate-premium --strict` -> "Change
  'rename-analytics-gate-premium' is valid"; `pnpm check` exit 0: "PASS: no forbidden patterns", public
  `total="57" passed="57" failed="0" skipped="0"`, "spec:check ok -- specs: 9 · active changes: 1 · archived: 10".
