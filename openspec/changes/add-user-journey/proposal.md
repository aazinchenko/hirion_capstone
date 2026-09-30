## Why

Changes 1 and 2 cover only what a guest sees. The part of hirion.ch that users depend on is still untested:
the signup wizard, the dashboard, the account settings and deleting the account. This change covers
groups REG, DASH, SET and DEL from `docs/intent.md` with one disposable test user per run. It pins known
defects D6, D8, D9, D10 and D14 (added 2026-09-30), and it adds the plan item AUTH-03 (wrong password), which change 2 moved here.

## What Changes

- Four new capability specs, one per theme:
  - `signup` (REG + AUTH-03): wizard steps 1-4 after the step 1 checks that already exist, the plan
    choice on step 4, landing on `/dashboard` without email confirmation, and a wrong password on `/login`.
  - `dashboard` (DASH): heading, tab buttons, job card, empty states, Save, "Already applied" and
    "Not relevant" through the Hide menu, the Analytics Pro gate a Free user sees, Preferences.
  - `account-settings` (SET): section buttons and deep link, Profile fields, Security fields, password
    change, D9 and D10.
  - `account-deletion` (DEL): the "Delete account" entry point in Settings > Security. The deletion
    dialog and its result stay in Open questions until the first live deletion.
- Test implementation: suite `src/test/resources/testng-journey.xml` (it already exists and lists the
  classes) with `RegistrationTest`, `DashboardTest`, `SettingsTest` and `JourneyCleanup` in
  `src/test/java/ch/hirion/journey/`. The suite runs with one disposable user from `support/TestUser`
  (`<mailbox>+hirion-qa-<digits>@gmail.com`), which `JourneyCleanup` deletes at the end of every run.
- Safety comes first. The cleanup and its email guard `\+hirion-qa-\d+@` are written before any test
  registers. A user left over from an interrupted run is deleted before the next registration.
  `JourneyCleanup.java`, `support/**` and `src/test/resources/**` are closed to the agent in
  `.claude/settings.json`, so a human writes them.
- On step 4 the test always selects Free, because Premium Trial is selected by default. No test starts
  a trial or a payment, no test clicks "Tailor my CV" or "Write Cover Letter", and no test asserts on
  AI text.
- Known defects keep the correct SHALL in the spec. Their tests are KNOWN BUG: D6 ("Show password" not
  reachable by keyboard), D8 ("?" in job titles), D9 (false "unsaved changes"), D10 (any string saved
  as email). The D10 test must never really save an invalid email (see design.md).

## Capabilities

### New Capabilities
- `signup`: wizard steps 2-4 and the step 1 errors that REG-1 / REG-2 do not cover (short password,
  taken email, keyboard access to "Show password" D6). Also plan choice, account creation only on
  step 4, landing on `/dashboard` signed in, and the wrong-password error on `/login` (plan AUTH-03).
- `dashboard`: `/dashboard` for a signed-in user: heading, tab buttons, job card, empty states, Save,
  Hide menu, the Analytics Pro gate for Free, Preferences, correct job title characters (D8).
- `account-settings`: `/settings` sections Profile and Security, deep links, required profile fields,
  password change, no false "unsaved changes" (D9), invalid email not saved (D10).
- `account-deletion`: the "Delete account" button in Settings > Security.

### Modified Capabilities
- (none). REG-1 and REG-2 stay in `access-and-quality` and are not repeated. `signup` starts at REG-3.
  AUTH-3 in `access-and-quality` is the login autofill requirement. The wrong-password requirement
  therefore gets the new ID AUTH-6 and lives in `signup`, because it needs the registered test user.

## Not in this change (human decision, 2026-09-30)

- DASH-8 "Save rate follows saved and feed jobs": Analytics is Pro-only (open question 6). Checking it would
  need Pro -- a payment or the Premium trial, both forbidden by docs/intent.md "Not doing". DASH-7 checks the
  Pro gate instead; the ID DASH-8 stays unused.
- AI buttons on job cards -- "Tailor my CV", "Write Cover Letter", "Help me stand out" (plan DASH-09): Pro-only.
  By the site code the feed passes the AI handler only when the user is Pro, so a Free user's cards have no
  AI buttons (live run 2026-09-30; the fact in docs/intent.md was recorded on the trial account). DASH-2
  checks that they are absent. On Pro a click starts an AI generation against a monthly quota, and
  AGENTS.md forbids assertions on AI text.
