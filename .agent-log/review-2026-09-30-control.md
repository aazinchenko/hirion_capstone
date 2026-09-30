# Locator review (control run): add-user-journey (src/test/java)

**Date:** 2026-09-30
**Reviewer:** locator-reviewer (read-only), control run after translating the reviewer instruction to English
**Repo:** D:\Demo\Hirion_Capstone\hirion_capstone
**Reviewed revision:** HEAD = 15b6071 (files read with `git show HEAD:<path>`, not from the working tree)

## Scope

`git diff 7b21f12^..HEAD -- src/test/java`. Test-code commits in this range:

| Commit | Message |
|---|---|
| 7b21f12 | chore: TestUser + CV fixture; shared page helpers moved to BaseTest (plan step 20) |
| 75720d0 | test: JourneyCleanup -- delete QA user with safety guard, leftover cleanup (plan step 21) |
| 022c27f | test: RegistrationTest (RED on the step 3 role picker) -- no account created (plan step 22) |
| 9e5fc59 | test: RegistrationTest -- healed via heal-loop.sh (step 3 role picker), GREEN on iteration 3 |
| 6afe69d | test: DashboardTest written by the agent (12 tests), reviewed; Analytics Pro tab name, DASH-9 headings (plan step 23) |
| 3511779 | test: DashboardTest fixes -- score badge text, slow Analytics, D8 regular check, AI buttons Pro-only (plan step 23) |
| d091494 | test: SettingsTest written by the agent (13 tests), reviewed; D14 required fields KNOWN BUG, D9 switch hardened (plan step 24) |
| b90a2e1, 67109db, f92bf6e, 53d8df0, 462cbc0 | test: SettingsTest live-run fixes (Profile inputs by textbox, CV analysis wait, First name settle, D9 SET-3 as regular checks) (plan step 24) |
| 63ceb04 | test: review 2026-09-30 fixes -- SET-3 counts visible matches, cleanup log via html, addRole comment (plan step 25) |
| 895161a | test: signup steps match the confirmed spec -- REG-7 ?plan preselection and Back (plan step 26) |

Files changed: journey/DashboardTest, journey/JourneyCleanup, journey/RegistrationTest, journey/SettingsTest (new); support/BaseTest (open/reload/h1/field/QUICK_ATTR moved in); support/TestUser (new); pub/PublicPageTest (helpers removed, now inherited unchanged from BaseTest).

Working tree vs HEAD: `git diff HEAD -- src/test/java` touches only support/BaseTest.java and support/AuthenticatedTest.java, and only comments (Russian to English: "Subclasses override...", "trace only for failed tests", "Find by role and EXACT name...", the AuthenticatedTest Javadoc). There is no code or locator difference, so the verdict below applies to both.

## CheckLocatorRules result

```
$ java scripts/CheckLocatorRules.java src/test/java          (working tree)
PASS: no forbidden patterns
EXIT=0

$ java scripts/CheckLocatorRules.java <git archive HEAD>/src/test/java   (HEAD snapshot in scratchpad)
PASS: no forbidden patterns
EXIT=0
```

## Formal checks (AGENTS.md)

