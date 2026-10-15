## 1. Test

- [x] 1.1 `DashboardTest`: priorities reordered as design.md Decision 1 (DASH-10 and DASH-11 before DASH-4). Verify: `grep -n "priority" src/test/java/ch/hirion/journey/DashboardTest.java` shows the new order.
- [x] 1.2 `pickCard()`: wait until no card with a used title is shown, then pick; `SkipException` when titles were used and no unused card is left (design.md Decisions 2-3). Verify: `mvn -q test-compile` exits 0; `pnpm check:locators` prints "PASS: no forbidden patterns".

## 2. Gate

- [x] 2.1 One journey run (human: local with `-Dqa.mailbox` on "да", or CI). Verify: `Failures: 0`, DASH-6 passed or skipped with the short-feed message, JourneyCleanup "deleted"; quoted in `change-log.md`.
- [x] 2.2 Run `pnpm exec openspec validate stabilize-dashboard-feed --strict` and `pnpm check`; quote the summary lines in `change-log.md`.
