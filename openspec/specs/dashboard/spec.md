# dashboard Specification

## Purpose

Dashboard `/dashboard` of a signed-in user (test group DASH): for a Free user the locked Job Feed teaser,
the Saved / Applied / Archive lists, the Pro gate of Analytics, and job preferences.

## Requirements

### Requirement: DASH-1 Dashboard heading and tab buttons
`/dashboard` SHALL show the heading level 1 "Welcome back, <first name>" and the tabs Job Feed, Saved,
Applied, Archive, Analytics and Preferences as elements with role `button` (not role `tab`). For a user
without Pro the Analytics button also carries the badge "Pro", so its accessible name is "Analytics Pro".

#### Scenario: Dashboard is rendered
- **WHEN** the signed-in test user opens `/dashboard`
- **THEN** the page shows the heading level 1 "Welcome back, <first name of the test user>" and the
  buttons "Job Feed", "Saved", "Applied", "Archive", "Analytics Pro" (the Free user's name of the Analytics
  button) and "Preferences"

### Requirement: DASH-2 Free Job Feed is a locked teaser
For a user on the Free plan the Job Feed SHALL show the matched jobs only as a locked teaser: the heading
"We found <N> jobs for you", N rows that are blurred and hidden from assistive technology
(`aria-hidden="true"`), each row with a match percentage, the text starting with "Unlock them with Basic", and
the links "Unlock with Basic" and "See all plans", both pointing to `/settings?section=plan`. A Free user's Job
Feed SHALL NOT show the job actions "View job", "Save" or "Hide". (Seen live 2026-10-20 after the site's new
plans; replaces "DASH-2 Job card actions". The server sends a Free user's jobs as locked entries without
company and URL.)

#### Scenario: Locked teaser for a Free user
- **WHEN** the signed-in Free test user opens `/dashboard` with the Job Feed shown
- **THEN** the page shows the heading "We found <N> jobs for you" and N match percentages inside an element
  with `aria-hidden="true"` whose rows are blurred, the text starting with "Unlock them with Basic", and the
  links "Unlock with Basic" and "See all plans" with `href="/settings?section=plan"` (neither is clicked), and
  there is no button "View job", "Save" or "Hide"

### Requirement: DASH-3 Empty lists of a new user
For a user who has not saved, applied to or archived any job, the tabs SHALL show the empty states:
Saved "No saved jobs yet", Applied "No applications tracked yet", Archive "No archived jobs yet".

#### Scenario: Empty Saved, Applied and Archive
- **WHEN** the freshly registered test user opens `/dashboard` and clicks the buttons "Saved", "Applied"
  and "Archive" in turn
- **THEN** the page shows "No saved jobs yet", "No applications tracked yet" and "No archived jobs yet"
  respectively

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

### Requirement: DASH-12 Preferences are saved
Adding a value to a preference and clicking "Save preferences" SHALL show the notification
"Preferences saved", and the value SHALL still be selected after the page is reloaded.

#### Scenario: Work mode survives a reload
- **WHEN** the signed-in test user opens the Preferences tab, adds "Remote" to "Work mode", clicks
  "Save preferences" and reloads `/dashboard` and opens Preferences again
- **THEN** the notification "Preferences saved" was shown and "Work mode" still shows "Remote"
