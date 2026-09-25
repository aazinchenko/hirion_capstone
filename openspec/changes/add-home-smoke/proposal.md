## Why

hirion.ch has no browser tests in CI. The home page is the first thing every visitor sees and the entry
point to signup, so group PUB (docs/intent.md) is change 1: a fast smoke suite that proves the hero CTA,
the FAQ accordion, the pricing periods and the plan CTAs work, and pins known defect D4.

## What Changes

- New capability spec `home-page` covering group PUB: hero CTA, FAQ accordion, pricing period toggle
  (Monthly / Quarterly / Yearly, default Yearly) and plan CTA links.
- New TestNG class `src/test/java/ch/hirion/pub/SmokeTest.java`, one test per spec scenario, run by the
  existing `testng-public.xml` suite (package `ch.hirion.pub`). Every test runs as a guest with no
  stored session (`#pricing` is hidden for Pro users).
- D4 (pricing toggle has no `aria-pressed`) gets a KNOWN BUG test; the spec keeps the correct SHALL.
- No signup is submitted, no Premium Trial is started, no payment is touched: CTA scenarios stop at the
  URL of the page the link opens.

## Capabilities

### New Capabilities
- `home-page`: public home page of hirion.ch (group PUB) -- hero CTA, FAQ accordion, pricing periods and
  prices, plan CTA links.

### Modified Capabilities
- (none -- `openspec/specs/` is empty)

## Impact

- Adds `src/test/java/ch/hirion/pub/SmokeTest.java` (extends `support/BaseTest`). No production code.
- Runs against `https://hirion.ch` (or `-DbaseUrl`); read-only for the site.
- `pnpm check` afterwards: locator rules, `mvn -q test`, `spec:check`.

## Confirmed on the live site (human, 2026-09-25)

- Hero primary CTA: link "Get my personalized jobs" -> `/signup` for a guest.
- `#faq` and `#pricing` exist; period buttons are exactly "Monthly", "Quarterly", "Yearly".
  `#pricing` is rendered only for guests and Free users (hidden for Pro), so all PUB tests run as a
  guest with no stored session.
- FAQ "Is Hirion free to use?" expands to an answer starting "Yes, start free with weekly job alerts."
- Plan CTAs: link "Start free" -> `/signup?plan=free`; link "Start 7-day free trial" ->
  `/signup?plan=trial`. Tests check only the href or the URL after click, never continue signup.
- Big price and period are separate elements ("CHF 10" + "/month"); billed lines are single strings:
  "CHF 120 billed yearly", "CHF 39 billed quarterly", "CHF 15 billed monthly".

## Open questions

- (none -- `scripts/CheckLocatorRules.java` is planned in step 7 of the capstone plan; task 2.4 waits
  for it)
