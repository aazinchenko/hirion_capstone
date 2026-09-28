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