| Rule | Result |
|---|---|
| Locators only getByRole / getByLabel / getByText / getByTestId | OK. The only `page.locator(...)` is JourneyCleanup.java:127 (`html`, whole-page text for the log) with `// locator-exception: <html>, ...`. All other locators are role/label/text based. |
| `role(...)` helper preferred / exact names | OK. `role(...)` is used for almost every button/link/heading/option/menuitem. Inline lookups outside the helper also set `setExact(true)` (DashboardTest:264-265, :269-270, :274; SettingsTest:281-283; JourneyCleanup:74, :79, :105-107). Regex names are anchored (DashboardTest:253 `^Analytics( Pro)?$`; notifications `^Notifications`). |
| `getByLabel("Password")` exact on /signup and /login | OK. `field()` (BaseTest, exact) is used in RegistrationTest:236, :274 and SettingsTest:83, :159, :238, :265-267; SettingsTest:282 and JourneyCleanup:106 pass `GetByLabelOptions().setExact(true)` inline. |
| "Continue" does not match OAuth buttons | OK. RegistrationTest:301-303 `role(BUTTON, "Continue")`. |
| No nth-child, .css-xxx, waitForTimeout, Thread.sleep | OK. Timed waits are event-based (`waitForCondition` SettingsTest:189, `waitForURL`, assertion timeouts). `cards.nth(i)` in DashboardTest:302 iterates role-based matches to read titles; it is not an nth-child CSS locator. |
| Test data / safety | OK. Only TestUser (email `<mailbox>+hirion-qa-<ts>@gmail.com`, TestUser.java:47-51). The JourneyCleanup GUARD `\+hirion-qa-\d+@` is intact (JourneyCleanup.java:33, :53). reg9 stops before "See my matches" unless Free is selected and Premium Trial is not (RegistrationTest:193-197). No OAuth, no trial, no payment; "Delete account", "Upgrade to Pro" and the AI buttons are never clicked. Uploads use `FIXTURES.resolve("qa-cv.pdf")`. No assertion on AI answer text (AI buttons are only checked for `hasCount(0)`). SET-4 aborts the request carrying the invalid email (SettingsTest:176-184). |
| KNOWN BUG tests: format and not weakened | OK. D6 (RegistrationTest:213-223), D14 (SettingsTest:105-124) and D10 (SettingsTest:168-206) use `expectedExceptions = AssertionFailedError.class`, "KNOWN BUG Dn" in the description, a specific `expectedExceptionsMessageRegExp`, and preconditions via TestNG / TimeoutError. D8 (DashboardTest:228-236, commit 3511779) and D9 (SettingsTest:137-164, commit 462cbc0) were changed from KNOWN BUG to regular checks. The assertion was not weakened: it still asserts the correct behaviour and now turns red when the defect shows. D9 even got stricter: `visibleText(...).hasCount(0)` instead of `.first().isHidden()`. See finding 8. |

## Findings

| # | File:line | Severity | Rule / aspect | Finding | Recommendation |
|---|---|---|---|---|---|
| 1 | SettingsTest.java:130-131 | Medium | Semantics, negative check | `role(BUTTON, "Remove")` / `role(BUTTON, "Delete")` with `hasCount(0)` are exact-name negative checks. A button named "Remove CV", "Delete file" or an icon button with the aria-label "Remove file" does not match, so the test stays green although the CV can be removed. Exact names are right for positive lookups, but here they make the negative check a possible false green. | Scope the check to the CV block (the container holding "Replace") and use an anchored regex name that starts with Remove or Delete there, so "Delete account" (Security section) is not hit. |
| 2 | DashboardTest.java:250-256 (used at :94, :111, :138) | Low | Locator scope | `tab(name)` is `role(BUTTON, name)` on the whole page, not scoped to the tab bar. The comment at :109 already notes that a saved card's button is also named "Saved". If a card with a "Saved" button is still in the DOM when `tab("Saved")` is clicked, this is a strict-mode violation. It works today only because of the ordering inside dash4. | Scope the tabs to their container (the element holding all six buttons), or exclude buttons inside `cards()`. |
| 3 | DashboardTest.java:187, :193, :200 | Low | Semantics | "Remote" is found as exact text anywhere in the main landmark, not inside the Work mode section. A "Remote" chip in another picker (e.g. Locations) would satisfy the check; two matches give a strict-mode error. | Scope to the Work mode section: the container of `role(HEADING, "Work mode")`. |
| 4 | DashboardTest.java:112, :139 | Low | Semantics | `exactText(title).first()` searches the whole page and `.first()` hides duplicates, so the match may come from somewhere other than the Saved / Applied list. | Scope to `cards()` or the main landmark after the tab switch; drop `.first()` or assert `hasCount(1)`. |
| 5 | RegistrationTest.java:40-41, :117-156, :194-197, :321-324 | Low | CSS-class oracle | Plan selection is judged by the Tailwind class token `border-primary` (documented: plan cards have no aria-pressed / aria-checked, candidate D12). It is not a forbidden locator (the cards are found by role + exact text), and the reg9 guard fails safe if the class changes (Free not selected means stop). It still ties the REG-7 checks to styling. | Keep until D12 is fixed; switch to `aria-pressed` / `aria-checked` once the site exposes state. |
| 6 | RegistrationTest.java:239-241 | Low | Semantics | AUTH-6 asserts that any toast list item is visible, not that it is an error. `toasts.getByRole(LISTITEM)` is a strict-mode violation if two toasts are shown. The wording "an error message is visible" is a human decision (commit 347efd9). | Assert `hasCount(1)` or use `.first()`; since Q4 is confirmed live ("Invalid login credentials"), consider asserting that text exactly. |
| 7 | DashboardTest.java:38-39, :233-235 | Info | Semantics, D8 | REPLACEMENT_CHAR only looks for an ASCII question mark next to a letter, space or another question mark. It does not look for the Unicode replacement character U+FFFD itself and can flag a real question mark (documented at :223-227). | Add U+FFFD to the pattern. |
| 8 | DashboardTest.java:221-236; SettingsTest.java:134-164 | Info | KNOWN BUG relabel | D8 and D9 are no longer `expectedExceptions` tests. The assertions still check the correct behaviour, so nothing was weakened. The D8 timeout went from 3 s to the default, which does not change the outcome of `hasCount(0)`. The change is traced in commits 3511779 / 8467dce / 462cbc0 (not reproduced on a new Free user). The KNOWN BUG labels are gone, though, so D8/D9 visibility now depends on design.md "Known bugs". | Confirm that a human approved the relabel (AGENTS.md "Known issues"); keep D8/D9 listed in design.md as not reproduced. |
| 9 | SettingsTest.java:102 | Info | Locator breadth | `exactText("Skills").first()` is page-wide with `.first()`, so it only checks that some "Skills" text exists. | Optionally scope to the CV block, or use `role(HEADING, "Skills")` if it is a heading. |
| 10 | RegistrationTest.java:279 | Info | Exactness | `page.getByLabel("Drop your CV here, or click to upload")` has no `setExact(true)`. Low risk, because the label is long and unique. | Add `GetByLabelOptions().setExact(true)` for consistency. |

