# Change log -- close-fixed-defects-d6-d10-d14

## 2026-10-06: D6, D10, D14 fixed by the site

- Propose: local guest run `mvn test -Dtest="RegistrationTest#reg10_showPasswordInTabOrder"` (no `-Dqa.mailbox`) ->
  `Method RegistrationTest.reg10_showPasswordInTabOrder()[pri:9, ...] should have thrown an exception of type class
  org.opentest4j.AssertionFailedError`, `Tests run: 1, Failures: 1`.
- Update (human): task 1.2 is covered by the GitHub Actions journey run; no local live run, nothing with
  `-Dqa.mailbox`; the live verification after the edits is a human-started CI run (task 4.1), archive after it.
- Site code, supporting only (`https://hirion.ch/assets/settings-DbPLXgwa.js`, fetched 2026-10-06):
  `id:"firstName",required:!0,"aria-required":"true"`, `id:"lastName",required:!0,"aria-required":"true"`,
  `id:"email",type:"email",autoComplete:"email",required:!0,"aria-required":"true"`.
- D10, site code, supporting only (same file; seen by the reviewer 2026-10-06, matched in the fetched copy), the
  Profile save handler, minified:
  `const s=y.trim()?null:"First name is required",v=x.trim()?null:"Last name is required",M=b.trim(),R=!M||!sa.test(M)?a("auth_validation_email"):null;if(m(s),D(v),Pe(R),s||v||R){u.error(a("toast_fill_required"));return}z(!0);try{await Be();...`
  An email that fails the regex `sa` sets the field error (`Pe(R)`), shows the toast "toast_fill_required" and
  returns before the save request (`await Be()`). The primary evidence for D10 stays the CI run (task 1.2).

## Evidence for tasks

- 1.1 [x]: `mvn test -Dtest="RegistrationTest#reg10_showPasswordInTabOrder"` (no `-Dqa.mailbox`, unchanged code) ->
  `Method RegistrationTest.reg10_showPasswordInTabOrder()[pri:9, ...] should have thrown an exception of type class
  org.opentest4j.AssertionFailedError`; `Tests run: 1, Failures: 1, Errors: 0, Skipped: 0`.
- 1.2 [x]: GitHub Actions journey run, 2026-10-06 16:32 UTC, unchanged journey code (last change `af1fb90`,
  2026-09-30). Run URL: https://github.com/aazinchenko/hirion_capstone/actions/runs/37496066639/job/112380813939 (pasted by the human).
  `reg10_showPasswordInTabOrder`, `set2_requiredProfileFields`, `set4_invalidEmailNotSent` -> "should have thrown
  an exception of type class org.opentest4j.AssertionFailedError" (reg10 confirmed by the human as the third
  failure); 37 other tests passed; JourneyCleanup: "deleted: ***+hirion-qa-... no longer signs in".
- 2.1 [x]: `grep -n "D6\|KNOWN BUG" src/test/java/ch/hirion/journey/RegistrationTest.java` -> no output (exit 1);
  unused import `AssertionFailedError` removed; `Assert.assertEquals` precondition and `tabindex` assertion kept.
- 2.2 [x]: `grep -n "KNOWN BUG D14" src/test/java/ch/hirion/journey/SettingsTest.java` -> no output (exit 1);
  `REQUIRED_FIELDS` loop and its `AssertionFailedError` kept (import stays, still used).
- 2.4 [x]: `mvn test -Dtest="RegistrationTest#reg10_showPasswordInTabOrder"` (no `-Dqa.mailbox`) ->
  `Tests run: 1, Failures: 0, Errors: 0, Skipped: 0` / `BUILD SUCCESS`; `mvn -q test-compile` exit 0.
- 3.1 [x]: `grep -n "D6\|D10\|D14" docs/intent.md docs/decisions.md docs/current-state.md` -> `intent.md:102`, `:117`,
  `:132` (D6, D10, D14, each followed by "FIXED (seen live 2026-10-06): regular test now (REG-10 / SET-4 / SET-2,
  change close-fixed-defects-d6-d10-d14)"); `intent.md:141` ("defects fixed by the site (D2, D4-D6, D10, D14) are
  regular tests", replaces "known defects D1-D10 are KNOWN BUG tests"); `decisions.md:48` ("KNOWN BUG tests: D1
  ..."), `:50` ("... D6, D10, D14 (seen live 2026-10-06 in a GitHub Actions journey run) ..."); `current-state.md:44`
  ("Defects: D1 KNOWN BUG tests; D2, D4, D5, D6, D10, D14 FIXED by the site ..."), `:47` ("D14 FIXED ...").
  `grep -n "KNOWN BUG" README.md` -> `11: ... 6 of them (D2, D4, D5, D6, D10, D14) fixed by the site, now regular tests`.
- 2.3 [x]: check narrowed by the human (2026-10-06): the planned `grep -n "KNOWN BUG\|known defect"` also matched
  three lines outside D10 (`:142` SET-3 D9 history, `:319` / `:324` `waitForProfile` comments). Human decision
  (option 2): narrow the check to D10, reword the `waitForProfile` comments without the KNOWN BUG reason (code
  unchanged), keep the D9 history comment.
  `grep -n "KNOWN BUG D10\|known defect D10" src/test/java/ch/hirion/journey/SettingsTest.java` -> no output (exit 1);
  the broad grep now matches only `142: // SET-3 (known defect D9: ...`; `mvn -q test-compile` exit 0;
  `pnpm check:locators` -> "PASS: no forbidden patterns".
- 4.2 [x] (run before the test commit and push, ahead of 4.1): `pnpm exec openspec validate close-fixed-defects-d6-d10-d14 --strict`
  -> "Change 'close-fixed-defects-d6-d10-d14' is valid"; `pnpm check` exit 0 -- "PASS: no forbidden patterns";
  public suite `TEST-TestSuite.xml tests="57" errors="0" skipped="0" failures="0"`;
  "spec:check ok — specs: 9 · active changes: 1 · archived: 6".
- 4.1 [x]: GitHub Actions run https://github.com/aazinchenko/hirion_capstone/actions/runs/37501768600
  (workflow_dispatch, commit `c855540`, 2026-10-06 17:12-17:16 UTC; reported by the human), both jobs success.
  Journey job: `[INFO] Tests run: 40, Failures: 0, Errors: 0, Skipped: 0` / `BUILD SUCCESS`;
  `[JourneyCleanup] deleted: ***+hirion-qa-... no longer signs in`; step "Check that the test account was deleted"
  success. The converted REG-10, SET-2, SET-4 pass as regular tests on the live site.
