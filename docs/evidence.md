# Evidence -- practices with clickable proof

Every practice below links to the rule (or tool) and to the place where it acted. Commits are in
chronological order inside each section. What went wrong is listed too: the useful part of this project is
where the agent was caught and what changed after.

Repo: <https://github.com/aazinchenko/hirion_capstone>

## 0. The project works, full cycle

- One command checks everything that needs no account: `pnpm check` = locator rules → public suite → spec
  validation ([package.json](https://github.com/aazinchenko/hirion_capstone/blob/master/package.json)).
- Full suite on the live site, 95/95 incl. a real account created and deleted:
  [7c5319a](https://github.com/aazinchenko/hirion_capstone/commit/7c5319a).
  Last journey run 40/40 after the final review fix: [af1fb90](https://github.com/aazinchenko/hirion_capstone/commit/af1fb90).
- 4 OpenSpec changes, each propose → apply → archive; `pnpm spec:check` went from `specs: 0 · archived: 0`
  ([475f204](https://github.com/aazinchenko/hirion_capstone/commit/475f204)) to `specs: 9 · archived: 4`
  ([d1b34a0](https://github.com/aazinchenko/hirion_capstone/commit/d1b34a0)).

## 1. Context engineering

**Static context.** [AGENTS.md](https://github.com/aazinchenko/hirion_capstone/blob/master/AGENTS.md)
(loaded by [CLAUDE.md](https://github.com/aazinchenko/hirion_capstone/blob/master/CLAUDE.md)) holds what I put
there on purpose: trust levels (3 inside an OpenSpec change, 1 outside), role-based locators only, disposable
test user only, no OAuth / payments / real contact-form submits, how known bugs are tested.
[openspec/config.yaml](https://github.com/aazinchenko/hirion_capstone/blob/master/openspec/config.yaml) adds
the product context and two rules for every spec: a scenario names page + action + exact expected text; the
first task is the tests, the last is `pnpm check`.

**Where a rule visibly acted -- the agent did it differently afterwards:**

| Rule | Before | After |
|---|---|---|
| Locators: role / label / text only, no guessed classes (AGENTS.md, repeated in the heal prompt) | stale `.faq-item-3 .faq-toggle` ([385b663](https://github.com/aazinchenko/hirion_capstone/commit/385b663)) | the healing agent replaced it with `inSection(faq, BUTTON, "Is Hirion free to use?")` ([871ebab](https://github.com/aazinchenko/hirion_capstone/commit/871ebab)) |
| "Every ticked task needs a quoted run" (from review 2026-09-28) | change 1 ticked tasks 2.1, 3.1, 3.2 without running them; there is no change-log in [the change 1 archive](https://github.com/aazinchenko/hirion_capstone/tree/master/openspec/changes/archive/2026-09-28-add-home-smoke) | from change 2 on every change has a `change-log.md` with the quoted failing lines and runs, first added in [e2ebe6e](https://github.com/aazinchenko/hirion_capstone/commit/e2ebe6e), 77 minutes after the review ([6b7250e](https://github.com/aazinchenko/hirion_capstone/commit/6b7250e)) |
| "A KNOWN BUG precondition never uses the expected exception" (from review 2026-09-29) | preconditions threw the same `AssertionFailedError` as the bug, so a broken page passed as "known bug"; fixed in [737c8f2](https://github.com/aazinchenko/hirion_capstone/commit/737c8f2) | the next tests the agent wrote follow it unprompted: RegistrationTest D6 precondition through TestNG ([022c27f](https://github.com/aazinchenko/hirion_capstone/commit/022c27f)), SettingsTest "so a precondition never passes as a known bug" ([change-log, add-user-journey](https://github.com/aazinchenko/hirion_capstone/blob/master/openspec/changes/archive/2026-09-30-add-user-journey/change-log.md)) |

The learned rules were moved into AGENTS.md "Rules learned" so every new session reads them:
[59a5ebf](https://github.com/aazinchenko/hirion_capstone/commit/59a5ebf).

**Dynamic context.** [FetchLiveDom.java](https://github.com/aazinchenko/hirion_capstone/blob/master/src/test/java/ch/hirion/tools/FetchLiveDom.java)
([f0b64d6](https://github.com/aazinchenko/hirion_capstone/commit/f0b64d6)) saves the live HTML of the failing
area on every red iteration; example output:
[.agent-log/dom-snippet.html](https://github.com/aazinchenko/hirion_capstone/blob/master/.agent-log/dom-snippet.html).
A PreToolUse hook, [guard-live-run.mjs](https://github.com/aazinchenko/hirion_capstone/blob/master/scripts/guard-live-run.mjs),
inspects every shell command at request time (see 6).

## 2. Loop engineering

- The loop: [scripts/heal-loop.sh](https://github.com/aazinchenko/hirion_capstone/blob/master/scripts/heal-loop.sh)
  -- run tests → if red, fetch live DOM → `claude -p` makes ONE targeted fix → CheckLocatorRules → max 5
  iterations ([3cd80a6](https://github.com/aazinchenko/hirion_capstone/commit/3cd80a6)).
- Real runs, from [.agent-log/heal-log.jsonl](https://github.com/aazinchenko/hirion_capstone/blob/master/.agent-log/heal-log.jsonl):
  SmokeTest RED → GREEN on iteration 2 in 2 min 42 s ([871ebab](https://github.com/aazinchenko/hirion_capstone/commit/871ebab));
  RegistrationTest GREEN on iteration 3 in about 5 min, run without a mailbox so no account was created
  ([9e5fc59](https://github.com/aazinchenko/hirion_capstone/commit/9e5fc59)).
  Per-iteration agent diffs: [heal-diff.patch](https://github.com/aazinchenko/hirion_capstone/blob/master/.agent-log/heal-diff.patch).
- **What went wrong:** the first version kept a fix that CheckLocatorRules had rejected, so a later
  iteration could turn green on top of a rule violation. Fixed by reverting the file:
  [b69297d](https://github.com/aazinchenko/hirion_capstone/commit/b69297d).
- Honest note: no real run hit `REJECTED` -- the agent never broke a locator rule. That the gate does reject
  is shown separately (see 3).

## 3. Verification

- One command: `pnpm check`. Locator gate: [CheckLocatorRules.java](https://github.com/aazinchenko/hirion_capstone/blob/master/scripts/CheckLocatorRules.java)
  ([34e4f91](https://github.com/aazinchenko/hirion_capstone/commit/34e4f91)); spec gate:
  [spec-check.mjs](https://github.com/aazinchenko/hirion_capstone/blob/master/scripts/spec-check.mjs) -- also fails
  where OpenSpec alone passes: an empty spec tree, an archive with unfinished tasks
  ([e0899b7](https://github.com/aazinchenko/hirion_capstone/commit/e0899b7)).
- Negative control: on the RED SmokeTest from 385b663 the gate prints
  `REJECTED: SmokeTest.java:58 -- guessed positional class, not a role-based locator` and exits 1.
- Red first, then green:
  - [385b663](https://github.com/aazinchenko/hirion_capstone/commit/385b663) RED → [871ebab](https://github.com/aazinchenko/hirion_capstone/commit/871ebab) GREEN (SmokeTest);
  - [d65811a](https://github.com/aazinchenko/hirion_capstone/commit/d65811a) 32 tests RED → [1928822](https://github.com/aazinchenko/hirion_capstone/commit/1928822) GREEN (public coverage);
  - [022c27f](https://github.com/aazinchenko/hirion_capstone/commit/022c27f) RED → [9e5fc59](https://github.com/aazinchenko/hirion_capstone/commit/9e5fc59) GREEN (RegistrationTest).
- Known defects of the site stay green as `KNOWN BUG` tests (correct assertion + `expectedExceptions`), e.g.
  [FooterLegalTest.java#L82](https://github.com/aazinchenko/hirion_capstone/blob/master/src/test/java/ch/hirion/pub/FooterLegalTest.java#L82).
- **What went wrong:** I fixed the wrong cause of a red Profile precondition
  ([b90a2e1](https://github.com/aazinchenko/hirion_capstone/commit/b90a2e1)); the trace showed the label is
  "Email*" ([67109db](https://github.com/aazinchenko/hirion_capstone/commit/67109db)). Rule since: read the
  underlying error before fixing.

## 4. Maker ≠ checker

- Read-only reviewer sub-agent: [.claude/agents/locator-reviewer.md](https://github.com/aazinchenko/hirion_capstone/blob/master/.claude/agents/locator-reviewer.md)
  (tools Read / Grep / Glob / Bash, "do not edit anything"). A second Claude Code session also reviewed the
  author session against the live site code and ran live suites.
- What the reviews found and what was done:

| Review | Finding | Result |
|---|---|---|
| [review-2026-09-28.md](https://github.com/aazinchenko/hirion_capstone/blob/master/.agent-log/review-2026-09-28.md) | locator correct, but tasks ticked without a run | rule "quoted run per [x]" (see 1) |
| [review-2026-09-29.md](https://github.com/aazinchenko/hirion_capstone/blob/master/.agent-log/review-2026-09-29.md) | 3 Medium: KNOWN BUG preconditions, D1 passes on any valid mailto, D3 filter | [eddc3c8](https://github.com/aazinchenko/hirion_capstone/commit/eddc3c8), [737c8f2](https://github.com/aazinchenko/hirion_capstone/commit/737c8f2) |
| [review-2026-09-30.md](https://github.com/aazinchenko/hirion_capstone/blob/master/.agent-log/review-2026-09-30.md) | D9 check misses a late warning | [63ceb04](https://github.com/aazinchenko/hirion_capstone/commit/63ceb04); the reviewer's exact-text suggestion was NOT taken (would be falsely green) |
| [review-2026-09-30-control.md](https://github.com/aazinchenko/hirion_capstone/blob/master/.agent-log/review-2026-09-30-control.md) | "CV cannot be removed" could pass falsely | [af1fb90](https://github.com/aazinchenko/hirion_capstone/commit/af1fb90); 2 Low findings left open |

- The reviewer can be wrong too: one later claim that a line was still unfixed was false, so findings are
  checked in the code before anything changes ([autonomy-log #14](https://github.com/aazinchenko/hirion_capstone/blob/master/docs/autonomy-log.md)).

## 5. Specs first (SDD)

- Intent before code: [docs/intent.md](https://github.com/aazinchenko/hirion_capstone/blob/master/docs/intent.md)
  ([9956502](https://github.com/aazinchenko/hirion_capstone/commit/9956502)), then the first spec
  ([897ed3a](https://github.com/aazinchenko/hirion_capstone/commit/897ed3a)) before the first test
  ([385b663](https://github.com/aazinchenko/hirion_capstone/commit/385b663)). Every change commits `spec:` before `test:`.
- **Where the spec changed because reality did not match:**
  - the dashboard facts were recorded on a Pro trial, the test user is Free → DASH-7 rewritten to check the Pro
    gate, DASH-8 dropped ([a29b257](https://github.com/aazinchenko/hirion_capstone/commit/a29b257), [3511779](https://github.com/aazinchenko/hirion_capstone/commit/3511779));
  - the site normalises the phone number → SET-6 changed ([8467dce](https://github.com/aazinchenko/hirion_capstone/commit/8467dce));
  - reading the site's code said `?plan=bogus` selects no plan; a live guest probe showed Premium Trial
    ([25cec2f](https://github.com/aazinchenko/hirion_capstone/commit/25cec2f)) → a separate MODIFIED change for
    REG-7 ([bc91ab0](https://github.com/aazinchenko/hirion_capstone/commit/bc91ab0) → [d1b34a0](https://github.com/aazinchenko/hirion_capstone/commit/d1b34a0)).
- Archived changes with proposals, tasks and run evidence:
  [openspec/changes/archive/](https://github.com/aazinchenko/hirion_capstone/tree/master/openspec/changes/archive).

## 6. Permissions and safe live testing

- [.claude/settings.json](https://github.com/aazinchenko/hirion_capstone/blob/master/.claude/settings.json):
  the agent may edit only test classes and specs; it may NOT edit `JourneyCleanup.java`, `support/**`,
  `src/test/resources/**`, `scripts/**`, `pom.xml`, `package.json`, `openspec/config.yaml`, `docs/intent.md`,
  `AGENTS.md` or `settings.json` itself -- i.e. nothing that checks it, sets its goal or can hurt a real account.
- Real accounts on production, with the site owner's consent: email always `+hirion-qa-<millis>@`
  ([TestUser.java](https://github.com/aazinchenko/hirion_capstone/blob/master/src/test/java/ch/hirion/support/TestUser.java));
  [JourneyCleanup.java](https://github.com/aazinchenko/hirion_capstone/blob/master/src/test/java/ch/hirion/journey/JourneyCleanup.java)
  refuses any other address before a browser opens, deletes leftovers of an interrupted run and checks the
  deleted account can no longer sign in ([75720d0](https://github.com/aazinchenko/hirion_capstone/commit/75720d0);
  three dry runs quoted in the change-log). Live runs end with `deleted: ... no longer signs in`, e.g.
  [1800448](https://github.com/aazinchenko/hirion_capstone/commit/1800448).
- Contact form never really sent: `page.route()` mock ([cf7f443](https://github.com/aazinchenko/hirion_capstone/commit/cf7f443)).
- **What went wrong:** the allow rule for `mvn -q test` would also let a live run (`-Dqa.mailbox`) through
  silently. Closed by a hook that turns such a command into a human prompt
  ([59a5ebf](https://github.com/aazinchenko/hirion_capstone/commit/59a5ebf)). Not yet tested live.

## 7. Trust log and budget

- [docs/autonomy-log.md](https://github.com/aazinchenko/hirion_capstone/blob/master/docs/autonomy-log.md)
  ([2b3655d](https://github.com/aazinchenko/hirion_capstone/commit/2b3655d)): 16 rows, **9 downgrades** -- what
  went wrong, the evidence and the rule since.
- Budget written before each change, actuals after
  ([intent.md → Budget](https://github.com/aazinchenko/hirion_capstone/blob/master/docs/intent.md),
  [usage-by-change.py](https://github.com/aazinchenko/hirion_capstone/blob/master/scripts/usage-by-change.py),
  [1443466](https://github.com/aazinchenko/hirion_capstone/commit/1443466)). **Over budget:** planned ≤ $11,
  actual ≈ $124 at list price -- mostly the second review window and long sessions; analysed in intent.md.

## 8. What I decided vs what the agent did

Full list: [docs/decisions.md](https://github.com/aazinchenko/hirion_capstone/blob/master/docs/decisions.md).

**I decided:** the stack and the spec-driven workflow; scope, facts and budget in intent.md; every rule in
AGENTS.md and every allow / deny entry; that real accounts are used only with the owner's consent, one per run,
each run after my explicit "yes"; the leftover-account cleanup (not in the course plan); which findings are
defects (D7 not a defect, D8 / D9 regular checks, D14 a known bug); which reviewer findings to fix and which
to reject; every archive.

**The agent did:** drafted proposals, specs and tasks; wrote the tests and the fixes; healed locators inside
the loop; reviewed as a separate read-only sub-agent; wrote drafts of the logs and docs that I then approved.

## 9. Still open

- Video ≤ 2 min (plan step 29).
- Live test of the hook (`echo -Dqa.mailbox` in the author window must prompt).
- 2 Low findings of the control review.
