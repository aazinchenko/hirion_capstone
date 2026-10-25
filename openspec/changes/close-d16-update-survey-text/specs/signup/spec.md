## MODIFIED Requirements

### Requirement: REG-7 Step 4 offers Free, Basic and Premium
Step 4 SHALL show "Step 4 of 4", the title "Choose your plan", the period options "Monthly" and "Yearly", the
plan cards Free, Basic and Premium (Premium with the text "7 days free") and the button "Back". Each plan card
SHALL expose whether it is selected with `aria-pressed` (`"true"` on the selected card, `"false"` on the
others). The submit button SHALL say what happens next: "Join for free" with Free selected, "Start my free
trial" with Premium selected, "Continue to payment" with Basic selected.
When `/signup` is opened without a `plan` parameter, Premium SHALL be the selected card. With `?plan=free`
Free SHALL be selected, with `?plan=trial` Premium, and with `?plan=basic` Basic. (Known defect D16 -- the
signup page dropped `plan=basic` and selected Premium, seen live 2026-10-20 -- was fixed by the site by
2026-10-25: `plan=basic` stays in the URL and Basic is selected.) "Back" on step 4 SHALL return to step 3
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
- **THEN** Basic has `aria-pressed="true"` and Premium has `aria-pressed="false"`

#### Scenario: Back keeps the wizard data
- **WHEN** a guest on step 4 of `/signup` who added the target role "QA / Test Engineer" on step 3
  selects the card Free, clicks "Back", and then clicks "Continue"
- **THEN** after "Back" the page shows "Step 3 of 4" and the chosen role "QA / Test Engineer", and after
  "Continue" the page shows "Step 4 of 4" with Free `aria-pressed="true"` and Premium `aria-pressed="false"`
