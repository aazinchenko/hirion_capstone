# Change log -- add-premium-trial-journey

## 2026-10-24: proposal

- Human (2026-10-24): "Берём вариант A (апгрейд того же Free-аккаунта через Settings). Согласие владельца на триалы
  есть. ИИ-тесты по флагу -Dqa.ai, в ночном CI без ИИ. Живую пробу делай сам после моего «да»."; then "да, запускай
  живую пробу без ИИ-кнопок"; then "Да, делай change add-premium-trial-journey: спека PREM-1…14 и тесты; живой прогон
  — после моего «да». Значок PRO у Analytics запиши как KNOWN BUG D20."
- Live probe 2026-10-24 (`../premium-probe-2026-10-24.log`): `Tests run: 24, Failures: 0`; "[PROBE] toast after ms:
  635 visible=1"; "You're on the Premium plan. Premium Free trial ends on 31/10/2026. No card on file, nothing will be
  charged."; "cancel button count: 0"; "cards visible after s: 13"; "analytics button name: Analytics PRO"; "home
  #pricing count: 0"; JourneyCleanup "deleted: ... no longer signs in". Rules commit `be76f20`, facts commit `0983bcd`.

## Evidence for tasks

- 1.1 [x]: `PremiumUpgradeTest` (prem1, prem2), `PremiumDashboardTest` (prem3, prem4, prem8 x2 incl. KNOWN BUG D20,
  prem10, prem5, prem9, prem6, prem7), `PremiumAiTest` (prem11, prem12, prem13; `@BeforeMethod` SkipException without
  `-Dqa.ai=true`). `mvn -q test-compile` exit 0; `pnpm check:locators` -> "PASS: no forbidden patterns".
- 1.2 [x]: `testng-journey.xml` lists PremiumUpgradeTest, PremiumDashboardTest, PremiumAiTest after SettingsTest and
  before JourneyCleanup; `set5_newPasswordSignsIn` priority 13 -> 300. `mvn test -Dsuite=testng-journey.xml` without
  `-Dqa.mailbox` (`../journey-nomailbox-2026-10-24.log`): `Tests run: 50, Failures: 0, Errors: 0, Skipped: 36`,
  `testng-results total="50" passed="14" skipped="36"`; `.auth` holds only `manual-user.json` (nothing created).
- 1.3 [x]: intent.md D20 "KNOWN BUG by human decision 2026-10-24, PREM-8"; decisions.md KNOWN BUG tests D16, D20;
  current-state.md change 14 bullet, journey 50, full 113; README 113 tests, row "Premium trial".
- 2.2 [x]: `pnpm exec openspec validate add-premium-trial-journey --strict` -> "Change 'add-premium-trial-journey' is
  valid"; `pnpm check` exit 0 (`../pnpm-check-2026-10-24b.log`): "PASS: no forbidden patterns", public
  `total="63" passed="63" failed="0" skipped="0"`, "spec:check ok — specs: 10 · active changes: 1 · archived: 13".
- 2.1 [ ]: open -- live journey run only after the human's "да".
