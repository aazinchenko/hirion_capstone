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
The form SHALL NOT send a request to `/api/public/contact` while a required field is empty or the email
is not a valid email address; the browser SHALL mark the offending field invalid and the visitor SHALL
stay on `/contact`.

#### Scenario: Empty form is blocked
- **WHEN** a guest opens `/contact` and clicks "Send message" without filling any field
- **THEN** the field "Name" reports `validity.valueMissing = true`, the URL path stays `/contact` and no
  request to `/api/public/contact` is sent

#### Scenario: Invalid email is blocked
- **WHEN** a guest opens `/contact`, fills "Name", "Subject" and "Message", types "not-an-email" into
  "Email" and clicks "Send message"
- **THEN** the field "Email" reports `validity.typeMismatch = true` and no request to
  `/api/public/contact` is sent

### Requirement: CONT-3 Honeypot is hidden from people
The form SHALL contain a spam honeypot input named `website` that people do not see or fill: it SHALL
have `tabindex="-1"` and `autocomplete="off"`, and it SHALL sit inside an element with
`aria-hidden="true"` that is placed outside the visible area.

#### Scenario: Honeypot attributes
- **WHEN** a guest opens `/contact`
- **THEN** the input `name="website"` has `tabindex="-1"` and `autocomplete="off"`, is inside an element
  with `aria-hidden="true"` and is not in the viewport

### Requirement: CONT-4 Valid input is sent once
When all fields are valid and the honeypot is empty, clicking "Send message" SHALL send exactly one
`POST` request to `/api/public/contact` whose body contains the entered email address, and after a
successful response the page SHALL show the notification "Thanks! Your message is ready to send.".

#### Scenario: Valid submit with a mocked network
- **WHEN** a guest opens `/contact` with every non-GET request intercepted and answered locally with
  status 200, fills "Name", "Email" = "qa-contact@example.com", "Subject" and "Message" and clicks
  "Send message"
- **THEN** exactly one `POST` request to `/api/public/contact` is intercepted, its body contains
  "qa-contact@example.com", and the page shows the text "Thanks! Your message is ready to send."
