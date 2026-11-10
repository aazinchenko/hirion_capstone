## 1. Rules and setup (human-reviewed files)

- [x] 1.1 AGENTS.md: exception for the one owner-granted Premium account (credentials only from `QA_PREMIUM_EMAIL` / `QA_PREMIUM_PASSWORD`, never printed; never clicked on it: Delete account, Manage/Cancel subscription, plan or payment buttons, Update password, Save preferences, profile/CV changes; PREM-6/7 only with `-Dqa.feedActions=true`); the old Premium-trial rule removed. Verify: `grep -n "Premium" AGENTS.md`.
- [x] 1.2 `scripts/guard-live-run.mjs` also asks for commands with `testng-premium.xml` or `-Dqa.feedActions`. Verify: pipe a sample command into the hook and quote the "ask" output.
- [x] 1.3 `support/PremiumAccountTest` (storage state `.auth/premium.json`). Verify: `mvn -q test-compile`.
- [ ] 1.4 Human: GitHub secrets `QA_PREMIUM_EMAIL`, `QA_PREMIUM_PASSWORD`; same variables set locally for a live run. Verify: human confirms (values never shown).

## 2. Tests

- [x] 2.1 New `PremiumSignInTest`: PREM-15 (email guard, sign-in, session file) and PREM-16 (Premium plan state), group `premium`. Verify: run without the variables and quote the clear failure / skips.
- [x] 2.2 `PremiumUpgradeTest` deleted; `testng-premium.xml` = `PremiumSignInTest`, `PremiumDashboardTest`, `PremiumAiTest` (no `reg9`, no `JourneyCleanup`). Verify: `git show --stat` and the suite file.
- [x] 2.3 `PremiumDashboardTest`, `PremiumAiTest` extend `PremiumAccountTest`, depend on group `premium` only; PREM-3 waits 60 s; PREM-5 save + unsave with before/after counts; PREM-9 relative formula; PREM-6/7 skip without `-Dqa.feedActions=true`. Verify: `mvn -q test-compile` and `java scripts/CheckLocatorRules.java src/test/java`.
- [x] 2.4 CI `tests.yml`: premium job active again (nightly + manual), env from the new secrets, checkbox `feed_actions` -> `-Dqa.feedActions`, no `QA_MAILBOX`, no leftover-user step, Allure masks the premium email. Verify: YAML parses; quote the job condition.
- [x] 2.5 docs: intent.md (Premium account, PREM-1 removed, candidate defect: no-card trial still in signup code), current-state.md, README counts, docs/premium-journey-plan.md marked superseded. Verify: `grep -n "PREM-1\b\|Premium account" docs/intent.md`.

## 3. Gate

- [ ] 3.1 Live premium run on the human's "да" (no AI, no feed actions); quote the result line and that the Saved count is back after PREM-5 in `change-log.md`.
- [ ] 3.2 `pnpm exec openspec validate use-existing-premium-account --strict` and `pnpm check`; quote the summary lines in `change-log.md`.
