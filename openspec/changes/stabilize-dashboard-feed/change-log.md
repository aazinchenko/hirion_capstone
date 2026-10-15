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
- 2.1 [x]: local live journey run 2026-10-15 13:23 CEST (human's "да", `../journey-run-2026-10-15-2.log`):
  `Tests run: 40, Failures: 0, Errors: 0, Skipped: 0`; dashboard start times in the new order DASH-1, 2, 3, 10, 11,
  4, 5 menu, 5 applied, 6, 7, 9, 12; JourneyCleanup "deleted: ... no longer signs in". DASH-6 passed, so this feed
  had a third job: the skip path was not exercised. DASH-5 menu took 21 s, DASH-5 applied 13 s, DASH-6 21 s (the
  settle wait; not proven which wait took the time, no trace on a passing test).
- 2.2 [x]: `pnpm exec openspec validate stabilize-dashboard-feed --strict` -> "Change 'stabilize-dashboard-feed' is
  valid"; `pnpm check` exit 0: "PASS: no forbidden patterns", public suite `total="57" passed="57" failed="0"`
  (testng-results.xml), "spec:check ok -- specs: 9 · active changes: 1 · archived: 7".
