# Change log -- stabilize-dashboard-feed

## 2026-10-15: short feed found

- CI journey runs (about 07:28 and 10:51 UTC), Allure: `Tests run: 40, Failures: 3` -- `dash6`, `dash11`,
  `dash10` "no job card in the Job Feed after 30 s"; `dash4`, `dash5` passed. Trend: red, green 97/97, red.
- Local live run (human's "да", temporary count line, reverted): `[FEED-COUNT TEMP] dash2_firstJobCard: 2 cards`;
  `Tests run: 40, Failures: 2` -- `dash5_alreadyAppliedMovesJob` "element was detached from the DOM" on
  "Already applied", `dash6_notRelevantRemovesJob` "no unused job card with a unique title in the Job Feed
  (1 cards)"; JourneyCleanup "deleted: ... no longer signs in".

## Evidence for tasks

- 1.1 [x]: `grep -n "priority" DashboardTest.java` -> DASH-1 1, DASH-2 2, DASH-3 3, DASH-10 4, DASH-11 5, DASH-4 6,
  DASH-5 menu 7, DASH-5 applied 8, DASH-6 9, DASH-7 10, DASH-9 11, DASH-12 12.
- 1.2 [x]: `mvn -q test-compile` exit 0; `pnpm check:locators` -> "PASS: no forbidden patterns".
