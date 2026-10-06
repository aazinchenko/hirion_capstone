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

## Evidence for tasks

- 1.2 [x]: GitHub Actions journey run, 2026-10-06 16:32 UTC, unchanged journey code (last change `af1fb90`,
  2026-09-30). Run URL: https://github.com/aazinchenko/hirion_capstone/actions/runs/37496066639/job/112380813939 (pasted by the human).
  `reg10_showPasswordInTabOrder`, `set2_requiredProfileFields`, `set4_invalidEmailNotSent` -> "should have thrown
  an exception of type class org.opentest4j.AssertionFailedError" (reg10 confirmed by the human as the third
  failure); 37 other tests passed; JourneyCleanup: "deleted: ***+hirion-qa-... no longer signs in".
