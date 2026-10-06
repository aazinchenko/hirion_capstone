# account-settings Specification

## Purpose

Account settings `/settings` of a signed-in user (test group SET): section buttons and deep links, the
Profile form with its required fields, and the Security form that changes the password.

## Requirements

### Requirement: SET-1 Settings sections and deep link
`/settings` SHALL show the sections Profile, Security and Plan & Billing as elements with role `button`
(not role `tab`). The deep link `/settings?section=security` SHALL open the Security section.

#### Scenario: Section buttons
- **WHEN** the signed-in test user opens `/settings`
- **THEN** the page shows the buttons "Profile", "Security" and "Plan & Billing"

#### Scenario: Security deep link
- **WHEN** the signed-in test user opens `/settings?section=security`
- **THEN** the page shows the field labelled exactly "Current password" and the button "Update password"

### Requirement: SET-2 Profile form
The Profile section SHALL show the button "Upload photo"; the required fields "First name", "Last name"
and "Email"; the fields "Phone", "Country" (combobox), "LinkedIn URL", "Headline" and "Short bio"; a CV
block with the link "View" (styled as a button, opens the CV file), the buttons "Re-analyse" and "Replace"
and no button to remove the CV; "Skills"; and the button "Save changes", which stays disabled until a field
is changed.

#### Scenario: Profile is rendered
- **WHEN** the signed-in test user opens `/settings` with the Profile section shown
- **THEN** the page shows the button "Upload photo", the fields "First name", "Last name", "Email",
  "Phone", "LinkedIn URL", "Headline" and "Short bio", the combobox "Country", the link "View", the buttons
  "Re-analyse", "Replace" and "Save changes", and the text "Skills"

#### Scenario: Required profile fields
- **WHEN** the signed-in test user opens `/settings` with the Profile section shown
- **THEN** the fields "First name", "Last name" and "Email" are marked required (`required` or
  `aria-required="true"`)

#### Scenario: CV cannot be removed
- **WHEN** the signed-in test user opens `/settings` with the Profile section shown
- **THEN** the CV block shows no button named "Remove" or "Delete"

### Requirement: SET-3 Profile shows no unsaved changes before an edit
Right after the Profile section loads, with no edit by the user, the page SHALL NOT show
"You have unsaved changes". Switching to another section without edits SHALL NOT open the prompt
"Unsaved changes -- Leave without saving?". (Known defect D9, not reproduced on a new Free user in three
live runs on 2026-09-30: it probably depends on the stored profile data, so both scenarios are regular
checks that turn red whenever the notice or the prompt appears.)

#### Scenario: Fresh profile has no unsaved-changes notice
- **WHEN** the signed-in test user opens `/settings` with the Profile section shown and makes no edit
- **THEN** the text "You have unsaved changes" is not visible

#### Scenario: Switching section without edits
- **WHEN** the signed-in test user opens `/settings` with the Profile section shown, makes no edit and
  clicks the button "Security"
- **THEN** no dialog with the text "Leave without saving?" is shown and the field labelled exactly
  "Current password" is visible

### Requirement: SET-4 Invalid profile email is rejected and not saved
The field "Email" in Profile SHALL reject a value that is not an email address: after "Save changes"
the page SHALL show an error for the field and SHALL NOT send or save the invalid value.

#### Scenario: Invalid email is not sent
- **WHEN** the signed-in test user opens `/settings` with the Profile section shown, replaces "Email" with
  "not-an-email" and clicks "Save changes"
- **THEN** no non-GET request whose body contains "not-an-email" leaves the browser, and the field
  "Email" shows an error

### Requirement: SET-5 Security form changes the password
The Security section SHALL show the fields "Current password", "New password" and "Confirm new password",
each with a button "Show password", and the button "Update password". After a successful update the
notification "Password updated" SHALL be shown and the new password SHALL sign the user in on `/login`.

#### Scenario: Security form is rendered
- **WHEN** the signed-in test user opens `/settings?section=security`
- **THEN** the page shows the fields labelled exactly "Current password", "New password" and
  "Confirm new password", three buttons "Show password" and the button "Update password"

#### Scenario: New password signs in
- **WHEN** the signed-in test user fills "Current password" with the current password, "New password" and
  "Confirm new password" with a new password, clicks "Update password", and then a fresh guest signs in on
  `/login` with the test user's email and the new password
- **THEN** the notification "Password updated" was shown and the browser URL path after "Sign in" is
  `/dashboard`

### Requirement: SET-6 Profile fields are saved
Filling profile fields and clicking "Save changes" SHALL show the notification "Profile updated", and the
values SHALL still be shown after the page is reloaded. "Phone" is free-form text by design (decision
2026-09-30) and "LinkedIn URL" is not validated by design (docs/intent.md). The site stores "Phone"
together with the country code and without spaces ("79 000 00 00" is saved as "+41790000000" and shown as
"790000000", live run 2026-09-30), so the scenario uses a phone number without spaces.

#### Scenario: Profile survives a reload
- **WHEN** the signed-in test user opens `/settings` with the Profile section shown, fills "Phone" with
  "790000000", "LinkedIn URL" with "https://www.linkedin.com/in/qa-tester-example", "Headline" with
  "QA Automation Engineer (test account)" and "Short bio" with "Automated test account. Created and deleted
  by Playwright.", clicks "Save changes" and reloads the page
- **THEN** the notification "Profile updated" was shown and the four fields show the values that were filled

### Requirement: SET-8 Plan & Billing shows the Free plan
For a user on the Free plan the section Plan & Billing SHALL say "You're currently on the Free plan." and
mark the Free plan with the button "Current plan".

#### Scenario: Free plan is shown
- **WHEN** the signed-in Free test user opens `/settings?section=plan`
- **THEN** the page shows the text "You're currently on the Free plan." and the button "Current plan"
