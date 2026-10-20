# Change log -- fix-teaser-row-count

## 2026-10-20: the teaser shows at most three rows

- CI journey run 2026-10-20 08:44 UTC (pasted by the human): `Tests run: 36, Failures: 1` --
  `dash2_freeJobFeedIsLockedTeaser:86 Locator expected to have count: 10 Received: 3` for
  `getByRole(AriaRole.MAIN).getByText(Pattern.compile("^\\d+%$"))`; JourneyCleanup "deleted: ***+hirion-qa-...
  no longer signs in".
- Local run 2026-10-20 10:28 CEST (change `update-plans-and-pricing`, task 3.2): "We found 2 jobs for you", 2 rows,
  `dash2` PASS.
- Site code, supporting only (`https://hirion.ch/assets/dashboard-CtYdgbir.js`, fetched 2026-10-20), minified:
  `function xo({jobs:e}){const{t}=D(),n=e.filter(a=>a.title).slice(0,3);` ... heading
  `e.length>0?t("feed_teaser_title",{n:e.length}):t("feed_teaser_title_zero")`, rows `n.map(...)`.

## Evidence for tasks

- 1.1 [x]: `DashboardTest` `TEASER_ROWS = 3`, `assertThat(scores).hasCount(Math.min(jobs, TEASER_ROWS))`;
  `mvn -q test-compile` exit 0; `pnpm check:locators` -> "PASS: no forbidden patterns".
- 2.2 [x]: `pnpm exec openspec validate fix-teaser-row-count --strict` -> "Change 'fix-teaser-row-count' is valid";
  `pnpm check` exit 0: "PASS: no forbidden patterns", public `total="57" passed="57" failed="0" skipped="0"`,
  "spec:check ok -- specs: 9 · active changes: 1 · archived: 9".
- 2.1 [x]: local live journey run 2026-10-20 10:56-10:59 CEST (human's "да", `../journey-run-2026-10-20c.log`):
  `Tests run: 36, Failures: 0, Errors: 0, Skipped: 0`, BUILD SUCCESS; `dash2_freeJobFeedIsLockedTeaser` PASS (15.0 s);
  JourneyCleanup "deleted: ... no longer signs in". The heading's N is not recorded: a passing test writes no trace.
  The case N > 3 is covered by the CI run of 08:44 UTC (N = 10, 3 rows).