- Plan DASH-05 "hidden job appears in Archive" is not a requirement: by the site code Archive holds older
  recommendations that move there after 30 days ("When new daily matches arrive, older recommendations
  move here for 30 days."), not hidden jobs. DASH-6 (Not relevant removes the job from the feed) stays.

- Plan SET-02 "Change photo": not done (human decision 2026-09-30). Every run would upload an image to the
  site's storage, and it is not known whether account deletion removes it.
- Plan SET-05 "LinkedIn URL not-a-url -> validation error": not a requirement -- LinkedIn URL is free text
  by design (docs/intent.md). Plan SET-03 / D7 "phone accepts letters": Phone is free-form text by design.
- Plan SET-04 "empty Email": not tested; D10 (SET-4) covers invalid email safely. No test ever types a valid
  foreign address: the site would send a confirmation link to it (the sign-in email changes only after
  that link is confirmed, so D10 cannot lock the cleanup out).

## Impact

- New test classes in `src/test/java/ch/hirion/journey/`. The agent may edit `RegistrationTest`,
  `DashboardTest` and `SettingsTest`. A human writes `JourneyCleanup.java`,
  `support/TestUser.java` and the CV fixture in `src/test/resources/fixtures/`.
- Each journey run creates one real account on hirion.ch (Free plan) and deletes it. The mailbox
  receives one welcome email per run. Run with
  `mvn -q test -Dsuite=testng-journey.xml -Dqa.mailbox=<mailbox>`.
- `pnpm check` still runs only the public suite (`mvn -q test`). The journey suite is started on
  purpose by a human, never by the gate.
- Session files `.auth/user.json` and `.auth/test-user.json` are git-ignored (`.auth/`).

## Sources of the strings in the specs

All strings in the four specs come from `docs/intent.md` "CONFIRMED live 2026-09-25" and from the known
defects D6, D8, D9 and D10. None of the strings listed below is used in a requirement.

## Open questions (ASSUMED -- drafts until a human confirms on the live site)

1. **Plan & Billing after a Free signup** -- answered from the site code (settings bundle, read 2026-09-30);
   SET-8 checks it in the next live run: a Free account shows "You're currently on the Free plan." (Pro: "You're on the Pro
   plan."), the card "Free plan" with the button "Current plan", and the note "Billing handled securely.
   Cancel anytime.". The next live journey run can confirm it; then a SET requirement can be added.
2. **Account deletion flow** -- CONFIRMED live 2026-09-29 by the first JourneyCleanup run: "Delete account"
   opens the dialog "Delete your account?" ("This permanently removes your profile, CV, preferences and
   saved matches. It cannot be undone.") with "Cancel" / "Delete"; after "Delete" the URL path is `/`,
   the toast "Your account has been deleted." is shown and the user is signed out; signing in again with
   the same email and password fails. Ready for DEL requirements (change 4 or `/opsx:update`).
3. **D7: Phone accepts letters** -- closed 2026-09-30 by a human decision: "Phone" is free-form text by
   design, so letters are not a defect. The site code has no phone validation. No KNOWN BUG test.
4. **Wrong password error text** (AUTH-6) -- from the site code (login bundle, 2026-09-30): the toast shows
   the auth backend's error message as it is (`signInWithPassword` -> `toast.error(error.message)`), not a
   site text; for this backend that is normally "Invalid login credentials", in English also when the site
   language is DE/FR/IT. Still to be read from the page in the next live run (auth6 saw the toast).
5. **How step 4 marks the selected plan** -- answered from the site code (signup bundle, read 2026-09-29):
   the Premium Trial and Free cards are plain `<button>` elements without `aria-pressed`, `aria-checked`
   or `role="radio"`; the selection is only visual (the selected card gets the border classes
   `border-primary` and a check icon). Candidate defect D12 in docs/intent.md. The "Free is selected"
   guard before "See my matches" therefore checks the visual state of the Free card, and REG-7 "selected"
   means that visual state until D12 is fixed. The code also shows why Free matters: with `trial`
   selected, account creation immediately starts the Premium trial.
6. **Analytics on a Free account** -- answered from the site code (dashboard bundle, 2026-09-30): Analytics
   is Pro-only. For a Free user the three sections are still rendered but hidden behind an overlay
   (`aria-hidden="true"` on the content) with the heading "Unlock Hirion Pro", the text "See your match
   analytics, Swiss salary benchmarks, and personalized opportunities to land roles faster." and the link
   "Upgrade to Pro" to `/settings?section=plan`. The Analytics facts in docs/intent.md were recorded on the
   trial (Pro) account. Consequence: DASH-7 / DASH-8 as written cannot be checked on the Free test user --
   decided 2026-09-30: DASH-7 checks the Pro gate (no click on "Upgrade to Pro"), DASH-8 is out of this
   change (see "Not in this change").
7. **Password change confirmation.** The toast or message after "Update password" is not recorded.
   SET-5 asserts the observable result instead: the new password signs in.
8. **Healing journey tests.** `scripts/heal-loop.sh` fetches the live DOM with `.auth/user.json`. After a
   normal run that session belongs to a deleted user. The human decides how heal sessions get a live
   user (design.md "Healing").
9. **Wizard Back and the `plan` parameter** (plan REG-09, REG-11) -- confirmed from the site code only
   (2026-09-29), not yet seen live: all wizard data (names, email, password, CV, roles, plan) lives in
   one component state, "Back" only lowers the step number, so the data of steps 1-3 is kept; "Back" on
   step 1 goes to `/`. The initial plan is the `plan` URL parameter, else `trial`: `?plan=trial` equals no
   parameter, `?plan=free` preselects Free, any other value selects no card. Candidates for change 4.
10. **Invalid email on step 1** (plan REG-03 "qa@ -> error, stay on step 1") -- the site code checks only
   "email not empty" and "password at least 8 characters"; the input has `type="email"` but there is no
   `<form>` submit, so the browser never checks the format and "qa@" reaches step 2. Candidate defect D11
   in docs/intent.md. CONFIRMED live 2026-09-30 by a temporary guest probe: "qa@" plus an 8+ character
   password -> "Step 2 of 4", no error message (no account created; the probe was not committed).
