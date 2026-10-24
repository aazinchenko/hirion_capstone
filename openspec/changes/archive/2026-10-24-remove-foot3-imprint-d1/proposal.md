## Why

FOOT-3 requires that `/imprint` names the operator, address, contact email and register data without unfilled
templates. The site shows 7 "[FILL: ...]" templates and `mailto:[FILL: e.g. info@hirion.ch]`; this was recorded as
known defect D1 and covered by two KNOWN BUG tests. On 2026-10-24 the human closed D1 as not relevant ("эти все
неактуальны или пофикшены"; then: "сделай change: убери FOOT-3 и KNOWN BUG тест D1, как с D3, весь цикл сам, потом
push"). The templates were still visible live on 2026-10-24, so this is a product decision, not a fix. Honest SDD
removes the requirement in its own change instead of keeping a SHALL the product owner does not want, the same way
D3 was closed (change `remove-qa2-mobile-nav`).

## What Changes

- REMOVED requirement `FOOT-3 Imprint is complete` (spec `footer-and-legal`) with its scenarios "Imprint has no
  unfilled templates" and "Imprint email is a valid mailto link".
- `FooterLegalTest.foot3_imprintHasNoFillTemplates` and `foot3_imprintEmailIsValidMailto` (KNOWN BUG D1) are
  deleted; FOOT-1 (footer links, HTTP 200) and FOOT-2 (imprint heading and title) keep covering `/imprint`.
- `docs/decisions.md`: D1 leaves the KNOWN BUG tests; `docs/intent.md` / `current-state.md` / README: D1 has no test.

## Capabilities

### New Capabilities

(none)

### Modified Capabilities

- `footer-and-legal`: requirement `FOOT-3 Imprint is complete` is removed.

## Impact

- Public suite: 65 -> 63 tests; full suite: 101 -> 99. Only KNOWN BUG test left: D16.
- Specs: 59 -> 58 requirements, 97 -> 95 scenarios.
- No live account, no journey run needed: only the public suite and `pnpm check`.
- `pnpm spec:check` after archive: `specs: 10 · active changes: 0 · archived: 13`.
