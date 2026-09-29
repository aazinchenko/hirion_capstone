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
