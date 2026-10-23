## MODIFIED Requirements

### Requirement: DASH-2 Free Job Feed is a locked teaser
For a user on the Free plan the Job Feed SHALL show the matched jobs only as a locked teaser: the heading
"We found <N> jobs for you", a preview of at most three of these jobs as rows that are blurred and hidden from
assistive technology (`aria-hidden="true"`), so min(N, 3) rows, each row with a match percentage, the text
starting with "Unlock them with Basic", the link "Unlock with Basic" to `/settings?section=plan&buy=basic` and
the link "See all plans" to `/settings?section=plan`. A Free user's Job Feed SHALL NOT show the job actions
"View job", "Save" or "Hide". Following "Unlock with Basic" starts the Basic checkout at once (the Settings page
reacts to `buy=basic`), so the tests SHALL NOT click it. (Seen live 2026-10-20 after the site's new plans: 2 jobs
-> 2 rows, 10 jobs -> 3 rows; replaces "DASH-2 Job card actions". The server sends a Free user's jobs as locked
entries without company and URL. "Unlock with Basic" carries `buy=basic` since the CI journey run of 2026-10-23.)

#### Scenario: Locked teaser for a Free user
- **WHEN** the signed-in Free test user opens `/dashboard` with the Job Feed shown
- **THEN** the page shows the heading "We found <N> jobs for you" and min(N, 3) match percentages inside an
  element with `aria-hidden="true"` whose rows are blurred, the text starting with "Unlock them with Basic", the
  link "Unlock with Basic" with `href="/settings?section=plan&buy=basic"` and the link "See all plans" with
  `href="/settings?section=plan"` (neither is clicked), and there is no button "View job", "Save" or "Hide"
