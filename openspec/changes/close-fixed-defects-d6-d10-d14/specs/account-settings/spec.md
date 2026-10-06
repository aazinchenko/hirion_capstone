## MODIFIED Requirements

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
