## Context

The repo has `support/BaseTest` (Playwright lifecycle, `BASE_URL`, `role(AriaRole, name)` with exact
match, traces for failed tests) and `testng-public.xml`, which runs package `ch.hirion.pub` with
`parallel="classes"`. There is no test class in `ch.hirion.pub` yet. Requirements: see
`specs/home-page/spec.md`; open items: see proposal.md "Open questions".

## Goals / Non-Goals

**Goals:**
- One class `ch.hirion.pub.SmokeTest extends BaseTest`, one `@Test` per spec scenario (12 tests).
- Guest only: SmokeTest uses BaseTest's fresh context (no storageState), never `AuthenticatedTest` --
  `#pricing` is hidden for Pro users.
- Test method names carry the requirement ID, e.g. `pub3_monthlyPrice`, so heal-log and reports map
  back to the spec.

**Non-Goals:**
- Page objects or shared helpers beyond `BaseTest` -- one page, ten tests do not need them.
- Anything past the `/signup` URL (no form fill, no plan selection, no account).

## Decisions

- **Locators.** `role(AriaRole.BUTTON/LINK, "...")` for named controls; section scoping via
  `page.locator("#faq")` / `page.locator("#pricing")` with `// locator-exception: section anchor`.
  FAQ item via `faq.getByRole(BUTTON, name "Is Hirion free to use?", exact)`; its answer via
  `faq.getByText(Pattern "^Yes, start free with weekly job alerts\.")` (prefix, the rest of the answer
  is not asserted). Prices via exact `getByText` on single-string elements only: big price "CHF 10" and
  billed line "CHF 120 billed yearly" -- never "CHF 10/month", which spans two elements.
  Alternative rejected: CSS classes of the pricing cards (generated, forbidden).
- **Plan CTAs.** `role(AriaRole.LINK, "Start free")` / `role(AriaRole.LINK, "Start 7-day free trial")`
  scoped to `#pricing`; assert `hasURL` with `/signup` and `plan=free` / `plan=trial` after click. The
  test ends there -- nothing on `/signup` is filled or clicked.
- **Assertions.** Playwright `assertThat(...)` (auto-wait) only: `hasAttribute("aria-expanded","true")`,
  `isVisible()`, `hasURL(Pattern)`. No `waitForTimeout`, no `Thread.sleep`.
- **KNOWN BUG D4.** `pub4_selectedPeriodIsAriaPressed` asserts the correct `aria-pressed` values and is
  annotated `@Test(expectedExceptions = AssertionFailedError.class, description = "PUB-4 KNOWN BUG D4
  pricing toggle has no aria-pressed")`. With a short assertion timeout (`setTimeout(3000)`) so the
  expected failure does not cost 10 s. When D4 is fixed the test turns red and the annotation is removed.
- **Isolation.** Each test opens `/` in its own context (BaseTest `@BeforeMethod`), so the pricing period
  never leaks between tests.

## Risks / Trade-offs

- [Copy changes on the live site (CTA names, billed lines)] → tests fail RED with a clear locator
  timeout; a human updates spec + test together (not via heal-loop, since the spec text changes).
- [Big price "CHF 10" also appears elsewhere in `#pricing`] → exact `getByText` may match several
  elements; scope to the paid plan card if strict mode complains.
- [Live site A/B tests] → smoke is intentionally small; heal-loop handles pure locator drift.
- [`pnpm check` fails until `scripts/CheckLocatorRules.java` exists] → added in step 7 of the capstone
  plan; reported, not worked around.
