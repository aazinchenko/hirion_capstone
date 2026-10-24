# План: третий сценарий — пользователь с активным Premium Trial (7 дней)

Статус: решения приняты 2026-10-24 (раздел 8). Шаг 2 (правила) сделан. Шаг 3 (живая проба) сделан 2026-10-24:
факты раздела 1 подтверждены и записаны в docs/intent.md (CONFIRMED live 2026-10-24); отличия от кода — ниже.

**Итоги живой пробы (2026-10-24, один аккаунт, удалён):**
- Кнопка в Plan & Billing — «Start 7-day free trial»; триал включился сразу, без Stripe и карты (тост через 0,6 с).
- После триала: «Free trial ends on 31/10/2026.», «No card on file…»; на карточке Premium — «Manage subscription»,
  «Cancel subscription» нет.
- Карточки в Job Feed появились через 13 с (задержка 3–5 мин из бага №49 не повторилась) — таймаут оставляем 6 мин.
- Вкладка Analytics у Premium всё ещё «Analytics PRO» — кандидат в дефект D20; PREM-8 проверяет только, что
  заглушки нет, а про значок — решение человека.
- Save и Already applied работают без тостов; в Applied — «Applied Oct 24, 2026».
- В Analytics есть ещё «Filter impact» и «Eligibility advantage»; на главной у Premium нет #pricing.
Заменяет устаревший черновик `docs/pro-journey-plan-draft.md` (2026-10-05, ещё про старый тариф «Pro»).

Сейчас есть два сценария:
1. **Гость** — публичный сьют, 63 теста, без аккаунта.
2. **Зарегистрированный Free** — journey, 36 тестов, один реальный аккаунт за прогон, удаляется `JourneyCleanup`.

Нужен третий: **зарегистрированный пользователь с активированным 7-дневным Premium** и проверки всего, что
появляется только у Premium.

## 1. Что известно о Premium (из кода сайта 2026-10-23, всё ASSUMED до живой пробы)

Правило проекта: факт из кода сайта — ASSUMED, пока не увиден вживую.

