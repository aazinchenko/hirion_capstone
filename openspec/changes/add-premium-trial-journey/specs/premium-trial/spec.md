## ADDED Requirements

### Requirement: PREM-1 A Free user starts the Premium trial in Plan & Billing
On `/settings?section=plan` a user on the Free plan SHALL see the Premium card with the button "Start 7-day free
trial". Clicking it SHALL start the 7-day Premium trial at once, without a payment step and without card data: the
page SHALL show the notification "Your 7-day Premium trial is active" and SHALL NOT open a Stripe checkout. (Seen live
2026-10-24: the toast came after 0.6 s.)

#### Scenario: Trial starts without checkout
- **WHEN** the signed-in Free test user opens `/settings?section=plan` and clicks the button "Start 7-day free trial"
- **THEN** the notification "Your 7-day Premium trial is active" is shown and no Stripe checkout frame is on the page

### Requirement: PREM-2 Plan & Billing shows the active trial
After the trial has started, Plan & Billing SHALL say "You're on the Premium plan.", SHALL show "Free trial ends on
<date>." with the date 7 days after the activation day in the format dd/mm/yyyy, and SHALL show "No card on file,
nothing will be charged.". The Free card SHALL show the disabled button "Free plan", the Premium card SHALL show the
button "Manage subscription" (never clicked) instead of "Start 7-day free trial", and there SHALL be no button
"Cancel subscription". (Seen live 2026-10-24: "Free trial ends on 31/10/2026.")

#### Scenario: Trial plan state
- **WHEN** the test user whose trial has started opens `/settings?section=plan`
- **THEN** the page shows "You're on the Premium plan.", "Free trial ends on <activation day + 7 days, dd/mm/yyyy>."
  and "No card on file, nothing will be charged.", the disabled button "Free plan" and the button "Manage
  subscription", and there is no button "Start 7-day free trial" or "Cancel subscription"

### Requirement: PREM-3 The Premium Job Feed shows job cards instead of the teaser
For a user on the Premium trial the Job Feed SHALL show job cards: at least one card with the button "Tailor my CV",
and SHALL NOT show the Free teaser (no heading starting with "We found", no link "Unlock with Basic"). The cards MAY
take some minutes to appear after the activation (reported delay of 3-5 minutes on 2026-10-22, 13 s on 2026-10-24),
so the check waits up to 6 minutes.

#### Scenario: Job cards after the activation
- **WHEN** the test user whose trial has started opens `/dashboard` (reloading for up to 6 minutes until a card
  appears)
- **THEN** the page shows at least one button "Tailor my CV", no heading starting with "We found" and no link
  "Unlock with Basic"

### Requirement: PREM-4 Every job card offers all Premium actions
Each job card in the Premium Job Feed SHALL show the buttons "View job", "Save", "Tailor my CV", "Write Cover
Letter", "Help me stand out" and "Hide", and a match score whose label is "match" (shown in capitals by CSS).

#### Scenario: Card actions
- **WHEN** the test user whose trial has started opens `/dashboard` with job cards shown
- **THEN** the number of buttons "View job", "Save", "Write Cover Letter", "Help me stand out" and "Hide" and the
  number of texts "match" each equal the number of buttons "Tailor my CV" (one per card)

### Requirement: PREM-5 Save moves a job to Saved
Clicking "Save" on a card SHALL remove that card from the Job Feed and SHALL show it in the Saved tab with the button
"Saved". (Seen live 2026-10-24, no notification is shown.)

#### Scenario: Save a job
- **WHEN** the test user whose trial has started clicks "Save" on the first card of the Job Feed and then clicks
  the button "Saved"
- **THEN** the Job Feed has one "Save" button less than before, and the Saved tab shows one button "Saved" and not
  the text "No saved jobs yet"

### Requirement: PREM-6 Already applied moves a job to Applied
"Hide" on a card SHALL open a menu with "Already applied" and "Not relevant". "Already applied" SHALL remove the card
from the Job Feed and SHALL show it in the Applied tab with the text "Applied <Mon d, yyyy>" of the current day.
(Seen live 2026-10-24: "Applied Oct 24, 2026".)

#### Scenario: Mark a job as applied
- **WHEN** the test user whose trial has started clicks "Hide" on the first card, then the menu item "Already
  applied", and then the button "Applied"
- **THEN** the Job Feed has one card less than before, and the Applied tab shows the text "Applied <today as Mon d,
  yyyy>"

### Requirement: PREM-7 Not relevant removes a job from the feed
"Not relevant" in the "Hide" menu SHALL remove the card from the Job Feed.

#### Scenario: Dismiss a job
- **WHEN** the test user whose trial has started clicks "Hide" on the first card and then the menu item "Not
  relevant"
- **THEN** the Job Feed has one card less than before

