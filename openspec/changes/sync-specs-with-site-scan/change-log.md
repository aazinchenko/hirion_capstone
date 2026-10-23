# Change log -- sync-specs-with-site-scan

## 2026-10-23: full site scan

- Human (2026-10-23): "На сайте Хирион.ЦХ новая поведение ... просканировал сайт ... по всему функционалу ... проапдейтил спеки".
- Guest suites against the live site, no account created: `mvn test` (public) -> `Tests run: 57, Failures: 0,
  Errors: 0, Skipped: 0` (`../public-scan-2026-10-23.log`); `mvn test -Dsuite=testng-journey.xml
  -Dtest=RegistrationTest` without `-Dqa.mailbox` -> `Tests run: 17, Failures: 0, Errors: 0, Skipped: 3`
  (`../reg-guest-scan-2026-10-23.log`).
- Nightly CI run 39918465621 (2026-10-23 07:32 UTC): journey job failure, public job success; Allure summary
  `failed 1, passed 92, total 93`; failed test "DASH-2 Locked teaser for a Free user": "Locator expected to have
  attribute 'href': /settings?section=plan / Received: /settings?section=plan&buy=basic".
- Live browser, guest (2026-10-23):
  - `/login`: buttons "Continue with Google", "Continue with Apple", "Show password", "Sign in"; text "OR"; links
    "Back to home" -> `/`, "Forgot password?", "Create one".
  - `/signup` step 1: "Continue with Google", "Continue with Apple", "OR", "Already have an account? Sign in".
  - `/forgot-password`: link "Back to home" -> `/login` (D19 candidate).
  - `/reset-password`: title "Set a new password — Hirion", h1 "Set a new password", "This reset link is invalid
    or has expired. Request a new one.", link "Send reset link" -> `/forgot-password`.
  - `/survey` -> `/survey?id=`: title "Share your feedback, Hirion", h1 "Help us make Hirion better", questions
    1-6, button "Send feedback" (not clicked); radios `q2-4`, `q2-3`, `q2-2` have empty labels (D17 candidate).
  - `/unsubscribe`: title "Unsubscribe — Hirion", h1 "Invalid link", "This unsubscribe link is invalid or has
    expired."
  - `/checkout/return`: h1 "No session found", "We couldn't find a checkout session in this URL.", link "Go to my
    account" -> `/settings?section=plan&checkout=success` (D18 candidate).
  - `/onboarding`, `/employer/`, `/employer/abc`, `/admin` -> `/login` "Welcome back".
  - `/imprint` still has "[FILL: ...]" and `mailto:[FILL: e.g. info@hirion.ch]` (D1 open).
- Site code, supporting only: see proposal.md "Why".

## Evidence for tasks

(none yet)
