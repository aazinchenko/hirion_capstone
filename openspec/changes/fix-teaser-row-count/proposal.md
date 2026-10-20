## Why

The GitHub Actions journey run on 2026-10-20 (08:44 UTC, first CI run after change `update-plans-and-pricing`)
failed 35/36: `DashboardTest.dash2_freeJobFeedIsLockedTeaser` "Locator expected to have count: 10 Received: 3"
for the match percentages. DASH-2 says the teaser shows N rows for "We found <N> jobs for you"; that was
generalised from one local run where N was 2. The teaser shows a preview of at most three jobs:

- Live: local run 2026-10-20 10:28 CEST, "We found 2 jobs for you" with 2 rows; CI run 08:44 UTC, N = 10 with
  3 rows.
- Supporting, site code `assets/dashboard-CtYdgbir.js` (fetched 2026-10-20): the teaser component takes
  `jobs.filter(job => job.title).slice(0, 3)` for the rows and `jobs.length` for the heading.

## What Changes

- MODIFIED `DASH-2 Free Job Feed is a locked teaser` (spec `dashboard`): min(N, 3) rows instead of N; the
  scenario name stays.
- `DashboardTest.dash2_freeJobFeedIsLockedTeaser` expects `Math.min(jobs, 3)` match percentages.

## Capabilities

### New Capabilities

(none)

### Modified Capabilities

- `dashboard`: DASH-2 row count.

## Impact

- Test counts unchanged (journey 36, full 93); specs 54 requirements, 89 scenarios.
- Verification needs one journey run with a real test account (local on the human's "да" or CI).

## Open Questions

- The site code also has "We are looking for jobs for you" with no rows when a user has no matched jobs. It is
  not seen live, so it stays out of the spec; DASH-2 fails loudly if a new test user ever gets no jobs.
