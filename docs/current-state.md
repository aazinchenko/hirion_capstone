# Current state -- Capstone Hirion.ch (handoff 2026-10-06)

Read this first in a new session. Plan: `../конспекты/hirion-capstone-plan-v3-java-openspec.pdf` (30 steps).
Reports: `../capstone-summary-2026-09-25.pdf`, `-09-28.pdf`, `-09-29.pdf`; cheat sheet
`../capstone-cheatsheet-agentic-engineering.pdf`.

## Where we are
- Steps 1-26 DONE (2026-09-30 ~18:40). Change 4 `update-signup-steps` archived: MODIFIED REG-7 (?plan=free /
  ?plan=trial preselection, Back keeps the wizard data; 5 scenarios). Commits: 25cec2f (intent facts from a
  guest probe), bc91ab0 (propose), 895161a (tests, journey 40/40 live), archive commit after it.
- 2026-10-05: full live run `testng-all.xml` GREEN 98/98 in 4:12 min (public 58 + journey 40), test account
  deleted by JourneyCleanup. Then the human decided D3 is NOT a defect: change 5 `remove-qa2-mobile-nav`
  (REMOVED QA-2, KNOWN BUG D3 test deleted). Commits: b575491 (propose), 68e0283 (test; `pnpm check` green,
  public 57/57), 68c9fce (archive, run by the human; verified: only QA-2 removed).
- 2026-10-06: GitHub Actions CI added (`a01b420`, `.github/workflows/tests.yml`): public suite on push/PR,
  journey on manual run and nightly (cron 02:15 UTC), mailbox from the secret `QA_MAILBOX`, no artifacts for the
  journey job. CI run 37496066639 found that the site fixed D2, D4, D5, D6, D10, D14 (their KNOWN BUG tests
  failed with "should have thrown"): change 6 `close-fixed-defects-d2-d4-d5` and change 7
  `close-fixed-defects-d6-d10-d14` archived, the tests are regular checks now; only D1 is still KNOWN BUG.
  CI run 37501768600 all green: public 57, journey 40, test account deleted.
- 2026-10-15: change 8 `stabilize-dashboard-feed` archived (DASH-10/11 before DASH-4, settled feed, DASH-6 skip
  on a 2-job feed).
- 2026-10-19/20: the site changed its plans (CI public 51/57). Change 9 `update-plans-and-pricing`: `#pricing`
  Monthly / Yearly with Free / Basic / Premium, new FAQ answer, signup step 4 with three cards (`aria-pressed`,
  D12 fixed) and the submit button "Join for free"; new KNOWN BUG D16 (`?plan=basic` selects Premium).
  The Free Job Feed is now a locked teaser ("We found N jobs for you", "Unlock with Basic"): DASH-2 checks it,
  DASH-4/5/6/10/11 removed (they need the paid plan Basic).
- 2026-10-20: change 10 `fix-teaser-row-count`: the teaser shows min(N, 3) rows (CI run N = 10 -> 3 rows); journey
  36/36 live after the fix.
- 2026-10-21: change 11 `rename-analytics-gate-premium`: the Analytics gate says "Unlock Hirion Premium" /
  "Upgrade to Premium" (CI journey 35/36 before); journey 36/36 live after the fix; archived by the agent at the
  human's request ("сделай весь цикл сам").
- 2026-10-23: full site scan (human's request). Nightly CI 92/93: DASH-2 "Unlock with Basic" now points to
  `/settings?section=plan&buy=basic` (starts the Basic checkout). Change 12 `sync-specs-with-site-scan`:
  MODIFIED DASH-2, AUTH-1 (+ /onboarding, /employer/, /admin); ADDED AUTH-7 (Google / Apple buttons), AUTH-8
  (reset-password without a link); new capability `email-link-pages` LINK-1..3 (survey, unsubscribe, checkout
  return). Candidates D17-D19 in intent.md. Public 65/65 green; live journey 36/36 (2026-10-23 20:09, account deleted); archived by the agent at
  the human's request via `pnpm exec openspec archive` (verified: only AUTH-1 and DASH-2 changed, AUTH-7/8 and
  LINK-1..3 added verbatim).
