## Purpose

Dashboard `/dashboard` of a signed-in user (test group DASH): the job feed with Save and the Hide menu,
the Saved / Applied / Archive lists, Analytics with the Save rate, and job preferences.

## ADDED Requirements

### Requirement: DASH-1 Dashboard heading and tab buttons
`/dashboard` SHALL show the heading level 1 "Welcome back, <first name>" and the tabs Job Feed, Saved,
Applied, Archive, Analytics and Preferences as elements with role `button` (not role `tab`).

#### Scenario: Dashboard is rendered
- **WHEN** the signed-in test user opens `/dashboard`
- **THEN** the page shows the heading level 1 "Welcome back, <first name of the test user>" and the
  buttons "Job Feed", "Saved", "Applied", "Archive", "Analytics" and "Preferences"

### Requirement: DASH-2 Job card actions
Every job card in the Job Feed SHALL show a match score "NN MATCH" and the buttons "View job", "Save",
"Tailor my CV", "Write Cover Letter" and "Hide". The button "Hide" SHALL have `aria-haspopup="menu"`.

#### Scenario: First job card
- **WHEN** the signed-in test user opens `/dashboard` with the Job Feed shown
- **THEN** the first job card shows a text matching "<digits> MATCH", the buttons "View job", "Save",
  "Tailor my CV", "Write Cover Letter" and "Hide", and "Hide" has `aria-haspopup="menu"`

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

### Requirement: DASH-7 Analytics sections
The Analytics tab SHALL show three sections: "Your performance" with the metrics "Total active jobs",
"Avg match score", "Save rate" and "Jobs in feed"; "Swiss market intelligence"; and
"Positioning & opportunity". It SHALL NOT show counters of saved, applied or hidden jobs.

#### Scenario: Analytics is rendered
- **WHEN** the signed-in test user opens `/dashboard` and clicks the button "Analytics"
- **THEN** the page shows the texts "Your performance", "Total active jobs", "Avg match score",
  "Save rate", "Jobs in feed", "Swiss market intelligence" and "Positioning & opportunity"

### Requirement: DASH-8 Save rate follows saved and feed jobs
The metric "Save rate" SHALL equal round(saved / (saved + jobs in feed) * 100) %, where saved is the
number of saved jobs and jobs in feed is the value of "Jobs in feed". Example: a feed of 10 jobs, Save
on one: "Jobs in feed" 9, "Save rate" 10%.

#### Scenario: Save rate after one saved job
- **WHEN** the signed-in test user has exactly one saved job and opens the Analytics tab
- **THEN** with N the number shown for "Jobs in feed", "Save rate" shows round(1 / (1 + N) * 100) followed by "%"

### Requirement: DASH-9 Preferences form
The Preferences tab SHALL show the comboboxes "Roles", "Work mode", "Locations", "Industries",
"Company type", "Employment eligibility", "Seniority" and "Languages", and the button "Save preferences".

#### Scenario: Preferences is rendered
- **WHEN** the signed-in test user opens `/dashboard` and clicks the button "Preferences"
- **THEN** the page shows comboboxes named "Roles", "Work mode", "Locations", "Industries",
  "Company type", "Employment eligibility", "Seniority" and "Languages" and the button "Save preferences"

### Requirement: DASH-10 Job titles show their characters
Job titles in the Job Feed SHALL show every character of the original title. A character SHALL NOT be
replaced by "?" (for example "Senior Azure Engineer | ? oder ... Next Level ??").

#### Scenario: No replacement characters in job titles
- **WHEN** the signed-in test user opens `/dashboard` with the Job Feed shown
- **THEN** no job card title contains a "?" that stands in for a letter (a "?" next to a letter, a space
  or another "?")
