## ADDED Requirements

### Requirement: PREM-15 The suite signs in to the existing Premium account
The premium suite SHALL use one existing account whose Premium plan was granted by the site owner, never a new or
disposable account. It SHALL sign in on `/login` with the email and password from the environment variables
`QA_PREMIUM_EMAIL` and `QA_PREMIUM_PASSWORD` and SHALL reach `/dashboard`. It SHALL refuse an email matching
`+hirion-qa-<digits>@`, which is reserved for disposable users deleted by `JourneyCleanup`. The credentials SHALL NOT
be printed. (Human decision 2026-11-10.)

#### Scenario: Sign-in to the Premium account
- **WHEN** the suite runs with `QA_PREMIUM_EMAIL` and `QA_PREMIUM_PASSWORD` set and signs in on `/login`
- **THEN** the browser reaches `/dashboard` and the session is saved for the other Premium tests

### Requirement: PREM-16 Plan & Billing shows the Premium plan
For the Premium test account `/settings?section=plan` SHALL say "You're on the Premium plan." and SHALL NOT show the
button "Start 7-day free trial". Nothing on this page is clicked. Every other Premium test depends on it: when the
account is no longer Premium, they are skipped.

#### Scenario: Premium plan state
- **WHEN** the Premium test account opens `/settings?section=plan`
- **THEN** the page shows "You're on the Premium plan." and there is no button "Start 7-day free trial"

## MODIFIED Requirements

### Requirement: PREM-3 The Premium Job Feed shows job cards instead of the teaser
For the Premium test account the Job Feed SHALL show job cards: at least one card with the button "Tailor my CV",
and SHALL NOT show the Free teaser (no heading starting with "We found", no link "Unlock with Basic"). The check waits
up to 60 s for the first card.

#### Scenario: Job cards after the activation
- **WHEN** the Premium test account opens `/dashboard`
- **THEN** within 60 s the page shows at least one button "Tailor my CV", no heading starting with "We found" and no
  link "Unlock with Basic"

### Requirement: PREM-4 Every job card offers all Premium actions
Each job card in the Premium Job Feed SHALL show the buttons "View job", "Save", "Tailor my CV", "Write Cover
Letter", "Help me stand out" and "Hide", and a match score whose label is "match" (shown in capitals by CSS).

#### Scenario: Card actions
- **WHEN** the Premium test account opens `/dashboard` with job cards shown
- **THEN** the number of buttons "View job", "Save", "Write Cover Letter", "Help me stand out" and "Hide" and the
  number of texts "match" each equal the number of buttons "Tailor my CV" (one per card)

### Requirement: PREM-5 Save moves a job to Saved
Clicking "Save" on a card SHALL remove that card from the Job Feed and SHALL show it in the Saved tab with the button
"Saved". Clicking "Saved" on a job in the Saved tab SHALL remove it from Saved again, so the permanent account keeps
its state after the run. Counts are compared before and after, because the account may already have saved jobs.
(Save seen live 2026-10-24, no notification is shown; unsave ASSUMED from site code 2026-11-10.)

#### Scenario: Save a job
- **WHEN** the Premium test account notes the number of "Saved" buttons in the Saved tab, clicks "Save" on the first
  card of the Job Feed and then clicks the Saved tab button
- **THEN** the Job Feed has one "Save" button less than before and the Saved tab has one "Saved" button more than
  before

#### Scenario: Unsave a job
- **WHEN** after "Save a job" the Premium test account clicks "Saved" on the job title saved before in the Saved tab
- **THEN** the Saved tab has as many "Saved" buttons as before "Save a job" and that job title is no longer in it

### Requirement: PREM-6 Already applied moves a job to Applied
"Hide" on a card SHALL open a menu with "Already applied" and "Not relevant". "Already applied" SHALL remove the card
from the Job Feed and SHALL show it in the Applied tab with the text "Applied <Mon d, yyyy>" of the current day.
It cannot be undone on the site, so it runs only with `-Dqa.feedActions=true` (manual run, never nightly).
(Seen live 2026-10-24: "Applied Oct 24, 2026"; human decision 2026-11-10.)

#### Scenario: Mark a job as applied
- **WHEN** the Premium test account runs the suite with `-Dqa.feedActions=true`, clicks "Hide" on the first card,
  then the menu item "Already applied", and then the button "Applied"
- **THEN** the Job Feed has one card less than before, and the Applied tab shows the text "Applied <today as Mon d,
  yyyy>"

### Requirement: PREM-7 Not relevant removes a job from the feed
"Not relevant" in the "Hide" menu SHALL remove the card from the Job Feed. It cannot be undone on the site, so it
runs only with `-Dqa.feedActions=true` (manual run, never nightly). (Human decision 2026-11-10.)

#### Scenario: Dismiss a job
- **WHEN** the Premium test account runs the suite with `-Dqa.feedActions=true`, clicks "Hide" on the first card and
  then the menu item "Not relevant"
- **THEN** the Job Feed has one card less than before

### Requirement: PREM-8 Analytics is unlocked for the Premium trial
For the Premium test account the Analytics tab SHALL show the analytics without the paid-plan gate (no heading
"Unlock Hirion Premium"): the sections "Your performance", "Swiss market intelligence" and "Positioning &
opportunity" and the figures "Total active jobs", "Avg match score", "Save rate" and "Jobs in feed". The Analytics
tab button SHALL NOT carry the badge "Pro" once the page has loaded, so its accessible name is "Analytics". (While
the subscription is still loading the badge can show for a moment; the candidate defect D20 "Analytics PRO" was
closed by the human on 2026-10-24 after the live run showed the plain name.)

