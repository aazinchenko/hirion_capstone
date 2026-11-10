# change-log: use-existing-premium-account

Human decisions 2026-11-10: the Premium trial needs card data (the Settings trial opens a Stripe checkout); the PREM
suite uses ONE existing Premium account granted by the site owner (owner consent, sign-in by email + password);
"Already applied" / "Not relevant" only on a manual run. "Да, делай" for the tasks after the spec commit 68e591f.

## 1.1 AGENTS.md
Exception for the Premium account (env-only credentials, never printed; never clicked: Delete account,
Manage/Cancel subscription, plan/payment buttons, Update password, Save preferences, profile/CV); old trial rule
replaced by "Never start a Premium trial"; hook line lists -Dqa.feedActions, testng-premium.xml, testng-all.xml.

## 1.2 guard hook
```
{"hookSpecificOutput":{...,"permissionDecision":"ask","permissionDecisionReason":"Premium suite signs in to the real Premium account and saves / unsaves a job: it needs an explicit human \"да\"."}}
 <- [mvn -q test -Dsuite=testng-premium.xml]
 <- [mvn -q test -Dsuite=testng-public.xml]          (no output = no opinion)
{... "Premium suite signs in ...; -Dqa.feedActions marks a job as applied / not relevant on the Premium account (cannot be undone): ..."}
 <- [mvn test -Dsuite=testng-premium.xml -Dqa.feedActions=true]
```

## 1.3 / 2.3 compile and locator rules
```
mvn -q -B test-compile  -> exit 0
java scripts/CheckLocatorRules.java src/test/java  -> PASS: no forbidden patterns
```

## 2.1 PremiumSignInTest without credentials (no sign-in, nothing sent to the site)
```
env -u QA_PREMIUM_EMAIL -u QA_PREMIUM_PASSWORD mvn -B -Dsuite=testng-premium.xml -Dqa.attachments=false test
Tests run: 15, Failures: 1, Errors: 0, Skipped: 14
java.lang.AssertionError: environment variable QA_PREMIUM_EMAIL is not set (the Premium account's credentials; see AGENTS.md)
```
Disposable-email guard (dummy values, refused before any page is opened):
```
QA_PREMIUM_EMAIL=someone+hirion-qa-123@example.com ...
Tests run: 15, Failures: 1, Errors: 0, Skipped: 14
java.lang.AssertionError: QA_PREMIUM_EMAIL is a disposable +hirion-qa-N address; the Premium suite needs the permanent Premium account
```

## 2.2 suites
testng-premium.xml = PremiumSignInTest, PremiumDashboardTest, PremiumAiTest (15 tests); testng-journey.xml = Free
classes + JourneyCleanup (36); PremiumUpgradeTest deleted.

## 2.4 CI
premium job `if: github.event_name == 'schedule' || (github.event_name == 'workflow_dispatch' && (inputs.suite == 'all' || inputs.suite == 'premium'))`;
run `mvn ... -Dsuite=testng-premium.xml "-Dqa.ai=$QA_AI" "-Dqa.feedActions=$QA_FEED_ACTIONS" -Dqa.attachments=false test`;
inputs `['suite', 'ai', 'feed_actions']` (YAML parsed with python yaml).

## 2.5 docs
intent.md (PREM line, 2026-11-10 facts incl. candidate defect "no-card trial still in signup code", Not doing),
README (114 tests, Premium account row), current-state.md, premium-journey-plan.md marked SUPERSEDED.

## 3.2 gate (partial; repeated after 3.1)
```
pnpm exec openspec validate use-existing-premium-account --strict -> Change 'use-existing-premium-account' is valid
pnpm check -> PASS: no forbidden patterns; public tests="63" errors="0" skipped="0" failures="0";
              spec:check ok — specs: 11 · active changes: 1 · archived: 16
```

## 3.1 live premium run
Open: needs GitHub secrets / local env vars by the human and the human's "да".
