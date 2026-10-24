# Change log -- fix-premium-ai-dialogs

## 2026-10-24: dialog texts from the first -Dqa.ai run

- First `-Dqa.ai` run (`../journey-premium-ai-2026-10-24.log`): testng `total="50" passed="48" failed="2"`;
  prem11 "waiting for getByRole(AriaRole.DIALOG).getByText("Worth addressing", ...setExact(true))"; prem12 "waiting for
  getByRole(AriaRole.DIALOG).getByText("Tailored CV", ...setExact(true)).first()"; prem13 PASS; JourneyCleanup
  "deleted: ... no longer signs in". Trace texts (stand-out): "What to highlight | ... | Skills | Keywords to use | ... |
  29 tailored applications left this month. | Based only on what is in your CV. ..."; (CV): "ATS-friendly | Classic |
  ... | Modern | ... | Try again | Closely tailored | Copy | .txt | PDF | 29 tailored applications left this month.".
  Facts commit `bc5bc2f`.

## Evidence for tasks

- 1.1 [x]: `PremiumAiTest` prem11 / prem12 updated (commit `3eb3909`); after the second run the counter pattern lost
  its `$` (see 2.1). `mvn -q test-compile` exit 0; `pnpm check:locators` -> "PASS: no forbidden patterns".
- 2.2 [x]: `pnpm exec openspec validate fix-premium-ai-dialogs --strict` -> "Change 'fix-premium-ai-dialogs' is valid";
  `pnpm check` exit 0 (`../pnpm-check-2026-10-24c.log`): "PASS: no forbidden patterns", public `total="63"
  passed="63" failed="0" skipped="0"`, "spec:check ok — specs: 11 · active changes: 1 · archived: 14".
- 2.1 [x]: third `-Dqa.ai` run 2026-10-24, finished 13:59:53 CEST, 04:26 min (human: "да, запусти ещё один прогон с
  -Dqa.ai=true (один аккаунт), потом заархивируй и сделай push"; `../journey-premium-ai-2026-10-24c.log`):
  `Tests run: 50, Failures: 0, Errors: 0, Skipped: 0`, BUILD SUCCESS; testng `total="50" passed="50"`;
  prem11_helpMeStandOut PASS (12.1 s), prem12_tailorMyCv PASS (13.0 s), prem13_writeCoverLetter PASS (7.6 s);
  JourneyCleanup "deleted: ...+hirion-qa-...@gmail.com no longer signs in"; `.auth` holds only `manual-user.json`.
- 2.1 (history): second `-Dqa.ai` run 2026-10-24, finished 13:44:44 CEST, 05:29 min (`../journey-premium-ai-2026-10-24b.log`,
  human-approved): testng `total="50" passed="49" failed="1"`; prem12 PASS (incl. "Copy" -> "Copied", now seen live),
  prem13 PASS; prem11 FAIL only on its last assertion "waiting for getByRole(AriaRole.DIALOG).getByText(
  Pattern.compile("^\d+ tailored applications left this month\.$"))" -- the trace snapshot shows the counter in
  one paragraph with the note: `["P",{"class":"text-xs text-muted-foreground"},"29 tailored applications left this
  month."," ","Based only on w...`. Fix: pattern without `$`; checked against that live text with a scratch Java
  program: "old stand-out=false fixed stand-out=true fixed cv=true". JourneyCleanup "deleted: ... no longer signs in".
  Not ticked: a fully green live run of prem11 is still missing (human decision: another -Dqa.ai run or accept).
