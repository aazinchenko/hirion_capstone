## Purpose

Public footer and legal pages of hirion.ch (test group FOOT): a visitor can reach the privacy policy,
the terms of service, the imprint and the contact page from the footer of any public page, and the
imprint names the operator completely.

## ADDED Requirements

### Requirement: FOOT-1 Footer links to legal pages and contact
The footer (landmark `contentinfo`) of the home page `/` SHALL contain the links "Privacy" to `/privacy`,
"Terms" to `/terms`, "Imprint" to `/imprint` and "Contact" to `/contact`.

#### Scenario: Footer link targets
- **WHEN** a guest opens `/` and scrolls to the footer
- **THEN** the footer link "Privacy" has `href="/privacy"`, "Terms" has `href="/terms"`, "Imprint" has
  `href="/imprint"` and "Contact" has `href="/contact"`

#### Scenario: Footer link opens the privacy policy
- **WHEN** a guest opens `/` and clicks the footer link "Privacy"
- **THEN** the browser URL path is `/privacy` and the page shows the heading level 1 "Privacy Policy"

### Requirement: FOOT-2 Legal pages have a heading and a page title
Each legal page SHALL render its heading level 1 and its document title:
`/privacy` "Privacy Policy" / "Privacy Policy, Hirion"; `/terms` "Terms of Service" /
"Terms of Service, Hirion"; `/imprint` "Imprint" / "Imprint, Hirion".

#### Scenario: Privacy page
- **WHEN** a guest opens `/privacy`
- **THEN** the page shows the heading level 1 "Privacy Policy" and the document title is "Privacy Policy, Hirion"

#### Scenario: Terms page
- **WHEN** a guest opens `/terms`
- **THEN** the page shows the heading level 1 "Terms of Service" and the document title is "Terms of Service, Hirion"

#### Scenario: Imprint page
- **WHEN** a guest opens `/imprint`
- **THEN** the page shows the heading level 1 "Imprint" and the document title is "Imprint, Hirion"

### Requirement: FOOT-3 Imprint is complete
The imprint `/imprint` SHALL name the operator, address, contact email and register data without any
unfilled template: no visible text SHALL contain "[FILL:", and the contact email link SHALL be a
`mailto:` link to a syntactically valid email address. (Known defect D1.)

#### Scenario: Imprint has no unfilled templates
- **WHEN** a guest opens `/imprint`
- **THEN** the page shows no text containing "[FILL:"

#### Scenario: Imprint email is a valid mailto link
- **WHEN** a guest opens `/imprint`
- **THEN** the page has a link whose `href` matches `^mailto:[^@\s\[\]]+@[^@\s\[\]]+\.[a-z]{2,}$`
