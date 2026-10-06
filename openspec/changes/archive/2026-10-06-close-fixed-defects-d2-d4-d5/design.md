## Context

See proposal.md - Why. The three tests are KNOWN BUG tests: `@Test(expectedExceptions = AssertionFailedError.class)`
around a correct assertion with a 3 s timeout (`QUICK_ATTR` in `support/BaseTest`, a local `quick` in
`SmokeTest.pub4`), so the expected failure is fast. Their preconditions (`switchTo` waiting for the button "DE",
`field(...).waitFor()` on `/login`) throw TimeoutError, never AssertionFailedError, so the 2026-10-06 "should have
thrown" result really means the attribute is now correct, not that the page was broken. The evidence is a run
on the live site, so the fix is a seen-live fact, not an ASSUMED one.

## Goals / Non-Goals

**Goals:**
- The three scenarios become regular, green checks that would fail again on a regression of D2, D4 or D5.
- The specs and docs stop listing D2, D4, D5 as open defects and keep their history.

**Non-Goals:**
- No change to any other KNOWN BUG test (D1, D6, D10, D14) and no change to journey tests.
- No new scenarios: the SHALL text was already correct and stays word for word.

## Decisions

1. **MODIFIED, not REMOVED, requirements.** The behaviour is still wanted; only the "(Known defect Dn.)" note is
   wrong now. Alternative "edit the main specs directly" was rejected: every spec change goes through a change
   (honest SDD, same as D3 / D7).
2. **Remove `expectedExceptions`, keep every assertion.** Same locators, attributes and values (`lang="de"`,
   `aria-pressed` true / false, `autocomplete` email / current-password). This is the opposite of "healing by
   weakening": the test gets stricter (it must pass). The preconditions stay.
3. **Drop the 3 s timeout in these three tests** (`QUICK_ATTR`, local `quick`) and use the default assertion
   timeout. The short timeout only made the expected failure fast; for a check that must pass it adds flake
   risk (e.g. `lang` is set after the language switch renders). `QUICK_ATTR` stays in `BaseTest` for the other
   users (`RegistrationTest`). Alternative "keep 3 s" was rejected for that reason; if the reviewer prefers it,
   the change is one argument per assertion.
4. **Comments and descriptions.** Section comments become `// I18N-4`, `// PUB-4`, `// AUTH-3`; descriptions lose
   "-- KNOWN BUG Dn: ..."; the `switchTo` Javadoc / comment in `LocalizationTest` no longer mentions bug D2 (the
   TimeoutError reasoning stays true for every caller). Imports that become unused (`AssertionFailedError`,
   `LocatorAssertions` if nothing else needs them) are removed.
5. **Docs keep the history.** `docs/intent.md` keeps lines D2, D4, D5 and adds "FIXED (seen live 2026-10-06):
   regular test now", like the D3 / D7 notes; `docs/decisions.md` moves them from "KNOWN BUG tests" to a "Fixed by
   the site" line.

## Risks / Trade-offs

- [The site reverts a fix] → that is exactly what the regular test should catch; it fails red with the real
  attribute value in the message.
- [One green run is luck, e.g. a cached build] → task 1 reruns the three tests before any edit and quotes the
  output; after the edit the full public suite runs in `pnpm check`.
- [Default timeout hides a slow attribute update] → acceptable: the spec asks for the end state, not a speed.

## Migration Plan

Spec commit (this change) first, then the test commit, then the human runs `/opsx:archive`. Rollback: revert the
test commit; the KNOWN BUG tests would then fail again with "should have thrown", which is the signal to redo
this change, not to restore them.
