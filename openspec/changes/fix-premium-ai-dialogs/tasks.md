## 1. Tests

- [ ] 1.1 `PremiumAiTest`: prem11 checks "What to highlight", "Keywords to use", "Copy", the counter "<n> tailored applications left this month."; prem12 checks "ATS-friendly", "Closely tailored", "Classic", "Modern", buttons "Copy", ".txt", "PDF", then "Copy" -> "Copied". Verify: `mvn -q test-compile` exits 0; `pnpm check:locators` prints "PASS: no forbidden patterns".

## 2. Gate

- [ ] 2.1 One live journey run with `-Dqa.ai=true` (human-approved 2026-10-24). Verify: testng-results 50 total, 0 failed, prem11-13 PASS, JourneyCleanup "deleted"; quote in `change-log.md`.
- [ ] 2.2 Run `pnpm exec openspec validate fix-premium-ai-dialogs --strict` and `pnpm check`; quote the summary lines in `change-log.md`.
