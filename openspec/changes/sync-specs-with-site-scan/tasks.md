## 1. Tests (scenario tests first)

- [ ] 1.1 `DashboardTest.dash2_*`: "Unlock with Basic" `href="/settings?section=plan&buy=basic"`, "See all plans" `href="/settings?section=plan"`; neither is clicked. Verify: `mvn -q test-compile` exits 0; `pnpm check:locators` prints "PASS: no forbidden patterns".
- [ ] 1.2 `AuthPagesTest`: one test per new scenario of AUTH-1 (other private pages), AUTH-7 (login, signup) and AUTH-8 (reset-password); the Google / Apple buttons are only checked for visibility, never clicked. Verify: run them with mvn and quote the result lines.
- [ ] 1.3 New `EmailLinkPagesTest` (package `pub`, added to `testng-public.xml`): LINK-1 (two scenarios; "sends nothing" with the catch-all `page.route` of `ContactFormTest`, counting requests to `/api/public/survey-response`), LINK-2, LINK-3. Verify: run with mvn and quote the result lines; LINK-1 "sends nothing" passing = the code-read behaviour is CONFIRMED live.
- [ ] 1.4 `docs/intent.md`, `docs/current-state.md`, README counts: group LINK, D17-D19 candidates, new counts. Verify: `grep -n "D17\|D18\|D19\|LINK" docs/intent.md`.

## 2. Gate

- [ ] 2.1 One journey run with a real test account (only after the human's "да"), or the next nightly CI journey run. Verify: `Tests run: 36, Failures: 0`, DASH-2 PASS, JourneyCleanup "deleted"; quote in `change-log.md`.
- [ ] 2.2 Run `pnpm exec openspec validate sync-specs-with-site-scan --strict` and `pnpm check`; quote the summary lines in `change-log.md`.
