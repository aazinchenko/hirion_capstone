## Purpose

Contact form on `/contact` (test group CONT): a visitor can write to the Hirion team; the form checks
required fields and the email format in the browser, hides a spam honeypot from people, and sends one
request only when the input is valid.

## ADDED Requirements

### Requirement: CONT-1 Contact form fields
The page `/contact` SHALL show the heading level 1 "Talk to the team." and a form with the required
fields labelled "Name", "Email", "Subject" and "Message" and the submit button "Send message".

#### Scenario: Contact form is rendered
- **WHEN** a guest opens `/contact`
- **THEN** the page shows the heading level 1 "Talk to the team.", the fields labelled "Name", "Email",
  "Subject" and "Message" each have the attribute `required`, and the button "Send message" is visible

### Requirement: CONT-2 Invalid input is not sent
The form SHALL NOT send any request while a required field is empty or the email is not a valid email
address; the browser SHALL mark the offending field invalid.

#### Scenario: Empty form is blocked
- **WHEN** a guest opens `/contact` and clicks "Send message" without filling any field
- **THEN** the field "Name" reports `validity.valueMissing = true` and no non-GET request is sent

#### Scenario: Invalid email is blocked
- **WHEN** a guest opens `/contact`, fills "Name", "Subject" and "Message", types "not-an-email" into
  "Email" and clicks "Send message"
- **THEN** the field "Email" reports `validity.typeMismatch = true` and no non-GET request is sent

### Requirement: CONT-3 Honeypot is hidden from people
The form SHALL contain a spam honeypot input named `website` that people do not fill: it SHALL have
`tabindex="-1"` and `autocomplete="off"`.

#### Scenario: Honeypot attributes
- **WHEN** a guest opens `/contact`
- **THEN** the input `name="website"` has `tabindex="-1"` and `autocomplete="off"`

### Requirement: CONT-4 Valid input is sent once
When all fields are valid and the honeypot is empty, clicking "Send message" SHALL send exactly one
non-GET request whose body contains the entered email address.

#### Scenario: Valid submit with a mocked network
- **WHEN** a guest opens `/contact` with every non-GET request intercepted and answered locally,
  fills "Name", "Email" = "qa-contact@example.com", "Subject" and "Message" and clicks "Send message"
- **THEN** exactly one non-GET request is intercepted and its body contains "qa-contact@example.com"
