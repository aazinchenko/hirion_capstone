# Current state -- Capstone Hirion.ch (handoff 2026-09-30, ~16:00)

Read this first in a new session. Plan: `../hirion-capstone-plan-v3-java-openspec.pdf` (30 steps).
Reports: `../capstone-summary-2026-09-25.pdf`, `-09-28.pdf`, `-09-29.pdf`; cheat sheet
`../capstone-cheatsheet-agentic-engineering.pdf`.

## Where we are
- Steps 1-23 DONE. Step 24 (SettingsTest, task 2.3 of change 3) IN PROGRESS.
- `pnpm spec:check`: specs 5 · active changes 1 (`add-user-journey`) · archived 2.
- Public suite `testng-public.xml`: 58/58 green.
- Journey suite `testng-journey.xml` = RegistrationTest, DashboardTest, SettingsTest, JourneyCleanup (37 tests).
  Last live run (3rd with Settings, 15:50): 33/37. Registration 12/12, Dashboard 12/12 are GREEN.

## Uncommitted work (commit first!)
Edits made while shell commands were blocked by a service outage -- not yet compiled or committed:
- `src/test/java/ch/hirion/journey/SettingsTest.java`: `waitForProfile` also waits up to 90 s for the CV block
  text "N skills detected" / "no skills detected" (constants `CV_ANALYSED`, `CV_ANALYSIS_TIMEOUT`); set2 checks
  `role(LINK, "View")` instead of a button.
- `openspec/changes/add-user-journey/specs/account-settings/spec.md`: SET-2 says the link "View"; "Save changes"
  disabled until a change.
- `openspec/changes/add-user-journey/change-log.md`: entry "third live run -- 33/37".
Do: `pnpm exec openspec validate add-user-journey --strict --no-interactive`,
`java scripts/CheckLocatorRules.java src/test/java`, `mvn -q test-compile`, then commit
("test: SettingsTest -- wait for the CV analysis, View is a link (plan step 24)").

## Next actions
1. Commit the edits above.
2. 4th live run -- ONLY after the human says "да" (creates one real account, deletes it):
   `mvn -q test -Dsuite=testng-journey.xml -Dqa.mailbox=anatoleyz`
3. Open failures of the 3rd run: set2 (View link -> fixed), set6 (form wiped by background CV analysis -> fixed by
   the wait), set3 x2 = D9 not reproduced. If D9 still does not show after the wait -> ask the human
   (like D8: make it a regular check or keep KNOWN BUG).
4. Then step 25: task 3.x (open questions, heal if needed), `pnpm check`, locator-reviewer in the human's own
   session, `/opsx:archive add-user-journey` -> expect specs 9 · archived 3; Purpose without TBD.
5. Steps 26-30: change 4 `update-signup-steps` (MODIFIED), autonomy-log.md, decisions.md + budget vs actual,
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
- Defects: D1-D6, D9, D10 KNOWN BUG tests; D7 closed (Phone free text by design); D8 regular check (feed-dependent);
  D11 confirmed live (qa@ passes step 1); D12, D13 candidates (a11y); D14 new, KNOWN BUG, confirmed live
  (required fields only a visual aria-hidden "*"; that star also breaks getByLabel exact -> use role TEXTBOX).
- DASH-8 (Save rate) and photo upload are out of change 3; plan AUTH-03 lives in change 3 as AUTH-6.
- A new user's CV is analysed in the background (AI fills Headline / Short bio) and the Profile form reloads when
  it finishes.
- Lesson logged: read the swallowed underlying error before fixing (a wrong diagnosis of the Profile precondition).
