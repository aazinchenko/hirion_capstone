## Why

On 2026-10-19 the public CI run failed 51/57: `SmokeTest.pub2_faqShowsAnswer` and all five `pub3_*` pricing
tests. A guest check of the live site the same day showed that hirion.ch changed its plans:

- `#pricing` has only the periods "Monthly" and "Yearly" ("Quarterly" is gone, "Yearly" is still the default),
  three plans Free / Basic / Premium, prices "CHF 0"; Basic "CHF 10" "/year" or "CHF 3" "/month"; Premium
  "CHF 120" "/year" or "CHF 15" "/month"; the "billed ..." lines are gone. The plan links are "Start free" ->
  `/signup?plan=free`, "Choose Basic" -> `/signup?plan=basic`, "Start 7-day free trial" -> `/signup?plan=trial`.
- The FAQ answer to "Is Hirion free to use?" is now "Get started for free and upgrade whenever you need
  automated job matching or personalized application support."

A guest run of the five REG-7 tests on 2026-10-19 (no `-Dqa.mailbox`, no account) failed 5/5: signup step 4
changed too. Seen in the trace, the failure screenshot and a guest probe on 2026-10-20 (not committed, never
clicked the submit button):

- Step 4 shows "Choose your plan", the periods "Monthly" / "Yearly" and the cards Free, Basic and Premium
  (Premium with "7 days free"). Each card now has `aria-pressed` ("true" on the selected card).
- The submit button is "Join for free" with Free selected, "Start my free trial" with Premium selected and
  "Continue to payment" with Basic selected. The button "See my matches" is gone.
- No `plan` parameter and `?plan=trial` select Premium; `?plan=free` selects Free; `?plan=basic` selects
  **Premium**, not Basic, although the home page link "Choose Basic" leads there.

The nightly journey would fail at REG-9 (its guard stops before the submit, so no account would be created).

## What Changes

- MODIFIED `PUB-2` (spec `home-page`): new FAQ answer text.
- REMOVED `PUB-3 Pricing shows prices per billing period` and ADDED `PUB-3 Pricing shows plans per billing
  period` (spec `home-page`): periods Monthly / Yearly, three plans, new prices. A MODIFIED block cannot drop
  the scenario "Quarterly price" (`openspec validate` refuses it), so the requirement is replaced.
- MODIFIED `PUB-5` (spec `home-page`): new scenario "Basic plan CTA" (`/signup?plan=basic`).
- RENAMED + MODIFIED `REG-7` (spec `signup`): "Step 4 offers Free, Basic and Premium"; selection through
  `aria-pressed`; submit button names per plan; new scenarios "Basic can be selected" and "plan=basic
  preselects Basic" (known defect D16).
- MODIFIED `REG-8` and `REG-9` (spec `signup`): the submit button is "Join for free".
- Tests: `SmokeTest` PUB-2 / PUB-3 / PUB-5, `RegistrationTest` REG-7 / REG-9 and the REG-9 guard (Free selected,
  Basic and Premium not, button "Join for free"), `TestUser` comments.
- Docs: D12 FIXED (cards expose `aria-pressed`), new KNOWN BUG D16; counts in README / current-state.

## Capabilities

### New Capabilities

(none)

### Modified Capabilities

- `home-page`: PUB-2 and PUB-5 modified, PUB-3 replaced (removed + added under a new name).
- `signup`: REG-7 (renamed), REG-8, REG-9.

## Impact

- Public suite stays 57 tests (pub3 Quarterly removed, pub5 Basic CTA added); journey 40 -> 42 (REG-7 "Basic can
  be selected" and the KNOWN BUG D16 test). Full suite 97 -> 99.
- Safety: REG-9 is still the only test that clicks a submit button on step 4, and only "Join for free" with
  Free selected. "Start my free trial" and "Continue to payment" are never clicked.
- Verification: public suite and the REG-7 guest tests locally (no account); one journey run with a real test
  account (local on the human's "да" or the human's CI run); the account is deleted by `JourneyCleanup`.

## Open Questions

- D16: is `?plan=basic` selecting Premium a defect (KNOWN BUG test) or intended? This proposal records it as a
  defect because the home page link "Choose Basic" leads to a step where Basic is not selected; a human may
  decide otherwise (then the scenario changes to "plan=basic selects Premium").
- D15 is left free: it was the "saved job shown for a moment after reload" candidate (2026-10-15), decided by the
  human outside this repository.
