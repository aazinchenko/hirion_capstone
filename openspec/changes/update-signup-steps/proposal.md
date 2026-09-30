## Why

The `signup` spec says how step 4 behaves without a `plan` parameter, but not what the plan items REG-09
("Back keeps the wizard data") and REG-11 ("?plan=trial preselects Premium Trial") expect. Change 3 left
this as Open question 9, answered then only from the site code. A temporary guest probe confirmed it live
on 2026-09-30 (docs/intent.md) and also showed that one code-based guess was wrong (`?plan=bogus` selects
Premium Trial, not "no card"). Honest SDD refines the spec to the confirmed reality in its own change, so
the history shows what was clarified and when.

## What Changes

- MODIFIED requirement `REG-7 Step 4 offers Premium Trial and Free` (spec `signup`): the full block is
  carried over with its two existing scenarios unchanged, and it now also states that
  - `/signup?plan=free` preselects Free and `/signup?plan=trial` preselects Premium Trial;
  - "Back" on step 4 returns to step 3 with the chosen role kept, and "Continue" returns to step 4 with
    the chosen plan still selected.
- Three new scenarios in REG-7: `?plan=free`, `?plan=trial`, Back keeps the wizard data.
- Three new guest tests in `RegistrationTest`. None clicks "See my matches", so no account is created
  (REG-8); the tests run without `-Dqa.mailbox`.
- Not in this change (human decision 2026-09-30, variant A): an unknown `plan` value (confirmed live:
  Premium Trial), "Back" to step 2 (file name kept, confirmed live), "Back" on step 1, the invalid step 1
  email D11 / REG-1 (`access-and-quality`), and the D9 hypothesis (Open question 11 of change 3).

## Capabilities

### New Capabilities

(none)

### Modified Capabilities

- `signup`: requirement `REG-7 Step 4 offers Premium Trial and Free` -- adds the `plan` parameter
  preselection and "Back" keeping the wizard data, with three scenarios.

## Impact

- `openspec/specs/signup/spec.md` after archive: REG-7 has 5 scenarios (2 old + 3 new); `pnpm spec:check`
  goes from `specs: 9 · active changes: 1 · archived: 3` to `specs: 9 · active changes: 0 · archived: 4`.
- `src/test/java/ch/hirion/journey/RegistrationTest.java`: three guest tests; the existing wizard helpers
  (`walkToStep`, `planCard`, `addRole`) are reused. No change to `support/**`, `JourneyCleanup` or the
  suites (the tests join the existing `testng-journey.xml` class).
- No new account, no payment, no Premium Trial: step 4 is only inspected; the Free-only rule of
  `docs/intent.md` "Not doing" stays untouched.
- Budget from `docs/intent.md`: <= 20 000 output tokens / <= $1.
