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
LINK  pages opened from emails: survey, unsubscribe, checkout return (guest)        -> sync-specs-with-site-scan
PREM  the journey user after activating the 7-day Premium Trial in Settings         -> add-premium-trial-journey

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
    "87" + "match" in two spans, capitals only by CSS; Analytics is Pro-only, Free sees "Unlock Hirion Pro";
    renamed to "Unlock Hirion Premium" / "Upgrade to Premium" by 2026-10-21.)
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

CONFIRMED live 2026-10-23 (full site scan, guest browser + nightly CI; change sync-specs-with-site-scan):
- Dashboard teaser: "Unlock with Basic" -> /settings?section=plan&buy=basic (CI run 39918465621, DASH-2 red);
  "See all plans" -> /settings?section=plan.
- /login and /signup step 1: "Continue with Google", "Continue with Apple", "OR" above the email form; /login also
  has "Show password".
- /reset-password without a link: h1 "Set a new password", "This reset link is invalid or has expired. Request a
  new one.", link "Send reset link" -> /forgot-password.
- /survey (public, -> /survey?id=): h1 "Help us make Hirion better", 6 questions, "Send feedback".
- /survey without an id: "Send feedback" shows "This survey link is missing its id. Please use the link from the
  email." (text changed by 2026-10-25, before: "… id — please use …") and sends no request to /api/public/survey-response (EmailLinkPagesTest, mocked network, 2026-10-23).
- /unsubscribe without a link: h1 "Invalid link", "This unsubscribe link is invalid or has expired."
- /checkout/return without a session: h1 "No session found", link "Go to my account" ->
  /settings?section=plan&checkout=success.
- /onboarding, /employer/, /employer/<id>, /admin redirect a guest to /login.
- Public suite 57/57 and guest RegistrationTest 14/14 (+3 skipped) still green: no other public change.

CONFIRMED live 2026-10-24 (Premium Trial probe, human "да, запускай живую пробу без ИИ-кнопок"; temporary
uncommitted probe class after RegistrationTest, log ../premium-probe-2026-10-24.log, 24/24, account deleted):
- Settings > Plan & Billing of a Free user: "You're currently on the Free plan.", toggle Monthly / Yearly, cards Free
  (CHF 0 forever, button "Current plan" disabled), Basic (CHF 10/year, "Subscribe to Basic"), Premium (CHF 120/year,
  features incl. "AI CV Tailoring", "AI Cover Letter Generation", "Help me stand out", "Analytics dashboard", button
  "Start 7-day free trial"), "Have a promo code?", "Billing handled securely. Cancel anytime."
- "Start 7-day free trial" starts the trial at once, no Stripe checkout, no card: toast "Your 7-day Premium trial is
  active" after ~0.6 s.
- After the trial (also after reload): "You're on the Premium plan.", badge "Premium", "Free trial ends on 31/10/2026."
  (= today + 7 days, dd/mm/yyyy), "No card on file, nothing will be charged."; Free card button "Free plan" (disabled),
  Premium card button "Manage subscription" (not clicked); NO "Cancel subscription".
