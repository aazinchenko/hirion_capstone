# Current state -- Capstone Hirion.ch (handoff 2026-09-30, ~17:55)

Read this first in a new session. Plan: `../hirion-capstone-plan-v3-java-openspec.pdf` (30 steps).
Reports: `../capstone-summary-2026-09-25.pdf`, `-09-28.pdf`, `-09-29.pdf`; cheat sheet
`../capstone-cheatsheet-agentic-engineering.pdf`.

## Where we are
- Steps 1-25 DONE (2026-09-30 ~17:55). Change 3 `add-user-journey` archived (commit 5e844a6).
- `pnpm spec:check`: specs 9 · active changes 0 · archived 3; `openspec validate --specs --strict` 9/9.
- Last full live run `testng-all.xml` 17:38: 95/95 (public 58 + journey 37), test account deleted.
- `pnpm check` green. Locator review 2026-09-30: APPROVE (`.agent-log/review-2026-09-30.md`); fixes in
  63ceb04 (incl. the Medium finding #1: SET-3 counts visible matches) -- verified live in the 95/95 run.
- Open, carried to change 4: proposal question 9 (wizard Back, `?plan=`), question 11 (D9 depends on the
  profile data? ASSUMED). Known limit: SET-3 notice check cannot catch a notice that shows > 3 s after the
  form settled (design.md of the archived change).

## Next actions
1. Step 26: change 4 `update-signup-steps` (MODIFIED requirements; take question 9 from the archived
   proposal). Show a plan first, act on "старт".
2. Steps 27-30: autonomy-log.md, decisions.md + budget vs actual, video <= 2 min, Pull Request.

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
