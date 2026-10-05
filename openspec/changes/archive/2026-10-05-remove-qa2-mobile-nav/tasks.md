## 1. Remove the test

- [x] 1.1 Delete `qa2_signInReachableOnPhone` (KNOWN BUG D3) from `src/test/java/ch/hirion/pub/MobileLayoutTest.java` and its now unused imports; QA-1 unchanged. Verify: `grep -c "@Test" MobileLayoutTest.java` gives 1, `grep -rn "D3" src/test/java` finds only the class comment of MobileLayoutTest, `java scripts/CheckLocatorRules.java src/test/java` prints "PASS: no forbidden patterns".

## 2. Docs

- [x] 2.1 `docs/intent.md`: D3 marked "NOT A DEFECT (human decision 2026-10-05)"; `docs/decisions.md`: D3 moved to "Not a defect". Verify: `grep -n "D3" docs/intent.md docs/decisions.md`.

## 3. Gate

- [x] 3.1 Run `pnpm check` and quote its summary lines in `change-log.md` (public suite 57 tests, 0 failures).
