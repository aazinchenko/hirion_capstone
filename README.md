# Hirion Capstone: Agentic Engineering for QA

End-to-end test suite for [hirion.ch](https://hirion.ch), a Swiss AI job-search platform. An AI coding agent (Claude Code) wrote most of the test code. I set the scope, the rules and the safety limits, and I checked the agent's work before anything was accepted.

The project is the capstone of the *Agentic Engineering Crash Course*. Its question: how do you let an AI agent do real QA work on a live production site and still trust the result?

| | |
|---|---|
| **Tests** | 94 automated tests, all green on the last full run |
| **Specs** | 9 capabilities, 59 requirements, 93 scenarios (OpenSpec) |
| **Defects found on the live site** | 12 reported, 7 pinned by `KNOWN BUG` tests; 3 of them (D2, D4, D5) fixed by the site, now regular tests |
| **Timeline** | 6 days, 85 commits (25–30 Sep 2026) |
| **Stack** | Java 21 · Maven · Playwright for Java 1.55 · TestNG 7.11 · OpenSpec 1.13 |

---

## What is tested

**Public site** (53 tests, run in parallel, no account needed)

| Area | What the tests check |
|---|---|
| Home page | hero, pricing toggle, FAQ accordion, calls to action |
| Footer and legal pages | every footer link answers HTTP 200; Imprint, Privacy and Terms content |
| Localization | EN / DE / FR / IT switch, translated headings and buttons |
| Contact form | validation, honeypot field, confirmation toast; the network is mocked, nothing is really sent |
| Auth pages | login and signup forms, 404 page, autocomplete attributes |
| Quality | mobile layout at 375 px, SEO basics, `robots.txt` |

**Signed-in user journey** (40 tests, one real disposable account per run)

| Area | What the tests check |
|---|---|
| Registration | 4-step signup wizard, plan preselection by URL, Back keeps the entered data |
| Dashboard | job feed, match score, job details, Pro-only features hidden for Free users, preferences |
| Settings | profile, CV upload, password change, plan and billing view |
| Account deletion | the test account is deleted at the end and can no longer sign in |

---

## How I worked with the agent

Every piece of work followed the same nine steps. The agent did the heavy lifting; a human owned the goal, the approval, every live run and the final archive.

```
1. intent.md        human writes goals, confirmed facts, budget — before any code
2. propose          agent drafts specs and tasks
3. approve          human reads and approves the specs
4. tests RED        agent writes one test per scenario and shows it fails
5. heal             heal-loop fixes broken locators, max 5 attempts
6. pnpm check       automated gate with an exit code
7. review           a separate read-only reviewer agent checks the work
8. live run         only after an explicit human "yes"
9. archive          approved specs become the source of truth
```

---

## Practices used

| Practice | Problem it solves | Where to look |
|---|---|---|
| **Spec-driven development** | Without a written spec there is no way to tell if a test checks the right thing. | `docs/intent.md`, `openspec/specs/` |
| **Context engineering** | The agent forgets rules between sessions and cannot see the site. | `AGENTS.md`, `openspec/config.yaml`, `FetchLiveDom.java` |
| **Gate, not status** | "All tests pass" from the agent is just words; a command's exit code is not. | `pnpm check`, `scripts/CheckLocatorRules.java`, `scripts/spec-check.mjs` |
| **Verification (RED → GREEN)** | A test that never failed may test nothing. | commit history: spec → RED → GREEN |
| **Loop engineering** | "Please fix it" in a chat has no limit, no rule check and no trace. | `scripts/heal-loop.sh`, `.agent-log/heal-log.jsonl` |
| **Maker ≠ checker** | The author of the code is blind to its own mistakes. | `.claude/agents/locator-reviewer.md`, `.agent-log/review-*.md` |
| **Permissions and guardrails** | A rule in a text file is only a request; the agent can ignore it. | `.claude/settings.json` (allow / deny / hook) |
| **Safe live testing** | No staging server: tests create real accounts on production. | `TestUser.java`, `JourneyCleanup.java`, `scripts/guard-live-run.mjs` |
| **Budget** | Agent time costs money and is easy to underestimate. | `docs/intent.md` → Budget |
| **Trust log** | One trust level for a whole project does not work. | `docs/autonomy-log.md`, `docs/decisions.md` |

---

## Tricks that made it work

- **Known bugs stay green.** A defect on the site gets a test that asserts the *correct* behaviour and expects it to fail (`expectedExceptions`). The suite stays green while the bug exists and turns red the day it is fixed. The agent cannot "heal" such a test by weakening the assertion.
- **The agent cannot weaken its own checks.** The gate scripts, the test-user code, `pom.xml`, `package.json`, `AGENTS.md` and the permissions file itself are on the agent's deny list.
- **A rejected fix is rolled back.** If a heal attempt breaks a locator rule, the file is reverted before the next try, so a rule violation can never turn green.
- **The agent repairs locators from the live page, not from memory.** On every red run the loop saves the real HTML and hands it to the agent.
- **Disposable accounts with a hard guard.** Test emails always look like `name+hirion-qa-<timestamp>@gmail.com`. The cleanup code refuses to touch any other address and checks that the deleted account can no longer sign in.
- **No orphan accounts.** If a run is interrupted, the next run first deletes the leftover user.
- **Live runs need a human "yes".** A Claude Code hook turns any command that would create a real account into a confirmation prompt, even when the command itself is allowed.
- **Every ticked task needs proof.** A checkbox in `tasks.md` is accepted only with a quoted test run in the change log.
- **Facts are CONFIRMED or ASSUMED.** Anything read from the site's code stays ASSUMED until someone sees it on the live site. One such fact turned out to be wrong.
- **The contact form is never really submitted.** `page.route()` intercepts the request, so the site owner gets no test spam.

---

## Time and cost

**Measured**

- 6 calendar days from the first commit to a green 95-test suite, working alone with the agent.
- A broken locator was healed automatically in **2 min 42 s** (SmokeTest) and **about 5 min** (RegistrationTest, 3 attempts), with no manual code edits.
- The full signed-in journey (signup → dashboard → settings → account deletion) runs unattended in **2 min 43 s**.
- Agent usage: 932k output tokens, about **$124** at list price. The work ran on a subscription, so this is an estimate, not an invoice. Planned budget was $11. Why it ran over is analysed in `docs/intent.md`.

**Estimated** *(assumption, not measured)*

- A manual pass over the same 94 scenarios at ~2 minutes each takes about 3 hours per release. The automated suite replaces that with one command and a few minutes of runtime.

---

## How to run

Requirements: Java 21, Maven, Node.js with pnpm.

```bash
pnpm install
mvn -q test                                  # public suite, safe to run anytime
pnpm check                                   # locator rules + public suite + spec validation
```

The signed-in journey creates a **real** account on hirion.ch and deletes it at the end. It was run only with the site owner's consent:

```bash
mvn -q test -Dsuite=testng-journey.xml -Dqa.mailbox=<your-gmail-name>
```

---

## Repository layout

```
AGENTS.md                 rules every agent session reads
docs/                     intent and budget, trust log, decisions, current state
openspec/specs/           9 approved capability specs (source of truth)
openspec/changes/archive/ 5 completed changes with proposals, tasks and run evidence
scripts/                  gates: locator rules, spec check, heal loop, live-run guard
src/test/java/.../pub     public-site tests
src/test/java/.../journey signed-in journey tests and account cleanup
.claude/                  agent permissions, reviewer sub-agent, OpenSpec commands
.agent-log/               heal-loop log and reviewer reports
```

---

**Author:** Anatolii · Agentic Engineering Course, 2026
