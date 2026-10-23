## MODIFIED Requirements

### Requirement: AUTH-1 Guests are redirected to sign in
A guest with no session who opens a private page `/dashboard`, `/settings`, `/onboarding`, `/employer/` or
`/admin` SHALL be redirected to `/login`, which shows the heading level 1 "Welcome back".

#### Scenario: Guest opens the dashboard
- **WHEN** a guest with no stored session opens `/dashboard`
- **THEN** the browser URL path is `/login` and the page shows the heading level 1 "Welcome back"

#### Scenario: Guest opens settings
- **WHEN** a guest with no stored session opens `/settings`
- **THEN** the browser URL path is `/login` and the page shows the heading level 1 "Welcome back"

#### Scenario: Guest opens the other private pages
- **WHEN** a guest with no stored session opens `/onboarding`, `/employer/` and `/admin` in turn
- **THEN** each time the browser URL path becomes `/login` and the page shows the heading level 1
  "Welcome back"

## ADDED Requirements

### Requirement: AUTH-7 Login and signup offer Google and Apple sign-in
The page `/login` and step 1 of `/signup` SHALL show the buttons "Continue with Google" and "Continue with
Apple" above the email form, separated from it by the text "OR". The tests SHALL NOT click these buttons
(AGENTS.md: never automate Google / Apple OAuth). (Seen live 2026-10-23.)

#### Scenario: Social sign-in buttons on the login page
- **WHEN** a guest opens `/login`
- **THEN** the page shows the buttons "Continue with Google" and "Continue with Apple" and the text "OR"

#### Scenario: Social sign-in buttons on signup step 1
- **WHEN** a guest opens `/signup`
- **THEN** the page shows "Step 1 of 4" and the buttons "Continue with Google" and "Continue with Apple"

### Requirement: AUTH-8 Reset-password page without a valid link
The page `/reset-password` opened without a valid recovery link SHALL NOT offer a password form: it SHALL
show the heading level 1 "Set a new password", the text "This reset link is invalid or has expired. Request a
new one." and the link "Send reset link" to `/forgot-password`, with the document title
"Set a new password — Hirion". (Seen live 2026-10-23.)

#### Scenario: Reset-password page opened directly
- **WHEN** a guest opens `/reset-password`
- **THEN** the page shows the heading level 1 "Set a new password", the text "This reset link is invalid or has
  expired. Request a new one.", the link "Send reset link" with `href="/forgot-password"` and no field labelled
  "New password"
