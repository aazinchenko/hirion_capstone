## Why

Change 1 (`add-home-smoke`) covers only the home page (group PUB). The rest of the public site has no
browser test: footer and legal pages, the language switcher, the contact form, the guest auth pages and
basic quality signals (mobile, console, SEO). This change covers groups FOOT, I18N, CONT, AUTH and QA
from `docs/intent.md` and pins known defects D1, D2, D3 and D5.

## What Changes

- Four new capability specs, one per theme:
  - `footer-and-legal` (FOOT): footer links, `/privacy`, `/terms`, `/imprint`.
  - `localization` (I18N): language menu EN / FR / DE / IT, switching, persistence, `<html lang>`.
  - `contact-form` (CONT): `/contact` fields, client validation, honeypot, submit with a mocked network.
  - `access-and-quality` (AUTH + QA): guest redirects, `/login`, `/forgot-password`, 404 page,
    mobile layout at 375 px, console errors, SEO basics.
- New TestNG classes in package `ch.hirion.pub` (run by the existing `testng-public.xml`), one `@Test`
  per spec scenario, guest context only.
- Known defects keep the correct SHALL in the spec; their tests are KNOWN BUG
  (`expectedExceptions = AssertionFailedError.class`): D1 (imprint placeholders), D2 (`<html lang>`
  stays `en`), D3 (no mobile navigation at 375 px), D5 (no `autocomplete` on `/login`).
  D4 is already covered by `home-page` PUB-4 and is not repeated.
- The contact form is never really submitted: tests either stop at client validation or intercept
  every non-GET request with `page.route(...)`. No sign-in, no password-reset email, no signup.

## Capabilities

### New Capabilities
- `footer-and-legal`: footer link targets and the three legal pages, including a complete imprint (D1).
- `localization`: language menu, switching to another language, persistence in `localStorage`
  `hirion.lang`, and `<html lang>` following the chosen language (D2).
- `contact-form`: `/contact` form fields, required / email validation, honeypot, mocked submit.
- `access-and-quality`: guest redirects to `/login`, login and forgot-password pages (D5), 404 page,
  mobile layout and navigation at 375 px (D3), no console errors, SEO basics on `/`.

### Modified Capabilities
- (none -- `home-page` is unchanged; D4 stays there)

## Impact

- Adds test classes under `src/test/java/ch/hirion/pub/`. May add a small page-open helper to
  `support/BaseTest` shared with `SmokeTest`. No production code.
- Runs against `https://hirion.ch` (or `-DbaseUrl`); read-only for the site: nothing is submitted.
- `pnpm check` afterwards: locator rules, `mvn -q test`, `spec:check`.

## Sources of the strings in the specs

- `docs/intent.md` Facts (CONFIRMED by a human): language menu and `hirion.lang`; guest redirect of
  `/dashboard` and `/settings` to `/login` "Welcome back"; contact form h1, labels, button, honeypot.
- OBSERVED by the agent in the server-rendered HTML (curl, 2026-09-28), **not yet confirmed by a human
  in a browser**:
  - Footer links: Privacy `/privacy`, Terms `/terms`, Imprint `/imprint`, Contact `/contact`.
  - h1 / `<title>`: "Privacy Policy" / "Privacy Policy, Hirion"; "Terms of Service" /
    "Terms of Service, Hirion"; "Imprint" / "Imprint, Hirion".
  - `/imprint`: 8 occurrences of `[FILL:` in the HTML (7 visible + `mailto:[FILL: e.g. info@hirion.ch]`).
  - `/login`: h1 "Welcome back", labels Email / Password, button "Sign in", links "Forgot password?"
    -> `/forgot-password` and "Create one" -> `/signup`; inputs `#email` / `#password` have no
    `autocomplete`.
  - `/forgot-password`: h1 "Reset your password", label Email, button "Send reset link", link
    "Sign in" -> `/login`.
  - Unknown path: HTTP 404, h1 "404", text "Page not found", link "Go home" -> `/`.
  - `/`: `<title>` "Hirion, Swiss jobs matched to your CV, sent by email", meta description
    "Stop scrolling job boards. Hirion scans every Swiss source and delivers only the roles that match
    your profile.", canonical `https://hirion.ch/`, h1 "Your AI job agent for Switzerland.";
    `/robots.txt` and `/sitemap.xml` return 200.

## Open questions (ASSUMED -- drafts until a human confirms on the live site; 1, 2, 4, 5 confirmed)

1. Value stored in `hirion.lang` after choosing DE: `"de"`. CONFIRMED in a browser 2026-09-28.
2. German hero h1 after switching to DE: "Dein KI-Job-Agent für die Schweiz." (line break between
   "Agent" and "für"). CONFIRMED in a browser 2026-09-28; I18N-2 and I18N-3 now assert it exactly.
3. What the contact form sends on submit (URL / method) and which confirmation text it shows after a
   successful response. The spec only requires one outgoing non-GET request carrying the entered
   email; the confirmation text is left out until confirmed. STILL OPEN: not checked on purpose,
   it would need a real submit.
4. The desktop header has a "Sign in" link: CONFIRMED in a browser 2026-09-28 (`<a>`, text
   "Sign in"). At 375 px "Sign in" is not visible and there is no menu button (D3 still present).
5. QA-1 (no horizontal scroll at 375 px) and QA-3 (no console errors on `/`, `/contact`, `/login`,
   `/privacy`, `/terms`, `/imprint`): both CONFIRMED in a browser 2026-09-28.
