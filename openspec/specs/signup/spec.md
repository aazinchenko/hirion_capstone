# signup Specification

## Purpose

Signup wizard on `/signup` (test group REG, steps 1-4 after the step 1 checks REG-1 / REG-2 in
`access-and-quality`) and signing in with the registered account (plan AUTH-03): a visitor creates a
Free account and lands on the dashboard without email confirmation.

## Requirements

### Requirement: REG-3 Step 1 rejects a missing email or a short password
On `/signup` step 1, with first and last name filled, clicking "Continue" while the email is empty or the
password is shorter than 8 characters SHALL NOT advance the wizard: the page SHALL show the text
"Enter an email and a password of at least 8 characters." and stay on "Step 1 of 4".

#### Scenario: Short password is blocked
- **WHEN** a guest opens `/signup`, fills "First name" and "Last name", fills "Email" with a
  `+hirion-qa-<digits>@` address, fills the field labelled exactly "Password" with "short7!" (7 characters)
  and clicks "Continue"
- **THEN** the page shows the text "Enter an email and a password of at least 8 characters." and
  still shows "Step 1 of 4"

### Requirement: REG-4 Step 1 rejects an email that already has an account
On `/signup` step 1, clicking "Continue" with the email of an existing account SHALL NOT advance the
wizard: the page SHALL show the text "An account with this email already exists. Sign in instead."
and stay on "Step 1 of 4".

#### Scenario: Taken email is blocked
- **WHEN** a guest opens `/signup`, fills step 1 with the email of the already registered test user and a
  valid password, and clicks "Continue"
- **THEN** the page shows the text "An account with this email already exists. Sign in instead." and
  still shows "Step 1 of 4"

### Requirement: REG-5 Step 2 asks for a CV
Step 2 of the wizard SHALL show "Step 2 of 4", the title "Upload your CV" and the hint
"PDF, DOCX, TXT up to 10MB". Clicking "Continue" without a file SHALL NOT advance the wizard and SHALL
show "Please upload your CV to continue. It is what powers your job matches.".

#### Scenario: Step 2 is rendered
- **WHEN** a guest completes step 1 of `/signup` with valid values and clicks "Continue"
- **THEN** the page shows "Step 2 of 4", the text "Upload your CV" and the text "PDF, DOCX, TXT up to 10MB"

#### Scenario: Step 2 without a file is blocked
- **WHEN** a guest on step 2 of `/signup` clicks "Continue" without uploading a file
- **THEN** the page shows the text "Please upload your CV to continue. It is what powers your job matches."
  and still shows "Step 2 of 4"

### Requirement: REG-6 Step 3 asks for target roles
After a CV is uploaded on step 2, step 3 SHALL show "Step 3 of 4" and the title
"What roles are you looking for?". Clicking "Continue" without a role SHALL NOT advance the wizard and
SHALL show "Please add at least one target role to continue.".

#### Scenario: Step 3 is rendered
- **WHEN** a guest on step 2 of `/signup` uploads the CV fixture and clicks "Continue"
- **THEN** the page shows "Step 3 of 4" and the text "What roles are you looking for?"

#### Scenario: Step 3 without a role is blocked
- **WHEN** a guest on step 3 of `/signup` clicks "Continue" without adding a role
- **THEN** the page shows the text "Please add at least one target role to continue." and still shows
  "Step 3 of 4"

### Requirement: REG-7 Step 4 offers Premium Trial and Free
Step 4 SHALL show "Step 4 of 4", the title "Choose your plan", the options Premium Trial (with the text
"7 days free") and Free, the button "Back" and the button "See my matches". When `/signup` is opened
without a `plan` parameter, Premium Trial SHALL be the selected option. The visitor SHALL be able to
select Free before clicking "See my matches".
When `/signup` is opened with `?plan=free`, Free SHALL be the selected option on step 4; with
`?plan=trial`, Premium Trial SHALL be the selected option. "Back" on step 4 SHALL return to step 3
without losing the wizard data: the chosen target role stays, and "Continue" SHALL lead to step 4 again
with the chosen plan still selected. (Confirmed live 2026-09-30 by a guest probe, docs/intent.md;
"selected" is the visual state of the card until defect D12 is fixed.)

#### Scenario: Step 4 is rendered with Premium Trial selected
- **WHEN** a guest on step 3 of `/signup` adds one target role and clicks "Continue"
- **THEN** the page shows "Step 4 of 4", the text "Choose your plan", the text "7 days free", the option
  Free, the buttons "Back" and "See my matches", and Premium Trial is the selected option

#### Scenario: Free can be selected
- **WHEN** a guest on step 4 of `/signup` selects the option Free
- **THEN** Free is the selected option and Premium Trial is not selected

#### Scenario: plan=free preselects Free
- **WHEN** a guest opens `/signup?plan=free`, completes steps 1-3 and reaches the page showing
  "Step 4 of 4"
- **THEN** Free is the selected option and Premium Trial is not selected

#### Scenario: plan=trial preselects Premium Trial
- **WHEN** a guest opens `/signup?plan=trial`, completes steps 1-3 and reaches the page showing
  "Step 4 of 4"
- **THEN** Premium Trial is the selected option and Free is not selected

#### Scenario: Back keeps the wizard data
- **WHEN** a guest on step 4 of `/signup` who added the target role "QA / Test Engineer" on step 3
  selects the option Free, clicks "Back", and then clicks "Continue"
- **THEN** after "Back" the page shows "Step 3 of 4" and the chosen role "QA / Test Engineer", and after
  "Continue" the page shows "Step 4 of 4" with Free as the selected option and Premium Trial not selected

### Requirement: REG-8 The account is created only on step 4
Steps 1-3 of `/signup` SHALL NOT create an account: the only request to the site's backend before
"See my matches" SHALL be the email-availability check of step 1.

#### Scenario: Steps 1-3 send no account data
- **WHEN** a guest goes from step 1 to step 4 of `/signup` without clicking "See my matches"
- **THEN** no non-GET request to hirion.ch was sent other than the email-availability check made by
  step 1 "Continue"

### Requirement: REG-9 Signup finishes on the dashboard without email confirmation
Clicking "See my matches" on step 4 with Free selected SHALL create the account and sign the user in
without an email confirmation step: the browser SHALL land on `/dashboard` with the heading level 1
"Welcome back, <first name>".

#### Scenario: Free signup lands on the dashboard
- **WHEN** a guest completes steps 1-3 of `/signup` with the disposable test user, selects Free on step 4
  and clicks "See my matches"
- **THEN** the browser URL path is `/dashboard` and the page shows the heading level 1
  "Welcome back, <first name of the test user>"

### Requirement: REG-10 Show password is reachable by keyboard
On `/signup` step 1 the button "Show password" SHALL be reachable with the Tab key, that is, it SHALL NOT
have `tabindex="-1"`.

#### Scenario: Show password is in the tab order
- **WHEN** a guest opens `/signup`
- **THEN** the button "Show password" next to the field labelled exactly "Password" does not have the
  attribute `tabindex="-1"`

### Requirement: AUTH-6 Wrong password keeps the user on the login page
On `/login`, submitting the email of an existing account with a wrong password SHALL NOT sign the user
in: the browser SHALL stay on `/login`, which still shows the heading level 1 "Welcome back", and the
page SHALL show an error message.

#### Scenario: Registered test user with a wrong password
- **WHEN** a guest opens `/login`, fills the field "Email" with the registered test user's email and the
  field labelled exactly "Password" with a wrong password, and clicks "Sign in"
- **THEN** the browser URL path is still `/login`, the page shows the heading level 1 "Welcome back" and
  an error message is visible
