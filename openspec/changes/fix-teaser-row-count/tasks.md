## 1. Test

- [x] 1.1 `DashboardTest.dash2_freeJobFeedIsLockedTeaser`: `hasCount(Math.min(jobs, TEASER_ROWS))` with `TEASER_ROWS = 3`. Verify: `mvn -q test-compile` exits 0; `pnpm check:locators` prints "PASS: no forbidden patterns".

## 2. Gate

- [x] 2.1 One journey run with a real test account (human: local with `-Dqa.mailbox` on "да", or CI). Verify: `Tests run: 36, Failures: 0`, JourneyCleanup "deleted"; quote in `change-log.md` with the heading's N if visible.
- [x] 2.2 Run `pnpm exec openspec validate fix-teaser-row-count --strict` and `pnpm check`; quote the summary lines in `change-log.md`.
