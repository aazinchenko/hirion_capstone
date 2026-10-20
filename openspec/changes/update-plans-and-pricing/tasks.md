## 1. Tests

- [ ] 1.1 `SmokeTest`: `pub2_faqShowsAnswer` checks the new answer; `pub3_*` follow "PUB-3 Pricing shows plans per billing period" (buttons Monthly / Yearly, plan names, new prices and units); `pub3_quarterlyPrice` deleted; new `pub5_basicPlanCta`. Verify: `grep -n "Quarterly\|billed" src/test/java/ch/hirion/pub/SmokeTest.java` finds nothing.
- [ ] 1.2 `RegistrationTest`: REG-7 tests use `aria-pressed` and the cards Free / Basic / Premium; new `reg7_basicCanBeSelected` and KNOWN BUG `reg7_planBasicPreselectsBasic` (D16); REG-9 clicks "Join for free" after the guard of design.md Decision 2; `SELECTED_CARD` / `isSelected` removed; `TestUser` comments say "Join for free". Verify: `grep -rn "See my matches\|Premium Trial\|border-primary" src/test/java` finds only the scenario names kept in test descriptions.
- [ ] 1.3 Verify: `mvn -q test-compile` exits 0; `pnpm check:locators` prints "PASS: no forbidden patterns".

## 2. Docs

- [ ] 2.1 `docs/intent.md`: D12 FIXED (seen live 2026-10-19), new D16 (CONFIRMED live 2026-10-20); `docs/decisions.md`, `docs/current-state.md`, `README.md`: KNOWN BUG list D1 + D16, counts (public 57, journey 42, full 99). Verify: `grep -n "D12\|D16" docs/intent.md docs/decisions.md docs/current-state.md README.md`.

## 3. Gate

- [ ] 3.1 Guest run without `-Dqa.mailbox`: `mvn test "-Dtest=RegistrationTest#reg7_*"`. Verify: 7 tests, 0 failures (D16 passes as the expected failure); quote in `change-log.md`.
- [ ] 3.2 One journey run with a real test account (human: local with `-Dqa.mailbox` on "да", or CI). Verify: `Tests run: 42, Failures: 0`, JourneyCleanup "deleted"; quote in `change-log.md`.
- [ ] 3.3 Run `pnpm exec openspec validate update-plans-and-pricing --strict` and `pnpm check`; quote the summary lines in `change-log.md` (validate "is valid"; public suite 57 tests, 0 failures; spec:check ok).
