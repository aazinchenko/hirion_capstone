## MODIFIED Requirements

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
