## 1. Tests

- [x] 1.1 `SmokeTest`: `pub2_faqShowsAnswer` checks the new answer; `pub3_*` follow "PUB-3 Pricing shows plans per billing period" (buttons Monthly / Yearly, plan names, new prices and units); `pub3_quarterlyPrice` deleted; new `pub5_basicPlanCta`. Verify: `grep -n "Quarterly\|billed" src/test/java/ch/hirion/pub/SmokeTest.java` finds nothing.
- [x] 1.2 `RegistrationTest`: REG-7 tests use `aria-pressed` and the cards Free / Basic / Premium; new `reg7_basicCanBeSelected` and KNOWN BUG `reg7_planBasicPreselectsBasic` (D16); REG-9 clicks "Join for free" after the guard of design.md Decision 2; `SELECTED_CARD` / `isSelected` removed; `TestUser` comments say "Join for free". Verify: `grep -rn "See my matches\|Premium Trial\|border-primary" src/test/java` finds only the scenario names kept in test descriptions.
- [x] 1.4 `DashboardTest`: `dash2_freeJobFeedIsLockedTeaser` replaces `dash2_firstJobCard` (design.md Decision 6); `dash4`, `dash5` x2, `dash6`, `dash10`, `dash11` and the card helpers deleted; priorities and class comment updated. Verify: `grep -c "@Test" src/test/java/ch/hirion/journey/DashboardTest.java` gives 6; `grep -n "View job\|pickCard\|feedCards" src/test/java/ch/hirion/journey/DashboardTest.java` finds only the "no View job" assertion.
- [x] 1.3 Verify: `mvn -q test-compile` exits 0; `pnpm check:locators` prints "PASS: no forbidden patterns".

## 2. Docs

- [x] 2.1 `docs/intent.md`: D12 FIXED (seen live 2026-10-19), new D16 (CONFIRMED live 2026-10-20); `docs/decisions.md`, `docs/current-state.md`, `README.md`: KNOWN BUG list D1 + D16, counts (public 57, journey 42, full 99). Verify: `grep -n "D12\|D16" docs/intent.md docs/decisions.md docs/current-state.md README.md`.

- [x] 2.2 Docs after the dashboard extension: `docs/intent.md` (Free Job Feed teaser fact; D8 not observable for Free), `docs/current-state.md`, `README.md` counts journey 36 (Registration 17, Dashboard 6, Settings 13), full 93. Verify: `grep -n "36\|93" README.md docs/current-state.md`.

## 3. Gate

- [x] 3.1 Guest run without `-Dqa.mailbox`: `mvn test "-Dtest=RegistrationTest#reg7_*"`. Verify: 7 tests, 0 failures (D16 passes as the expected failure); quote in `change-log.md`.
- [x] 3.2 One journey run with a real test account (human: local with `-Dqa.mailbox` on "да", or CI). Verify: `Tests run: 36, Failures: 0`, JourneyCleanup "deleted"; quote in `change-log.md`.
- [x] 3.3 Run `pnpm exec openspec validate update-plans-and-pricing --strict` and `pnpm check`; quote the summary lines in `change-log.md` (validate "is valid"; public suite 57 tests, 0 failures; spec:check ok).
