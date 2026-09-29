# Change log -- add-public-coverage

## 1.1 Scenario tests written (2026-09-28)

- 32 `@Test` in `ch.hirion.pub`: AuthPagesTest 8, ContactFormTest 5, FooterLegalTest 7,
  LocalizationTest 7, MobileLayoutTest 2, QualityTest 3. KNOWN BUG annotations from the start:
  D1 x2, D2, D3, D5.
- Deviation from design: `support/**` is not editable (`.claude/settings.json`), so the shared
  `open()` helper lives in abstract `ch.hirion.pub.PublicPageTest`; SmokeTest unchanged
  (design.md "Page opening" updated).
- `java scripts/CheckLocatorRules.java src/test/java` -> `PASS: no forbidden patterns`.

## 1.2 RED run: `mvn -q test -Dsuite=testng-public.xml` (2026-09-28)

```
[ERROR] Tests run: 44, Failures: 3, Errors: 0, Skipped: 0, Time elapsed: 75.39 s <<< FAILURE! -- in TestSuite
[ERROR]   ContactFormTest.cont2_emptyFormBlocked:86->assertNoNonGetRequest:54 unexpected non-GET request, recorded: []
[ERROR]   ContactFormTest.cont2_invalidEmailBlocked:97->assertNoNonGetRequest:54 unexpected non-GET request, recorded: []
[ERROR]   ContactFormTest.cont4_validSubmitMocked:119->assertNoNonGetRequest:54 unexpected non-GET request, recorded: [POST https://hirion.ch/api/public/contact {"name":"QA Contact","email":"qa-contact@example.com","subject":"QA test, not delivered","message":"Automated test message. The network is mocked; nothing is sent.","website":""}]
```

- 41 of 44 pass, including the 12 SmokeTest tests and the 5 KNOWN BUG tests (D1 x2, D2, D3, D5 --
  expected exception raised).
- Cause of the 3 failures (from the non-GET entries in `target/traces/ContactFormTest-*.zip`):
  - all three: `POST https://m.stripe.com/6` (Stripe.js telemetry beacon) is caught by the catch-all
    `waitForRequest(!GET)` check; the message prints `[]` because the route callback had not yet run.
  - cont4: the form itself sent exactly one `POST https://hirion.ch/api/public/contact` with the JSON
    body above, answered locally with 200 by the mock. Nothing reached the real endpoint.
  - cont2: no request to `/api/public/contact` -- client validation blocks as specified.
- Answers proposal open question 3 (endpoint part): `POST /api/public/contact`, JSON body with
  name, email, subject, message, website. Confirmation text still unknown.
- Proposed for 2.x (needs a human decision, it narrows the CONT-2 / CONT-4 wording "non-GET request"):
  count only requests to `/api/public/contact`, keep the catch-all mock so nothing leaves the browser.

## Plan step 14: footer coverage completed (2026-09-28)

- Spec `footer-and-legal` extended in commit ba1a3e1 with the plan step 14 items that were missing:
  HTTP 200 scenario in FOOT-1 (plan FOOT-01), FOOT-4 footer anchors (plan FOOT-02), FOOT-5 LinkedIn
  link attributes (plan FOOT-03), FOOT-6 "Last updated:" on /privacy and /terms (plan FOOT-05),
  FOOT-7 copyright year (plan FOOT-07). Plan FOOT-04 and FOOT-06 were already covered by FOOT-2 and FOOT-3.
- `FooterLegalTest`: 6 new `@Test` methods (13 in total); foot4_anchorScrolls runs 5 times via
  DataProvider, so the class has 17 runs.
- `mvn -q test -Dtest=FooterLegalTest` (2026-09-28 20:42), from target/surefire-reports/testng-results.xml:
  Tests run: 17, Failures: 0, Errors: 0, Skipped: 0 -- both KNOWN BUG D1 tests pass (expected exception).
- `java scripts/CheckLocatorRules.java src/test/java` -> `PASS: no forbidden patterns`.
- tasks.md updated: 1.1 now 38 tests (FooterLegalTest 13), 3.1 now 54 tests. 1.2 left as is: it records
  the RED run with 32 tests.

## Plan step 15: localization coverage completed (2026-09-29)

- Checked in a browser as a guest (no login, nothing submitted):
  - after DE the header has the link "Loslegen" -> `/signup` (the plan calls it a button; it is `<a>`).
    A second "Loslegen" link sits in a page section, so the test scopes it to the header (`banner`).
  - `/contact` in DE: h1 "Sprich mit dem Team.".
  - FR h1 "Votre agent IA d'emploi pour la Suisse." (plain apostrophe U+0027); IT h1
    "Il tuo agente IA per il lavoro in Svizzera."; `hirion.lang` is "fr" / "it".
  - Observation for D2: `<html lang>` stays "en" after FR and IT as well, not only after DE.
    No extra KNOWN BUG tests: I18N-4 keeps the single DE scenario from the plan.
- Spec `localization` (commit 653a9d8): I18N-2 adds the header link "Loslegen", I18N-3 adds the German
  /contact heading, new I18N-5 (FR and IT headings; plan I18N-05, ASSUMED -> CONFIRMED).
