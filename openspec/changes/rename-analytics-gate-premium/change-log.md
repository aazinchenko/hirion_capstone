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
