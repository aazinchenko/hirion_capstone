## Why

The first journey run with `-Dqa.ai=true` (2026-10-24 13:28, `../journey-premium-ai-2026-10-24.log`, 48/50, account
deleted) showed that two AI dialogs differ from the texts read from the site code: `prem11_helpMeStandOut` failed on
"Worth addressing" (not shown: the part appears only when the AI finds gaps) and `prem12_tailorMyCv` failed on
"Tailored CV" (not in the dialog). The dialog texts were read from the traces and recorded in docs/intent.md.
Human (2026-10-24): "Да, сделай change fix-premium-ai-dialogs по живым надписям, потом запусти ещё один прогон с
-Dqa.ai=true (один аккаунт), заархивируй и сделай push."

## What Changes

- MODIFIED `PREM-11`: required parts "What to highlight", "Keywords to use", button "Copy", monthly counter;
  "Worth addressing" optional.
- MODIFIED `PREM-12`: "ATS-friendly", "Closely tailored", "Classic", "Modern", buttons "Copy", ".txt", "PDF";
  "Copy" -> "Copied" (still to be seen live).
- `PremiumAiTest.prem11_helpMeStandOut` and `prem12_tailorMyCv` check these texts.

## Capabilities

### New Capabilities

(none)

### Modified Capabilities

- `premium-trial`: PREM-11 and PREM-12 dialog texts.

## Impact

- Test counts unchanged (journey 50, full 113); specs 72 requirements, 110 scenarios.
- One more live journey run with `-Dqa.ai=true`: one account (deleted by JourneyCleanup) and one generation per AI
  button, approved by the human.