- `LocalizationTest`: 9 `@Test` (+2: i18n5_switchToFrench, i18n5_switchToItalian); the reload scenario now
  uses a real `page.reload()` (new `PublicPageTest.reload()` with the same hydration wait as `open()`).
- `mvn -q test -Dtest=LocalizationTest`: Tests run: 9, Failures: 0, Errors: 0, Skipped: 0.
- `mvn -q test -Dsuite=testng-public.xml`: Tests run: 56, Failures: 1, Errors: 0, Skipped: 0. The one
  failure is ContactFormTest.cont4_validSubmitMocked (Stripe beacon, see 1.2). In the RED run all 3
  ContactFormTest checks failed, now only cont4: the failure depends on timing, the test is flaky until
  CONT-2 / CONT-4 are decided (plan step 16).
- `java scripts/CheckLocatorRules.java src/test/java` -> `PASS: no forbidden patterns`.
- tasks.md: 1.1 now 40 tests (LocalizationTest 9), 3.1 now 56 tests.

## Plan step 16: contact form completed (2026-09-29)

- Human decision (2026-09-29): CONT-2 / CONT-4 count only requests to `/api/public/contact`. Stripe.js on the
  page posts `https://m.stripe.com/6` at a random moment, which made the "no non-GET request" checks flaky
  (RED run: 3 failures; 2026-09-29 full run: 1). The catch-all `page.route` stays, so the beacon and every
  other non-GET request are still answered locally and nothing leaves the browser.
- Confirmation text found without a real submit: one run of cont4 with a temporary debug print (removed
  before commit) and the mocked 200 response `{"ok":true}`: the page shows the toast
  "Thanks! Your message is ready to send." and clears the fields. Proposal open question 3 is closed.
- Honeypot checked in the server HTML: the `website` input sits in a `div aria-hidden="true"` with
  `position:absolute; left:-9999px`.
- Spec `contact-form` (commit 404b0c2): CONT-2 names the endpoint and adds "stays on /contact" (plan CONT-03);
  CONT-3 adds aria-hidden and "not in the viewport" (plan CONT-04); CONT-4 requires `POST` and the toast
  (plan CONT-05, ASSUMED -> CONFIRMED). Plan CONT-02 uses "qa@", the spec keeps "not-an-email": same check
  (`validity.typeMismatch`).
- `ContactFormTest`: still 5 `@Test`; runs of `mvn -q test -Dtest=ContactFormTest`: 3 in a row,
  each Tests run: 5, Failures: 0, Errors: 0, Skipped: 0.
- `mvn -q test -Dsuite=testng-public.xml`: Tests run: 56, Failures: 0, Errors: 0, Skipped: 0 (exit 0).
- `java scripts/CheckLocatorRules.java src/test/java` -> `PASS: no forbidden patterns`.
- tasks.md: task 2.3 reworded for the form endpoint; counts unchanged (40 tests, 56 runs).

## Plan step 17: auth, signup step 1, 404, quality (2026-09-29)

- Plan table compared with `specs/access-and-quality` and AuthPagesTest / QualityTest / MobileLayoutTest:
  AUTH-01, 02, 04, 05, 06 (D5), QA-01, 02 (D3), 03 were covered; QA-04 only partly (status codes, no
  robots rules); plan class `SignupValidationTest` (REG-01, REG-02) was missing.
- Facts: `/robots.txt` has `Disallow: /dashboard` and `Disallow: /settings` (curl); on `/signup` a click
  on "Continue" with an empty form shows "Please enter your first and last name.", stays on
  "Step 1 of 4" and `/signup` (browser as a guest, nothing typed); the "Show password" button has
  `aria-label="Show password"`.
- Human decisions: AUTH-03 (wrong password) moved to change 3 -- needs the test user, and failed logins
  on the live site could lock an account; QA-06 (axe-core) not done -- optional, needs a pom.xml
  dependency; QA-05 (D4) stays in home-page / SmokeTest. Recorded in proposal.md "Not in this change".
- Spec `access-and-quality` (commit b2b0844): QA-4 adds the robots rules; new REG-1 (empty step 1) and
  REG-2 (Show password), kept in this capability so the change still ends with 5 specs.
- Tests: QualityTest checks the two Disallow lines; MobileLayoutTest now emulates a phone
  (`setIsMobile(true).setHasTouch(true)`, as in the plan) -- 2 runs, 2/2 each, D3 still fails as
  expected; new SignupValidationTest (2): types a made-up password, never submits.
- Runs: QualityTest 3/3, SignupValidationTest 2/2, MobileLayoutTest 2/2 twice, AuthPagesTest 8/8;
  `mvn -q test -Dsuite=testng-public.xml`: Tests run: 58, Failures: 0, Errors: 0, Skipped: 0 (exit 0).
- `java scripts/CheckLocatorRules.java src/test/java` -> `PASS: no forbidden patterns`.
- tasks.md: 1.1 now 42 tests (+ SignupValidationTest 2), 3.1 now 58 tests.

## Plan step 18: tasks 2.1-3.2 with evidence (2026-09-29)

