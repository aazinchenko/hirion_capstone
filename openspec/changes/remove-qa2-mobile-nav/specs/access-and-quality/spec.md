## REMOVED Requirements

### Requirement: QA-2 Navigation is reachable on a phone
**Reason**: Human decision 2026-10-05: known defect D3 (no "Sign in" and no burger menu at 375 px) is not a
defect; the mobile header is the intended design.
**Migration**: None. `MobileLayoutTest.qa2_signInReachableOnPhone` is deleted; QA-1 (no horizontal scroll at
375 px) stays.
