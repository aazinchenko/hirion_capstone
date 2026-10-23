## Why

The human asked on 2026-10-23 for a full scan of hirion.ch ("new behaviour on the site"): every page and every
feature, then specs updated. What the scan did:

- Live, guest (browser, 2026-10-23): `/`, `/login`, `/signup` step 1, `/forgot-password`, `/reset-password`,
  `/contact`, `/privacy`, `/terms`, `/imprint`, `/survey`, `/unsubscribe`, `/checkout/return`, `/onboarding`,
  `/employer/`, `/employer/abc`, `/admin`.
- Existing tests against the live site, guest only, no account: public suite `Tests run: 57, Failures: 0`
  (`../public-scan-2026-10-23.log`); `RegistrationTest` without `-Dqa.mailbox` `Tests run: 17, Failures: 0,
  Skipped: 3` (`../reg-guest-scan-2026-10-23.log`). So the public specs and signup steps 1-4 still hold.
- Nightly CI journey run 2026-10-23 07:32 UTC (run 39918465621, Allure report): 92/93, the only failure is
  `DASH-2 Locked teaser for a Free user`: "Locator expected to have attribute 'href': /settings?section=plan
  Received: /settings?section=plan&buy=basic" for the link "Unlock with Basic".
- Site code diff, supporting only (`index-D8wCQ5XQ.js` / `dashboard-DIUGP6tg.js` of 2026-10-21 against
  `index-TZr5-rLq.js` / `dashboard-Ck5JA8pk.js` / `settings-CvXQ9SRQ.js` of 2026-10-23): the teaser link gets
  `search:{section:"plan",buy:"basic"}`; Settings, on `buy=basic`, replaces the URL with `?section=plan` and starts
  the Basic checkout at once; new texts "Free trial ends on {date}." / "No card on file, nothing will be charged."
  (trial without a card) and a promo code QATEST25 "25% off your first payment"; account deletion signs out only
  the local session. Nothing else changed in the strings of these bundles.

So one requirement is out of date (DASH-2, nightly red), and the scan found pages that exist (at least since
2026-10-20) but have no spec: Google / Apple sign-in buttons, `/reset-password`, `/survey`, `/unsubscribe`,
`/checkout/return`, and three more private pages that redirect guests.

## What Changes

- MODIFIED `DASH-2 Free Job Feed is a locked teaser` (spec `dashboard`): "Unlock with Basic" points to
  `/settings?section=plan&buy=basic` (starts the Basic checkout, never clicked); "See all plans" stays
  `/settings?section=plan`.
- MODIFIED `AUTH-1 Guests are redirected to sign in` (spec `access-and-quality`): also `/onboarding`,
  `/employer/` and `/admin` (new scenario).
- ADDED `AUTH-7 Login and signup offer Google and Apple sign-in` (presence only, never clicked).
- ADDED `AUTH-8 Reset-password page without a valid link`.
- New capability `email-link-pages` (test group LINK, pages opened from emails): ADDED `LINK-1 Feedback survey
  page`, `LINK-2 Unsubscribe page without a valid link`, `LINK-3 Checkout return page without a session`.
- `docs/intent.md`: facts of the scan, group LINK, candidate defects D17-D19, code-only facts (ASSUMED).

## Capabilities

### New Capabilities

- `email-link-pages`: survey, unsubscribe and checkout-return pages opened by a guest without a valid link.

### Modified Capabilities

- `dashboard`: DASH-2 link targets.
- `access-and-quality`: AUTH-1 more private pages; AUTH-7, AUTH-8.

## Out of scope (human decisions needed, see Open questions)

- Clicking "Continue with Google / Apple", "Unlock with Basic", "Choose Basic", any checkout or promo code
  (AGENTS.md: no OAuth, no payments).
- `/survey?id=<real id>` submit (would store an answer), real unsubscribe links, `/onboarding` after OAuth,
  `/employer` and `/admin` for signed-in users (need roles the test user does not have).
- Trial-only Settings texts ("Free trial ends on ...") -- the test user is always Free.

## Open questions

1. D17 (`/survey` question 2: the rating options 2, 3 and 4 have no accessible name) -- KNOWN BUG test or note
   only? The spec keeps the correct SHALL "Every answer option SHALL have an accessible name"; no scenario yet.
2. D18 (`/checkout/return` without a session: "Go to my account" carries `checkout=success`) -- defect or by
   design? Not asserted.
3. D19 (`/forgot-password`: the link "Back to home" points to `/login`, on `/login` and `/signup` it points to
   `/`) -- defect or by design? Not in a spec yet.
4. LINK-1 "Survey without an id sends nothing" is read from the site code; task 1.3 confirms it live with a
   mocked network before the scenario counts as CONFIRMED.

## Impact

- Specs 9 -> 10 capabilities; requirements 54 -> 59, scenarios 89 -> 97 (to verify with `openspec`).
- Public suite 57 -> 65 tests (8 new guest tests, no account); journey 36 unchanged (DASH-2 updated); full 93 ->
  101.
- One journey run with a real test account for DASH-2 (only after the human's "да", or the nightly CI run).