### Requirement: PREM-8 Analytics is unlocked for the Premium trial
For a user on the Premium trial the Analytics tab SHALL show the analytics without the paid-plan gate (no heading
"Unlock Hirion Premium"): the sections "Your performance", "Swiss market intelligence" and "Positioning &
opportunity" and the figures "Total active jobs", "Avg match score", "Save rate" and "Jobs in feed". The Analytics
tab button SHALL NOT carry the badge "Pro", so its accessible name is "Analytics". (Known defect D20, seen live
2026-10-24: the trial user still sees "Analytics PRO".)

#### Scenario: Analytics without the gate
- **WHEN** the test user whose trial has started opens `/dashboard` and clicks the Analytics tab button
- **THEN** the page shows "Your performance", "Swiss market intelligence", "Positioning & opportunity", "Total
  active jobs", "Avg match score", "Save rate" and "Jobs in feed" and no heading "Unlock Hirion Premium"

#### Scenario: Analytics tab has no Pro badge
- **WHEN** the test user whose trial has started opens `/dashboard`
- **THEN** there is a button named exactly "Analytics" (known defect D20: today it is "Analytics PRO")

### Requirement: PREM-9 Save rate counts saved jobs
After the first job is saved, Analytics SHALL show "Save rate" = round(saved / (saved + jobs in feed) x 100) % with
saved = 1 and "Jobs in feed" as shown on the same page. (Formula recorded on a trial account on 2026-09-25,
docs/intent.md; first live check by this requirement.)

#### Scenario: Save rate after one save
- **WHEN** the test user whose trial has started has saved exactly one job (PREM-5) and opens the Analytics tab
- **THEN** with F = the number shown next to "Jobs in feed", "Save rate" shows round(100 / (1 + F)) %

### Requirement: PREM-10 The home page hides the pricing for a Premium user
For a signed-in user on the Premium trial the home page `/` SHALL NOT render the pricing section `#pricing`, and its
header SHALL show the link "Dashboard" to `/dashboard`.

#### Scenario: Home page of a Premium user
- **WHEN** the test user whose trial has started opens `/`
- **THEN** there is no element with the id `pricing` and the header link "Dashboard" has `href="/dashboard"`

### Requirement: PREM-11 Help me stand out shows a review of the job
"Help me stand out" on a card SHALL open a dialog titled "Help me stand out" that, after loading, shows the parts
"What to highlight", "Worth addressing" and "Keywords to use". The text written by the AI is not checked. Runs only
with `-Dqa.ai=true`, at most once per run. (Read from the site code 2026-10-23; ASSUMED until the first `-Dqa.ai`
run.)

#### Scenario: Stand-out review
- **WHEN** the test user whose trial has started runs the suite with `-Dqa.ai=true` and clicks "Help me stand out"
  on the first card
- **THEN** within 90 s a dialog shows "Help me stand out", "What to highlight", "Worth addressing" and "Keywords to
  use"

### Requirement: PREM-12 Tailor my CV writes a tailored CV
"Tailor my CV" on a card SHALL open a dialog "Tailored CV" that, after loading, shows the badge "ATS-friendly", the
templates "Classic" and "Modern" and the button "Copy"; "Copy" SHALL show the notification "Copied". The text written
by the AI is not checked. Runs only with `-Dqa.ai=true`, at most once per run. (Site code 2026-10-23; ASSUMED until
the first `-Dqa.ai` run.)

#### Scenario: Tailored CV
- **WHEN** the test user whose trial has started runs the suite with `-Dqa.ai=true`, clicks "Tailor my CV" on the
  first card and, once loaded, clicks "Copy"
- **THEN** within 90 s a dialog shows "Tailored CV", "ATS-friendly", "Classic" and "Modern", and after "Copy" the
  notification "Copied" is shown

### Requirement: PREM-13 Write Cover Letter writes a cover letter
"Write Cover Letter" on a card SHALL open a dialog "Cover letter" that, after loading, shows the templates
"Letterhead" and "Plain" and the button "Copy". The text written by the AI is not checked. Runs only with
`-Dqa.ai=true`, at most once per run. (Site code 2026-10-23; ASSUMED until the first `-Dqa.ai` run.)

#### Scenario: Cover letter
- **WHEN** the test user whose trial has started runs the suite with `-Dqa.ai=true` and clicks "Write Cover Letter"
  on the first card
- **THEN** within 90 s a dialog shows "Cover letter", "Letterhead", "Plain" and the button "Copy"

### Requirement: PREM-14 An account with an active trial can be deleted
Deleting the account from Settings > Security SHALL also work while the Premium trial is active: afterwards the
account no longer signs in. The suite's `JourneyCleanup` performs
it after every run; its output is the evidence (as for DEL-1, no separate TestNG test clicks "Delete"). (Seen live
2026-10-24 in the Premium probe.)

#### Scenario: Cleanup of the trial account
- **WHEN** `JourneyCleanup` deletes the test user after the Premium tests
- **THEN** its output contains "deleted: ... no longer signs in" (the page text right after "Delete" is only logged:
  it sometimes still shows the old page)
