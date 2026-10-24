# Change log -- remove-foot3-imprint-d1

## 2026-10-24: D1 closed by the human

- Human (2026-10-24): "d11 , d1, d19, d9, d8, d2, d4, d5, d6, d10, d12, d14 эти все неактуальны или пофикшены";
  then "Да, сделай change: убери FOOT-3 и KNOWN BUG тест D1, как с D3, весь цикл сам, потом push."
- Live check 2026-10-24 (browser, guest): `/imprint` text contains 7 x "[FILL", main `mailto:` links
  `["mailto:[FILL: e.g. info@hirion.ch]"]` -- the behaviour is unchanged, D1 is closed by decision, not fixed.

## Evidence for tasks

- 1.1 [x]: `grep -rn "FOOT-3\|D1\|VALID_MAILTO" src/test/java` -> no output (exit 1); `mvn -q test-compile` exit 0;
  `pnpm check:locators` -> "PASS: no forbidden patterns". `FooterLegalTest.java`: 34 lines deleted (two tests, the
  `VALID_MAILTO` constant, imports `LocatorAssertions`, `Issue`, `AssertionFailedError`). Commit `b8d6cac`.
- 2.1 [x]: `docs/decisions.md` -- D16 is the only KNOWN BUG test, D1 under "Not a defect" with this change;
  `docs/intent.md` D1 "FOOT-3 and its two KNOWN BUG tests removed"; `current-state.md` and README: public 63, full
  99. Commit `b8d6cac`.
- 3.1 [x]: `pnpm exec openspec validate remove-foot3-imprint-d1 --strict` -> "Change 'remove-foot3-imprint-d1' is
  valid"; `pnpm check` exit 0 (`../pnpm-check-2026-10-24.log`): "PASS: no forbidden patterns", public
  `total="63" passed="63" failed="0" skipped="0"`, "spec:check ok — specs: 10 · active changes: 1 · archived: 12".
