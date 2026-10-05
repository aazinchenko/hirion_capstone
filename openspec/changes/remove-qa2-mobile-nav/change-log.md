# Change log -- remove-qa2-mobile-nav

## 2026-10-05: human decision, D3 is not a defect

- Human: "d3 это не баг" -> "сделай". QA-2 removed (REMOVED requirement), KNOWN BUG D3 test deleted.
- `pnpm exec openspec validate remove-qa2-mobile-nav --strict` -> "Change 'remove-qa2-mobile-nav' is valid".

## Evidence for tasks

- 1.1 [x]: `grep -c "@Test" src/test/java/ch/hirion/pub/MobileLayoutTest.java` -> `1`;
  `grep -rn "D3" src/test/java` -> only `MobileLayoutTest.java:8` (class comment);
  `pnpm check:locators` -> "PASS: no forbidden patterns".
- 2.1 [x]: `grep -n "D3" docs/intent.md docs/decisions.md` -> `intent.md:95` (D3, followed by line 96
  "NOT A DEFECT (human decision 2026-10-05) ..."), `decisions.md:49` ("Not a defect: D7 ...; D3, the mobile
  header without "Sign in" is the intended design ...").
- 3.1 [x]: `pnpm check` exit 0 -- "PASS: no forbidden patterns"; public suite
  `TEST-TestSuite.xml tests="57" errors="0" skipped="0" failures="0"`;
  "spec:check ok — specs: 9 · active changes: 1 · archived: 4".
