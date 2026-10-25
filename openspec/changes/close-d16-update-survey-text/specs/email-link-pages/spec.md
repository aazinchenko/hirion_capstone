## MODIFIED Requirements

### Requirement: LINK-1 Feedback survey page
The page `/survey` (linked from emails as `/survey?id=<id>`) SHALL be public and SHALL show the heading level 1
"Help us make Hirion better", the six questions "1. What made you sign up for Hirion?", "2. How useful were
Hirion's job recommendations for you?", "3. Did Hirion help you discover jobs you probably wouldn't have found
yourself?", "4. What was the biggest problem or frustration you had with Hirion?", "5. Why didn't you continue
with Premium after the free trial?" and "6. What would make Hirion valuable enough for you to pay for it?", and
the button "Send feedback". Without an `id` the survey SHALL NOT send anything: "Send feedback" SHALL show the
notification "This survey link is missing its id. Please use the link from the email." and SHALL NOT send a
request to `/api/public/survey-response`. Every answer option SHALL have an accessible name. (Page seen live
2026-10-23; the behaviour without an id is confirmed live with a mocked network. The site changed the text from
"… missing its id — please use …" by 2026-10-25. The rating options 2-4 of question 2 have no name, candidate
defect D17.)

#### Scenario: Survey page is rendered
- **WHEN** a guest opens `/survey`
- **THEN** the page shows the heading level 1 "Help us make Hirion better", the six question texts and the
  button "Send feedback"

#### Scenario: Survey without an id sends nothing
- **WHEN** a guest opens `/survey` with every non-GET request intercepted and answered locally, and clicks
  "Send feedback" without answering
- **THEN** the page shows the text "This survey link is missing its id. Please use the link from the email."
  and no request to `/api/public/survey-response` is intercepted
