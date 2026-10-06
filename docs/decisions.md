# Decisions -- who decided what

Human = the course participant. Author agent = the Claude Code session in `hirion_capstone` (`/opsx:apply`,
`/opsx:archive`, heal-loop). Reviewer = a second Claude Code session that reads the live site code, proposes
fixes, runs live suites after a human "да" and commits. Trust levels per task: `docs/autonomy-log.md`.
Budget vs actual: `docs/intent.md` "Budget".

## Human decisions

**Stack, tooling, workflow**
- Stack: Java 21 + Maven + Playwright for Java + TestNG; Node/pnpm only for OpenSpec (`c03ab0c`, `c594d68`).
- SDD with OpenSpec 1.13.0 pinned, called only as `pnpm exec openspec`; `spec:check` is part of `pnpm check`
  (`e0899b7`). Four changes, each propose -> apply -> archive; done = `specs: 9 · archived: 4` (`d1b34a0`).
- Scope, confirmed facts, done criteria and a budget per change written in `docs/intent.md` before any code
  (`9956502`).
- `AGENTS.md`: locator rules and their exceptions, the `role(...)` helper with `setExact(true)`, trust levels
  (3 inside a change, 1 outside) (`475f204`); rules learned from the downgrades added later (`59a5ebf`).
- Working agreement: a plan first, action only on "старт"; a spec commit before the test commit; every `[x]`
  in `tasks.md` needs evidence in the change's `change-log.md`.
- Maker != checker: tests are written in the author window, reviewed in a second window, and checked again by
  the read-only locator-reviewer subagent run by the human.

**Safety**
- The site owner consented to real test accounts: one disposable `+hirion-qa-<millis>@` account per live run,
  created only after an explicit "да", deleted by `JourneyCleanup` (`75720d0`); leftover cleanup at suite start.
- The author agent may not edit `JourneyCleanup`, `support/**`, fixtures, `scripts/**`, `pom.xml`,
  `docs/intent.md`, `AGENTS.md` or `.claude/settings.json` (`.claude/settings.json` deny list).
- A PreToolUse hook asks the human before any command with `-Dqa.mailbox` (`59a5ebf`).
- No real contact-form submits (mocked; the test counts only `/api/public/contact`, `404b0c2`), no OAuth, no
  Premium Trial or payments (Free is always selected before "See my matches"), no assertions on AI text.
- Heal-loop for journey tests runs without `-Dqa.mailbox`; other journey fixes are targeted
  (question 8 of change 3).

**Scope and specs**
- Change 2: AUTH-03 (wrong password) moved to change 3; QA-06 (axe) skipped; REG-1 / REG-2 added (`b2b0844`).
- Change 3, honest SDD option (a): only confirmed facts in the specs, wizard steps 2-3 included; open
  questions listed (`c4a6700`).
- Dashboard for a Free user: DASH-7 checks the Analytics Pro gate, DASH-8 (Save rate) out of scope
  (`a29b257`); DASH-11 View job and DASH-12 Preferences added (`7cd277f`).
- Settings: photo upload out of scope (`ecfe8f5`); SET-6 types Phone without spaces because the site
  normalises it (`8467dce`); no DEL-2 requirement for the deletion dialog (`1518130`); AUTH-6 does not assert
  the backend text "Invalid login credentials" (`347efd9`).
- Change 4, variant A: MODIFIED REG-7 only (`?plan=free` / `?plan=trial`, Back keeps the wizard data),
  confirmed first by a guest probe (`25cec2f`, `bc91ab0`); D11 / REG-1 not extended.
- Every archive is run by the human; the reviewer verifies the synced spec and commits it.

**Which findings are defects** (`docs/intent.md` "Known defects")
- KNOWN BUG tests: D1 (D14 was added in `d091494`, closed with D6 and D10 below).
- Fixed by the site: D2, D4, D5 (seen live 2026-10-06, their KNOWN BUG tests failed with "should have
  thrown"); now regular tests I18N-4, PUB-4, AUTH-3 (change `close-fixed-defects-d2-d4-d5`). D6, D10, D14 (seen
  live 2026-10-06 in a GitHub Actions journey run); now regular tests REG-10, SET-4, SET-2 (change
  `close-fixed-defects-d6-d10-d14`).
- Not a defect: D7, Phone is free text by design (`ecfe8f5`); D3, the mobile header without "Sign in" is
  the intended design (human decision 2026-10-05, change `remove-qa2-mobile-nav`, QA-2 removed).
- Regular checks instead of KNOWN BUG, because they did not show on a new Free user: D8 (`3511779`),
  D9 (`8467dce`).
- Recorded, no test in these changes: D11 (confirmed live), D12, D13 (`2d5dc5d`, `421abe4`).

**Review outcomes**
- Locator reviews 2026-09-28/29/30: which findings to fix now and which to accept (`.agent-log/review-*.md`,
  `737c8f2`, `63ceb04`); the exact-text suggestion for D9 was not taken (`f769028`).
- Final approval of the Pull Request (plan step 30).

## Agent decisions / actions

**Author agent (project folder)**
- Scenario tests from the spec via `/opsx:apply` in all four changes: SmokeTest (change 1, `385b663`), the
  public coverage tests (change 2, `d65811a`), `RegistrationTest` first version (`022c27f`), `DashboardTest`
  (`6afe69d`), `SettingsTest` (`d091494`), the change 4 tests (`895161a`).
- Concrete locator fixes per heal-loop iteration: SmokeTest FAQ (`871ebab`), RegistrationTest role picker
  (`9e5fc59`); every iteration in `.agent-log/heal-log.jsonl` and `heal-diff.patch`.
- DOM snippets for the heal loop (`FetchLiveDom` -> `.agent-log/dom-snippet.html`).
- Spec sync at archive (`/opsx:archive`).

**locator-reviewer (read-only subagent)**
- Review reports `.agent-log/review-2026-09-28.md`, `-29.md`, `-30.md`.

**Reviewer (parent folder)**
- Facts read from the site's JS bundles, recorded as ASSUMED until seen live (e.g. `b618ec9`, `2d5dc5d`).
- Diagnosis from traces and proposed fixes, applied after "старт" (e.g. `67109db`, `53d8df0`, `462cbc0`).
- Live suite runs after "да"; guest probes that create no account (D11, question 9).
- Drafts of `docs/autonomy-log.md`, this file and the budget actuals (`scripts/usage-by-change.py`), each
  reviewed by the human before commit.
