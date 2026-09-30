## Context

`RegistrationTest` (change 3) already walks the wizard as a guest: `walkToStep(step, path)` fills step 1
with made-up values (`journey-guest+hirion-qa-<millis>@example.com`, never a real mailbox), uploads the
CV fixture, adds the role "QA / Test Engineer" and stops on step 4. `planCard(label)` finds a plan card,
and "selected" is the class token `border-primary` (no aria state, defect D12). Only reg9 clicks
"See my matches". Facts for this change: docs/intent.md "CONFIRMED live 2026-09-30" (guest probe).

## Goals / Non-Goals

**Goals:**
- One guest test per new REG-7 scenario, reusing the existing wizard helpers.
- Run them without `-Dqa.mailbox`, so the RED and GREEN runs create no account.

**Non-Goals:**
- No change to reg7_stepFourPremiumTrialSelected / reg7_freeCanBeSelected (the two old scenarios keep
  their tests).
- No new helper in `support/**` (human-owned) and no change to `JourneyCleanup`.

## Decisions

- **Test names and order.** `reg7_planFreePreselectsFree`, `reg7_planTrialPreselectsTrial`,
  `reg7_backKeepsWizardData`, with priorities below reg9 (10), so they run as guests before the account
  exists in a journey run. Alternative rejected: a new class -- it would duplicate the private wizard
  helpers.
- **Selected state.** Reuse `SELECTED_CARD` / `planCard` exactly like the existing REG-7 tests; both
  cards are checked (selected and not selected), so a page that marks both or none fails.
- **Back check.** After "Back": `stepLabel(3)` and the role chip inside `<main>`
  (`getByRole(MAIN).getByText("QA / Test Engineer", exact)`, the option list is portaled outside
  `<main>`). The picker then reads "1 selected" instead of "Add"; the test does not click the picker
  again, so `addRole()` is not reused after "Back". Then "Continue" → `stepLabel(4)` and Free selected.
- **Guard.** No new test clicks "See my matches"; the guest email pattern is the one of the existing
  guest tests.
- **Runs.** RED/GREEN with `mvn -q test -Dtest=RegistrationTest` (no mailbox: reg9, reg4, auth6 skip).
  The plan's full journey run (`-Dqa.mailbox`, one real account) happens only after a human "да".

## Risks / Trade-offs

- [The visual selected state changes in a redesign] → The same risk as the existing REG-7 tests
  (review 2026-09-30 #4); switch to aria state once D12 is fixed.
- [Four wizard walks add ~1 min per run] → Accepted; each test stays independent.
