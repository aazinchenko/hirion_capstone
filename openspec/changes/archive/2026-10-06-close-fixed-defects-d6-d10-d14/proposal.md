## Why

After D2, D4, D5 (change `close-fixed-defects-d2-d4-d5`) the site has also fixed the remaining journey
defects D6, D10 and D14, seen live:

- GitHub Actions journey run, 2026-10-06 16:32 UTC, on the unchanged journey code (journey tests last changed in
  `af1fb90`, 2026-09-30): `reg10_showPasswordInTabOrder` (D6), `set2_requiredProfileFields` (D14) and
  `set4_invalidEmailNotSent` (D10) failed with "should have thrown an exception of type class
  org.opentest4j.AssertionFailedError"; the other 37 tests passed; JourneyCleanup deleted the account.
- D6 also on 2026-10-06 in a local guest run of `RegistrationTest#reg10_showPasswordInTabOrder` (no account).
- Supporting, from the public site code `assets/settings-DbPLXgwa.js`: First name, Last name and Email have
  `required` and `aria-required="true"`; the save handler returns before the request when the email is invalid.

A KNOWN BUG test that no longer fails turns the suite red and hides regressions.

## What Changes

- `RegistrationTest.reg10_showPasswordInTabOrder` (D6), `SettingsTest.set2_requiredProfileFields` (D14) and
  `SettingsTest.set4_invalidEmailNotSent` (D10) lose `expectedExceptions` / `expectedExceptionsMessageRegExp`
  and "KNOWN BUG Dn" in the description; their assertions and preconditions stay.
- MODIFIED `SET-2 Profile form` (spec `account-settings`): the scenario "Required profile fields" loses the
  note "(Known defect D14: only a visual "*" in the label today.)".
- REG-10 (spec `signup`) and SET-4 (spec `account-settings`) carry no defect note in the spec: no delta.
- `docs/intent.md`, `docs/decisions.md`, `docs/current-state.md`, `README.md`: D6, D10, D14 marked
  "FIXED (seen live 2026-10-06)"; KNOWN BUG lists and counts updated.

## Capabilities

### New Capabilities

(none)

### Modified Capabilities

- `account-settings`: requirement SET-2, scenario "Required profile fields" is no longer a known defect (D14).

## Impact

- Code: three test methods in `src/test/java/ch/hirion/journey/` (`RegistrationTest`, `SettingsTest`); no
  `support/**`, no `JourneyCleanup`.
- No local live run and nothing with `-Dqa.mailbox`. After the test commit and push the human starts the journey
  once in GitHub Actions (Run workflow): one real account, deleted by `JourneyCleanup`. Archive only after that
  run is green.
- Test counts unchanged: journey 40, full suite 97. KNOWN BUG tests left: D1 x2 only.
- Spec counts unchanged (59 requirements / 93 scenarios); `pnpm spec:check` after archive: archived 7.
