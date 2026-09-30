# Current state -- Capstone Hirion.ch (handoff 2026-09-30, ~18:40)

Read this first in a new session. Plan: `../конспекты/hirion-capstone-plan-v3-java-openspec.pdf` (30 steps).
Reports: `../capstone-summary-2026-09-25.pdf`, `-09-28.pdf`, `-09-29.pdf`; cheat sheet
`../capstone-cheatsheet-agentic-engineering.pdf`.

## Where we are
- Steps 1-26 DONE (2026-09-30 ~18:40). Change 4 `update-signup-steps` archived: MODIFIED REG-7 (?plan=free /
  ?plan=trial preselection, Back keeps the wizard data; 5 scenarios). Commits: 25cec2f (intent facts from a
  guest probe), bc91ab0 (propose), 895161a (tests, journey 40/40 live), archive commit after it.
- `pnpm spec:check`: specs 9 · active changes 0 · archived 4 (the "done" criterion of docs/intent.md);
  `openspec validate --specs --strict` 9/9.
- Suites: public 58/58; journey 40 (Registration 15, Dashboard 12, Settings 13) + JourneyCleanup.
- Known flaky: `BaseTest.closeContext` once threw "Target page, context or browser has been closed" in
  `pnpm check` (public cont3/cont4 skipped); rerun green. Human-owned file, not fixed.
- Open: question 11 (D9 depends on profile data, ASSUMED); unknown `?plan` value -> Premium Trial (confirmed,
  not in a spec); D11 has no test (REG-1 not extended, human decision variant A).

## Next actions
1. Step 27 DONE (docs/autonomy-log.md). 2. Step 28: decisions.md + budget vs actual (docs/intent.md budget table).
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
- Defects: D1-D6, D10 KNOWN BUG tests; D7 closed (Phone free text by design); D8 and D9 regular checks (data-dependent);
  D11 confirmed live (qa@ passes step 1); D12, D13 candidates (a11y); D14 new, KNOWN BUG, confirmed live
  (required fields only a visual aria-hidden "*"; that star also breaks getByLabel exact -> use role TEXTBOX).
- DASH-8 (Save rate) and photo upload are out of change 3; plan AUTH-03 lives in change 3 as AUTH-6.
- A new user's CV is analysed in the background (AI fills Headline / Short bio) and the Profile form reloads when
  it finishes.
- Lesson logged: read the swallowed underlying error before fixing (a wrong diagnosis of the Profile precondition).
