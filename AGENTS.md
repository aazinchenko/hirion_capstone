# AGENTS.md -- Hirion.ch test rules (Java, Playwright + TestNG, OpenSpec)
## Trust levels
- Inside an OpenSpec change (/opsx:apply): level 3 -- the agent edits files allowed by
.claude/settings.json, runs allowed commands and finishes with the output of `pnpm check`.
- Outside a change: level 1 -- the agent proposes and waits for a human decision.
## Locators
- Only getByRole(...), getByLabel(...), getByText(...) or getByTestId(...).
- Prefer the helper role(AriaRole.X, "name") from BaseTest: it always uses setExact(true).
- getByLabel("Password") on /signup ALSO matches the "Show password" button:
use new Page.GetByLabelOptions().setExact(true).
- Forbidden: nth-child, generated CSS classes (.css-xxxxx), waitForTimeout, Thread.sleep.
- Allowed exceptions, each with a comment "// locator-exception: <why>":
section anchors (#pricing, #faq ...), <html>, the contact-form honeypot input[name='website'].
- Every change MUST pass `java scripts/CheckLocatorRules.java src/test/java`.
## Test accounts and data
- Only the disposable user from support/TestUser.java. Never a real personal account.
- JourneyCleanup deletes ONLY emails matching \+hirion-qa-\d+@. Never weaken this check.
- Never automate Google / Apple OAuth. Never start Premium Trial or any payment.
- Never submit the real contact form: mock the network with page.route(...).
- Never assert on the text of AI answers (Tailor my CV, Cover Letter).
- Uploads use files from src/test/resources/fixtures/.
## Known issues
- Tests for known defects use @Test(expectedExceptions = AssertionFailedError.class,
description = "... KNOWN BUG Dn ..."). Do not "heal" them by weakening the assertion.
## Workflow
- OpenSpec CLI only as `pnpm exec openspec`, never a bare `openspec`.
- scripts/heal-loop.sh is the only way locators change automatically.
- A human reviews .agent-log/heal-log.jsonl and the locator-reviewer output before merging.
- ASSUMED items (docs/intent.md, "Open questions" in proposal.md) are drafts until
a human confirms them against the live site.
## Rules learned (details and evidence: docs/autonomy-log.md)
- A live run with -Dqa.mailbox creates a REAL hirion.ch account: only after an explicit human "да",
one account per run. A PreToolUse hook (scripts/guard-live-run.mjs) turns such a command into a prompt.
- heal-loop for journey tests runs WITHOUT -Dqa.mailbox; other journey fixes are targeted and use the trace.
- A KNOWN BUG test checks its preconditions with org.testng.Assert, never with the expected exception.
- Before fixing a failure, read the underlying error (trace, swallowed exception), not only the summary.
- A fact read from the site's code stays ASSUMED until it is seen on the live site.
- Every [x] in tasks.md needs a quoted run or command output in the change's change-log.md.
