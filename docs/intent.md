# Intent -- hirion.ch end-to-end tests
## Why
hirion.ch has no tests in its CI that a browser runs. We want a suite that proves the public site and
the signed-in journey work, finds real defects, and heals broken locators with an agent under rules.
## Groups (IDs are used in specs, tests, heal-log and autonomy-log)
PUB home page: hero CTA, FAQ accordion, pricing periods, CTA links -> change 1
FOOT footer links, legal pages (privacy / terms / imprint) -> change 2
I18N language switcher EN / FR / DE / IT and its persistence -> change 2
CONT contact form: validation only, real submit only with a mocked network -> change 2
AUTH guest redirects, login and forgot-password pages, 404 page -> change 2
QA mobile layout, console errors, SEO basics -> change 2
REG signup wizard, steps 1-4, up to "See my matches" -> change 3
DASH dashboard: feed, save / applied / hide, analytics, preferences -> change 3
SET settings: profile fields, photo, security, plan & billing -> change 3
DEL delete the test user at the end of every run -> change 3
## Facts
CONFIRMED live 2026-09-24 (browser):
- FAQ items are <button> with aria-expanded (false -> true on click).
- Pricing: default period is Yearly. Monthly CHF 15; Quarterly CHF 13/month,
CHF 39 billed quarterly; Yearly CHF 10/month, CHF 120 billed yearly.
- Language menu: button "EN" -> role=menu with menuitems EN / FR / DE / IT,
choice stored in localStorage "hirion.lang".
- Guests opening /dashboard or /settings are redirected to /login.
- Signup step 1 validates on the client: toast "Please enter your first and last name."
CONFIRMED from screenshots (texts only, roles still to verify):
- Signup step 4 "Choose your plan": Premium Trial ("7 days free"), Free (selected
by default), Back, "See my matches".
- Dashboard tabs and job card buttons; Settings > Profile fields.
ASSUMED, NOT YET VERIFIED:
- Signup steps 2 (job preferences) and 3 (CV upload).
- Where "Delete account" lives and how it is confirmed.
- How a job becomes "Applied"; what Analytics shows.
- Whether signup requires email confirmation.
- English labels of the contact form (seen only in the German version).
## Done means
- `pnpm check` exits 0 and ends with non-zero counters: specs: 9 · active changes: 0 · archived: 4.
- Every change has commits in the order: spec -> RED -> GREEN -> archive.
- `mvn test -Dsuite=testng-all.xml` is green; known defects D1-D8 are KNOWN BUG tests.
## Not doing
- OAuth (Google / Apple), real payments, real contact-form delivery.
- Assertions on the text of AI answers (Tailor my CV, Cover Letter).
- Tests against a real personal account.
## Budget (written by a human BEFORE running the agent; actuals from the transcript AFTER)
| Change | output tokens | USD | actual output | actual USD |
|----------------------------|---------------|-------|---------------|------------|
| 1 add-home-smoke | <= 40 000 | <= $2 | | |
| 2 add-public-coverage | <= 80 000 | <= $4 | | |
| 3 add-user-journey | <= 80 000 | <= $4 | | |
| 4 update-signup-steps | <= 20 000 | <= $1 | | |