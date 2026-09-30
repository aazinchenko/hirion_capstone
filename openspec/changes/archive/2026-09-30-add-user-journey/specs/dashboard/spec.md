## Purpose

Dashboard `/dashboard` of a signed-in user (test group DASH): the job feed with Save and the Hide menu,
the Saved / Applied / Archive lists, the Pro gate of Analytics for a Free user, and job preferences.

## ADDED Requirements

### Requirement: DASH-1 Dashboard heading and tab buttons
`/dashboard` SHALL show the heading level 1 "Welcome back, <first name>" and the tabs Job Feed, Saved,
Applied, Archive, Analytics and Preferences as elements with role `button` (not role `tab`). For a user
without Pro the Analytics button also carries the badge "Pro", so its accessible name is "Analytics Pro".

#### Scenario: Dashboard is rendered
- **WHEN** the signed-in test user opens `/dashboard`
- **THEN** the page shows the heading level 1 "Welcome back, <first name of the test user>" and the
  buttons "Job Feed", "Saved", "Applied", "Archive", "Analytics Pro" (the Free user's name of the Analytics
  button) and "Preferences"

### Requirement: DASH-2 Job card actions
Every job card in the Job Feed SHALL show a match score badge -- a number followed by the label "match",
which the page renders in capitals ("87 MATCH") -- and the buttons "View job", "Save" and "Hide". The button
"Hide" SHALL have `aria-haspopup="menu"`. The AI buttons "Tailor my CV", "Write Cover Letter" and
"Help me stand out" are Pro-only: a Free user's cards SHALL NOT show them.

#### Scenario: First job card
- **WHEN** the signed-in test user opens `/dashboard` with the Job Feed shown
- **THEN** the first job card shows a match score (a number followed by "match", in any letter case), the buttons "View job", "Save"
  and "Hide", "Hide" has `aria-haspopup="menu"`, and there is no button "Tailor my CV", "Write Cover Letter"
  or "Help me stand out" on the card

### Requirement: DASH-3 Empty lists of a new user
For a user who has not saved, applied to or archived any job, the tabs SHALL show the empty states:
Saved "No saved jobs yet", Applied "No applications tracked yet", Archive "No archived jobs yet".

#### Scenario: Empty Saved, Applied and Archive
- **WHEN** the freshly registered test user opens `/dashboard` and clicks the buttons "Saved", "Applied"
  and "Archive" in turn
- **THEN** the page shows "No saved jobs yet", "No applications tracked yet" and "No archived jobs yet"
  respectively

### Requirement: DASH-4 Save moves a job to Saved
Clicking "Save" on a job card SHALL remove the job from the Job Feed and show it in the Saved tab.

#### Scenario: Save the first job
- **WHEN** the signed-in test user clicks "Save" on the first job card in the Job Feed and then clicks
  the button "Saved"
- **THEN** that job title is no longer in the Job Feed and the Saved tab shows a card with that job title

### Requirement: DASH-5 Already applied moves a job to Applied
Clicking "Hide" on a job card SHALL open a menu (role `menu`) with the menu items "Already applied" and
"Not relevant". Choosing "Already applied" SHALL remove the job from the Job Feed and show it in the
Applied tab.

#### Scenario: Hide menu items
- **WHEN** the signed-in test user clicks "Hide" on a job card in the Job Feed
- **THEN** a menu is shown with the menu items "Already applied" and "Not relevant"

#### Scenario: Mark a job as already applied
- **WHEN** the signed-in test user clicks "Hide" on a job card and chooses the menu item "Already applied",
  then clicks the button "Applied"
- **THEN** that job title is no longer in the Job Feed and the Applied tab shows a card with that job title

### Requirement: DASH-6 Not relevant removes a job from the feed
Choosing "Not relevant" in the Hide menu SHALL remove the job from the Job Feed.

#### Scenario: Mark a job as not relevant
- **WHEN** the signed-in test user clicks "Hide" on a job card and chooses the menu item "Not relevant"
- **THEN** that job title is no longer in the Job Feed

### Requirement: DASH-7 Analytics is Pro-only for a Free user
For a user on the Free plan the Analytics tab SHALL show the Pro gate: the heading "Unlock Hirion Pro",
the text "See your match analytics, Swiss salary benchmarks, and personalized opportunities to land roles
faster." and the link "Upgrade to Pro" to `/settings?section=plan`. The analytics content behind the gate
SHALL be hidden from assistive technology (`aria-hidden="true"`).

#### Scenario: Free user sees the Pro gate on Analytics
- **WHEN** the signed-in Free test user opens `/dashboard` and clicks the button "Analytics"
- **THEN** the page shows the heading "Unlock Hirion Pro", the text "See your match analytics, Swiss salary
  benchmarks, and personalized opportunities to land roles faster." and the link "Upgrade to Pro" with
  `href="/settings?section=plan"`

### Requirement: DASH-9 Preferences form
The Preferences tab SHALL show the sections with the headings "Roles", "Work mode", "Locations",
"Industries", "Company type", "Employment eligibility", "Seniority" and "Languages", each with a picker
(role `combobox`), and the button "Save preferences". The pickers themselves have no accessible name
(candidate defect D13).

#### Scenario: Preferences is rendered
- **WHEN** the signed-in test user opens `/dashboard` and clicks the button "Preferences"
- **THEN** the page shows the headings "Roles", "Work mode", "Locations", "Industries", "Company type",
  "Employment eligibility", "Seniority" and "Languages" and the button "Save preferences"

### Requirement: DASH-10 Job titles show their characters
Job titles in the Job Feed SHALL show every character of the original title. A character SHALL NOT be
replaced by "?" (for example "Senior Azure Engineer | ? oder ... Next Level ??"). (Known defect D8,
not reproduced in the feed of a new Free user on 2026-09-30: it depends on which job ads are in the feed,
so the scenario is a regular check that turns red whenever such a title appears.)

#### Scenario: No replacement characters in job titles
- **WHEN** the signed-in test user opens `/dashboard` with the Job Feed shown
- **THEN** no job card title contains a "?" that stands in for a letter (a "?" next to a letter, a space
  or another "?")

### Requirement: DASH-11 View job opens the vacancy in a new tab
Clicking the button "View job" on a job card SHALL open the vacancy in a new browser tab on the external
site of the job ad (not hirion.ch), and the dashboard SHALL stay open in its own tab.

#### Scenario: View job opens an external tab
- **WHEN** the signed-in test user clicks "View job" on the first job card in the Job Feed
- **THEN** a new tab opens whose URL host is not hirion.ch, and the original tab still has the URL path `/dashboard`

### Requirement: DASH-12 Preferences are saved
Adding a value to a preference and clicking "Save preferences" SHALL show the notification
"Preferences saved", and the value SHALL still be selected after the page is reloaded.

#### Scenario: Work mode survives a reload
- **WHEN** the signed-in test user opens the Preferences tab, adds "Remote" to "Work mode", clicks
  "Save preferences" and reloads `/dashboard` and opens Preferences again
- **THEN** the notification "Preferences saved" was shown and "Work mode" still shows "Remote"