- Job Feed: real cards 13 s after the trial (bug #49 delay of 3-5 min not reproduced this time); intro "These are the
  latest jobs that match your profile. ..."; no teaser, no "Unlock with Basic". 3 cards, each: title, company,
  location, age, category, "Onsite", "Full-time", buttons "View job", "Save", "Tailor my CV", "Write Cover Letter",
  "Help me stand out", "Hide", score "95" + "MATCH".
- The Analytics tab button still reads "Analytics PRO" for the Premium trial user (D20, closed: only while loading).
- Analytics without gate ("Unlock Hirion Premium" absent): headings "Your performance" (KPIs "Total active jobs"
  38,215, "Avg match score" 92%, "Save rate" 0%, "Jobs in feed" 3), "Match score distribution", "New matches per day",
  "Swiss market intelligence", "Roles in demand by canton", "Salary benchmark", "Top companies hiring", "Language
  demand", "Positioning & opportunity", "Skills gap", "Filter impact", "Eligibility advantage".
- Save: no toast; the card leaves the feed (3 -> 2) and appears in Saved (helper "Jobs you save appear here, ...") with
  the button "Saved". Hide -> menu "Already applied" / "Not relevant"; "Already applied": no toast, the card appears in
  Applied (helper "Jobs you've marked as applied, ...") with "Applied Oct 24, 2026". Archive: "No archived jobs yet".
- Home page for the Premium user: no #pricing section, header shows "Dashboard" and the avatar link to /settings.
- Settings > Profile: "Job alert notification time" ("Any time") and "How often do you want job emails?" ("Every
  day"); "15 skills detected" from the CV fixture.
- JourneyCleanup deletes the trial account normally ("Your account has been deleted.", no longer signs in).

CONFIRMED live 2026-10-24 (first -Dqa.ai journey run, human "да, запусти один живой journey-прогон с -Dqa.ai=true";
../journey-premium-ai-2026-10-24.log, testng 50 total, 48 pass, 2 fail, account deleted; dialog texts from the traces):
- "Help me stand out" dialog: summary paragraph, "Copy", "What to highlight" (3 points), "Skills", "Keywords to use"
  (7 chips), "29 tailored applications left this month.", "Based only on what is in your CV. Opening it again for this
  job costs nothing.", "Try again". NO "Worth addressing" in this run (the section probably shows only when the AI
  finds gaps) -> PREM-11 failed on it.
- "Tailor my CV" dialog: "ATS-friendly", templates "Classic" ("Serif, section bands, dates on the right") and "Modern"
  ("Sans-serif, accent rule, roomier"), label "Closely tailored", buttons "Copy", ".txt", "PDF", "Try again",
  "29 tailored applications left this month.", "Asking for the other document on this job does not use another one.
  ...", "Built to be read cleanly by applicant-tracking systems: ...". The text "Tailored CV" was NOT found in the
  dialog -> PREM-12 failed before "Copy" was clicked ("Copied" still unverified).
- "Write Cover Letter" dialog: PREM-13 passed ("Letterhead", "Cover letter", "Plain", "Copy" visible).
- One AI generation per dialog: the counter showed 29 left of the month after the run.
- Third -Dqa.ai run 2026-10-24 13:59 (change fix-premium-ai-dialogs): 50/50 green; "Copy" in the CV dialog shows
  "Copied"; the stand-out counter shares one paragraph with "Based only on what is in your CV. ...".

ASSUMED, NOT YET VERIFIED:
- (site code 2026-10-23) /settings?buy=basic replaces the URL with ?section=plan and starts the Basic checkout at
  once for a Free user -- never opened by the tests.
- (site code) /survey with an id requires answers 1-3 ("Please answer questions 1-3 before submitting.") and
  POSTs /api/public/survey-response.
- (site code) Promo codes incl. new QATEST25 "25% off your first payment"; /settings?checkout=success shows
  "Welcome to Premium!" / "Your subscription is being activated."
- (site code) /onboarding = target roles (and CV) after Google / Apple sign-up; account deletion now signs out
  only the local session.

## Known defects (each gets a KNOWN BUG test; the spec keeps the correct SHALL)
D1   /imprint: 7 unfilled templates "[FILL: ...]", email mailto:[FILL: ...].              (walkthrough)
     CLOSED 2026-10-24 (human decision: not relevant). Still visible live on 2026-10-24 (7 "[FILL", mailto:[FILL: ...]);
     FOOT-3 and its two KNOWN BUG tests removed (change remove-foot3-imprint-d1).
D2   After switching to DE the text is German but <html lang> stays "en".                 (walkthrough)
     CLOSED 2026-10-24 (human decision: fixed).
     FIXED (seen live 2026-10-06): regular test now (I18N-4, change close-fixed-defects-d2-d4-d5).
D3   Mobile 375 px: header links and Sign in hidden, no burger menu.                      (walkthrough)
     NOT A DEFECT (human decision 2026-10-05): intended mobile design; QA-2 removed, no test.
D4   Pricing toggle Monthly / Quarterly / Yearly has no aria-pressed.                     (walkthrough)
     CLOSED 2026-10-24 (human decision: fixed).
     FIXED (seen live 2026-10-06): regular test now (PUB-4, change close-fixed-defects-d2-d4-d5).
D5   /login: Email / Password inputs have no autocomplete.                                (walkthrough)
     CLOSED 2026-10-24 (human decision: fixed).
     FIXED (seen live 2026-10-06): regular test now (AUTH-3, change close-fixed-defects-d2-d4-d5).
D6   /signup: "Show password" has tabindex=-1, not reachable by keyboard.                 (walkthrough)
     CLOSED 2026-10-24 (human decision: fixed).
     FIXED (seen live 2026-10-06): regular test now (REG-10, change close-fixed-defects-d6-d10-d14).
D7   Settings > Phone accepts letters ("00000000000000jj").                  (screenshot, to re-check)
     NOT A DEFECT (human decision 2026-09-30): Phone is free-form text by design; no test.
D8   Job titles show "?" instead of characters: "Senior Azure Engineer | ? oder ... Next Level ??".
                                                                          (CONFIRMED live 2026-09-25)
     Not reproduced 2026-09-30 in the feed of a new Free user: depends on the job ads in the feed,
     so DASH-10 is a regular check, not a KNOWN BUG test.
     2026-10-20: not observable any more for a Free user (the Job Feed is a locked teaser with blurred,
     aria-hidden titles); DASH-10 removed in change update-plans-and-pricing.
     CLOSED 2026-10-24 (human decision: not relevant).
D9   Settings > Profile shows "You have unsaved changes" right after load, with no edits;
     switching section opens "Unsaved changes -- Leave without saving?".  (CONFIRMED live 2026-09-25)
     Workaround in tests: open sections via /settings?section=... .
     Not reproduced 2026-09-30 on a new Free user (3 live runs, also on the fully loaded form). It was
     seen on a manual account with odd Phone data; it probably depends on the stored profile, so SET-3 is
     a regular check, not a KNOWN BUG test (human decision). Open question (ASSUMED): D9 appears when a
     stored value differs from what the form shows (the site normalises Phone to "+41..." without spaces).
     CLOSED 2026-10-24 (human decision: not relevant). SET-3 stays a regular check.
D10  Settings > Profile > Email* has no effective validation: an error is shown, but any string
     is saved as the email.                                               (CONFIRMED live 2026-09-25)
     CLOSED 2026-10-24 (human decision: fixed).
     FIXED (seen live 2026-10-06): regular test now (SET-4, change close-fixed-defects-d6-d10-d14).

D11  /signup step 1 accepts an invalid email such as "qa@": only "not empty" and "password >= 8" are
     checked, the input is type=email but not inside a form, so the error comes only on step 4.
                                                   (CONFIRMED live 2026-09-30: "qa@" -> "Step 2 of 4", no error)
     CLOSED 2026-10-24 (human decision: not relevant; not re-checked live).
D12  /signup step 4: the Premium Trial / Free cards do not expose which one is selected (no
     aria-pressed / aria-checked / role=radio); the selection is only visual.
                                                   (candidate, from the site code 2026-09-29, confirm live)
     FIXED (seen live 2026-10-19): the cards (now Free / Basic / Premium) expose aria-pressed; REG-7 checks
     it (change update-plans-and-pricing).
     CLOSED 2026-10-24 (human decision: fixed).

D13  /signup step 3: the role picker is <button role="combobox"> with the visible text "Add" but no
     accessible name (a combobox does not take its name from content, no aria-label); a screen reader
     announces only "combobox". The same unnamed picker is used for every field of Dashboard > Preferences
     (site code, 2026-09-30).                          (candidate, seen in the live run 2026-09-29)
D14  Settings > Profile: First name, Last name and Email show a "*" in the label (aria-hidden), but the inputs
     have no required / aria-required, so browsers and screen readers do not know they are required.
                                                   (from the site code 2026-09-30, human decision: KNOWN BUG)
     FIXED (seen live 2026-10-06): regular test now (SET-2, change close-fixed-defects-d6-d10-d14).
     CLOSED 2026-10-24 (human decision: fixed).
D16  /signup?plan=basic, the target of the home page link "Choose Basic", does not preselect Basic on step 4:
     the page drops plan=basic from the URL, Premium is selected and the button says "Start my free trial".
                                                   (CONFIRMED live 2026-10-20 by a guest probe; KNOWN BUG,
                                                   REG-7, change update-plans-and-pricing)
     FIXED (CI Free run 2026-10-25 07:38 UTC, reproduced by a guest run; plan=basic stays in the URL): regular
     test now (REG-7, PUB-5 follows "Choose Basic"; change close-d16-update-survey-text).
D17  /survey question 2: the rating options 2, 3 and 4 (radio buttons q2-4 / q2-3 / q2-2) have an empty label, so
     a screen reader announces only "radio button".             (candidate, seen live 2026-10-23)
D18  /checkout/return without a checkout session says "No session found", but its link "Go to my account" carries
     checkout=success, and Settings then announces "Welcome to Premium!" (site code).
                                                   (candidate, link seen live 2026-10-23, toast ASSUMED)
D19  /forgot-password: the link "Back to home" points to /login, while on /login and /signup it points to /.
                                                   (candidate, seen live 2026-10-23)
     CLOSED 2026-10-24 (human decision: not relevant). Still visible live on 2026-10-24.
D20  Dashboard: a Premium trial user still sees the badge on the Analytics tab ("Analytics PRO"), although
     Analytics is unlocked for them; by the site code the badge is meant only for users without the paid plan.
                                                   (seen live 2026-10-24 in the Premium probe; KNOWN BUG by human
                                                   decision 2026-10-24, PREM-8, change add-premium-trial-journey)
     2026-10-24 live journey run: NOT reproduced -- the button was named exactly "Analytics" within 5 s. By the site
     code the badge shows while isPro is false, also while the subscription is loading: the probe probably read
     the text during loading. CLOSED 2026-10-24 (human decision: "D20 закрыть"); PREM-8 checks the plain name as a
     regular test.
D21  Dashboard: right after "Already applied" the Applied tab can stay empty for up to 30 s. The card leaves the
     feed at once (optimistic), the applied record is saved by a second request, and the Applied tab reads its own
     cached list ["applied-jobs-dated"] (staleTime 30 s) that the action does not refresh (only ["applied-jobs"]).
                                                   (candidate, site code 2026-10-30, ASSUMED; CI premium run
                                                   2026-10-30 10:05 UTC: PREM-6 found no "Applied <date>" in 15 s)
     (D15 is not used here: the 2026-10-15 candidate "saved job shown for a moment after reload" was decided
     by the human outside this file.)

## Done means
- `pnpm check` exits 0 and ends with non-zero counters: specs: 9 · active changes: 0 · archived: 4.
- Every change has commits in the order: spec -> RED -> GREEN -> archive.
- `mvn test -Dsuite=testng-all.xml` is green; no KNOWN BUG test is open (D16 fixed 2026-10-25);
  defects fixed by the site (D2, D4-D6, D10, D12, D14) are regular tests.

## Not doing
- OAuth (Google / Apple), real payments, real contact-form delivery.
- Any payment, card data or Stripe checkout. Signup tests MUST select Free on step 4 (or open /signup?plan=free)
  and click only "Join for free", because Premium (trial) is the default and Basic leads to "Continue to payment".
  Since 2026-10-24 (human decision, owner consent) the Premium Trial is started ONLY by the PREM scenario: the same
  disposable user, once per run, in Settings > Plan & Billing (docs/premium-journey-plan.md, variant A).
- AI buttons in CI; locally only with -Dqa.ai=true, each at most once per run.
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
