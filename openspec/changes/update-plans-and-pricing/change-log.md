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
