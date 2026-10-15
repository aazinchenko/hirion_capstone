## MODIFIED Requirements

### Requirement: DASH-6 Not relevant removes a job from the feed
Choosing "Not relevant" in the Hide menu SHALL remove the job from the Job Feed. (The Job Feed of a new Free
user can hold only two jobs -- seen live on 2026-10-15 -- and DASH-4 and DASH-5 use them first; when no unused
job card is left, the scenario is skipped, not failed.)

#### Scenario: Mark a job as not relevant
- **WHEN** the signed-in test user clicks "Hide" on a job card and chooses the menu item "Not relevant"
- **THEN** that job title is no longer in the Job Feed
