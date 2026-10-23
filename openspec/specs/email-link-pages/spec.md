# email-link-pages Specification

## Purpose
Pages that hirion.ch links from its emails (test group LINK), opened by a guest without a valid link: the
feedback survey, the unsubscribe page and the checkout return page. They explain the missing link and send
nothing; real survey answers, unsubscribes and payments are out of scope.

## Requirements

### Requirement: LINK-1 Feedback survey page
The page `/survey` (linked from emails as `/survey?id=<id>`) SHALL be public and SHALL show the heading level 1
"Help us make Hirion better", the six questions "1. What made you sign up for Hirion?", "2. How useful were
Hirion's job recommendations for you?", "3. Did Hirion help you discover jobs you probably wouldn't have found
yourself?", "4. What was the biggest problem or frustration you had with Hirion?", "5. Why didn't you continue
with Premium after the free trial?" and "6. What would make Hirion valuable enough for you to pay for it?", and
the button "Send feedback". Without an `id` the survey SHALL NOT send anything: "Send feedback" SHALL show the
notification "This survey link is missing its id — please use the link from the email." and SHALL NOT send a
request to `/api/public/survey-response`. Every answer option SHALL have an accessible name. (Page seen live
2026-10-23; the behaviour without an id is read from the site code and is confirmed by task 1.3 with a mocked
network; the rating options 2-4 of question 2 have no name, candidate defect D17.)

#### Scenario: Survey page is rendered
- **WHEN** a guest opens `/survey`
- **THEN** the page shows the heading level 1 "Help us make Hirion better", the six question texts and the
  button "Send feedback"

#### Scenario: Survey without an id sends nothing
- **WHEN** a guest opens `/survey` with every non-GET request intercepted and answered locally, and clicks
  "Send feedback" without answering
- **THEN** the page shows the text "This survey link is missing its id — please use the link from the email."
  and no request to `/api/public/survey-response` is intercepted

### Requirement: LINK-2 Unsubscribe page without a valid link
The page `/unsubscribe` opened without a valid unsubscribe link SHALL show the heading level 1 "Invalid link"
and the text "This unsubscribe link is invalid or has expired.", with the document title
"Unsubscribe — Hirion". (Seen live 2026-10-23.)

#### Scenario: Unsubscribe page opened directly
- **WHEN** a guest opens `/unsubscribe`
- **THEN** the page shows the heading level 1 "Invalid link" and the text "This unsubscribe link is invalid or
  has expired."

### Requirement: LINK-3 Checkout return page without a session
The page `/checkout/return` opened without a checkout session SHALL show the heading level 1 "No session
found", the text "We couldn't find a checkout session in this URL." and the link "Go to my account" whose path
is `/settings`. (Seen live 2026-10-23. The link carries `checkout=success` although no checkout happened;
Settings then announces "Welcome to Premium!" according to the site code: candidate defect D18, not asserted.)

#### Scenario: Checkout return opened directly
- **WHEN** a guest opens `/checkout/return`
- **THEN** the page shows the heading level 1 "No session found", the text "We couldn't find a checkout session
  in this URL." and the link "Go to my account" whose `href` starts with `/settings`
