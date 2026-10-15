## Context

Evidence (2026-10-15): CI journey runs at about 07:28 and 10:51 UTC, Allure report on GitHub Pages, both
`Tests run: 40, Failures: 3` with `dash6`, `dash11`, `dash10` "no job card in the Job Feed after 30 s". Local live
run 2026-10-15 (`../journey-run-2026-10-15.log`, temporary `[FEED-COUNT TEMP]` line in `feedCards`, reverted):
feed counts dash2 2, dash4 2, dash5 (menu) 1, dash5 (applied) 2, dash6 1, dash11 1, dash10 1; failures `dash5`
(menu item "Already applied" detached) and `dash6` ("no unused job card ... (1 cards)"). The Playwright trace of
`dash5` shows the server function that returns the matched jobs answering with two jobs, "Senior Test Automation
Engineer / Tosca SME" and "Senior Test Engineer Automated Testing", while the saved-jobs answer already held
"Senior Test Automation Engineer / Tosca SME"; the page first rendered both cards and removed the saved one about
0.1 s after "Hide" was clicked.

## Goals / Non-Goals

**Goals:**
- No red journey run only because a new user's feed is short or still settling.
- A real regression stays red: an empty feed for DASH-2/10/11, a saved/applied/hidden job that stays in the feed.

**Non-Goals:**
- No fixed waits (AGENTS.md); no retries of a failed click.
- No new defect entry: whether a saved job showing for a moment after reload is a defect is a human decision
  (Open Questions).

## Decisions

1. **Order.** New priorities: DASH-1, DASH-2, DASH-3, DASH-10, DASH-11, DASH-4, DASH-5 menu, DASH-5 applied,
   DASH-6, DASH-7, DASH-9, DASH-12. DASH-10 only reads titles. DASH-11 clicks "View job" and closes the new tab;
   it is not known to change the feed (if it did, DASH-4 would see it). DASH-3 still runs before DASH-4.
2. **Settled feed.** `pickCard()` first waits (up to `FEED_TIMEOUT`) until no card whose title is in `usedTitles`
   is shown. These jobs were saved / applied / hidden by earlier tests, so their absence is what DASH-4/5/6
   require; when the wait times out the test fails with the title, which is a real finding, not a skip.
   Alternative "wait for network idle" was rejected: in the trace the re-render came after the menu click.
3. **Skip when the feed is used up.** When earlier tests already used cards (`usedTitles` not empty) and no
   unused card with a unique title is left -- including an empty feed -- `pickCard()` throws `SkipException`
   with the card count. With no used titles yet, an empty feed still fails (DASH-4 is the first card user).
   `feedCards(min)` for DASH-2/10/11 still fails on an empty feed.

## Risks / Trade-offs

- [DASH-6 silently never runs for 2-job feeds] → the skip message names the cause and Allure shows it as skipped;
  it runs whenever the feed has 3 or more jobs.
- [The saved job never leaves the feed by itself] → the settle wait fails with the title after 30 s; that is the
  DASH-4 requirement failing and goes to the human.
- [A skip after a 30 s wait on an empty feed] → only DASH-6 in the worst case.

## Migration Plan

Spec commit (this change), test commit, one journey run (the human's choice: local on "да" or CI), then the human
runs `/opsx:archive`. Rollback: revert the test commit.

## Open Questions

- Is "a saved job shows in the Job Feed for a moment after reload" a defect (candidate D15) or acceptable?
