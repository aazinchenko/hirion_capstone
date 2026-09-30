# Change log -- add-user-journey

## Group 1 "Safety first" done by a human (plan steps 20-21, 2026-09-29)

Written in a human session (JourneyCleanup, support/** and src/test/resources/** are denied to the agent).
No account was created in this group.

- 1.3 `support/TestUser` (commit 7b21f12): `<qa.mailbox>+hirion-qa-<millis>@gmail.com`, random 16-character
  password, first name "Qa", state in `.auth/test-user.json` (email, firstName, currentPassword,
  pendingPassword, accountCreated). Check run with `-Dqa.mailbox=demo`:
  `email=demo+hirion-qa-1790697052699@gmail.com matchesGuard=true passwordLength=16` (nothing saved);
  without `-Dqa.mailbox` the test is SKIPPED ("-Dqa.mailbox=<gmail local part> is required ...").
  `.auth/` is in .gitignore. The shared helpers `open()`, `reload()`, `h1()`, `field()`, `QUICK_ATTR`
  moved from `pub/PublicPageTest` to `support/BaseTest`, so `AuthenticatedTest` classes can use them;
  public suite still 58/58.
- 1.4 `src/test/resources/fixtures/qa-cv.pdf`: a one-page fictional CV ("Alex Muster", QA Automation
  Engineer, alex.muster@example.com, marked "FICTIONAL TEST CV"), 2.4 KB, generated with reportlab.
  `.gitattributes` now marks `*.pdf` / `*.png` binary (commit 43bc8ad): with `core.autocrlf=true` git had
  taken the PDF for text. `testng-journey.xml` already lists RegistrationTest, DashboardTest,
  SettingsTest, JourneyCleanup with `preserve-order="true"`.
- 1.1 / 1.2 `journey/JourneyCleanup`: one `deleteUser()` for `@BeforeSuite(alwaysRun = true)` (leftover user
  of an interrupted run) and `@AfterSuite(alwaysRun = true)` (this run's user). Order: guard
  `\+hirion-qa-\d+@` before any browser -> sign in with currentPassword, then pendingPassword ->
  `/settings?section=security` -> "Delete account" -> dialog (logged) -> "Delete" -> URL path `/` ->
  a new sign-in must fail (plan DEL-03) -> delete `.auth/test-user.json` and `.auth/user.json` (plan DEL-04).
  Sign-in fails with accountCreated=false -> only the state file is removed; with accountCreated=true ->
  fails loudly with the email, state kept.
  Dry runs with a temporary test class (not committed):
  1. state `someone@gmail.com` -> `IllegalStateException: Refusing to delete a non-test account:
     someone@gmail.com` after 376 ms (no browser started); state file and `.auth/user.json` untouched.
  2. no state file -> nothing happens (193 ms).
  3. state `qa-dryrun+hirion-qa-1@example.com`, accountCreated=false (no such account; one failed sign-in on
     the live site) -> "sign-in did not reach /dashboard" -> "removing the state file only"; file removed,
     `.auth/user.json` and `.auth/manual-user.json` untouched. The login page showed no error text at the
     moment of the snapshot, so Open question 4 (wrong-password text) stays open.
  A failing `@BeforeSuite` makes TestNG skip the rest of the suite, so a foreign leftover state stops the
  run before RegistrationTest; this is TestNG behaviour, not yet seen in a full journey run.
- The human's own session of the hand-made test account was copied to the git-ignored
  `.auth/manual-user.json` before any journey run can overwrite `.auth/user.json` (proposal Open question 8).
- `java scripts/CheckLocatorRules.java src/test/java` -> `PASS: no forbidden patterns`.

## 2.1 RegistrationTest written (2026-09-29) -- NOT run

`src/test/java/ch/hirion/journey/RegistrationTest.java` extends `support/BaseTest` (guest context per
test). It has 12 `@Test`, in the tasks.md 2.1 order. Only `mvn -q test-compile` ran (exit 0); no test ran and
no account was created. A human starts the first live run (task 2.5).

- Run order (`priority`): reg3 1, reg5 2/3, reg6 4/5, reg7 6/7, reg8 8, reg10 9, **reg9 10**, reg4 11,
  auth6 12. So every guest test runs before the account exists. reg4 and auth6 have
  `dependsOnMethods = "reg9_freeSignupLandsOnDashboard"` and read the user through `TestUser.load()`
  (TestNG asserts that the state exists and `accountCreated=true`).
- reg9 is the only account creation. `TestUser.generate()` comes first (it skips without `-Dqa.mailbox`).
  Then `/signup?plan=free` → steps 1-3 → click on the Free card → a Playwright wait for the selected
  class → **guard** with `org.testng.Assert`: Free selected and Premium Trial not, otherwise it fails before
  the submit. Then `accountCreated=false` + `save()` → click "See my matches" (the only click, line 171)
  → URL `/dashboard` (60 s) → `accountCreated=true` + `save()` → `context.storageState` to
  `.auth/user.json` → h1 "Welcome back, Qa". Emails and passwords are not printed. Assertion messages
  contain neither. The Playwright trace of a failed test (git-ignored `target/traces`) does contain the
  typed values.
- Selection check (proposal Open question 5, D12). The selected card has the class token `border-primary`,
  and an unselected card has `hover:border-primary/40`, so a plain substring would give a false "selected".
  Playwright checks `hasAttribute("class", (^|\s)border-primary(\s|$))`, and the guard splits the class
  into tokens. The card is found as `getByRole(BUTTON).filter(has: getByText("Free", exact))`.
- Locators taken from the live signup bundle (`/assets/signup-*.js`, `main-*.js`, read with curl
  2026-09-29, only GET requests):
  - Step 2: the file input sits inside the `<label>` "Drop your CV here, or click to upload". It is found
    by `getByLabel(...)` and clicked inside `page.waitForFileChooser`. After the upload the card shows
    the file name "qa-cv.pdf".
  - Step 3: `combobox` "Add" → `option` "QA / Test Engineer" → Escape. The site has no "QA Engineer" role;
    this is the closest entry in the list.
  - Titles "Upload your CV", "What roles are you looking for?" and "Choose your plan" are `h2` (heading).
    "Step N of 4" is a separate `<span>`.
  - `/login` shows the error as a sonner toast (`toast.error(error.message)` from Supabase) in the region
    "Notifications alt+T". auth6 checks for a list item in that region, and the text stays Open question 4.
- reg8: `page.onRequest` records requests from step 2 up to reaching step 4. It records any
  `/_serverFn/` call, plus non-GET requests to `hirion.ch` / `*.supabase.co`. It stores only
  method + URL. The step 1 email check is not counted. Stripe and other third parties are ignored. The code
  confirms REG-8: `supabase.auth.signUp`, role seeding and the CV upload run only in the step 4 handler.
- reg10 (KNOWN BUG D6): `expectedExceptions = AssertionFailedError.class`,
  `expectedExceptionsMessageRegExp = "(?s).*tabindex.*"`, precondition "exactly one Show password" through
  `org.testng.Assert`, assertion `not().hasAttribute("tabindex", "-1", QUICK_ATTR)`.
- Risk for the RED run: if the site creates the account but returns no session (`!data.session` → "check
  your email" screen), reg9 does not reach `/dashboard` and `accountCreated` stays false. The cleanup then
  removes only the state file. intent.md says there is no email confirmation, so this case should not
  happen. If it does, the account must be deleted by hand.
- `java scripts/CheckLocatorRules.java src/test/java` → `PASS: no forbidden patterns`.

## Plan step 22: first live run of RegistrationTest -- RED (2026-09-29 18:18-18:19)

- Before the run (human): `TestUser.submitted` added. reg9 sets it right before "See my matches";
  JourneyCleanup removes the state file silently only when the user was neither submitted nor created,
  otherwise it fails loudly with the email -- so an account created without a session cannot be dropped.
- `mvn -q test -Dsuite=testng-journey.xml -Dqa.mailbox=<mailbox>` (suite: RegistrationTest + JourneyCleanup;
  DashboardTest / SettingsTest are added in plan steps 23 / 24): 12 tests, 6 passed, 4 failed, 2 skipped.
  - PASS: reg3_shortPasswordBlocked, reg5_stepTwoRendered, reg5_stepTwoWithoutFileBlocked (CV upload through
    the file chooser works), reg6_stepThreeRendered, reg6_stepThreeWithoutRoleBlocked,
    reg10_showPasswordInTabOrder (KNOWN BUG D6 caught as expected).
  - FAIL (all at RegistrationTest.java:257, `role(AriaRole.COMBOBOX, "Add").click()`, TimeoutError 10 s):
    reg7_stepFourPremiumTrialSelected, reg7_freeCanBeSelected, reg8_stepsOneToThreeSendNothing,
    reg9_freeSignupLandsOnDashboard.
  - SKIP: reg4_takenEmailBlocked, auth6_wrongPasswordStaysOnLogin (depend on reg9).
- Cause (from the trace snapshot of step 3): the role picker is `<button role="combobox"
  aria-haspopup="dialog">` with the visible text "Add". A combobox does not take its accessible name from
  its content and there is no `aria-label`, so the element has no accessible name and the locator with the
  name "Add" matches nothing. A locator error for heal-loop (plan step 25) -- and a candidate
  accessibility defect: a screen reader announces only "combobox".
- No account was created: reg9 failed inside walkToStep on step 3, before the Free guard, the state save
  and "See my matches"; `.auth/test-user.json` never existed, JourneyCleanup had nothing to delete
  (no log lines), `.auth/user.json` and `.auth/manual-user.json` untouched. Open questions 2, 4, 5 remain
  open (step 4, login error and deletion were not reached).

## Plan step 25 (early, registration only): healed with heal-loop.sh (2026-09-29 16:28-16:33 UTC)

- Run by a human: `./scripts/heal-loop.sh "-Dsuite=testng-journey.xml" src/test/java/ch/hirion/journey/RegistrationTest.java
  https://hirion.ch/signup main` -- on purpose WITHOUT `-Dqa.mailbox`, so reg9 is skipped and no iteration can
  create an account (with `-Dtest=RegistrationTest` a green iteration would create one and JourneyCleanup
  would not run).
- `.agent-log/heal-log.jsonl`: RED on iteration 1 -> fix -> RED on iteration 2 -> fix -> GREEN on iteration 3;
  no REJECTED. `.agent-log/heal-diff.patch`: only `addRole()` changed --
  iteration 1: the unnamed combobox (D13) is found by role + exact visible text "Add";
  iteration 2: the chosen-role chip is checked inside `main`, because the option list is portaled outside it.
- Human review of the diff: the Free guard (lines 166-167), `submitted = true` and the single
  "See my matches" click (line 172) are unchanged; `CheckLocatorRules` PASS.
- Last run: 12 tests, 9 passed, 3 skipped (reg9, reg4, auth6 -- they need `-Dqa.mailbox`). Confirmed live on the
  way: step 4 shows Premium Trial selected by default and Free can be selected (visual state, open question 5
  for the guard), steps 2-3 send nothing to the backend (REG-8).

## First live run with an account: RegistrationTest GREEN, account created and deleted (2026-09-29 18:35-18:36)

- Human "go" in chat. `mvn -q test -Dsuite=testng-journey.xml -Dqa.mailbox=<mailbox>` -> exit 0,
  Tests run: 12, passed 12, failed 0, skipped 0 (68 s).
- reg9 created one Free account (`<mailbox>+hirion-qa-<millis>@gmail.com`) after the Free guard passed and
  landed on /dashboard; reg4 got "An account with this email already exists. Sign in instead." for it;
  auth6 made one wrong-password attempt: stayed on /login with an error toast (text not recorded).
- JourneyCleanup (@AfterSuite) output:
  ```
  deleting this run's user: <qa-user> (accountCreated=true)
  dialog: Delete your account?  This permanently removes your profile, CV, preferences and saved matches. It cannot be undone.  Cancel Delete
  redirected to https://hirion.ch/; page text after delete: ... Your account has been deleted.
  sign-in did not reach /dashboard; page says: ... Welcome back ... Sign in ...
  deleted: <qa-user> no longer signs in
  ```
  `.auth/test-user.json` and `.auth/user.json` removed; `.auth/manual-user.json` kept.
- Open question 2 (deletion) CONFIRMED live and recorded in proposal.md; open question 5 answered (visual
  selection works as the guard); open question 4 (wrong-password text) still open.

## Open items resolved (2026-09-30)

- Gmail (checked with the human's consent through the Gmail connector): exactly one mail to the test address of
  the 2026-09-29 run -- "Welcome to Hirion. Your job search starts now" at 18:36 -- and no trial / billing mail:
  the Free guard held.
- D11 CONFIRMED live by a temporary guest probe (not committed): "qa@" + an 8+ character password -> the wizard
  shows "Step 2 of 4", no error. No account was created.
- Open question 1 (Plan & Billing on Free), 4 (wrong-password text) and 6 (Analytics on Free) answered from the
  site code (read-only), details in proposal.md. The important one is 6: Analytics is Pro-only; a Free user sees
  the overlay "Unlock Hirion Pro" with "Upgrade to Pro", so DASH-7 / DASH-8 need a human decision before
  DashboardTest is written.
- `.auth/user.json`: nothing to do -- the journey run writes a fresh one in reg9 and cleanup removes it; the
  hand-made session stays in `.auth/manual-user.json`.
