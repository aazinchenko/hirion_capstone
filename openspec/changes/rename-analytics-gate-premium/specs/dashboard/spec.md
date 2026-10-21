## MODIFIED Requirements

### Requirement: DASH-7 Analytics is Pro-only for a Free user
For a user on the Free plan the Analytics tab SHALL show the paid-plan gate: the heading "Unlock Hirion
Premium", the text "See your match analytics, Swiss salary benchmarks, and personalized opportunities to land
roles faster." and the link "Upgrade to Premium" to `/settings?section=plan`. The analytics content behind the
gate SHALL be hidden from assistive technology (`aria-hidden="true"`). (The site renamed the gate from "Pro" to
"Premium" by 2026-10-21; the Analytics tab button still carries the badge "Pro".)

#### Scenario: Free user sees the Pro gate on Analytics
- **WHEN** the signed-in Free test user opens `/dashboard` and clicks the button "Analytics"
- **THEN** the page shows the heading "Unlock Hirion Premium", the text "See your match analytics, Swiss salary
  benchmarks, and personalized opportunities to land roles faster." and the link "Upgrade to Premium" with
  `href="/settings?section=plan"`
