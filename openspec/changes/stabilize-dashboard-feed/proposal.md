## Why

The nightly journey failed twice on 2026-10-15 in GitHub Actions (94/97): `dash6`, `dash11` and `dash10` all
stopped with "no job card in the Job Feed after 30 s", while `dash4` and `dash5` had passed; a green 97/97 run
lay between the two red ones. A local live run the same day (human's "да", temporary count logging, reverted)
showed the cause: the Job Feed of a new Free user held only 2 jobs. DASH-4 and DASH-5 take them, so the tests
after them find no card. The tests assumed at least 3 cards; the site does not promise that.

The same run showed a second cause of flakiness: right after a reload the feed briefly showed a job that DASH-4
had already saved, then removed it while the Hide menu was open, so the menu item "Already applied" was detached
and `dash5` timed out.

## What Changes

- MODIFIED requirement `DASH-6 Not relevant removes a job from the feed` (spec `dashboard`): the requirement
  records that a new Free user's feed can be too short for DASH-6; the scenario is then skipped, not failed.
- `DashboardTest` (test design, see design.md): the read-only checks DASH-10 and DASH-11 run before DASH-4, while
  the feed is still full; before a card is picked the test waits until no job used by an earlier test is shown in
  the feed; `pickCard()` skips (`SkipException`) instead of failing when no unused card is left.

## Capabilities

### New Capabilities

(none)

### Modified Capabilities

- `dashboard`: requirement `DASH-6 Not relevant removes a job from the feed` gets the short-feed note.

## Impact

- Journey still 40 tests, journey suite total unchanged; with a 2-job feed DASH-6 is reported as skipped.
- No change to `support/**`, `JourneyCleanup`, `pom.xml` or CI.
- Verification needs one journey run with a real test account (local on the human's "да" or the CI run started
  by the human); the account is deleted by `JourneyCleanup`.
