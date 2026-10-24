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

(none yet)
