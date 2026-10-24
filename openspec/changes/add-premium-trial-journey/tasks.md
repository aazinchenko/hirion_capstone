## 1. Tests (scenario tests first)

- [ ] 1.1 `PremiumUpgradeTest` (PREM-1, PREM-2), `PremiumDashboardTest` (PREM-3..10 incl. the KNOWN BUG D20 test), `PremiumAiTest` (PREM-11..13, skipped without `-Dqa.ai=true`); one test per scenario except PREM-14 (evidence = JourneyCleanup output). Verify: `mvn -q test-compile` exits 0; `pnpm check:locators` prints "PASS: no forbidden patterns".
- [ ] 1.2 `testng-journey.xml`: the three classes after `SettingsTest`, before `JourneyCleanup`; `SettingsTest.set5_newPasswordSignsIn` priority 300. Verify: run the journey suite WITHOUT `-Dqa.mailbox` and quote the counts (all journey tests skipped, nothing created).
- [ ] 1.3 docs: `docs/intent.md` (D20 KNOWN BUG), `docs/decisions.md`, `docs/current-state.md`, README counts. Verify: `grep -n "D20" docs/intent.md docs/decisions.md`.

## 2. Gate

- [ ] 2.1 One live journey run without AI (only after the human's "да"). Verify: `Tests run: 50`, failures 0, the 3 AI tests skipped, D20 KNOWN BUG passes, JourneyCleanup "deleted: ... no longer signs in" (= PREM-14 evidence); quote in `change-log.md`.
- [ ] 2.2 Run `pnpm exec openspec validate add-premium-trial-journey --strict` and `pnpm check`; quote the summary lines in `change-log.md`.
