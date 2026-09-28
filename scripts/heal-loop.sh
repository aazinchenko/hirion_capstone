#!/usr/bin/env bash
# usage: ./scripts/heal-loop.sh "<maven selector>" <file-to-fix> [url] [css-hint] [storageState]
# ./scripts/heal-loop.sh "-Dtest=SmokeTest" \
# src/test/java/ch/hirion/pub/SmokeTest.java https://hirion.ch "#faq"
set -u
SELECTOR=${1:--Dtest=SmokeTest}
FILE=${2:-src/test/java/ch/hirion/pub/SmokeTest.java}
URL=${3:-https://hirion.ch}
HINT=${4:-main}
STORAGE=${5:-}
MAX=5
LOG=".agent-log/heal-log.jsonl"
RAW=".agent-log/heal-raw.log"
mkdir -p .agent-log
log() { printf '{"ts":"%s","file":"%s","msg":"%s"}\n' "$(date -u +%FT%TZ)" "$FILE" "$1" | tee -a "$LOG"; }
for i in $(seq 1 $MAX); do
log "iteration $i: running tests"
if mvn -q test $SELECTOR >> "$RAW" 2>&1; then
log "GREEN on iteration $i"; exit 0
fi
log "RED -- fetching live DOM"
mvn -q exec:java -Dexec.classpathScope=test -Dexec.mainClass=ch.hirion.tools.FetchLiveDom \
-Dexec.args="$URL $HINT $STORAGE" >> "$RAW" 2>&1
log "asking agent for ONE targeted fix"
claude -p "Test file $FILE is failing (see target/surefire-reports).
.agent-log/dom-snippet.html has the live DOM fragment. Fix the locator STRICTLY per AGENTS.md:
getByRole / getByLabel / getByText / getByTestId or the role(...) helper, setExact(true) for
shared prefixes, no nth-child, no waitForTimeout, no Thread.sleep, no guessed classes.
Do NOT touch tests with expectedExceptions (known bugs). Make ONE targeted fix." >> "$RAW" 2>&1
echo "### iteration $i" >> .agent-log/heal-diff.patch
git diff -U0 -- "$FILE" >> .agent-log/heal-diff.patch
if ! java scripts/CheckLocatorRules.java src/test/java >> "$RAW" 2>&1; then
log "REJECTED by CheckLocatorRules -- reverting and retrying"
git checkout -- "$FILE"
continue
fi
done
log "FAILED after $MAX iterations"
exit 1