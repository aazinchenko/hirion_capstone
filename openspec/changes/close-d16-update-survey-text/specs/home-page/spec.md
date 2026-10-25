## MODIFIED Requirements

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
