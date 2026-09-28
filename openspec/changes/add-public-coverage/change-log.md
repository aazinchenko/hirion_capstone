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
