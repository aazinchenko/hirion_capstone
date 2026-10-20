## RENAMED Requirements

- FROM: `### Requirement: REG-7 Step 4 offers Premium Trial and Free`
- TO: `### Requirement: REG-7 Step 4 offers Free, Basic and Premium`

## MODIFIED Requirements

### Requirement: REG-7 Step 4 offers Free, Basic and Premium
Step 4 SHALL show "Step 4 of 4", the title "Choose your plan", the period options "Monthly" and "Yearly", the
plan cards Free, Basic and Premium (Premium with the text "7 days free") and the button "Back". Each plan card
SHALL expose whether it is selected with `aria-pressed` (`"true"` on the selected card, `"false"` on the
others). The submit button SHALL say what happens next: "Join for free" with Free selected, "Start my free
trial" with Premium selected, "Continue to payment" with Basic selected.
When `/signup` is opened without a `plan` parameter, Premium SHALL be the selected card. With `?plan=free`
Free SHALL be selected, with `?plan=trial` Premium, and with `?plan=basic` Basic. (Known defect D16, seen
live 2026-10-20: with `?plan=basic` the site selects Premium.) "Back" on step 4 SHALL return to step 3
without losing the wizard data: the chosen target role stays, and "Continue" SHALL lead to step 4 again
with the chosen plan still selected. (Seen live 2026-10-19 and 2026-10-20 by guest runs that never clicked
the submit button.)

#### Scenario: Step 4 is rendered with Premium Trial selected
- **WHEN** a guest on step 3 of `/signup` adds one target role and clicks "Continue"
- **THEN** the page shows "Step 4 of 4", the text "Choose your plan", the text "7 days free", the cards Free,
  Basic and Premium and the buttons "Back" and "Start my free trial"; Premium has `aria-pressed="true"`, Free
  and Basic have `aria-pressed="false"`

#### Scenario: Free can be selected
- **WHEN** a guest on step 4 of `/signup` selects the card Free
- **THEN** Free has `aria-pressed="true"`, Premium has `aria-pressed="false"` and the button "Join for free"
  is shown

#### Scenario: Basic can be selected
- **WHEN** a guest on step 4 of `/signup` selects the card Basic
- **THEN** Basic has `aria-pressed="true"`, Premium has `aria-pressed="false"` and the button "Continue to
  payment" is shown (never clicked)

#### Scenario: plan=free preselects Free
- **WHEN** a guest opens `/signup?plan=free`, completes steps 1-3 and reaches the page showing
  "Step 4 of 4"
- **THEN** Free has `aria-pressed="true"` and Premium has `aria-pressed="false"`

#### Scenario: plan=trial preselects Premium Trial
- **WHEN** a guest opens `/signup?plan=trial`, completes steps 1-3 and reaches the page showing
  "Step 4 of 4"
- **THEN** Premium has `aria-pressed="true"` and Free has `aria-pressed="false"`

#### Scenario: plan=basic preselects Basic
- **WHEN** a guest opens `/signup?plan=basic`, completes steps 1-3 and reaches the page showing
  "Step 4 of 4"
- **THEN** Basic has `aria-pressed="true"` (known defect D16: today Premium is selected)

#### Scenario: Back keeps the wizard data
- **WHEN** a guest on step 4 of `/signup` who added the target role "QA / Test Engineer" on step 3
  selects the card Free, clicks "Back", and then clicks "Continue"
- **THEN** after "Back" the page shows "Step 3 of 4" and the chosen role "QA / Test Engineer", and after
  "Continue" the page shows "Step 4 of 4" with Free `aria-pressed="true"` and Premium `aria-pressed="false"`

### Requirement: REG-8 The account is created only on step 4
Steps 1-3 of `/signup` SHALL NOT create an account: the only request to the site's backend before
the submit button of step 4 is clicked SHALL be the email-availability check of step 1.

#### Scenario: Steps 1-3 send no account data
- **WHEN** a guest goes from step 1 to step 4 of `/signup` without clicking the submit button of step 4
- **THEN** no non-GET request to hirion.ch was sent other than the email-availability check made by
  step 1 "Continue"

### Requirement: REG-9 Signup finishes on the dashboard without email confirmation
Clicking "Join for free" on step 4 with Free selected SHALL create the account and sign the user in
without an email confirmation step: the browser SHALL land on `/dashboard` with the heading level 1
"Welcome back, <first name>".

#### Scenario: Free signup lands on the dashboard
- **WHEN** a guest completes steps 1-3 of `/signup` with the disposable test user, selects Free on step 4
  and clicks "Join for free"
- **THEN** the browser URL path is `/dashboard` and the page shows the heading level 1
  "Welcome back, <first name of the test user>"
