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
    CONFIRMED live 2026-09-30 (temporary guest probe, "See my matches" never clicked, no account,
    probe not committed; plan REG-09 / REG-11, question 9 of change 3):
    - Step 4 preselection: /signup and /signup?plan=trial -> Premium Trial; /signup?plan=free -> Free;
      /signup?plan=bogus -> Premium Trial (NOT "no card" as read from the site code on 2026-09-29).
    - "Back" on step 4 -> "Step 3 of 4" still shows the chosen role chip ("QA / Test Engineer"), the
      picker then says "1 selected" instead of "Add"; "Continue" -> step 4 again with the chosen plan
      (Free) still selected. "Back" twice more -> "Step 2 of 4" still shows the file name "qa-cv.pdf".
  - The account is created only on step 4. Steps 1-3 send nothing except the email-taken check.
  - No email confirmation: after "See my matches" the user lands on /dashboard signed in;
    a welcome email arrives with no action required.
- Premium Trial lasts 7 days, then the account falls back to Free (no payment).
- Dashboard: h1 "Welcome back, <first name>".
  - Tabs are role=button (NOT role=tab): Job Feed, Saved, Applied, Archive, Analytics (PRO badge),
    Preferences.
  - Job card buttons: View job, Save, Tailor my CV, Write Cover Letter, Hide; match score "NN MATCH".
    (recorded on the trial = Pro account. 2026-09-30, live + site code: a Free user's cards show only
    View job, Save and Hide -- the AI buttons incl. "Help me stand out" are Pro-only; the score badge is
    "87" + "match" in two spans, capitals only by CSS; Analytics is Pro-only, Free sees "Unlock Hirion Pro".)
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
     NOT A DEFECT (human decision 2026-09-30): Phone is free-form text by design; no test.
D8   Job titles show "?" instead of characters: "Senior Azure Engineer | ? oder ... Next Level ??".
                                                                          (CONFIRMED live 2026-09-25)
     Not reproduced 2026-09-30 in the feed of a new Free user: depends on the job ads in the feed,
     so DASH-10 is a regular check, not a KNOWN BUG test.
D9   Settings > Profile shows "You have unsaved changes" right after load, with no edits;
     switching section opens "Unsaved changes -- Leave without saving?".  (CONFIRMED live 2026-09-25)
     Workaround in tests: open sections via /settings?section=... .
     Not reproduced 2026-09-30 on a new Free user (3 live runs, also on the fully loaded form). It was
     seen on a manual account with odd Phone data; it probably depends on the stored profile, so SET-3 is
     a regular check, not a KNOWN BUG test (human decision). Open question (ASSUMED): D9 appears when a
     stored value differs from what the form shows (the site normalises Phone to "+41..." without spaces).
D10  Settings > Profile > Email* has no effective validation: an error is shown, but any string
     is saved as the email.                                               (CONFIRMED live 2026-09-25)

D11  /signup step 1 accepts an invalid email such as "qa@": only "not empty" and "password >= 8" are
     checked, the input is type=email but not inside a form, so the error comes only on step 4.
                                                   (CONFIRMED live 2026-09-30: "qa@" -> "Step 2 of 4", no error)
D12  /signup step 4: the Premium Trial / Free cards do not expose which one is selected (no
     aria-pressed / aria-checked / role=radio); the selection is only visual.
                                                   (candidate, from the site code 2026-09-29, confirm live)

D13  /signup step 3: the role picker is <button role="combobox"> with the visible text "Add" but no
     accessible name (a combobox does not take its name from content, no aria-label); a screen reader
     announces only "combobox". The same unnamed picker is used for every field of Dashboard > Preferences
     (site code, 2026-09-30).                          (candidate, seen in the live run 2026-09-29)
D14  Settings > Profile: First name, Last name and Email show a "*" in the label (aria-hidden), but the inputs
     have no required / aria-required, so browsers and screen readers do not know they are required.
                                                   (from the site code 2026-09-30, human decision: KNOWN BUG)

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
| 1 add-home-smoke      | <= 40 000     | <= $2 | 125 899 (author agent 48 790)  | $16.22     |
| 2 add-public-coverage | <= 80 000     | <= $4 | 236 817 (author agent 52 736)  | $23.10     |
| 3 add-user-journey    | <= 80 000     | <= $4 | 395 065 (author agent 106 952) | $67.32     |
| 4 update-signup-steps | <= 20 000     | <= $1 | 43 784 (author agent 14 996)   | $4.01      |
| setup before change 1 | --            | --    | 81 869                         | $7.87      |
| after change 4 (docs) | --            | --    | 48 304 (step 28 still running) | $5.92      |
| **total**             | <= 220 000    | <= $11| **931 738**                    | **$124.45**|

Actuals (2026-09-30 17:34 UTC) from `python scripts/usage-by-change.py`: every assistant message in the
session transcripts of both windows (project folder = author agent incl. subagents, parent folder = reviewer)
is assigned to a change by time (previous archive commit -> own archive commit), counted once, priced at the
list price of claude-opus-5-5 (all token types). The work ran on a subscription, so USD is an estimate, not an
invoice. Why the budget was exceeded:
- All changes: the budget was written for one agent, but the process ran two windows (maker != checker). The
  reviewer window produced 74% of the output (686k of 932k). The author agent alone stayed under budget in
  changes 2 and 4 and exceeded it in 1 (+22%) and 3 (+34%).
- USD: output is only ~15% of the cost. ~49% is re-reading the long context (304M cache-read tokens) and ~36%
  is writing it to the 1 h cache (5.6M tokens at 2x input). Long sessions in one context made it expensive.
- 1: the loop tooling (CheckLocatorRules, FetchLiveDom, heal-loop.sh) was built inside this change.
- 2: 32 scenario tests plus a locator review whose three Medium findings were fixed in the same change.
- 3: reading the site's JS bundles for facts, 11 live runs (Registration 1, Dashboard 3, Settings 6, full
  suite 1) with trace analysis (one wrong diagnosis), and a second review round; 42% of all output.
- 4: over by 2.2x -- the guest probe that corrected a code-read fact, and checking a failed archive sync.
