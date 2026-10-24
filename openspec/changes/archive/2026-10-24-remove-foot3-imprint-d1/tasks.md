## 1. Remove the tests

- [x] 1.1 Delete `foot3_imprintHasNoFillTemplates` and `foot3_imprintEmailIsValidMailto` (KNOWN BUG D1) from `src/test/java/ch/hirion/pub/FooterLegalTest.java` and their now unused imports and constants; FOOT-1, FOOT-2, FOOT-4..7 unchanged. Verify: `grep -n "FOOT-3\|D1\b" src/test/java -r` finds nothing, `mvn -q test-compile` exits 0, `pnpm check:locators` prints "PASS: no forbidden patterns".

## 2. Docs

- [x] 2.1 `docs/decisions.md`, `docs/intent.md`, `docs/current-state.md`, README: D1 closed without a test, counts public 63 / full 99. Verify: `grep -n "D1\b" docs/decisions.md docs/intent.md`.

## 3. Gate

- [x] 3.1 Run `pnpm exec openspec validate remove-foot3-imprint-d1 --strict` and `pnpm check`; quote the summary lines in `change-log.md` (public suite 63 tests, 0 failures).
