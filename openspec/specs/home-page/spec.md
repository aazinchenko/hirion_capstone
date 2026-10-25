# home-page Specification

## Purpose

Public home page of hirion.ch (test group PUB): a visitor can start signup from the hero, read the FAQ,
compare prices per billing period and open signup from a plan card.

## Requirements

### Requirement: PUB-1 Hero call to action opens signup
The home page `/` SHALL show a primary hero link "Get my personalized jobs" that, for a guest, points to
the signup page `/signup`.

#### Scenario: Guest clicks the hero CTA
- **WHEN** a guest with no stored session opens `/` and clicks the link "Get my personalized jobs"
- **THEN** the browser URL path is `/signup`

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

### Requirement: PUB-4 Pricing period toggle exposes its state
Each period button in `#pricing` SHALL expose its selection state to assistive technology with
`aria-pressed`: `"true"` on the selected period, `"false"` on the others.

#### Scenario: Selected period is aria-pressed
- **WHEN** a guest opens `/` and clicks the button "Monthly" in `#pricing`
- **THEN** the "Monthly" button has `aria-pressed="true"` and the "Yearly" button has `aria-pressed="false"`

### Requirement: PUB-5 Plan cards link to signup
Each plan card in `#pricing` SHALL have a call to action link that opens signup with the plan
preselected: "Start free" SHALL point to `/signup?plan=free`, "Choose Basic" SHALL point to
`/signup?plan=basic` and "Start 7-day free trial" SHALL point to `/signup?plan=trial`. Following a CTA SHALL
NOT create an account, start a trial or start a payment by itself. (Since the fix of D16, seen 2026-10-25,
following "Choose Basic" keeps `plan=basic` in the signup URL.)

#### Scenario: Free plan CTA
- **WHEN** a guest opens `/` and clicks the link "Start free" in `#pricing`
- **THEN** the browser URL path is `/signup` and its query contains `plan=free`

#### Scenario: Basic plan CTA
- **WHEN** a guest opens `/` and clicks the link "Choose Basic" in `#pricing`
- **THEN** the browser URL path is `/signup` and its query contains `plan=basic`

#### Scenario: Paid plan CTA
- **WHEN** a guest opens `/` and clicks the link "Start 7-day free trial" in `#pricing`
- **THEN** the browser URL path is `/signup` and its query contains `plan=trial`
