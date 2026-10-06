## MODIFIED Requirements

### Requirement: AUTH-3 Login fields support autofill
On `/login` the field "Email" SHALL have `autocomplete="email"` and the field "Password" SHALL have
`autocomplete="current-password"`, so password managers can fill them.

#### Scenario: Login autocomplete attributes
- **WHEN** a guest opens `/login`
- **THEN** the field "Email" has `autocomplete="email"` and the field "Password" has `autocomplete="current-password"`