- `pnpm spec:check`: specs 10 · active changes 0 · archived 12; `openspec validate --specs --strict` 10/10.
  Specs: 59 requirements, 97 scenarios.
- Suites (TestNG counts): public 65 (since change 12; before 57); journey 36 (Registration 17, Dashboard 6,
  Settings 13) + JourneyCleanup; full suite 101.
- Draft, not started: Pro-user journey plan `docs/pro-journey-plan-draft.md` (needs human decisions, section 7).
- Known flaky: `BaseTest.closeContext` once threw "Target page, context or browser has been closed" in
  `pnpm check` (public cont3/cont4 skipped); rerun green. Human-owned file, not fixed.
- Open: question 11 (D9 depends on profile data, ASSUMED); unknown `?plan` value -> Premium Trial (confirmed,
  not in a spec); D11 has no test (REG-1 not extended, human decision variant A).

## Next actions
1. Step 27 DONE (docs/autonomy-log.md). 2. Step 28 DONE (docs/decisions.md, budget actuals in docs/intent.md).
3. Step 29: video <= 2 min. 4. Step 30: Pull Request. Show a plan first, act on "старт".

## Rules of the workflow (agreed with the human)
- Show a plan first, act on "старт"; spec commit before test commit; every [x] in tasks.md needs evidence in
  change-log.md.
- Live journey runs create a real account on hirion.ch (owner consented): run only after an explicit "да".
  One account per run; `JourneyCleanup` deletes it (guard `\+hirion-qa-\d+@`, @BeforeSuite leftover cleanup,
  `submitted` flag). Never type a password into a form myself; never a valid foreign email.
- Tests are written by the agent via `/opsx:apply` in the human's session; I review against the site code
  (read-only JS bundles) and fix human-owned files (`support/**`, `JourneyCleanup`, resources).
- heal-loop for journey tests creates an account per iteration -- prefer targeted human fixes when the cause is
  known; heal RegistrationTest only without `-Dqa.mailbox`.

## Key facts / decisions (details in docs/intent.md and proposal.md of change 3)
- Test user is always Free; facts in intent.md about the dashboard/settings were recorded on a trial (Pro)
  account: Analytics is Pro-only (Free sees "Unlock Hirion Premium" since 2026-10-21, before "Unlock Hirion Pro"), AI buttons on job cards are Pro-only, the score
  badge is "87"+"match" (capitals by CSS).
- Defects, human decision 2026-10-24: D1, D2, D4, D5, D6, D8, D9, D10, D11, D12, D14, D19 are CLOSED (fixed or not
  relevant; D1 and D19 still visible live 2026-10-24, D1 KNOWN BUG test / FOOT-3 still in the suite). Open: D13,
  D16 (KNOWN BUG), D17, D18. History: D1 and D16 KNOWN BUG tests; D2, D4, D5, D6, D10, D14 FIXED by the site (seen live 2026-10-06, changes
  `close-fixed-defects-d2-d4-d5` and `close-fixed-defects-d6-d10-d14`: regular tests now); D3 closed 2026-10-05 (mobile header without "Sign in" is the
  intended design, QA-2 removed); D7 closed (Phone free text by design); D8 not observable for Free since 2026-10-20 (DASH-10 removed), D9 regular check (data-dependent);
  D11 confirmed live (qa@ passes step 1); D12 FIXED (seen live 2026-10-19); D13 candidate (a11y); D14 FIXED (see above;
  the aria-hidden "*" in the label still breaks getByLabel exact -> use role TEXTBOX).
- DASH-8 (Save rate) and photo upload are out of change 3; plan AUTH-03 lives in change 3 as AUTH-6.
- A new user's CV is analysed in the background (AI fills Headline / Short bio) and the Profile form reloads when
  it finishes.
- Lesson logged: read the swallowed underlying error before fixing (a wrong diagnosis of the Profile precondition).
