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
