## Context

The public suite from changes 1 and 2 opens a new guest context for every test. `ch.hirion.pub.PublicPageTest`
provides `open()` (hydration wait), `h1()` and `field()` (exact label). `support/AuthenticatedTest` already
starts contexts from `.auth/user.json`. `src/test/resources/testng-journey.xml` already lists
`RegistrationTest`, `DashboardTest`, `SettingsTest` and `JourneyCleanup` with `preserve-order="true"`.
The following files do not exist yet: `support/TestUser.java`, `journey/JourneyCleanup.java` and
`src/test/resources/fixtures/`.
`.claude/settings.json` lets the agent edit only the three journey test classes. `JourneyCleanup.java`,
`support/**` and `src/test/resources/**` are human-owned. Requirements are in the four specs, and
motivation and open items are in proposal.md.

## Goals / Non-Goals

**Goals:**
- One `@Test` per spec scenario, method names prefixed with the requirement ID
  (`reg5_stepTwoRendered`, `dash4_saveMovesJob`, `set4_invalidEmailNotSent`).
- Exactly one real account per run, always on Free, always deleted. The only emails the suite ever
  touches match `\+hirion-qa-\d+@`.

**Non-Goals:**
- Plan & Billing, the deletion dialog texts and D7 (proposal Open questions 1-3).
- Photo upload, "View job", "Tailor my CV", "Write Cover Letter", "Re-analyse", "Replace", and saving
  Preferences. These actions call AI, change the CV or open external pages. Their buttons are only
  checked for presence.
- Running the journey from `pnpm check`.

## Decisions

- **Test user state (human, `support/TestUser`).** The user is created from `-Dqa.mailbox=<local part>`
  as `<mailbox>+hirion-qa-<epoch millis>@gmail.com`, with a random password of at least 12 characters and
  the fixed first name "Qa". If `qa.mailbox` is missing, the journey is skipped (`SkipException`) and
  nothing is created. State lives in the git-ignored `.auth/test-user.json`:
  `{email, firstName, currentPassword, pendingPassword, accountCreated}`. The file is written before
  "See my matches" is clicked (`accountCreated=false`), and `accountCreated` is set to `true` when
  `/dashboard` is reached. As a result, a crash at any point after the click still leaves a record.
  Alternative rejected: keeping the user only in memory. An interrupted run would then leave an
  orphan account that no one can find.
- **Password tracking.** SET-5 changes the password. Before clicking "Update password" the test writes
  the new password to `pendingPassword`. After a fresh guest context signs in with it (the SET-5
  assertion), `currentPassword = pendingPassword` and `pendingPassword = null`. Cleanup signs in with
  `currentPassword`, then falls back to `pendingPassword`, so it works whether the update succeeded,
  failed or was interrupted. SET-5 runs last in `SettingsTest`, so no other test depends on the old
  password. AUTH-6 uses a password that is neither current nor pending and makes exactly one wrong
  attempt, so the account is not locked.
- **Cleanup (human, `journey/JourneyCleanup`).** A standalone class, not a subclass of the tests, with
  two entry points that share one `deleteUser(state)` method:
  - `@BeforeSuite(alwaysRun = true)` (task 1.2): if `.auth/test-user.json` exists from an interrupted
    run, it deletes that user before anything registers.
  - `@AfterSuite(alwaysRun = true)` (task 1.1): it deletes this run's user, even when tests failed or
    were skipped.
  `deleteUser` first checks the email against `\+hirion-qa-\d+@`. On a mismatch it throws and touches
  nothing. It then opens a fresh context, signs in on `/login`, opens `/settings?section=security`,
  clicks "Delete account", confirms "Delete" in the dialog, and waits for the URL path `/`. On success it
  deletes `.auth/test-user.json` and `.auth/user.json`. If sign-in fails and `accountCreated=false`,
  there is nothing to delete, so the file is removed. If sign-in fails and `accountCreated=true`, it
  keeps the file, fails loudly and prints the email for a human. The dialog strings are unconfirmed
  (Open question 2). The first live run records what it saw in the change log, and a human confirms it.
- **Order and dependencies.** The suite is ordered: `RegistrationTest` → `DashboardTest` →
  `SettingsTest` → `JourneyCleanup`. Inside a class, `priority` sets the order that the scenarios need:
  DASH-3 empty states come before DASH-4 Save (DASH-8 Save rate was dropped on 2026-09-30, Pro-only). DASH-5 and DASH-6 act on other cards than DASH-4. SET-5 comes last. The registration test
  (`reg9_freeSignupLandsOnDashboard`) belongs to group `registered` and saves the storage state to
  `.auth/user.json`. `DashboardTest` and `SettingsTest` extend `AuthenticatedTest` and use
  `dependsOnGroups = "registered"`, so they are skipped rather than run against a stale session.
  Guest-only scenarios (REG-3, REG-5 / REG-6 blocked steps, REG-10) run with a fresh guest context and
  never click "See my matches".
- **Walking the wizard.** A helper in `RegistrationTest` drives steps 1-3 (fill step 1, upload the CV,
  add one role) and returns on step 4. REG-3 and REG-5 to REG-8 reuse it and stop early. Only
  `reg9_freeSignupLandsOnDashboard` clicks "See my matches". The CV is uploaded through
  `page.waitForFileChooser(() -> click "Upload your CV" area)` with
  `src/test/resources/fixtures/qa-cv.pdf`, so the hidden file input needs no CSS locator. The fixture is
  a made-up CV with no real personal data, and a human creates it.
