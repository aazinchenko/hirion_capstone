# Change log -- update-plans-and-pricing

## 2026-10-19/20: plans and prices changed on the site

- Public CI run 2026-10-19 07:48 UTC: `Tests run: 57, Failures: 6, Errors: 0, Skipped: 0` --
  `pub2_faqShowsAnswer` ("Yes, start free with weekly job alerts." not found), `pub3_pricingVisibleToGuest`
  (no button "Quarterly"), `pub3_yearlyIsDefault` / `pub3_backToYearly` ("CHF 120 billed yearly" not found),
  `pub3_monthlyPrice` ("CHF 15 billed monthly" not found), `pub3_quarterlyPrice` (TimeoutError on "Quarterly").
- Browser, guest, 2026-10-19: `#pricing` buttons `Monthly | pressed=false`, `Yearly | pressed=true`; links
  `"Start free" /signup?plan=free`, `"Choose Basic" /signup?plan=basic`, `"Start 7-day free trial" /signup?plan=trial`;
  Yearly `CHF 0`, `CHF 10` `/year`, `CHF 120` `/year`; Monthly `CHF 3` `/month`, `CHF 15` `/month`; FAQ answer
  "Get started for free and upgrade whenever you need automated job matching or personalized application support."
- Guest run 2026-10-19 `mvn test "-Dtest=RegistrationTest#reg7_*"` (no `-Dqa.mailbox`): `Tests run: 5, Failures: 5`;
  trace of `reg7_planTrialPreselectsTrial`: Monthly/Yearly `role=radio` (Yearly `aria-checked=true`), cards Free /
  Basic / Premium with `aria-pressed`, button "Start my free trial"; failure screenshot of `reg7_planFreePreselectsFree`:
  Free selected, button "Join for free".
- Guest probe 2026-10-20 (temporary test, removed with `git checkout` before any commit; submit never clicked):
  `[PROBE] /signup?plan=basic` -> `Free aria-pressed=false`, `Basic aria-pressed=false`, `Premium aria-pressed=true`,
  `buttons=[Back, Start my free trial]`; `/signup` after a click on Basic -> `buttons=[Back, Continue to payment] Basic=true`;
  after "Monthly" -> `monthly prices=[CHF 0, CHF 3, CHF 15] units=[/month, /month]`.
- 2026-10-20 first `pnpm check` with the new tests: `Tests run: 57, Failures: 1` -- `pub5_basicPlanCta`
  "Page URL expected to match regex: /signup\?(.*&)?plan=basic(&.*)?$ Received: https://hirion.ch/signup".
  Browser, guest: opening `https://hirion.ch/signup?plan=basic` -> `location.href` "https://hirion.ch/signup";
  `/signup?plan=free` keeps its query. Spec amended: the Basic CTA scenario checks the link `href`, D16 also
  covers the dropped parameter.
- `pnpm exec openspec validate update-plans-and-pricing --strict` -> "Change 'update-plans-and-pricing' is valid"
  (a first draft with MODIFIED PUB-3 was refused: "omits scenario(s) the current spec still has: "Quarterly price"").

## Evidence for tasks

- 1.1 [x]: `grep -n "Quarterly\|billed" src/test/java/ch/hirion/pub/SmokeTest.java` -> no output (exit 1);
  `pub3_quarterlyPrice` deleted, `pub5_basicPlanCta` checks `href="/signup?plan=basic"`.