#### Scenario: Analytics without the gate
- **WHEN** the Premium test account opens `/dashboard` and clicks the Analytics tab button
- **THEN** the page shows "Your performance", "Swiss market intelligence", "Positioning & opportunity", "Total
  active jobs", "Avg match score", "Save rate" and "Jobs in feed" and no heading "Unlock Hirion Premium"

#### Scenario: Analytics tab has no Pro badge
- **WHEN** the Premium test account opens `/dashboard`
- **THEN** within 30 s there is a button named exactly "Analytics"

### Requirement: PREM-9 Save rate counts saved jobs
Analytics SHALL show "Save rate" = round(S / (S + F) x 100) % with S = the number of saved jobs and F = "Jobs in
feed" as shown on the same page (0 % when S + F = 0). (Formula from site code, docs/intent.md; relative to the
account's current state since 2026-11-10.)

#### Scenario: Save rate after one save
- **WHEN** after "Save a job" (PREM-5) the Premium test account counts S = the "Saved" buttons in the Saved tab and
  opens the Analytics tab
- **THEN** with F = the number shown next to "Jobs in feed", "Save rate" shows round(100 x S / (S + F)) %

### Requirement: PREM-10 The home page hides the pricing for a Premium user
For the signed-in Premium test account the home page `/` SHALL NOT render the pricing section `#pricing`, and its
header SHALL show the link "Dashboard" to `/dashboard`.

#### Scenario: Home page of a Premium user
- **WHEN** the Premium test account opens `/`
- **THEN** there is no element with the id `pricing` and the header link "Dashboard" has `href="/dashboard"`

### Requirement: PREM-11 Help me stand out shows a review of the job
"Help me stand out" on a card SHALL open a dialog titled "Help me stand out" that, after loading, shows the parts
"What to highlight" and "Keywords to use", the button "Copy" and the monthly counter "<n> tailored applications left
this month.". The part "Worth addressing" is shown only when the AI finds gaps and is not required. The text written
by the AI is not checked. Runs only with `-Dqa.ai=true`, at most once per run. (Seen live 2026-10-24 in the first
`-Dqa.ai` run: no "Worth addressing", counter "29 tailored applications left this month.")

#### Scenario: Stand-out review
- **WHEN** the Premium test account runs the suite with `-Dqa.ai=true` and clicks "Help me stand out" on the first
  card
- **THEN** within 90 s a dialog shows "Help me stand out", "What to highlight", "Keywords to use", the button "Copy"
  and a text matching "<n> tailored applications left this month."

### Requirement: PREM-12 Tailor my CV writes a tailored CV
"Tailor my CV" on a card SHALL open a dialog that, after loading, shows the badge "ATS-friendly", the label "Closely
tailored", the templates "Classic" and "Modern" and the buttons "Copy", ".txt" and "PDF"; "Copy" SHALL show the
notification "Copied". The text written by the AI is not checked. Runs only with `-Dqa.ai=true`, at most once per
run. (Seen live 2026-10-24 in the `-Dqa.ai` runs.)

#### Scenario: Tailored CV
- **WHEN** the Premium test account runs the suite with `-Dqa.ai=true`, clicks "Tailor my CV" on the first card and,
  once loaded, clicks "Copy"
- **THEN** within 90 s a dialog shows "ATS-friendly", "Closely tailored", "Classic", "Modern" and the buttons
  "Copy", ".txt" and "PDF", and after "Copy" the notification "Copied" is shown

### Requirement: PREM-13 Write Cover Letter writes a cover letter
"Write Cover Letter" on a card SHALL open a dialog "Cover letter" that, after loading, shows the templates
"Letterhead" and "Plain" and the button "Copy". The text written by the AI is not checked. Runs only with
`-Dqa.ai=true`, at most once per run. (Seen live 2026-10-24 in the `-Dqa.ai` runs.)

#### Scenario: Cover letter
- **WHEN** the Premium test account runs the suite with `-Dqa.ai=true` and clicks "Write Cover Letter" on the first
  card
- **THEN** within 90 s a dialog shows "Cover letter", "Letterhead", "Plain" and the button "Copy"

## REMOVED Requirements

### Requirement: PREM-1 A Free user starts the Premium trial in Plan & Billing
**Reason**: Since 2026-11-10 the button "Start 7-day free trial" in Plan & Billing opens a Stripe checkout (CI premium
run 2026-11-10, `settings-K0ouh2tF.js`); the Premium trial needs card data by design (human decision) and AGENTS.md
forbids any checkout.
**Migration**: The suite uses an existing Premium account (PREM-15, PREM-16).

### Requirement: PREM-2 Plan & Billing shows the active trial
**Reason**: No trial is started any more; the trial-only texts ("Free trial ends on", "No card on file") do not apply
to the owner-granted Premium account.
**Migration**: PREM-16 checks the Premium plan state.

### Requirement: PREM-14 An account with an active trial can be deleted
**Reason**: The permanent Premium account is never deleted; the premium suite no longer runs `JourneyCleanup`.
**Migration**: Deletion stays covered for the Free user by DEL-1 (`account-deletion`).
