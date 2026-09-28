## Purpose

Language switching on hirion.ch (test group I18N): a visitor can choose English, French, German or
Italian from the header language menu, the choice survives a reload, and the document language
follows the choice.

## ADDED Requirements

### Requirement: I18N-1 Language menu offers four languages
The header of `/` SHALL show a button "EN" for a guest with no stored language. Clicking it SHALL open an
element with role `menu` containing the menu items "EN", "FR", "DE" and "IT".

#### Scenario: Default language is English
- **WHEN** a guest with empty `localStorage` opens `/`
- **THEN** the header shows the button "EN" and the heading level 1 "Your AI job agent for Switzerland."

#### Scenario: Language menu lists four languages
- **WHEN** a guest opens `/` and clicks the button "EN"
- **THEN** a `menu` is visible with the menu items "EN", "FR", "DE" and "IT"

### Requirement: I18N-2 Choosing a language translates the page
Choosing a menu item SHALL switch the visible text of the page to that language, SHALL show the chosen
code on the language button and SHALL store the choice in `localStorage` under the key `hirion.lang`.

#### Scenario: Switch to German
- **WHEN** a guest opens `/`, clicks the button "EN" and clicks the menu item "DE"
- **THEN** the header shows the button "DE", the heading level 1 is
  "Dein KI-Job-Agent für die Schweiz." and `localStorage["hirion.lang"]` is `"de"`

### Requirement: I18N-3 Language choice persists
A language stored in `hirion.lang` SHALL be applied again when the visitor reloads the page or opens
another public page.

#### Scenario: German survives a reload
- **WHEN** a guest opens `/`, switches to "DE" and reloads the page
- **THEN** the header shows the button "DE" and the heading level 1 is "Dein KI-Job-Agent für die Schweiz."

#### Scenario: German applies on another page
- **WHEN** a guest opens `/`, switches to "DE" and then opens `/contact`
- **THEN** the header shows the button "DE"

### Requirement: I18N-4 Document language follows the chosen language
The `lang` attribute of the `<html>` element SHALL equal the chosen language code in lower case:
`en` by default and `de` after choosing "DE". (Known defect D2.)

#### Scenario: Default document language
- **WHEN** a guest with empty `localStorage` opens `/`
- **THEN** `<html>` has `lang="en"`

#### Scenario: Document language after switching to German
- **WHEN** a guest opens `/`, clicks the button "EN" and clicks the menu item "DE"
- **THEN** `<html>` has `lang="de"`
