## MODIFIED Requirements

### Requirement: I18N-4 Document language follows the chosen language
The `lang` attribute of the `<html>` element SHALL equal the chosen language code in lower case:
`en` by default and `de` after choosing "DE".

#### Scenario: Default document language
- **WHEN** a guest with empty `localStorage` opens `/`
- **THEN** `<html>` has `lang="en"`

#### Scenario: Document language after switching to German
- **WHEN** a guest opens `/`, clicks the button "EN" and clicks the menu item "DE"
- **THEN** `<html>` has `lang="de"`
