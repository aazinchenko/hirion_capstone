## MODIFIED Requirements

### Requirement: PREM-11 Help me stand out shows a review of the job
"Help me stand out" on a card SHALL open a dialog titled "Help me stand out" that, after loading, shows the parts
"What to highlight" and "Keywords to use", the button "Copy" and the monthly counter "<n> tailored applications left
this month.". The part "Worth addressing" is shown only when the AI finds gaps and is not required. The text written
by the AI is not checked. Runs only with `-Dqa.ai=true`, at most once per run. (Seen live 2026-10-24 in the first
`-Dqa.ai` run: no "Worth addressing", counter "29 tailored applications left this month.")

#### Scenario: Stand-out review
- **WHEN** the test user whose trial has started runs the suite with `-Dqa.ai=true` and clicks "Help me stand out"
  on the first card
- **THEN** within 90 s a dialog shows "Help me stand out", "What to highlight", "Keywords to use", the button "Copy"
  and a text matching "<n> tailored applications left this month."

### Requirement: PREM-12 Tailor my CV writes a tailored CV
"Tailor my CV" on a card SHALL open a dialog that, after loading, shows the badge "ATS-friendly", the label "Closely
tailored", the templates "Classic" and "Modern" and the buttons "Copy", ".txt" and "PDF"; "Copy" SHALL show the
notification "Copied". The text written by the AI is not checked. Runs only with `-Dqa.ai=true`, at most once per
run. (Seen live 2026-10-24 in the first `-Dqa.ai` run; the title "Tailored CV" from the site code was not in the
dialog. "Copied" is checked for the first time by this change.)

#### Scenario: Tailored CV
- **WHEN** the test user whose trial has started runs the suite with `-Dqa.ai=true`, clicks "Tailor my CV" on the
  first card and, once loaded, clicks "Copy"
- **THEN** within 90 s a dialog shows "ATS-friendly", "Closely tailored", "Classic", "Modern" and the buttons
  "Copy", ".txt" and "PDF", and after "Copy" the notification "Copied" is shown
