# Autonomy log -- Capstone Hirion.ch

For every significant piece of work: the trust level chosen for it, who decided, the evidence, and why that
level. Downgrades are marked **⬇** -- they are the useful rows: each one names what went wrong and the rule
that has applied since. Three questions behind every row: how fast would I notice a mistake? how cleanly can
I roll it back? what evidence convinces me?

Course levels: 1 Assistant (agent proposes, human decides) · 2 Assistant → agent · 3 Agent (reaches the goal
with evidence) · 4 Agents (parallel subagents) · 5 Autonomous agents (human only on exceptions).
`AGENTS.md` maps them to this repo: level 3 inside an OpenSpec change (`/opsx:apply`, limited by
`.claude/settings.json`), level 1 outside a change.

Actors: **human** -- the course participant; **author agent** -- the Claude Code session in `hirion_capstone`
that runs `/opsx:apply`, `/opsx:archive` and heal-loop; **reviewer** -- a second Claude Code session that
reviews against the live site code, runs the live suites after a human "да" and commits;
**locator-reviewer** -- a read-only subagent (`.claude/agents/locator-reviewer.md`).

| # | Work | Level | Who decided | Evidence | Why this level |
|---|------|-------|-------------|----------|----------------|
| 1 | Stack, OpenSpec 1.13.0 pinned to `pnpm exec`, `AGENTS.md` trust levels, `docs/intent.md` (scope, facts, budget) | 1 · Assistant | human | `c03ab0c`, `e0899b7`, `475f204`, `9956502` | policy and scope: the agent may propose, it does not decide what it is allowed to do |
| 2 | The harness that controls the agent: `CheckLocatorRules.java`, `FetchLiveDom`, `heal-loop.sh` | 1 · Assistant | human | `34e4f91`, `f0b64d6`, `3cd80a6`; `scripts/**` is `deny` in `.claude/settings.json` | human-owned: the author agent it guards cannot edit it |
| 3 | ⬇ Heal of the stale FAQ locator in SmokeTest (PUB-02) | 3 → 2 | locator-reviewer found it; human re-ran and approved | `871ebab`; `.agent-log/review-2026-09-28.md`: the locator fix was correct, but the heal agent ticked tasks 2.1, 3.1, 3.2 after running only one test method | a green checkbox without a run is not evidence. Since then every `[x]` in `tasks.md` needs a quoted run in `change-log.md` |
| 4 | ⬇ heal-loop kept a fix that CheckLocatorRules had rejected | loop rule tightened | human | `b69297d`: the rejected file is now reverted before the next iteration | without the revert a later iteration could turn GREEN on top of a rule violation |
| 5 | ⬇ Public coverage, 32 scenario tests incl. KNOWN BUG D1-D5 (change 2) | 3 → 2 for KNOWN BUG tests | locator-reviewer found it; human approved the fixes | `.agent-log/review-2026-09-29.md` #1-#3; fixes `eddc3c8`, `737c8f2` | a KNOWN BUG test could keep "passing" after the fix, or pass on a broken precondition. Since then preconditions use `org.testng.Assert`, never the expected exception |
| 6 | Deleting the test user: `JourneyCleanup`, `TestUser`, `support/**`, fixtures | 1 · agent denied | human | `75720d0`, `7b21f12`; `deny` for `JourneyCleanup.java`, `support/**`, `src/test/resources/**`; guard `\+hirion-qa-\d+@` checked before any action | a mistake here can delete a real account |
| 7 | Every live run that creates a real account on hirion.ch | 1 per run | human, explicit "да" each time | live runs quoted in the `change-log.md` of changes 3 and 4 (e.g. 37/37 `1800448`, 95/95 `7c5319a`, 40/40 `895161a`); each ends with "deleted: ... no longer signs in" | the site owner consented to one disposable account per run, not to an agent creating accounts at will |
| 8 | ⬇ Healing the journey tests | 3 → 2 | human (open question 8 of change 3) | RegistrationTest healed WITHOUT `-Dqa.mailbox`, GREEN on iteration 3 (`9e5fc59`); later fixes are targeted, human-approved commits based on the trace | every heal iteration with a mailbox would create and delete a real account |
| 9 | ⬇ Dashboard facts in `docs/intent.md` | 3 → 2 | reviewer found it; human decided DASH-7 / DASH-8 | `1e44064`, `3511779`, `a29b257`: the facts had been recorded on a trial (Pro) account; the test user is Free (AI buttons and Analytics are Pro-only) | a fact is tied to the account it was seen on; Free behaviour is re-checked from the site code and live |
| 10 | ⬇ Diagnosis of the failing Profile precondition (SettingsTest) | reviewer downgraded itself | reviewer; human approved the fix | `b90a2e1` fixed the wrong cause; the trace showed `getByLabel("Email", exact)` finds nothing because the label is "Email*" (D14) -- `67109db`; correction logged in `change-log.md` | rule since: read the swallowed underlying error before fixing |
| 11 | Which findings are defects: D7 not a defect, D8 and D9 regular checks, D14 KNOWN BUG, AUTH-6 without the toast text | 1 · Assistant | human | `ecfe8f5`, `3511779`, `8467dce`, `347efd9`; design.md of the archived change 3 | a KNOWN BUG test that never fails, or a wrong defect, misleads every later run |
| 12 | ⬇ Facts read from the site's JS bundles | ASSUMED until seen live | human chose a guest probe | `25cec2f`: the code reading said `?plan=bogus` selects no card; live it selects Premium Trial | code reading is a hypothesis; only a live check makes a spec string CONFIRMED |
| 13 | Locator reviews of changes 1-3 | 4 · separate read-only subagent | human ran it in the author window | `.agent-log/review-2026-09-28.md`, `-29.md`, `-30.md` (APPROVE) | maker ≠ checker: a fresh context finds what the author and the reviewer missed |
| 14 | ⬇ Acting on the locator-reviewer output | output treated as data | reviewer verified each finding; human approved | review 2026-09-30: all 10 findings checked in the code; the suggested exact match for D9 was NOT taken (unknown full texts would make the check falsely green, `63ceb04`); a later claim that `SettingsTest:144` was unfixed was wrong | a reviewer can be wrong too; its findings are checked against the code before anything changes |
| 15 | ⬇ Syncing the delta spec at archive (change 4) | 3, verified line by line | reviewer | the author agent's first sync removed REG-7 without inserting the new block; it restored and redid it. Before `d1b34a0` the reviewer compared REG-7 with the delta and the other requirements with HEAD | an archive rewrites the source of truth; a silent loss would only show in a later change |
| 16 | Tests of change 4 via `/opsx:apply` | 3 · Agent | author agent; reviewer checked that the tests are not vacuous | `895161a`: 3 guest tests green on the first run (facts confirmed before the spec), journey 40/40 live | inside a change with confirmed facts the agent reaches the goal with evidence; no account is created in its own runs |

## Escalation / de-escalation

- **Escalations we deliberately did NOT make:** heal-loop never runs with `-Dqa.mailbox`; neither agent
  starts a live run without "да"; nothing is merged without a human; the author agent never commits the
  archive -- the reviewer verifies and commits it.
- **Default de-escalation (always level 1):** `.claude/settings.json`, `scripts/**`, `support/**`,
  `JourneyCleanup.java`, `src/test/resources/**`, `pom.xml`, `package.json`, `openspec/config.yaml`,
  `docs/intent.md`, `AGENTS.md` -- whatever level the rest of the work runs at.
- **Evidence rules that came out of the downgrades:** every `[x]` needs a quoted run (#3); preconditions never
  count as a known bug (#5); read the underlying error before fixing (#10); a fact from site code stays
  ASSUMED until seen live (#9, #12); reviewer output is verified in the code (#14).
