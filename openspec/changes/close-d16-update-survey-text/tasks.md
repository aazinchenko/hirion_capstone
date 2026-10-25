## 1. Tests

- [ ] 1.1 `EmailLinkPagesTest.link1_surveyWithoutIdSendsNothing` expects "This survey link is missing its id. Please use the link from the email.". Verify: run `EmailLinkPagesTest` with mvn and quote the result line.
- [ ] 1.2 `RegistrationTest.reg7_planBasicPreselectsBasic` is a regular test (no `expectedExceptions`, no `@Issue("D16")`), checks Basic `aria-pressed="true"` and Premium `"false"`. Verify: guest run of the REG-7 tests without `-Dqa.mailbox` and quote the result line.
- [ ] 1.3 `SmokeTest.pub5_basicPlanCta` clicks "Choose Basic" and checks the URL `/signup?…plan=basic`. Verify: run `SmokeTest` and quote the result line.
- [ ] 1.4 docs: D16 FIXED (intent.md, decisions.md, current-state.md, README). Verify: `grep -n "D16" docs/intent.md README.md`.

## 2. Gate

- [ ] 2.1 Run `pnpm exec openspec validate close-d16-update-survey-text --strict` and `pnpm check`; quote the summary lines in `change-log.md`.