- 1.2 [x]: `grep -rn "See my matches\|Premium Trial\|border-primary" src/test/java` -> only the two REG-7 test
  descriptions that keep the scenario names ("... with Premium Trial selected", "plan=trial preselects Premium
  Trial"); REG-9 clicks "Join for free" after the aria-pressed guard; `TestUser` comments say "Join for free".
- 1.3 [x]: `mvn -q test-compile` exit 0; `pnpm check:locators` -> "PASS: no forbidden patterns".
- 2.1 [x]: `grep -c "D12\|D16"` -> docs/intent.md 4, docs/decisions.md 2, docs/current-state.md 3, README.md 1
  (D12 FIXED, D16 KNOWN BUG, counts public 57 / journey 42 / full 99).
- 3.1 [x]: guest run 2026-10-20 `mvn -B test "-Dtest=RegistrationTest#reg7_*"` (no `-Dqa.mailbox`, no account):
  `Tests run: 7, Failures: 0, Errors: 0, Skipped: 0` (D16 passes as the expected failure).
- 3.3 [x]: `pnpm exec openspec validate update-plans-and-pricing --strict` -> "Change 'update-plans-and-pricing'
  is valid"; `pnpm check` exit 0: "PASS: no forbidden patterns", public suite `total="57" passed="57" failed="0"
  skipped="0"` (testng-results.xml), "spec:check ok -- specs: 9 · active changes: 1 · archived: 8".
- 3.2 [ ]: NOT met. Local live journey run 2026-10-20 (human's "да", `../journey-run-2026-10-20.log`):
  `Tests run: 42, Failures: 7, Errors: 0, Skipped: 0`; all 17 RegistrationTest tests passed (REG-9 clicked
  "Join for free" and landed on /dashboard), Settings 13/13, Dashboard 5/12; JourneyCleanup "deleted: ... no longer
  signs in". The 7 failures (`dash2`, `dash4`, `dash5` x2, `dash6`, `dash10`, `dash11`) are "no job card in the
  Job Feed after 30 s": the site changed the Free dashboard too. Trace and failure screenshot: the Job Feed of a
  Free user is a locked teaser "We found 2 jobs for you" with blurred rows (title, location, match %), the text
  "Unlock them with Basic: every week the jobs that fit your profile best, each with a match score. Or go Premium
  for daily alerts and a CV and cover letter for each job." and the links "Unlock with Basic"
  (`href="/settings?section=plan"`) and "See all plans"; no "View job", "Save" or "Hide". The feed server function
  returns the jobs with `id` "locked-0"/"locked-1", empty company and url, and `locked`; another one returns
  `teaser`. Out of the scope written for this change: needs a human decision on the dashboard spec.

## 2026-10-20: extended to the dashboard (human decision)

- Teaser DOM from the trace of `dash10_noReplacementCharsInTitles`: `H3` "We found 2 jobs for you"; rows inside
  `DIV aria-hidden="true"`, each `DIV` with a blurred block (title, location) and `SPAN` "88" "%"; `P` "Unlock them
  with Basic: ..."; `A href="/settings?section=plan"` "Unlock with Basic" and `A href="/settings?section=plan"`
  "See all plans".
- Spec commit `9a8215f`: ADDED "DASH-2 Free Job Feed is a locked teaser", REMOVED DASH-2 "Job card actions",
  DASH-4, DASH-5, DASH-6, DASH-10, DASH-11; `openspec validate --strict` -> "is valid".
- 1.4 [x]: `grep -c "@Test" src/test/java/ch/hirion/journey/DashboardTest.java` -> 6;
  `grep -n "View job\|pickCard\|feedCards\|usedTitles" ...DashboardTest.java` -> only the class comment and
  `JOB_ACTIONS = List.of("View job", "Save", "Hide")` (the "no job action" check); `mvn -q test-compile` exit 0;
  `pnpm check:locators` -> "PASS: no forbidden patterns".
- 2.2 [x]: README "93 automated tests (public 57 + journey 36)", "Signed-in user journey (36 tests ...)";
  current-state "journey 36 (Registration 17, Dashboard 6, Settings 13) + JourneyCleanup; full suite 93"; D8 noted
  as not observable for Free in intent.md, decisions.md, current-state.md. (README "59 requirements, 93 scenarios"
  is updated after the archive, when the main specs change.)
- `pnpm check` exit 0 after the dashboard edits: public `total="57" passed="57" failed="0"`, "spec:check ok --
  specs: 9 · active changes: 1 · archived: 8".
