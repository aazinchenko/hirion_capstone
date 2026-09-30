## Purpose

Deleting one's own account from Settings > Security (test group DEL). The suite uses it to remove the
disposable test user at the end of every run; the dialog and its result are specified once confirmed live.

## ADDED Requirements

### Requirement: DEL-1 Security section offers account deletion
The Security section of `/settings` SHALL show, below the password form, a "Delete account" section
with the button "Delete account".

#### Scenario: Delete account button is present
- **WHEN** the signed-in test user opens `/settings?section=security`
- **THEN** the page shows the button "Delete account" below the button "Update password"
