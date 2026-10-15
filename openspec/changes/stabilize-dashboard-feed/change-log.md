# Change log -- stabilize-dashboard-feed

## 2026-10-15: short feed found

- CI journey runs (about 07:28 and 10:51 UTC), Allure: `Tests run: 40, Failures: 3` -- `dash6`, `dash11`,
  `dash10` "no job card in the Job Feed after 30 s"; `dash4`, `dash5` passed. Trend: red, green 97/97, red.
- Local live run (human's "да", temporary count line, reverted): `[FEED-COUNT TEMP] dash2_firstJobCard: 2 cards`;
  `Tests run: 40, Failures: 2` -- `dash5_alreadyAppliedMovesJob` "element was detached from the DOM" on
  "Already applied", `dash6_notRelevantRemovesJob` "no unused job card with a unique title in the Job Feed
  (1 cards)"; JourneyCleanup "deleted: ... no longer signs in".
