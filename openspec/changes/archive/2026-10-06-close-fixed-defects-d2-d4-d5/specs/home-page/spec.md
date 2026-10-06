## MODIFIED Requirements

### Requirement: PUB-4 Pricing period toggle exposes its state
Each period button in `#pricing` SHALL expose its selection state to assistive technology with
`aria-pressed`: `"true"` on the selected period, `"false"` on the others.

#### Scenario: Selected period is aria-pressed
- **WHEN** a guest opens `/` and clicks the button "Monthly" in `#pricing`
- **THEN** the "Monthly" button has `aria-pressed="true"` and the "Yearly" button has `aria-pressed="false"`
