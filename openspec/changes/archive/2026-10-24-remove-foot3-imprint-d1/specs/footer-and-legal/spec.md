## REMOVED Requirements

### Requirement: FOOT-3 Imprint is complete
**Reason**: Human decision 2026-10-24: known defect D1 (unfilled "[FILL: ...]" templates and an invalid mailto on
`/imprint`) is closed as not relevant; the imprint content is the site owner's business, not a tested requirement.
**Migration**: None. `FooterLegalTest.foot3_imprintHasNoFillTemplates` and `foot3_imprintEmailIsValidMailto` are
deleted; FOOT-1 (link and HTTP 200) and FOOT-2 (heading "Imprint", title "Imprint, Hirion") still cover `/imprint`.
