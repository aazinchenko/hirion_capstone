## 1. Test and docs

- [ ] 1.1 `DashboardTest.dash7_analyticsProGate`: heading "Unlock Hirion Premium", link "Upgrade to Premium" with `href="/settings?section=plan"`; comments say "Upgrade to Premium". Verify: `grep -n "Unlock Hirion Pro\"\|Upgrade to Pro\"" src/test/java` finds nothing; `mvn -q test-compile` exits 0; `pnpm check:locators` prints "PASS: no forbidden patterns".
- [ ] 1.2 `docs/intent.md`, `docs/current-state.md`: Free sees "Unlock Hirion Premium". Verify: `grep -n "Unlock Hirion" docs/intent.md docs/current-state.md`.

## 2. Gate

- [ ] 2.1 One journey run with a real test account (local, human's request of 2026-10-21). Verify: `Tests run: 36, Failures: 0`, `dash7_analyticsProGate` PASS, JourneyCleanup "deleted"; quote in `change-log.md`.
- [ ] 2.2 Run `pnpm exec openspec validate rename-analytics-gate-premium --strict` and `pnpm check`; quote the summary lines in `change-log.md`.
