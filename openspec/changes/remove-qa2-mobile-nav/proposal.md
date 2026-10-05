## Why

QA-2 requires that on a 375 px phone the header lets a visitor reach "Sign in" (a visible link or a burger
menu). The site hides the header links and "Sign in" and has no burger menu; this was recorded as known
defect D3 and covered by a KNOWN BUG test. On 2026-10-05 the human decided that D3 is NOT a defect: the
mobile header without "Sign in" is the intended design. Honest SDD removes the requirement in its own
change instead of keeping a SHALL the product owner does not want, the same way D7 was closed.

## What Changes

- REMOVED requirement `QA-2 Navigation is reachable on a phone` (spec `access-and-quality`) with its
  scenario "Sign in reachable at 375 px".
- `MobileLayoutTest.qa2_signInReachableOnPhone` (KNOWN BUG D3) is deleted; QA-1 stays unchanged.
- `docs/intent.md`: D3 is marked "NOT A DEFECT (human decision 2026-10-05)", like D7.
- `docs/decisions.md`: D3 moves from "KNOWN BUG tests" to "Not a defect".

## Capabilities

### New Capabilities

(none)

### Modified Capabilities

- `access-and-quality`: requirement `QA-2 Navigation is reachable on a phone` is removed.

## Impact

- Public suite: 58 -> 57 tests; full suite `testng-all.xml`: 98 -> 97.
- KNOWN BUG tests: D1 x2, D2, D4, D5, D6, D10, D14 (8 instead of 9).
- No live account, no journey run needed: only the public suite and `pnpm check`.
- `pnpm spec:check` after archive: `specs: 9 · active changes: 0 · archived: 5`.
