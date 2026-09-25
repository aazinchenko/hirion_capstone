# Intent -- hirion.ch end-to-end tests

## Why
hirion.ch has no tests in its CI that a browser runs. We want a suite that proves the public site and
the signed-in journey work, finds real defects, and heals broken locators with an agent under rules.

## Groups (IDs are used in specs, tests, heal-log and autonomy-log)
PUB   home page: hero CTA, FAQ accordion, pricing periods, CTA links              -> change 1
FOOT  footer links, legal pages (privacy / terms / imprint)                       -> change 2
I18N  language switcher EN / FR / DE / IT and its persistence                     -> change 2
CONT  contact form: validation only, real submit only with a mocked network       -> change 2
AUTH  guest redirects, login and forgot-password pages, 404 page                  -> change 2
QA    mobile layout, console errors, SEO basics                                   -> change 2
REG   signup wizard, steps 1-4, up to "See my matches"                            -> change 3
DASH  dashboard: feed, save / applied / hide, analytics, preferences              -> change 3
SET   settings: profile fields, photo, security, plan & billing                   -> change 3
DEL   delete the test user at the end of every run                                -> change 3

## Facts

CONFIRMED live 2026-09-24 (browser, course author):
- FAQ items are <button> with aria-expanded (false -> true on click).
- Pricing: default period is Yearly. Monthly CHF 15; Quarterly CHF 13/month,
  CHF 39 billed quarterly; Yearly CHF 10/month, CHF 120 billed yearly.
- Language menu: button "EN" -> role=menu with menuitems EN / FR / DE / IT,
  choice stored in localStorage "hirion.lang".
- Signup step 1 validates on the client: toast "Please enter your first and last name."

CONFIRMED live 2026-09-25 (browser, signed-in test account):
- Guests opening /dashboard or /settings are redirected to /login ("Welcome back").
- Contact form (/contact), h1 "Talk to the team.": required fields labelled Name, Email,
  Subject, Message; submit button "Send message"; honeypot input#website, tabindex=-1.
- Signup wizard (4 steps, "Step N of 4"):
  - Step 1: First name, Last name, Email, Password. Errors: "Please enter your first and last
    name." / "Enter an email and a password of at least 8 characters." / taken email:
    "An account with this email already exists. Sign in instead."
  - Step 2: CV upload, title "Upload your CV", "PDF, DOCX, TXT up to 10MB".
    Without a file: "Please upload your CV to continue. It is what powers your job matches."
  - Step 3: target roles, title "What roles are you looking for?".
    Without a role: "Please add at least one target role to continue."
  - Step 4: "Choose your plan": Premium Trial ("7 days free") and Free, Back, "See my matches".
    PREMIUM TRIAL IS SELECTED BY DEFAULT. Free is preselected only via /signup?plan=free.
  - The account is created only on step 4. Steps 1-3 send nothing except the email-taken check.
  - No email confirmation: after "See my matches" the user lands on /dashboard signed in;
    a welcome email arrives with no action required.
- Premium Trial lasts 7 days, then the account falls back to Free (no payment).
- Dashboard: h1 "Welcome back, <first name>".
  - Tabs are role=button (NOT role=tab): Job Feed, Saved, Applied, Archive, Analytics (PRO badge),
    Preferences.
  - Job card buttons: View job, Save, Tailor my CV, Write Cover Letter, Hide; match score "NN MATCH".
  - "Applied" has no own button: Hide (aria-haspopup=menu) -> role=menu with menuitems
    "Already applied" and "Not relevant".
    - "Already applied": the job leaves the feed and appears in the Applied tab.
    - "Not relevant": the job is removed from the feed.
  - Save: the job leaves the Job Feed and appears in the Saved tab.
  - Empty states: Saved "No saved jobs yet"; Applied "No applications tracked yet";
    Archive "No archived jobs yet".
  - Analytics: Section A "Your performance" (Total active jobs, Avg match score, Save rate,
    Jobs in feed); Section B "Swiss market intelligence"; Section C "Positioning & opportunity".
    There are NO saved / applied / hidden counters.
    Save rate = round(saved / (saved + jobs in feed) * 100) %.
    Example: feed of 10 jobs, Save on one -> Jobs in feed 9, Save rate 10%.
  - Preferences: role=combobox fields Roles, Work mode, Locations, Industries, Company type,
    Employment eligibility, Seniority, Languages; button "Save preferences".
