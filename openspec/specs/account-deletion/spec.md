# account-deletion Specification

## Purpose

Deleting one's own account from Settings > Security (test group DEL). The suite uses it to remove the
disposable test user at the end of every run. The dialog "Delete your account?" and its result (toast
"Your account has been deleted.", signed out) were confirmed live on 2026-09-29/30; by a human decision
they stay out of the requirements, because only the cleanup clicks "Delete" and its output is the evidence.

## Requirements

### Requirement: DEL-1 Security section offers account deletion
The Security section of `/settings` SHALL show, below the password form, a "Delete account" section
with the button "Delete account".

#### Scenario: Delete account button is present
- **WHEN** the signed-in test user opens `/settings?section=security`
- **THEN** the page shows the button "Delete account" below the button "Update password"
