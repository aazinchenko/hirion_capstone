## 1. Scenario tests (RED)

- [x] 1.1 Create `src/test/java/ch/hirion/pub/SmokeTest.java` extending `BaseTest` with one `@Test` per scenario in `specs/home-page/spec.md` (12 tests: pub1_heroCtaOpensSignup, pub2_faqStartsCollapsed, pub2_faqClickExpands, pub2_faqShowsAnswer, pub3_pricingVisibleToGuest, pub3_yearlyIsDefault, pub3_monthlyPrice, pub3_quarterlyPrice, pub3_backToYearly, pub4_selectedPeriodIsAriaPressed, pub5_freePlanCta, pub5_paidPlanCta), guest context only (no storageState); verify by counting 12 `@Test` methods
- [x] 1.2 Run `mvn -q test -Dtest=SmokeTest` and quote the failing lines (test name + assertion/locator message) in the change log; verify the output lists all 12 tests

## 2. Make scenarios GREEN

- [ ] 2.1 Verify every string in SmokeTest matches `specs/home-page/spec.md` verbatim (link/button names, billed lines, FAQ answer prefix, `plan=free` / `plan=trial`)
- [ ] 2.2 Fix locators/assertions per AGENTS.md (role/label/text/testid only, `// locator-exception:` on `#faq` / `#pricing`) until PUB-1, PUB-2, PUB-3, PUB-5 tests pass; verify with `mvn -q test -Dtest=SmokeTest`
- [x] 2.3 Mark `pub4_selectedPeriodIsAriaPressed` as `@Test(expectedExceptions = AssertionFailedError.class, description = "PUB-4 KNOWN BUG D4 ...")` without weakening its assertion; verify it is reported as passed
- [ ] 2.4 Run `java scripts/CheckLocatorRules.java src/test/java`; verify exit 0 (the script is added in step 7 of the capstone plan)

## 3. Verify

- [ ] 3.1 Run `mvn test -Dsuite=testng-public.xml`; verify the suite is green with 12 SmokeTest tests
- [ ] 3.2 Run pnpm check and quote its summary line
