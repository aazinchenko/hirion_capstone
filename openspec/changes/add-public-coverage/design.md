## Context

`ch.hirion.pub.SmokeTest` (change 1) established the patterns: `BaseTest` opens a fresh guest context per
test, pages are opened with `DOMCONTENTLOADED` plus a hydration wait (`!('$_TSR' in window)`, 30 s
navigation timeout), and known defects use `expectedExceptions = AssertionFailedError.class` with a 3 s
assertion timeout. `scripts/CheckLocatorRules.java` rejects `nth-child`, hardcoded waits, generated CSS
classes and any `.locator(` line without `// locator-exception:`. Requirements: the four specs of this
change; motivation and open items: proposal.md.

## Goals / Non-Goals

**Goals:**
- One test class per spec area in `ch.hirion.pub`, one `@Test` per scenario (32 tests), method names
  prefixed with the requirement ID (`foot3_imprintHasNoFillTemplates`).
- Everything runs as a guest; no test signs in, submits a form to the real backend or sends an email.

**Non-Goals:**
- French / Italian translations (only EN default and DE are checked; FR / IT only appear in the menu).
- Submitting `/login` or `/forgot-password`, any signup step, any authenticated page.
- Asserting the contact-form confirmation text (proposal open question 3).

## Decisions

- **Classes.** `FooterLegalTest` (FOOT, 7), `LocalizationTest` (I18N, 7), `ContactFormTest` (CONT, 5),
  `AuthPagesTest` (AUTH, 8), `QualityTest` (QA-3, QA-4, 3), `MobileLayoutTest` (QA-1, QA-2, 2).
  Mobile tests get their own class because the viewport is a context option: `MobileLayoutTest`
  overrides `contextOptions()` with `setViewportSize(375, 812)`. Alternative rejected: one class per
  capability with `page.setViewportSize` in the test -- mixing viewports inside a class hides which
  tests run on mobile.
- **Page opening.** Move SmokeTest's navigate + hydration wait into a protected `BaseTest.open(String path)`
  that returns the `Response`; SmokeTest calls it unchanged in behavior. The 404 scenario reads
  `response.status()` from it. Alternative rejected: copying the wait into six classes.
- **Locators.** `role(...)` for links, buttons, headings (`AriaRole.HEADING` + `setLevel(1)` where the
  heading is asserted). Footer via `page.getByRole(AriaRole.CONTENTINFO)`. Form fields via
  `page.getByLabel("...", exact)` -- on `/login` "Password" must be exact because the "Show password"
  button also matches. Language menu via `getByRole(MENU)` / `getByRole(MENUITEM, "DE", exact)`.
  Locator exceptions, each with `// locator-exception:`: `page.locator("html")` for `lang`,
  `page.locator("input[name='website']")` for the honeypot.
- **Attributes and metadata.** `assertThat(loc).hasAttribute(...)` for `href`, `required`, `tabindex`,
  `autocomplete`, `lang`; `assertThat(page).hasTitle(...)`. Meta description and canonical are read with
  `page.evaluate` of `document.querySelector(...)` (not a locator, so no exception comment), then
  compared with `assertEquals`. Field validity via `locator.evaluate("el => el.validity.valueMissing")`.
  `robots.txt` / `sitemap.xml` via `page.request().get(...)` (APIRequestContext, no page load).
- **Imprint (D1).** "No [FILL:" = `assertThat(page.getByText("[FILL:")).hasCount(0)` (substring match).
  The mailto scenario collects all `a[href^=mailto:]` hrefs via `page.evaluate` and asserts one matches
  the spec regex.
- **Localization.** `localStorage["hirion.lang"]` read with `page.evaluate`. The German heading level 1
  is asserted exactly: `role(AriaRole.HEADING, "Dein KI-Job-Agent für die Schweiz.")` (confirmed in a
  browser 2026-09-28; the `<br>` between "Agent" and "für" is whitespace in the accessible name, as in
  the English h1). Every test starts from a fresh context, so the stored language never leaks.
- **Contact form network (CONT).** Before `open("/contact")` the test installs
  `page.route("**/*", ...)`: GET requests `route.resume()`, every other method is recorded and answered
  `route.fulfill(status 200, body "{}")` -- nothing non-GET can leave the browser, whatever endpoint
  the form uses (open question 3). CONT-2 asserts the recorded list is empty after the click (checked
  after `assertThat(field)` has settled the validity state); CONT-4 waits with
  `page.waitForRequest(r -> !"GET".equals(r.method()), click)` and then asserts exactly one recorded
  request whose `postData()` contains the email. Test data are fixed fake values
  (`qa-contact@example.com`), not the TestUser -- no account is involved and nothing is delivered.
  Alternative rejected: routing only a guessed endpoint -- a wrong guess would send a real message.
- **Console errors (QA-3).** One test, a loop over the six paths; listeners `page.onConsoleMessage`
  (type `error`) and `page.onPageError` collect into a list; the assertion prints the path and message.
- **Known bugs.** D1 (2 tests), D2 (`i18n4_htmlLangAfterGerman`), D3 (`qa2_signInReachableOnPhone`),
  D5 (`auth3_loginAutocomplete`): `@Test(expectedExceptions = AssertionFailedError.class,
  description = "<ID> ... KNOWN BUG Dn ...")`, assertions with `setTimeout(3000)`. D3's test looks for
  the link "Sign in"; if not visible, it clicks a visible header button with `aria-expanded` other
  than the language button, then asserts the link again.

## Risks / Trade-offs

- [Strings OBSERVED only in server HTML, not confirmed by a human] → first RED run shows every
  mismatch; a human confirms or corrects spec + test together (heal-loop only for pure locator drift).
- [Catch-all route slows page loads or breaks analytics beacons (POST)] → beacons get a local 200,
  which is harmless; CONT-4 counts only requests fired after the click.
- [Contact form submits via a GET or a third-party form service] → CONT-4 fails RED with "0 requests";
  a human decides the matching rule before GREEN (proposal open question 3).
- [QA-1 / QA-3 fail on the live site] → not "healed" by weakening; reported, a human decides whether it
  becomes a new known defect.
- [A burger menu fix for D3 names its button differently] → the test then fails to find the menu and
  stays KNOWN BUG; when D3 is fixed a human updates the locator and removes the annotation.
- [Suite time: 32 more live-site tests] → `testng-public.xml` runs classes in parallel (4 threads).
