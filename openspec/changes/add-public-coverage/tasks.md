## 1. Scenario tests (RED)

- [x] 1.1 Write one `@Test` per spec scenario (32 tests) in `src/test/java/ch/hirion/pub/`, all extending `BaseTest`, guest context only; add `BaseTest.open(String path)` (navigate DOMCONTENTLOADED + hydration wait, returns `Response`) and switch `SmokeTest.openHome` to it. Known-bug tests carry `@Test(expectedExceptions = AssertionFailedError.class, description = "<ID> ... KNOWN BUG Dn ...")` from the start. Classes and methods:
  - `FooterLegalTest` (7): foot1_footerLinkTargets, foot1_footerOpensPrivacy, foot2_privacyPage, foot2_termsPage, foot2_imprintPage, foot3_imprintHasNoFillTemplates (KNOWN BUG D1), foot3_imprintEmailIsValidMailto (KNOWN BUG D1)
  - `LocalizationTest` (7): i18n1_defaultIsEnglish, i18n1_menuListsFourLanguages, i18n2_switchToGerman, i18n3_germanSurvivesReload, i18n3_germanOnAnotherPage, i18n4_defaultHtmlLang, i18n4_htmlLangAfterGerman (KNOWN BUG D2)
  - `ContactFormTest` (5): cont1_formRendered, cont2_emptyFormBlocked, cont2_invalidEmailBlocked, cont3_honeypotAttributes, cont4_validSubmitMocked -- catch-all `page.route` installed before `open("/contact")` in every test of this class
  - `AuthPagesTest` (8): auth1_guestDashboardRedirects, auth1_guestSettingsRedirects, auth2_loginPageRendered, auth2_forgotPasswordLink, auth2_createAccountLink, auth3_loginAutocomplete (KNOWN BUG D5), auth4_forgotPasswordPage, auth5_unknownPathIs404
  - `QualityTest` (3): qa3_noConsoleErrors, qa4_homeMetadata, qa4_robotsAndSitemap
  - `MobileLayoutTest` (2, viewport 375 x 812): qa1_noHorizontalScroll, qa2_signInReachableOnPhone (KNOWN BUG D3)
  Verify: `grep -c "@Test" src/test/java/ch/hirion/pub/*.java` gives 7/7/5/8/3/2 for the new classes.
- [x] 1.2 Run `mvn -q test -Dsuite=testng-public.xml` and quote the failing lines (test name + assertion / locator message) in the change log; verify the report lists all 32 new tests plus the 12 SmokeTest tests.

## 2. Make scenarios GREEN

- [ ] 2.1 Compare every string in the new tests with the four specs verbatim (link / button / label / heading names, titles, URLs, meta description, `hirion.lang`); verify no mismatch remains.
- [ ] 2.2 Fix locators and assertions per AGENTS.md (role / label / text / testid; `// locator-exception:` only on `html` and `input[name='website']`; exact "Password" on `/login`) until all non-KNOWN-BUG tests pass; verify with `mvn -q test -Dsuite=testng-public.xml`. A failure caused by a wrong spec string (proposal "Sources" / "Open questions") is reported to a human, not fixed by editing the assertion.
- [ ] 2.3 Verify the contact form never reaches the network: in `ContactFormTest` every non-GET request is fulfilled locally (`route.fulfill`), cont2 tests record 0 such requests and cont4 records exactly 1 containing "qa-contact@example.com"; verify by reading the class and the test output.
- [ ] 2.4 Verify the five KNOWN BUG tests (D1 x2, D2, D3, D5) keep the full SHALL assertion with `setTimeout(3000)` and are reported as passed (expected exception); D4 is not repeated in any new class (`grep -rn "D4" src/test/java/ch/hirion/pub` hits only SmokeTest).
- [ ] 2.5 Run `java scripts/CheckLocatorRules.java src/test/java`; verify it prints "PASS: no forbidden patterns".

## 3. Verify

- [ ] 3.1 Run `mvn test -Dsuite=testng-public.xml`; verify the suite is green with 44 tests (12 SmokeTest + 32 new), none skipped.
- [ ] 3.2 Run pnpm check and quote its summary line