- Settings: sections are role=button Profile / Security / Plan & Billing,
  deep links /settings?section=security and /settings?section=plan.
  - Profile: Upload photo; First name*, Last name*, Email* (required); Phone; Country (combobox);
    LinkedIn URL (free text, no validation by design); Headline; Short bio; CV block (View, Re-analyse, Replace; CV cannot be removed);
    Skills; job-alert time and frequency; button "Save changes".
  - Security: Current password, New password, Confirm new password (each with "Show password"),
    "Update password"; below it the "Delete account" section.
  - Plan & Billing (after default signup): "You're on the Pro plan.", "Renews on <date + 7 days>",
    Cancel subscription, Monthly / Quarterly / Yearly, "Manage subscription".
- Delete account (Settings > Security): button "Delete account" -> dialog "Delete your account?",
  buttons "Cancel" / "Delete". Success: toast "Your account has been deleted.", sign-out,
  redirect to "/". Failure: "Could not delete your account."
  (dialog texts and redirect confirmed from the live site code, the dialog was not submitted)

ASSUMED, NOT YET VERIFIED:
- (none)

## Known defects (each gets a KNOWN BUG test; the spec keeps the correct SHALL)
D1   /imprint: 7 unfilled templates "[FILL: ...]", email mailto:[FILL: ...].              (walkthrough)
D2   After switching to DE the text is German but <html lang> stays "en".                 (walkthrough)
D3   Mobile 375 px: header links and Sign in hidden, no burger menu.                      (walkthrough)
D4   Pricing toggle Monthly / Quarterly / Yearly has no aria-pressed.                     (walkthrough)
D5   /login: Email / Password inputs have no autocomplete.                                (walkthrough)
D6   /signup: "Show password" has tabindex=-1, not reachable by keyboard.                 (walkthrough)
D7   Settings > Phone accepts letters ("00000000000000jj").                  (screenshot, to re-check)
D8   Job titles show "?" instead of characters: "Senior Azure Engineer | ? oder ... Next Level ??".
                                                                          (CONFIRMED live 2026-09-25)
D9   Settings > Profile shows "You have unsaved changes" right after load, with no edits;
     switching section opens "Unsaved changes -- Leave without saving?".  (CONFIRMED live 2026-09-25)
     Workaround in tests: open sections via /settings?section=... .
D10  Settings > Profile > Email* has no effective validation: an error is shown, but any string
     is saved as the email.                                               (CONFIRMED live 2026-09-25)

## Done means
- `pnpm check` exits 0 and ends with non-zero counters: specs: 9 · active changes: 0 · archived: 4.
- Every change has commits in the order: spec -> RED -> GREEN -> archive.
- `mvn test -Dsuite=testng-all.xml` is green; known defects D1-D10 are KNOWN BUG tests.

## Not doing
- OAuth (Google / Apple), real payments, real contact-form delivery.
- Starting Premium Trial: signup tests MUST select Free on step 4 (or open /signup?plan=free),
  because Premium Trial is the default.
- Assertions on the text of AI answers (Tailor my CV, Cover Letter).
- Tests against a real personal account.

## Budget (written by a human BEFORE running the agent; actuals from the transcript AFTER)
| Change                | output tokens | USD   | actual output | actual USD |
|-----------------------|---------------|-------|---------------|------------|
| 1 add-home-smoke      | <= 40 000     | <= $2 |               |            |
| 2 add-public-coverage | <= 80 000     | <= $4 |               |            |
| 3 add-user-journey    | <= 80 000     | <= $4 |               |            |
| 4 update-signup-steps | <= 20 000     | <= $1 |               |            |
