## Purpose

Public footer and legal pages of hirion.ch (test group FOOT): a visitor can reach the privacy policy,
the terms of service, the imprint and the contact page from the footer of any public page, and the
imprint names the operator completely.

## ADDED Requirements

### Requirement: FOOT-1 Footer links to legal pages and contact
The footer (landmark `contentinfo`) of the home page `/` SHALL contain the links "Privacy" to `/privacy`,
"Terms" to `/terms`, "Imprint" to `/imprint` and "Contact" to `/contact`, and each of `/`, `/contact`,
`/privacy`, `/terms` and `/imprint` SHALL respond with HTTP status 200.

#### Scenario: Footer link targets
- **WHEN** a guest opens `/` and scrolls to the footer
- **THEN** the footer link "Privacy" has `href="/privacy"`, "Terms" has `href="/terms"`, "Imprint" has
  `href="/imprint"` and "Contact" has `href="/contact"`

#### Scenario: Footer link opens the privacy policy
- **WHEN** a guest opens `/` and clicks the footer link "Privacy"
- **THEN** the browser URL path is `/privacy` and the page shows the heading level 1 "Privacy Policy"

#### Scenario: Internal footer links respond
- **WHEN** a client requests `/`, `/contact`, `/privacy`, `/terms` and `/imprint`
- **THEN** each response has status 200

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
unfilled template: no visible text SHALL contain "[FILL:", the main content SHALL contain at least one
`mailto:` link, and every `mailto:` link in the main content SHALL point to a syntactically valid email
address. (Known defect D1.)

#### Scenario: Imprint has no unfilled templates
- **WHEN** a guest opens `/imprint`
- **THEN** the page shows no text containing "[FILL:"

#### Scenario: Imprint email is a valid mailto link
- **WHEN** a guest opens `/imprint`
- **THEN** the main content has at least one `mailto:` link and every such `href` matches
  `^mailto:[^@\s\[\]]+@[^@\s\[\]]+\.[a-z]{2,}$`

### Requirement: FOOT-4 Footer anchors lead to home page sections
The footer SHALL contain the links "How it works" to `/#how`, "Pricing" to `/#pricing`,
"Sample matches" to `/#examples`, "Manifesto" to `/#manifesto` and "FAQ" to `/#faq`, and each
target section SHALL exist on `/`.

#### Scenario: Footer anchor scrolls to its section
- **WHEN** a guest opens `/` and clicks one of these footer links
- **THEN** the URL ends with the anchor (for example `#how`) and the section with that id is in the viewport

### Requirement: FOOT-5 LinkedIn link opens safely
The footer SHALL contain the link "Hirion on LinkedIn" to `https://www.linkedin.com/company/hirionch`
that opens in a new tab with `rel` containing `noopener` and `noreferrer`.

#### Scenario: LinkedIn link attributes
- **WHEN** a guest opens `/`
- **THEN** the link "Hirion on LinkedIn" has `href="https://www.linkedin.com/company/hirionch"`,
  `target="_blank"` and a `rel` containing `noopener` and `noreferrer`

### Requirement: FOOT-6 Privacy and Terms show their update date
The pages `/privacy` and `/terms` SHALL show a text starting with "Last updated:".

#### Scenario: Privacy shows its update date
- **WHEN** a guest opens `/privacy`
- **THEN** the page shows a text starting with "Last updated:"

#### Scenario: Terms shows its update date
- **WHEN** a guest opens `/terms`
- **THEN** the page shows a text starting with "Last updated:"

### Requirement: FOOT-7 Copyright shows the current year
The footer SHALL show "©" followed by the current calendar year.

#### Scenario: Copyright year
- **WHEN** a guest opens `/`
- **THEN** the footer contains the text "© <current year>"