Semantically correct and worth noting: `planCard("Free")` uses exact text, so it does not match "7 days free" (RegistrationTest:317-319). `field("Password")` never matches "Show password"; SET-5 checks all three password fields exactly and that there are exactly three "Show password" buttons (SettingsTest:237-240). `profileField()` uses role textbox + exact accessible name, which correctly works around the aria-hidden star of D14 (SettingsTest:302-310). `role(COMBOBOX, "Country")` does not hit "Country code". `cardButton()` is scoped to one card with exact names. `tab("Analytics")` handles the "Analytics Pro" name with an anchored regex. The unnamed comboboxes (D13) are matched by role + anchored visible text (RegistrationTest:290-291, DashboardTest:316-319). The JourneyCleanup confirm button is scoped to the dialog with exact "Delete", so it cannot hit "Delete account". The D6 precondition uses `org.testng.Assert`, and the D10 safety reload runs in `finally` with TestNG failures, so neither can pass as the known bug.

## Verdict

**PASS / APPROVE**, with non-blocking follow-ups.

- Check 1 (formal): PASS. There is no forbidden pattern. The only raw locator (`html`) carries a `// locator-exception:` comment. CheckLocatorRules passes on HEAD and on the working tree.
- Check 2 (semantic): PASS with findings. No locator points at the wrong element today (95/95 live, commit 7c5319a). Finding 1 (Medium) is a negative check that can be falsely green. Findings 2-6 are scope and strict-mode risks.
- Check 3 (known bugs): PASS. No expectedExceptions test was weakened into a "fixed" one. D6, D10 and D14 keep specific message regexes and TestNG preconditions. D8/D9 were relabelled to regular checks that still assert the correct behaviour (finding 8: confirm human approval).

Reviewed files (at HEAD 15b6071):
- src/test/java/ch/hirion/journey/DashboardTest.java
- src/test/java/ch/hirion/journey/JourneyCleanup.java
- src/test/java/ch/hirion/journey/RegistrationTest.java
- src/test/java/ch/hirion/journey/SettingsTest.java
- src/test/java/ch/hirion/support/BaseTest.java
- src/test/java/ch/hirion/support/TestUser.java
- src/test/java/ch/hirion/support/AuthenticatedTest.java (not changed in this range at HEAD; the working tree differs only in a comment)
- src/test/java/ch/hirion/pub/PublicPageTest.java
