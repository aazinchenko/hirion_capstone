## Context

See proposal.md - Why. The three KNOWN BUG tests are journey tests:

- `RegistrationTest.reg10_showPasswordInTabOrder` (D6) only opens `/signup` as a guest; it does not depend on
  `reg9` and runs locally without `-Dqa.mailbox` (the signup chain is skipped by `TestUser.generate()`).
- `SettingsTest.set2_requiredProfileFields` (D14) and `SettingsTest.set4_invalidEmailNotSent` (D10) depend on
  the group "registered", so they only run inside the full journey suite with a mailbox, which creates a real
  account. That is done only in GitHub Actions (`tests.yml`, job "journey", manual or nightly). Their failure
  messages are matched by `expectedExceptionsMessageRegExp` ("not marked required", "not-an-email"), and their
  preconditions throw TimeoutError or TestNG failures, so the "should have thrown" results of the CI run of
  2026-10-06 16:32 UTC mean the defects are gone, not that the page broke.
- SET-4 has a safety `finally` block (unroute, reload, the account email is unchanged) checked with TestNG; it is
  never masked by `expectedExceptions` and stays as it is.

## Goals / Non-Goals

**Goals:**
- D6, D10, D14 become regular checks that fail again on a regression.
- No local live run: nothing is run locally with `-Dqa.mailbox`; the only account is created by the human's CI run.

**Non-Goals:**
- No change to `support/**`, `JourneyCleanup` or the D1 KNOWN BUG tests.
- No new assertion for the SET-4 clause "the field Email shows an error": its text is not recorded (it would be
  ASSUMED), so the test keeps checking only that nothing is sent. See Open Questions.
- No heal-loop: the edits are targeted.

## Decisions

1. **Confirmed live before any edit.** The CI journey run of 2026-10-06 16:32 UTC ran the unchanged journey code
   (`af1fb90`) and all three tests failed with "should have thrown"; D6 was also seen in a local guest run. The
   site code (`settings-DbPLXgwa.js`) is supporting evidence only. Alternative "a new local live run" was
   rejected by the human: no local `-Dqa.mailbox` runs.
2. **Remove `expectedExceptions` and `expectedExceptionsMessageRegExp`, keep every assertion.** REG-10 keeps the
   `Assert.assertEquals(show.count(), 1, ...)` precondition and `not().hasAttribute("tabindex", "-1")`. SET-2
   keeps the loop over `REQUIRED_FIELDS` and its `AssertionFailedError("fields not marked required: ...")`
   (now a real failure). SET-4 keeps the catch-all route, the abort, the 3 s wait and the safety `finally`.
3. **Timeouts.** REG-10 drops `QUICK_ATTR` (default assertion timeout), as in the D2/D4/D5 change. SET-4 keeps
   its 3 s `waitForCondition`: there it is the observation window for "no request is sent", not a fast-failure
   shortcut.
4. **Comments and descriptions** lose "known defect Dn" / "KNOWN BUG Dn"; the SET-4 route comment keeps its
   pointer to the original design ("D10" names the analysis, not an open bug) reworded to "change
   add-user-journey design.md". The `INVALID_EMAIL` comment "(D10)" stays: it explains why the value can never
   reach a mailbox. Imports left unused are removed.
5. **Verification after the edits.** Locally: `mvn -q test-compile`, the REG-10 guest run and `pnpm check`
   (public only). The converted SET-2 / SET-4 are proven by ONE journey run in GitHub Actions that the human
   starts after the test commit and push; evidence is the run URL, "Tests run: 40, Failures: 0" and the
   JourneyCleanup "deleted" line. The archive waits for that green run. Alternative "wait for the nightly run"
   was not chosen: the manual run gives the evidence before the archive without waiting.

## Risks / Trade-offs

- [One real account in the CI run] → created and deleted by the existing journey job (`JourneyCleanup`, guard
  `\+hirion-qa-\d+@`); the job fails loudly if `.auth/test-user.json` is left.
- [The converted journey tests are unproven between the push and the CI run] → the archive is gated on the
  green run (task 4.1).
- [D10 is only partly fixed, e.g. the request is no longer sent but no error is shown] → this test cannot see it
  (non-goal above); it is recorded as an open question, not hidden.
- [The account email in run logs] → change-log.md quotes test lines only, never the generated email or the
  mailbox name (the CI log already masks it as `***`).
- [Flaky `BaseTest.closeContext` TargetClosedError] → known, human-owned; one rerun allowed for the public
  `pnpm check`; a journey rerun is a new account and the human's decision.

## Migration Plan

Spec commit (this change), then the test commit and push, then the human's CI journey run, then the human runs
`/opsx:archive`. Rollback: revert the test commit; the KNOWN BUG tests would then fail with "should have thrown"
again.

## Open Questions

- SET-4 also requires "the field Email shows an error". The exact error text is not recorded in
  `docs/intent.md`; adding an assertion for it is a separate change after a human reads it on the live site.
  This does not change the tasks here.
