## Context

Evidence, all as a guest without an account (proposal.md "Why"):

- 2026-10-19 public CI run: `Tests run: 57, Failures: 6` (`pub2_faqShowsAnswer`, `pub3_pricingVisibleToGuest`,
  `pub3_yearlyIsDefault`, `pub3_monthlyPrice`, `pub3_quarterlyPrice`, `pub3_backToYearly`).
- 2026-10-19 browser check of `https://hirion.ch/#pricing` and `#faq` (JavaScript read of the DOM, only the
  period buttons and the FAQ item were clicked): texts and link targets in the proposal; every price text and
  plan name is a single exact-text element inside `#pricing`.
- 2026-10-19 guest run `mvn test -Dtest=RegistrationTest#reg7_*` (no `-Dqa.mailbox`): 5/5 failed; the trace and
  the failure screenshot show the new step 4.
- 2026-10-20 guest probe (temporary test, removed before any commit): `?plan=basic` and no parameter both give
  Free `aria-pressed=false`, Basic `false`, Premium `true`, buttons `[Back, Start my free trial]`; after a click
  on the Basic card: Basic `true`, buttons `[Back, Continue to payment]`; after "Monthly": `CHF 0, CHF 3, CHF 15`
  and `/month` twice. The submit button was never clicked.

## Goals / Non-Goals

**Goals:**
- PUB and REG tests describe the site as it is on 2026-10-19/20.
- The REG-9 guard can never let a paid plan or a trial through.

**Non-Goals:**
- No test clicks "Start my free trial", "Continue to payment" or "Choose Basic" beyond the URL check.
- No check of the "Save 72%" / "Save 33%" badges or of the plan feature lists (marketing text that changes often).
- No change to `support/**` except comments in `TestUser`, no change to `JourneyCleanup`.

## Decisions

1. **Selection through `aria-pressed`.** The plan cards now expose `aria-pressed` (candidate defect D12 is fixed),
   so `SELECTED_CARD` (CSS class `border-primary`) and `isSelected()` are replaced by
   `hasAttribute("aria-pressed", "true"/"false")`. Cards stay `planCard(label)`: a button holding the exact text
   "Free", "Basic" or "Premium" (exact, so "Join for free" and "7 days free" do not match).
2. **REG-9 guard.** Before the click: Free `aria-pressed="true"`, Basic and Premium `"false"` (TestNG
   `Assert` on `getAttribute`, so a missing card fails loudly), and the button "Join for free" is visible. Only
   then `user.save()` and the click on "Join for free". Alternative "click the button whatever its name" was
   rejected: Basic leads to "Continue to payment".
3. **D16 as a KNOWN BUG.** `reg7_planBasicPreselectsBasic` uses `expectedExceptions = AssertionFailedError.class`
   and "KNOWN BUG D16" in the description, a 3 s assertion timeout (`QUICK_ATTR`), and checks its precondition with
   TestNG: if `walkToStep` throws an `AssertionFailedError`, the test rethrows it as `Assert.fail(...)`
   (`java.lang.AssertionError`), so a broken wizard is never mistaken for the known bug.
4. **PUB-3 locators.** Exact texts inside `#pricing` (`text(pricing, "CHF 10")`); "/year" and "/month" appear
   twice, so the test checks `.first()` for visibility and `hasCount(2)` for the unit.
5. **Requirement names.** `@Story` labels follow the new names ("PUB-3 Pricing shows plans per billing period",
   "REG-7 Step 4 offers Free, Basic and Premium"); test descriptions keep the scenario names.
6. **Free Job Feed teaser (DASH-2).** Heading by role (`^We found (\d+) jobs? for you$`); the rows are
   `aria-hidden`, so they are found by text: the match percentages `^\d+%$` inside `<main>`, count = N from the
   heading. "Hidden and blurred" is read in the browser for the first percentage: its closest
   `[aria-hidden="true"]` ancestor exists and the row's text block has a CSS `filter` containing `blur`.
   Links by role and exact name, checked by `href` only; "Unlock with Basic" and "See all plans" are never
   clicked. No button "View job", "Save" or "Hide" inside `<main>`. All card helpers (`cards`, `feedCards`,
   `pickCard`, `usedTitles`) are deleted with the tests that used them.

## Risks / Trade-offs

- [The site changes plans again] → the PUB-3 / REG-7 tests fail on text, not silently.
- [The REG-9 guard is wrong] → it fails before `user.save()` and the click: no account, no payment.
- [D16 is intended behaviour] → open question for the human; the test is one line to flip.
- [Fewer dashboard checks] DASH-4/5/6/10/11 cannot run without a paid plan → recorded as removed with the reason;
  a paid-plan journey stays a separate decision (`docs/pro-journey-plan-draft.md`).

## Migration Plan

Spec commit (this change), then the test and docs commit, `pnpm check` (public), the REG-7 guest tests, one
journey run with a real test account (human's "да" locally or the human's CI run), then the human runs
`/opsx:archive` in the author window. Rollback: revert the test commit.

## Open Questions

- D16 (proposal.md).