- **Free, never the trial.** Before clicking "See my matches" the test selects Free. It then checks with
  `org.testng.Assert` that Free is selected and Premium Trial is not. On a failure it stops without
  clicking, so a wrong selection can never start a trial. REG-7 checks the Premium Trial default in the
  same flow before switching. The attribute that marks the selection is still open (Open question 5).
  Alternative considered: `/signup?plan=free`. It is kept as a fallback, but it would hide the default
  check.
- **REG-8 network check.** `page.onRequest` records non-GET requests whose host is `hirion.ch` (or the
  backend host seen in the RED run). The Stripe beacon and other third parties are ignored, as in
  `ContactFormTest`. Requests made during the step 1 "Continue" (the email check) are allowed. From
  step 2 to step 4 the count must be 0.
- **D10 without saving (SET-4).** The test never lets the invalid email leave the browser. Before
  editing it installs `page.route("**/*")` on all hosts, because the profile may be saved directly to
  a backend host. Every non-GET request whose `postData` contains "not-an-email" is recorded and
  `route.abort()`-ed. Every other request `route.resume()`s. The KNOWN BUG assertion is "0 recorded
  requests". Today the site sends the request, so the assertion fails as expected, but the abort means
  nothing is saved. A safety check follows with `org.testng.Assert`, so it cannot be masked by
  `expectedExceptions`: after `page.unrouteAll()` and a reload, "Email" still equals `TestUser.email`.
  If that ever fails, the account email changed and the cleanup will fail loudly (see above).
  Alternatives rejected: a real save followed by a revert (the account is lost if the revert fails);
  and `route.fulfill(200)` (the UI would show success and hide what the site really does).
- **Known bugs.** D6 (REG-10), D8 (DASH-10), D9 (SET-3, both scenarios) and D10 (SET-4) use
  `@Test(expectedExceptions = AssertionFailedError.class, expectedExceptionsMessageRegExp = "...",
  description = "... KNOWN BUG Dn ...")` with a 3 s assertion timeout. Preconditions (page open,
  Profile visible, first card present) use `org.testng.Assert`, which throws a plain `AssertionError`
  that does not count as the expected exception. This follows the review of change 2
  (`.agent-log/review-2026-09-29.md` #3). D9 opens Profile by clicking "Profile" on `/settings`, not
  through a deep link. The documented workaround (deep links) is used by the other SET tests.
- **Locators.** `role(...)` for buttons, links, headings and menu items. The dashboard tabs and settings
  sections are `AriaRole.BUTTON` (never `TAB`). Fields use `getByLabel(..., exact)`. "Password",
  "Current password", "New password" and "Confirm new password" must be exact, because every "Show password"
  button also matches. Job cards: the job title is read from the card, and the card is re-found with
  `getByRole(ARTICLE or LISTITEM).filter(hasText(title))`. The exact card role is decided in RED from
  the live DOM. Match score: `getByText(Pattern.compile("^\\d+ MATCH$"))`.
- **Healing.** Locators change only through `scripts/heal-loop.sh "<-Dtest=...>" <file> https://hirion.ch/dashboard main .auth/user.json`.
  That needs a live session. After a normal run, `.auth/user.json` belongs to a deleted user. The human
  chooses before task 3.2 (proposal Open question 8). Recommended: run the heal loop with the full
  journey selector `-Dsuite=testng-journey.xml -Dqa.mailbox=...`, so each iteration registers and
  deletes its own user. `FetchLiveDom` then runs in a short window before cleanup, or on a second user
  that a human registers and deletes by hand. The cleanup guard is never relaxed.

## Risks / Trade-offs

- [D8 depends on the feed content] The "?" titles come from specific job ads. If today's feed has none,
  the KNOWN BUG test does not throw and TestNG reports it as failed, which looks like "fixed".
  → Treat a D8 failure as "check the feed" and never as "fixed" until the defect is confirmed gone on a
  known broken ad. The regex (`?` next to a letter, space or `?`) can also flag a real question mark in
  a title. Both cases are reported to a human, not healed.
- [The feed is too short] DASH-4, DASH-5 and DASH-6 need three different cards. A new Free user may have
  fewer. → Precondition with `org.testng.Assert` ("feed has at least 3 jobs") so the result shows skip
  or fail with a clear message.
- [CV analysis is slow] Step 2 → step 3 and the first feed can take tens of seconds. → Explicit
  `setTimeout` on those assertions only (event-based, no fixed waits).
- [A real account is left behind] Crash, CI kill or a wrong password state. → `accountCreated` flag,
  password fallback, and deletion of leftover users before the next registration. Loud failure with the
  email when deletion is impossible.
- [Cleanup deletes the wrong account] → Guard `\+hirion-qa-\d+@` before any action. The file is
  human-owned and closed to the agent.
- [Rate limits / lockout on /login] → One wrong attempt per run (AUTH-6). Cleanup signs in at most twice.
- [Analytics is Pro-only] → Proposal Open question 6, decided 2026-09-30: DASH-7 checks the Pro gate a Free
  user sees and never clicks "Upgrade to Pro"; DASH-8 (Save rate) is out of this change. No test switches
  to the trial.
