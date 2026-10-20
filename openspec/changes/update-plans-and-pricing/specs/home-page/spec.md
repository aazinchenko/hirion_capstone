## ADDED Requirements

### Requirement: PUB-3 Pricing shows plans per billing period
The pricing section `#pricing` on `/` SHALL be rendered for guests and Free users (it is hidden for Pro
users). It SHALL offer the period buttons "Monthly" and "Yearly", SHALL select "Yearly" by default, and SHALL
show the plans Free ("CHF 0"), Basic and Premium. For the selected period the paid plans SHALL show their
price and unit: Yearly: Basic "CHF 10" and Premium "CHF 120", each with "/year"; Monthly: Basic "CHF 3" and
Premium "CHF 15", each with "/month". (Seen live 2026-10-19; replaces "PUB-3 Pricing shows prices per billing
period" after the site removed the period "Quarterly" and the "billed ..." lines.)

#### Scenario: Pricing is visible to a guest
- **WHEN** a guest with no stored session opens `/`
- **THEN** the section `#pricing` is visible with the buttons "Monthly" and "Yearly" and the plan names
  "Free", "Basic" and "Premium"

#### Scenario: Yearly is the default period
- **WHEN** a guest opens `/` and scrolls to `#pricing` without clicking any period
- **THEN** `#pricing` shows the texts "CHF 0", "CHF 10", "CHF 120" and "/year"

#### Scenario: Monthly price
- **WHEN** a guest opens `/` and clicks the button "Monthly" in `#pricing`
- **THEN** `#pricing` shows the texts "CHF 3", "CHF 15" and "/month" and does not show "CHF 120"

#### Scenario: Switching back to Yearly
- **WHEN** a guest opens `/`, clicks "Monthly", then clicks "Yearly" in `#pricing`
- **THEN** `#pricing` shows the texts "CHF 10", "CHF 120" and "/year"

## MODIFIED Requirements

### Requirement: PUB-2 FAQ items expand as an accordion
Each FAQ item in the section `#faq` on `/` SHALL be a `<button>` whose `aria-expanded` attribute
reflects whether its answer is shown: `false` when collapsed, `true` after the visitor clicks it.
The expanded answer SHALL be visible.

#### Scenario: FAQ item starts collapsed
- **WHEN** a guest opens `/` and scrolls to the FAQ section `#faq`
- **THEN** the button "Is Hirion free to use?" has `aria-expanded="false"`

#### Scenario: Clicking a FAQ item expands it
- **WHEN** a guest opens `/` and clicks the button "Is Hirion free to use?" in `#faq`
- **THEN** that button has `aria-expanded="true"`

#### Scenario: Expanded FAQ item shows its answer
- **WHEN** a guest opens `/` and clicks the button "Is Hirion free to use?" in `#faq`
- **THEN** `#faq` shows visible text starting with "Get started for free and upgrade whenever you need"

### Requirement: PUB-5 Plan cards link to signup
Each plan card in `#pricing` SHALL have a call to action link that opens signup with the plan
preselected: "Start free" SHALL point to `/signup?plan=free`, "Choose Basic" SHALL point to
`/signup?plan=basic` and "Start 7-day free trial" SHALL point to `/signup?plan=trial`. Following a CTA SHALL
NOT create an account, start a trial or start a payment by itself.

#### Scenario: Free plan CTA
- **WHEN** a guest opens `/` and clicks the link "Start free" in `#pricing`
- **THEN** the browser URL path is `/signup` and its query contains `plan=free`

#### Scenario: Basic plan CTA
- **WHEN** a guest opens `/` and clicks the link "Choose Basic" in `#pricing`
- **THEN** the browser URL path is `/signup` and its query contains `plan=basic`

#### Scenario: Paid plan CTA
- **WHEN** a guest opens `/` and clicks the link "Start 7-day free trial" in `#pricing`
- **THEN** the browser URL path is `/signup` and its query contains `plan=trial`

## REMOVED Requirements

### Requirement: PUB-3 Pricing shows prices per billing period
**Reason**: Seen live 2026-10-19: the site removed the period "Quarterly" and the "billed ..." lines and
now shows three plans. A MODIFIED block cannot drop the scenario "Quarterly price", so the requirement is
replaced by "PUB-3 Pricing shows plans per billing period".
**Migration**: `SmokeTest.pub3_quarterlyPrice` is deleted; the other `pub3_*` tests move to the new
requirement with the new prices.
