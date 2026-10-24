## Context

The journey suite creates one disposable Free user (RegistrationTest REG-9), runs DashboardTest and SettingsTest with
its saved session (`.auth/user.json`) and deletes it in `JourneyCleanup`. Variant A of docs/premium-journey-plan.md
upgrades that same user to the 7-day Premium trial after the Free checks.

## Decisions

- **Order.** TestNG sorts by `priority` across all classes of the `<test>` (the existing classes interleave
  priorities 1-13 after `dependsOnGroups = "registered"`). The Premium tests use priorities 101+ so they start only
  after every Free test; `SettingsTest.set5_newPasswordSignsIn` moves from 13 to 300 so the password change stays the
  last test and the Premium tests keep the saved session. `JourneyCleanup` signs in with current or pending password
  as before.
- **Gate.** `PremiumUpgradeTest.prem1` is in group `premium`; every other Premium test has
  `dependsOnGroups = {"registered", "premium"}`, so nothing Premium runs if the trial did not start.
- **Order inside Premium.** Read-only checks first (PREM-2, 3, 4, 8, D20, 10, priorities 102-107), then the AI
  dialogs on the first card (108-110, only with `-Dqa.ai=true`), then the feed-changing actions (PREM-5 save 111,
  PREM-9 save rate 112, PREM-6 applied 113, PREM-7 not relevant 114). The live feed had 3 cards; each action needs one
  card: when the feed has no card left, the test throws `SkipException` (as DASH-6 did), it does not fail.
- **Waiting for cards (PREM-3).** Reopen `/dashboard` and wait 30 s for "Tailor my CV", up to 12 times (6 min); no
  `waitForTimeout` / `Thread.sleep` (AGENTS.md). Later tests wait 60 s for the first card.
- **Card identity.** AGENTS.md allows only role / label / text / test-id locators and the cards have no test id, so
  the tests count buttons per kind instead of addressing one card: one "Tailor my CV" per card. After an action the
  count must drop by one (`hasCount` waits for the feed to settle, which also covers the 2026-10-15 stale-card
  re-render).
- **Stripe guard (PREM-1).** After the click a small `page.evaluate` checks for an iframe whose `src` contains
  "stripe"; if found, the test presses Escape and fails with "Stripe checkout opened instead of the trial". It never
  types into the page.
- **Dates.** PREM-2 expects activation day + 7 (Europe/Zurich) and accepts +8 when the run crosses midnight; PREM-6
  expects today as `MMM d, yyyy` (English, the date of the run in Europe/Zurich, again accepting the day before).
- **AI (PREM-11..13).** `PremiumAiTest` throws `SkipException` in `@BeforeMethod` unless `-Dqa.ai=true`; each test
  clicks one AI button once and checks only labels of the dialog, never the AI text. The CI workflow does not pass
  `-Dqa.ai`. The hook `scripts/guard-live-run.mjs` asks the human for any command with `-Dqa.ai`.
- **Analytics badge (D20 closed).** The badge is rendered while `isPro` is false, also while the subscription is
  loading, so `prem8_analyticsTabHasNoProBadge` waits up to 30 s for a button named exactly "Analytics". It was a
  KNOWN BUG test at first; the first live run showed the plain name and the human closed D20 (2026-10-24).
- **PREM-14.** No TestNG test clicks "Delete" (human decision for DEL-1); the evidence is the `JourneyCleanup` line
  "deleted: ... no longer signs in" of the same run.
- **No TestUser change.** The planned flag `premiumActivated` is not needed: the `premium` group gate and the cleanup
  log tell whether the trial started; `support/**` stays untouched.

## Risks

- The trial could be refused for an email / device / IP (abuse protection): PREM-1 then fails with a clear message
  and all Premium tests are skipped; no checkout is touched.
- AI costs: only with `-Dqa.ai=true`, at most one call per button per run.
- Feed with fewer than 3 cards: the last action tests skip instead of failing.