**Как включается триал (без карты):**
- Регистрация: на шаге 4 Premium выбран по умолчанию, кнопка «Start my free trial», подпись
  «Start with 7 days free, no credit card». После создания аккаунта сервер запускает триал, тост
  «Your 7-day Premium trial is active» (при ошибке — «Couldn't start the trial — you can try again from Settings.»).
- Settings → Plan & Billing у Free-пользователя: кнопка Premium запускает триал на сервере, тот же тост.
  **Риск:** если триал не стартовал (например, уже был), код открывает Stripe checkout — тест должен это
  распознать и остановиться, ничего не вводя.
- Триал без карты помечается на сервере как `local_trial` — в Stripe подписки нет.

**Plan & Billing у триала:**
- «You're on the Premium plan.»
- «Free trial ends on {date}. No card on file, nothing will be charged.»
- Кнопка «Cancel subscription» для триала без карты по коду **не показывается** (только у Stripe-подписки).

**Dashboard у Premium:**
- Job Feed — настоящие карточки вместо закрытого тизера (нет «We found N jobs», нет «Unlock with Basic»).
  **Важно:** ваш баг №49 (22/10/2026) — после активации триала карточки появляются с задержкой 3–5 минут.
- Карточка: оценка «NN» + «match», кнопки «View job», «Save», «Hide» (меню «Already applied» / «Not relevant»),
  «Tailor my CV», «Write Cover Letter», «Help me stand out».
- Вкладка Analytics без значка «Pro», без заглушки «Unlock Hirion Premium»; разделы
  «Your performance» (Total active jobs, Avg match score, Save rate, Jobs in feed),
  «Swiss market intelligence» (кантоны, зарплаты, компании, языки), «Positioning & opportunity» (Skills gap).
- Saved: «Jobs you save appear here…»; Applied: «Applied {date}»; Archive: хранит показанные вакансии 30 дней.

**ИИ-функции (тратят ресурсы владельца сайта, есть лимиты):**
- «Help me stand out»: окно с разделами «What to highlight», «Worth addressing», «Keywords to use»;
  «Opening it again for this job costs nothing.»
- «Tailor my CV»: окно «Tailored CV», бейдж «ATS-friendly», шаблоны «Classic» / «Modern», кнопка «Copy» → «Copied»,
  счётчики «{n} more generations left for this document», «{n} tailored applications left this month».
- «Write Cover Letter»: окно «Cover letter», шаблоны «Letterhead» / «Plain». Повторное открытие показывает
  сохранённый результат: «Reopening it costs you nothing.»

**Главная страница:** блок `#pricing` по спеке PUB-3 скрыт для платных пользователей — проверим у Premium.

## 2. Правила проекта, которые придётся изменить (решение человека)

- `AGENTS.md`: «Never start Premium Trial or any payment.» → станет «Premium Trial — только в сценарии PREM,
  один триал-аккаунт за прогон; никогда никаких платежей и ввода карты».
- `docs/intent.md` → «Not doing»: убрать запрет на триал; «Assertions on the text of AI answers» остаётся.
- Хук `scripts/guard-live-run.mjs`: спрашивать разрешение и на новый флаг `-Dqa.ai=true`.

## 3. Как получить Premium-пользователя — два варианта

| | A. Апгрейд того же аккаунта (рекомендую) | B. Отдельный аккаунт через регистрацию с триалом |
|---|---|---|
| Как | Free-journey проходит как сейчас, затем Settings → Plan & Billing → Premium → тесты Premium → `JourneyCleanup` | Новый набор `testng-premium.xml`: регистрация «Start my free trial» → тесты Premium → своя очистка |
| Аккаунтов за прогон | 1 (как сейчас) | 2 (Free + Premium) |
| Покрывает | Путь из бага №49 (Free → триал из Settings, тизер → карточки) | Путь по умолчанию на шаге 4 регистрации |
| Минусы | Падение Free-части блокирует Premium-часть; задержка 3–5 мин делает прогон длиннее | Вдвое больше аккаунтов (и в ночном CI тоже); дольше |

В варианте A путь регистрации «Start my free trial» не проверяется: его можно один раз проверить вручную
(живая проба, раздел 6) или добавить позже вариантом B.

## 4. Новая спека `premium-trial` (группа PREM) — проект сценариев

Точные тексты — после живой пробы.

| ID | Требование | Сценарий (кратко) | ИИ? |
|---|---|---|---|
| PREM-1 | Free-пользователь включает триал в Plan & Billing | Кнопка Premium → тост «Your 7-day Premium trial is active», без Stripe checkout и без ввода карты | нет |
| PREM-2 | Plan & Billing показывает триал | «You're on the Premium plan.», «Free trial ends on <сегодня + 7 дней>.», «No card on file, nothing will be charged.»; «Current plan» на Premium | нет |
| PREM-3 | Job Feed открыт: карточки вместо тизера | Ждём до 6 мин (баг №49); нет «We found N jobs», нет «Unlock with Basic»; есть хотя бы 1 карточка с «NN match» | нет |
| PREM-4 | Карточка вакансии показывает все действия | «View job» (ссылка во внешний источник, `target=_blank`), «Save», «Hide», «Tailor my CV», «Write Cover Letter», «Help me stand out» | нет |
| PREM-5 | Save переносит вакансию в Saved | Карточка исчезает из ленты и появляется в Saved | нет |
| PREM-6 | Hide → Already applied переносит в Applied | В Applied «Applied <дата>» | нет |
| PREM-7 | Hide → Not relevant убирает из ленты | Карточки больше нет в Job Feed | нет |
| PREM-8 | Analytics открыта без заглушки | Кнопка «Analytics» без «Pro»; нет «Unlock Hirion Premium»; видны 3 раздела и 4 KPI | нет |
| PREM-9 | Save rate считается верно (бывший DASH-8) | После Save: «Jobs in feed» −1, Save rate = round(saved / (saved + feed) × 100) % | нет |
| PREM-10 | На главной нет блока цен | Вошедший Premium открывает `/` → `#pricing` отсутствует | нет |
| PREM-11 | Help me stand out показывает разбор | Окно «Help me stand out», три раздела не пустые; текст не проверяем | да |
| PREM-12 | Tailor my CV выдаёт CV | Окно «Tailored CV», «ATS-friendly», шаблоны Classic / Modern, «Copy» → «Copied», счётчик генераций | да |
| PREM-13 | Write Cover Letter выдаёт письмо | Окно «Cover letter», шаблоны Letterhead / Plain; повторное открытие — «Reopening it costs you nothing.» | да |
| PREM-14 | Аккаунт с триалом удаляется | Существующий `JourneyCleanup`: «deleted … no longer signs in» | нет |

Итого 14 тестов: 11 без ИИ (всегда) + 3 с ИИ (только с `-Dqa.ai=true`, каждая ИИ-кнопка максимум 1 раз за прогон).

**Не входит:** реальная оплата и ввод карты, Basic, переход на Free через 7 дней, отмена Stripe-подписки (у триала
её нет), письма (ежедневные подборки), тексты ответов ИИ, промокоды.

## 5. Архитектура (для варианта A)

- Новые классы в `journey/`: `PremiumUpgradeTest` (PREM-1, 2), `PremiumDashboardTest` (PREM-3…10),
  `PremiumAiTest` (PREM-11…13, пропускаются без `-Dqa.ai=true`).
- В `testng-journey.xml` они идут после Free-тестов и перед `JourneyCleanup`; `dependsOnGroups` на группу
  «registered», своя группа «premium» (Premium-тесты не запускаются, если триал не включился).
- `TestUser` получает флаг `premiumActivated` — как `submitted`, для понятного отчёта очистки.
  Защита `\+hirion-qa-\d+@` не меняется.
- Ожидание карточек (до 6 мин) — один раз в начале `PremiumDashboardTest`, через `assertThat(...).isVisible`
  с длинным таймаутом, без `Thread.sleep` (правила локаторов).
- Защита от оплаты: если после клика на Premium появилось окно Stripe checkout — тест падает с понятным
  сообщением, ничего не заполняя.
- journey: 36 → 50 тестов (47 без ИИ); полный сьют 99 → 113.

## 6. План по шагам

1. **Решения человека** — раздел 8 (особенно согласие владельца сайта).
2. **Правила:** `AGENTS.md`, `docs/intent.md` («Not doing», группа PREM), хук — отдельный коммит.
3. **Живая проба** (только после «да»): один аккаунт, Free → триал из Settings (и при желании регистрация
   с «Start my free trial»), записываю факты раздела 1 как CONFIRMED, замеряю задержку карточек,
   один раз нажимаю каждую ИИ-кнопку (если разрешено), аккаунт удаляется.
4. **Proposal** change `add-premium-trial-journey`: proposal.md, design.md, спека `premium-trial`, tasks.md.
   Спек-коммит → `openspec validate --strict`.
5. **Тесты:** три класса, `testng-journey.xml`, `TestUser`; `mvn test-compile`, проверка локаторов.
6. **Живой прогон** после «да»: сначала без ИИ, затем один раз с `-Dqa.ai=true`; доказательства в change-log.
7. **CI:** решить, идёт ли Premium-часть в ночной прогон (раздел 8, вопрос 4); ИИ в CI — по умолчанию нет.
8. `pnpm check` → все задачи с доказательствами → архив → счётчики → push.

## 7. Риски

| Риск | Что делаем |
|---|---|
| Расходы владельца на ИИ и лимиты генераций | ИИ-тесты только по флагу, по 1 разу; в ночном CI выключены |
| Сайт может не дать триал повторно (email / устройство / IP) | Каждый прогон — новый email `+hirion-qa-<время>@`; если триал не стартовал — тест падает, checkout не трогаем |
| Задержка карточек 3–5 мин (баг №49) | Длинный таймаут один раз; прогон станет дольше на ~5 мин |
| Удаление аккаунта с триалом | Проверяем в живой пробе; при ошибке — решение до написания тестов |
| Ответы ИИ меняются | Проверяем только структуру (окно, разделы не пустые, кнопки) |

## 8. Решения человека

**Ответы человека 2026-10-24:** «Берём вариант A (апгрейд того же Free-аккаунта через Settings). Согласие владельца
на триалы есть. ИИ-тесты по флагу -Dqa.ai, в ночном CI без ИИ. Живую пробу делай сам после моего «да».»
1. Согласие владельца на триал-аккаунты — есть; ИИ-вызовы — только в прогонах с `-Dqa.ai=true`.
2. Вариант A.
3. PREM-11…13 входят, по флагу `-Dqa.ai=true`.
4. Premium-часть (без ИИ) идёт в ночном CI вместе с Free-journey; ИИ в CI не запускается.
5. Живую пробу делает агент после «да».

Исходные вопросы:


1. Есть ли согласие владельца сайта на триал-аккаунты (каждый прогон, включая ночной CI) и на ИИ-вызовы?
2. Вариант получения Premium: A (апгрейд того же аккаунта, рекомендую) или B (отдельный аккаунт с триалом при
   регистрации)?
3. ИИ-сценарии PREM-11…13 — включать в это изменение (по флагу) или отложить?
4. Premium-часть в ночном CI — да (каждую ночь +1 триал и +5 мин) или только локально по «да»?
5. Живую пробу (шаг 3) делаю я после вашего «да» или вы вручную, а я записываю факты?
