# Current state -- Capstone Hirion.ch (handoff 2026-10-05)

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
- `pnpm spec:check`: specs 9 · active changes 0 · archived 5; `openspec validate --specs --strict` 9/9.
  Specs: 59 requirements, 93 scenarios.
- Suites (TestNG counts): public 57/57; journey 40 (Registration 15, Dashboard 12, Settings 13) + JourneyCleanup;
  full suite 97.
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
  account: Analytics is Pro-only (Free sees "Unlock Hirion Pro"), AI buttons on job cards are Pro-only, the score
  badge is "87"+"match" (capitals by CSS).
- Defects: D1, D6, D10 KNOWN BUG tests; D2, D4, D5 FIXED by the site (seen live 2026-10-06, change
  `close-fixed-defects-d2-d4-d5`: regular tests now); D3 closed 2026-10-05 (mobile header without "Sign in" is the
  intended design, QA-2 removed); D7 closed (Phone free text by design); D8 and D9 regular checks (data-dependent);
  D11 confirmed live (qa@ passes step 1); D12, D13 candidates (a11y); D14 new, KNOWN BUG, confirmed live
  (required fields only a visual aria-hidden "*"; that star also breaks getByLabel exact -> use role TEXTBOX).
- DASH-8 (Save rate) and photo upload are out of change 3; plan AUTH-03 lives in change 3 as AUTH-6.
- A new user's CV is analysed in the background (AI fills Headline / Short bio) and the Profile form reloads when
  it finishes.
- Lesson logged: read the swallowed underlying error before fixing (a wrong diagnosis of the Profile precondition).
