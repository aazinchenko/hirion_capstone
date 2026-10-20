## ADDED Requirements

### Requirement: DASH-2 Free Job Feed is a locked teaser
For a user on the Free plan the Job Feed SHALL show the matched jobs only as a locked teaser: the heading
"We found <N> jobs for you", N rows that are blurred and hidden from assistive technology
(`aria-hidden="true"`), each row with a match percentage, the text starting with "Unlock them with Basic", and
the links "Unlock with Basic" and "See all plans", both pointing to `/settings?section=plan`. A Free user's Job
Feed SHALL NOT show the job actions "View job", "Save" or "Hide". (Seen live 2026-10-20 after the site's new
plans; replaces "DASH-2 Job card actions". The server sends a Free user's jobs as locked entries without
company and URL.)

#### Scenario: Locked teaser for a Free user
- **WHEN** the signed-in Free test user opens `/dashboard` with the Job Feed shown
- **THEN** the page shows the heading "We found <N> jobs for you" and N match percentages inside an element
  with `aria-hidden="true"` whose rows are blurred, the text starting with "Unlock them with Basic", and the
  links "Unlock with Basic" and "See all plans" with `href="/settings?section=plan"` (neither is clicked), and
  there is no button "View job", "Save" or "Hide"

## REMOVED Requirements

### Requirement: DASH-2 Job card actions
**Reason**: Seen live 2026-10-20: a Free user no longer gets job cards; the Job Feed is a locked teaser and
the job actions need the paid plan Basic. A MODIFIED block cannot drop the scenario "First job card", so the
requirement is replaced by "DASH-2 Free Job Feed is a locked teaser".
**Migration**: `DashboardTest.dash2_firstJobCard` becomes `dash2_freeJobFeedIsLockedTeaser`. Job cards of a
paid plan are out of scope (no test pays; see `docs/pro-journey-plan-draft.md`).

### Requirement: DASH-4 Save moves a job to Saved
**Reason**: "Save" is not offered to a Free user any more (locked teaser, seen live 2026-10-20); it needs the
paid plan Basic, and no test pays.
**Migration**: `DashboardTest.dash4_saveMovesJob` is deleted. DASH-3 (empty Saved, Applied, Archive) stays.

### Requirement: DASH-5 Already applied moves a job to Applied
**Reason**: The "Hide" menu is not offered to a Free user any more (locked teaser, seen live 2026-10-20).
**Migration**: `DashboardTest.dash5_hideMenuItems` and `dash5_alreadyAppliedMovesJob` are deleted.

### Requirement: DASH-6 Not relevant removes a job from the feed
**Reason**: The "Hide" menu is not offered to a Free user any more (locked teaser, seen live 2026-10-20).
**Migration**: `DashboardTest.dash6_notRelevantRemovesJob` is deleted, with the short-feed skip of change
`stabilize-dashboard-feed`.

### Requirement: DASH-10 Job titles show their characters
**Reason**: A Free user sees the job titles only blurred and hidden from assistive technology (seen live
2026-10-20), so D8 can no longer be observed by the Free journey.
**Migration**: `DashboardTest.dash10_noReplacementCharsInTitles` is deleted; D8 stays recorded in
`docs/intent.md` as not observable for a Free user.

### Requirement: DASH-11 View job opens the vacancy in a new tab
**Reason**: "View job" is not offered to a Free user any more (locked teaser, seen live 2026-10-20); the
server sends no job URL to a Free user.
**Migration**: `DashboardTest.dash11_viewJobOpensExternalTab` is deleted.
