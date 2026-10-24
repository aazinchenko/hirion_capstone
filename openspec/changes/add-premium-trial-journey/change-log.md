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

(none yet)
