# Current state -- Capstone Hirion.ch (handoff 2026-09-30, ~16:35)

Read this first in a new session. Plan: `../hirion-capstone-plan-v3-java-openspec.pdf` (30 steps).
Reports: `../capstone-summary-2026-09-25.pdf`, `-09-28.pdf`, `-09-29.pdf`; cheat sheet
`../capstone-cheatsheet-agentic-engineering.pdf`.

## Where we are
- Steps 1-24 DONE. Step 24 (SettingsTest) closed with the 6th live run GREEN 37/37 (16:30, account deleted).
- `pnpm spec:check`: specs 5 · active changes 1 (`add-user-journey`) · archived 2.
- Public suite `testng-public.xml`: 58/58 green.
- Journey suite `testng-journey.xml` = RegistrationTest 12, DashboardTest 12, SettingsTest 13, JourneyCleanup.
- Step 24 findings (details in change-log.md of change 3): the Profile form re-fills when the profile record
  arrives -- "Email" comes from the session, so `waitForProfile` waits for First name = "Qa" (+ CV "skills
  detected"); the site stores Phone as "+41..." without spaces (SET-6 types "790000000"); D9 not reproduced on
  a new Free user in 4 runs -> SET-3 regular checks (human decision, like D8), hypothesis in design.md.

## Next actions
1. Step 25: task 3.x (open questions, heal if needed), `pnpm check`, locator-reviewer in the human's own
   session, `/opsx:archive add-user-journey` -> expect specs 9 · archived 3; Purpose without TBD.
   Before archiving: proposal.md still lists D8 and D9 among the KNOWN BUG tests -- update (human agreed to look).
2. Steps 26-30: change 4 `update-signup-steps` (MODIFIED), autonomy-log.md, decisions.md + budget vs actual,
   video <= 2 min, Pull Request.
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