- 2.1 strings: every string literal of the 7 new classes checked against the four specs by a script
  (comments stripped): 206 found verbatim; the other 83 are not site texts (test descriptions, JavaScript
  snippets, URL regexes such as `/privacy([?#].*)?$`, `^Last updated:` = "starting with", assertion
  messages, made-up input such as "QA Contact" / "Secret-123!", mock settings). Second script: 42 spec
  scenarios <-> 42 `@Test` descriptions "<ID> <scenario title>", none missing, none extra, no duplicates.
- 2.2 locators: nothing to heal -- the whole suite is green, heal-loop.sh was not needed in this change.
  The RED run of 1.2 failed on the contact-form check (Stripe beacon), not on locators; a human narrowed
  CONT-2 / CONT-4 in step 16. Task text updated: AGENTS.md also allows section anchors as
  `// locator-exception:` (SmokeTest `#faq` / `#pricing`, FooterLegalTest `#` + id).
- 2.3 network: ContactFormTest installs `page.route("**/*")` (line 36) before `open("/contact")`; GET is
  resumed (l.39), every other request is fulfilled locally (l.45); only `/api/public/contact` is counted
  (l.29, l.42); cont2 asserts 0 (l.96, l.106), cont4 exactly 1 POST with the email (l.132).
- 2.4 KNOWN BUG: D1 x2 (FooterLegalTest), D2 (LocalizationTest), D3 (MobileLayoutTest), D5 (AuthPagesTest)
  all keep `expectedExceptions = AssertionFailedError.class` and the full SHALL check; waiting assertions use
  3 s (`QUICK_ATTR`, `setTimeout(3000)`); the D1 mailto test waits for nothing (reads hrefs with
  `evaluate` and throws at once), so a timeout does not apply. `grep -rn D4 src/test/java/ch/hirion/pub`
  hits only SmokeTest.
- 2.5 `java scripts/CheckLocatorRules.java src/test/java` -> `PASS: no forbidden patterns` (exit 0).
- 3.1 `mvn test -Dsuite=testng-public.xml` -> `Tests run: 58, Failures: 0, Errors: 0, Skipped: 0`,
  BUILD SUCCESS.
- 3.2 `pnpm check` (exit 0):
  ```
  PASS: no forbidden patterns
  $ mvn -q test
  spec:check ok — specs: 1 · active changes: 1 · archived: 1
  ```

## Plan step 18: independent review and fixes (2026-09-29)

- `@agent-locator-reviewer` ran in the human's own Claude Code session on `d65811a^..HEAD`
  (output: `.agent-log/review-2026-09-29.md`): APPROVE, no AGENTS.md rule broken, KNOWN BUG checks not
  weakened; 3 medium and 5 minor remarks. The reviewer said it did not open the test files itself, so
  every remark was checked against the code before acting.
- Medium 1 (D3, MobileLayoutTest): `setHasNotText("EN")` is a case-insensitive substring, so a burger
  labelled "Menu" / "Open" would have been filtered out and D3 would stay "known" after a fix; also
  `setExpanded(false)` misses a button without `aria-expanded`. Fixed: any visible header button except
  the language switcher, matched exactly by `^\s*(EN|FR|DE|IT)\s*$`. Debug run at 375 px: header buttons
  [EN], menu candidates [] -> D3 still caught.
- Medium 2 (D1, FooterLegalTest): one valid mailto anywhere would have passed. Spec FOOT-3 changed
  (commit eddc3c8): at least one mailto in the main content and every one valid; test uses `allMatch`
  inside `main`.
- Medium 3 (all KNOWN BUG tests): preconditions used `assertThat(...)`, which throws the same
  `AssertionFailedError` as the bug check, so a broken page would look like the expected failure.
  Fixed: preconditions use `waitFor()` (throws `TimeoutError`) -- FooterLegalTest D1 x2, LocalizationTest
  switchTo (D2), AuthPagesTest D5, MobileLayoutTest D3 (header present). Negative control: preconditions
  broken on purpose in foot3_imprintHasNoFillTemplates, auth3_loginAutocomplete, i18n4_htmlLangAfterGerman
  -> all 3 FAILED ("Expected exception ... AssertionFailedError but got ... TimeoutError"); code restored.
  SmokeTest D4 already had no such precondition (it starts with a click).
- Minor, done: AUTH-4 "Sign in" link looked up inside `main` (spec updated in eddc3c8); the header may
  also carry a "Sign in" link.
- Minor, not done (reasons): splitting D5 into two tests -- the spec has one scenario, the failure
  message shows which field; marking `page.evaluate` CSS with `locator-exception` -- it reads data, it is
  not a locator; footer links from a fixed list -- that is what FOOT-1 specifies; `Year.now()` in the
  first days of January -- rare, accepted as a known limitation.
- After the fixes: `CheckLocatorRules` PASS; `mvn test -Dsuite=testng-public.xml` -> Tests run: 58,
  Failures: 0, Errors: 0, Skipped: 0; `pnpm check` exit 0 (spec:check ok -- specs: 1 · active changes: 1
  · archived: 1).
